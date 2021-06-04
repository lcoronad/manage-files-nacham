package co.com.redhat.integration.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.beans.ResponseHandler;

/**
 * Ruta que procesa la consulta de listado de archivos con error.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 04/06/2021
 * @version 1.0
 */
@Component
public class GetFileListErrorRoute extends RouteBuilder {
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
    
    @Override
    public void configure() throws Exception {
        camelContext.setUseMDCLogging(Boolean.TRUE);
        
        //Manejo de error de validacion de campos
        onException(BeanValidationException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	        .log(LoggingLevel.ERROR, log, "| GetFileListErrorRoute | Error Field Validator: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseValidation(${exception})");
        
        //Manejo de error de JDBC
        onException(CannotGetJdbcConnectionException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
	        .log(LoggingLevel.ERROR, log, "| GetFileListErrorRoute | Error: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseErrorDB(${exception})");
        
        //Inicio de la ruta de consulta de lista de archivos con error
        from("direct:get-file-list-error")
        	.id("get-file-list-error")
        	.streamCaching("true")
        		.log(LoggingLevel.INFO, logger, " | GetFileListErrorRoute | Message: Inicia la ruta")
        		.log(LoggingLevel.INFO, logger , " | GetFileListErrorRoute | Message: se invoca el select SQL con los valores initFileDate: ${header.initFileDate}, endFileDate: ${header.endFileDate}")
        	//Se ejecuta la sentencia SQL
        	.to("sql:{{service.rest.select.file.list.error}}?dataSource=#dataSourceFiles")
        		.log(LoggingLevel.INFO, logger , "| GetFileListErrorRoute | Message: respuesta bd : ${body}")
        	//Se arma la respuesta
        	.bean(ResponseHandler.class, "responseGetFileList(${exchange})")
        		.log(LoggingLevel.INFO, logger , " | GetFileListErrorRoute | Message: Fin de la ruta")
        .end();
    }
}
