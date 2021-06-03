package co.com.redhat.integration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.dto.RequestSaveMovementFile;
import co.com.redhat.integration.dto.ResponseSaveMovementFile;

/**
 * Ruta principal que expone el servicio rest.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class RestConfigurationRoute extends RouteBuilder {
	/**
	 * Inyecta las propiedades
	 */
    @Autowired
    private Environment env;

    @Override
    public void configure() throws Exception {
    	//Configuracion del servicio rest
    	restConfiguration()
	        .component("servlet")
	        .bindingMode(RestBindingMode.json)
	        .skipBindingOnErrorCode(false)
	        .dataFormatProperty("prettyPrint", "true")
	        .apiContextPath("api-doc")
	  		.apiProperty("api.description", "{{service.rest.description}}")
	  		.apiProperty("api.title", "{{camel.springboot.name}}")
	  		.apiProperty("api.version", "1.0.0");;
    	//Se expone el contexto del servicio rest
        rest(env.getProperty("service.rest.uri"))
            .description(env.getProperty("service.rest.description"))
            .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
        //Se expone el endpoint post para guardar el movimiento del archivo
        .post(env.getProperty("service.rest.save.uri"))
            .description(env.getProperty("service.rest.save.description"))
            .type(RequestSaveMovementFile.class)
            .outType(ResponseSaveMovementFile.class)
            .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .description(env.getProperty("service.rest.save.description"))
            .responseMessage()
                .code(200)
                .message("All users successfully created")
            .endResponseMessage()
            .to("direct:save-movement-file")
        //Se expone el endpoint get para la consulta de estado
        .get(env.getProperty("service.rest.get.state.uri"))
           .description(env.getProperty("service.rest.get.state.description"))
           .param()
	  			.name("initFileDate")
		        .type(RestParamType.path)
		        .required(true)
		        .description("Initial File Date")
	       .endParam()
           .param()
	  			.name("endFileDate")
		        .type(RestParamType.path)
		        .required(true)
		        .description("End File Date")
	       .endParam()
           .param()
	  			.name("financialEntity")
		        .type(RestParamType.path)
		        .required(true)
		        .description("Financial Entity")
	       .endParam()
           .responseMessage()
               .code(200)
               .message("Get file state OK")
           .endResponseMessage()
           .to("direct:get-state-files")
        //Se expone el endpoint get para la consulta de estado
        .get(env.getProperty("service.rest.get.file.list.uri"))
           .description(env.getProperty("service.rest.get.file.list.description"))
           .param()
  	   		 .name("initFileDate")
	         .type(RestParamType.path)
	         .required(true)
	         .description("Initial File Date")
          .endParam()
          .param()
  			.name("endFileDate")
	        .type(RestParamType.path)
	        .required(true)
	        .description("End File Date")
          .endParam()
          .responseMessage()
              .code(200)
              .message("Get file list OK")
          .endResponseMessage()
          .to("direct:get-file-list")
        //Se expone el endpoint get para el health check
        .get(env.getProperty("service.rest.health.uri"))
            .description(env.getProperty("service.rest.health.description"))
            .to("direct:health");
    }
}
