package co.com.redhat.integration.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.camel.Exchange;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.dto.MovementFile;
import co.com.redhat.integration.dto.ResponseGetFileState;
import co.com.redhat.integration.dto.ResponseSaveMovementFile;

/**
 * Bean que arma las respuestas de la ruta principal
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class ResponseHandler {
	
	/**
	 * Metodo que arma la respuesta normal de la ruta.
	 * 
	 * @param exchange
	 * @return
	 */
	public Object response(Exchange exchange) {
		ResponseSaveMovementFile response = new ResponseSaveMovementFile();
		response.setCodigo("200");
		response.setDescripcion("Ok");
		
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		
		return response;
	}
	
	/**
	 * Metodo que arma la respuesta normal de la ruta.
	 * 
	 * @param exchange
	 * @return
	 */
	public Object responseGetStateFile(Exchange exchange) {
		ResponseGetFileState response = new ResponseGetFileState();
		response.setCodigo("200");
		response.setDescripcion("Ok");
		
		List<Map<String, String>> respuesta = (List) exchange.getIn().getBody();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		
		if( !respuesta.isEmpty() ) {
			List<MovementFile> movements = new ArrayList();
			for( Map<String, String> registro : respuesta ) {
				MovementFile movementFile = new MovementFile( );
				movementFile.setFileDate(formatoFecha.format(registro.get("FILE_DATE")));
				movementFile.setFileName(registro.get("FILE_NAME"));
				movementFile.setFileState(registro.get("FILE_STATE"));
				movementFile.setFinancialEntity(registro.get("FINANCIAL_ENTITY"));
				
				movements.add(movementFile);
			}
			response.setMovementFile(movements);
			
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		}
		
		return response;
	}
	
	/**
	 * Metodo que arma la respuesta normal de la ruta.
	 * 
	 * @param exchange
	 * @return
	 */
	public Object responseGetFileList(Exchange exchange) {
		ResponseGetFileState response = new ResponseGetFileState();
		response.setCodigo("200");
		response.setDescripcion("Ok");
		
		List<Map<String, String>> respuesta = (List) exchange.getIn().getBody();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		
		if( !respuesta.isEmpty() ) {
			List<MovementFile> movements = new ArrayList();
			for( Map<String, String> registro : respuesta ) {
				MovementFile movementFile = new MovementFile( );
				movementFile.setFileDate(formatoFecha.format(registro.get("FILE_DATE")));
				movementFile.setFileName(registro.get("FILE_NAME"));
				movementFile.setFileState(registro.get("FILE_STATE"));
				movementFile.setFinancialEntity(registro.get("FINANCIAL_ENTITY"));
				
				movements.add(movementFile);
			}
			response.setMovementFile(movements);
			
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		}
		
		return response;
	}
	
	/**
	 * Metodo que arma la respuesta de errores de validacion
	 * 
	 * @param objBeanValidationException
	 * @param exchange
	 */
	public void buildResponseValidation(BeanValidationException objBeanValidationException, Exchange exchange) {
		BeanValidationException beanValidationException = (BeanValidationException) objBeanValidationException;
		Set<ConstraintViolation<Object>> listErrors = beanValidationException.getConstraintViolations();

		ResponseSaveMovementFile response = new ResponseSaveMovementFile();

		Iterator<ConstraintViolation<Object>> listError = listErrors.iterator();
		if (listError.hasNext()) {
			ConstraintViolation<Object> constraint = listError.next();
			
			String message = constraint.getPropertyPath() + " - " + constraint.getMessage();
			
			response.setCodigo("400");
			response.setDescripcion(message);
		}
		exchange.getIn().setBody(response);
	}
	
	/**
	 * Metodo que arma la respuesta de errores de JDBC.
	 * 
	 * @param excepcion
	 * @param exchange
	 */
	public void buildResponseErrorDB(CannotGetJdbcConnectionException excepcion, Exchange exchange) {
		ResponseSaveMovementFile response = new ResponseSaveMovementFile();
		String message = excepcion.getMessage();

		response.setCodigo("400");
		response.setDescripcion(message);

		exchange.getIn().setBody(response);
	}
}
