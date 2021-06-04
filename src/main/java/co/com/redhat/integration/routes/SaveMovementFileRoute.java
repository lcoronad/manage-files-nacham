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
 * Ruta que procesa la insercion de movimientos de archivos.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class SaveMovementFileRoute extends RouteBuilder {
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
	        .log(LoggingLevel.ERROR, log, "| SaveMovementFileRoute | Error Field Validator: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseValidation(${exception})");
        
        //Manejo de error de JDBC
        onException(CannotGetJdbcConnectionException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
	        .log(LoggingLevel.ERROR, log, "| SaveMovementFileRoute | Error: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseErrorDB(${exception})");
        
        //Inicio de la ruta de insertar el movimiento de un archivo
        from("direct:save-movement-file")
        	.id("save-movement-file")
        	.streamCaching("true")
        		.log(LoggingLevel.INFO, logger, "| SaveMovementFileRoute | Message: Inicio de la ruta")
        	//Se convierte el request a JSON
        	.marshal(jsonDataFormat)
        		.log(LoggingLevel.INFO, logger, "| SaveMovementFileRoute | Message: Payload De Entrada: ${body}")
        	//Se convierte el JSON a objeto
        	.unmarshal(jsonDataFormat)
        	//Se validan los campos de entrada segun las anotacion del objeto request
        	.to("bean-validator://validatorFields")
        		.log(LoggingLevel.INFO, logger , "| SaveMovementFileRoute | Message: se invoca el insert SQL con los valores fileName: ${body.fileName}, fileDate: ${body.fileDate}, fileState: ${body.fileState}, financialEntity: ${body.financialEntity}, target: ${body.target} y errorDescription: ${body.errorDescription}")
        	//Se crean los headers para el insert parametrizado
        	.setHeader("fileName")
        		.simple("${body.fileName}")
        	.setHeader("fileDate")
        		.simple("${body.fileDate}")
        	.setHeader("fileState")
        		.simple("${body.fileState}")
        	.setHeader("financialEntity")
        		.simple("${body.financialEntity}")
        	.setHeader("target")
        		.simple("${body.target}")
        	.setHeader("errorDescription")
        		.simple("${body.errorDescription}")
        	//Se ejecuta la sentencia SQL
        	.to("sql:{{service.rest.insert.movement.file}}?dataSource=#dataSourceFiles")
        		.log(LoggingLevel.INFO, logger , "| SaveMovementFileRoute | Message: respuesta bd : ${body}")
        	//Se arma la respuesta
        	.bean(ResponseHandler.class, "response(${exchange})")
        		.log(LoggingLevel.INFO, logger , "| SaveMovementFileRoute | Message: Fin de la ruta")
        .end();
    }
}
