package co.com.redhat.integration.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

/**
 * Response del endpoint de guardar un movimiento de un archivo.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovementFile implements Serializable {

    private static final long serialVersionUID = -6104876573750302537L;

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
}
