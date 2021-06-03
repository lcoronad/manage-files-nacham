package co.com.redhat.integration.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Request del endpoint de guardar el movimiento de un archivo.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 02/06/2021
 * @version 1.0
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestSaveMovementFile {

    @JsonProperty
    @NotEmpty(message = "El nombre del archivo no puede ser vacio")
    private String fileName;
    
    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    //@NotEmpty(message = "La fecha no puede ser vacia")
    private String fileDate;

    @JsonProperty
    @NotEmpty(message = "El estado no puede ser vacio")
    private String fileState;
    
    @JsonProperty
    @NotEmpty(message = "La entidad financiera no puede ser vacia")
    private String financialEntity;
    
    @JsonProperty
    @NotEmpty(message = "El destino no puede ser vacio")
    private String target;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public String getFileState() {
		return fileState;
	}

	public void setFileState(String fileState) {
		this.fileState = fileState;
	}

	public String getFinancialEntity() {
		return financialEntity;
	}

	public void setFinancialEntity(String financialEntity) {
		this.financialEntity = financialEntity;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
