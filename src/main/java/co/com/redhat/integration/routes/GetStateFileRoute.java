package co.com.redhat.integration.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.beans.ResponseHandler;
import co.com.redhat.integration.dto.RequestSaveMovementFile;

/**
 * Ruta que procesa la consulta de estado de un archivo.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class GetStateFileRoute extends RouteBuilder {
	/**
	 * Inyecta el logger
	 */
    @Autowired
    private Logger logger;
    
    /**
     * Inyecta el camelContex por defecto
     */
    @Autowired
    private CamelContext camelContext;
    
    /**
     * Crea el dataformat para el request
     */
    private JacksonDataFormat jsonDataFormat = new JacksonDataFormat(RequestSaveMovementFile.class);

    @Override
    public void configure() throws Exception {
        camelContext.setUseMDCLogging(Boolean.TRUE);
        
        //Manejo de error de validacion de campos
        onException(BeanValidationException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	        .log(LoggingLevel.ERROR, log, "| GetStateFileRoute | Error Field Validator: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseValidation(${exception})");
        
        //Manejo de error de JDBC
        onException(CannotGetJdbcConnectionException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
	        .log(LoggingLevel.ERROR, log, "| GetStateFileRoute | Error: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseErrorDB(${exception})");
        
        //Inicio de la ruta de consulta de estado de archivo
        from("direct:get-state-files")
        	.id("get-state-files")
        	.streamCaching("true")
        		.log(LoggingLevel.INFO, logger, " | GetStateFileRoute | Message: mensaje que llega ${body}")
        	//Se convierte el request a JSON
        	.marshal(jsonDataFormat)
        		.log(LoggingLevel.INFO, logger, " | GetStateFileRoute | Message: Payload De Entrada: ${body}")
        	//Se validan los campos de entrada segun las anotacion del objeto request
        	.to("bean-validator://validatorFields")
        		.log(LoggingLevel.INFO, logger , " | GetStateFileRoute | Message: se invoca el select SQL con los valores initFileDate: ${header.initFileDate}, endFileDate: ${header.endFileDate} y financialEntity: ${header.financialEntity}")
        	//Se ejecuta la sentencia SQL
        	.to("sql:SELECT * FROM movements_files WHERE file_date >= CAST ( :#${header.initFileDate} AS DATE ) AND file_date <= CAST ( :#${header.endFileDate} AS DATE ) AND financial_entity = :#${header.financialEntity}?dataSource=#dataSourceFiles")
        		.log(LoggingLevel.INFO, logger , "| GetStateFileRoute | Message: respuesta bd : ${body}")
        	//Se arma la respuesta
        	.bean(ResponseHandler.class, "responseGetStateFile(${exchange})")
        		.log(LoggingLevel.INFO, logger , " | GetStateFileRoute | Message: Response : ${body}")
        .end();
    }
}
