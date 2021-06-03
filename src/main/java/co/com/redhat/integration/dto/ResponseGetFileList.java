package co.com.redhat.integration.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
public class ResponseGetFileList implements Serializable {

    private static final long serialVersionUID = -6104876573750302537L;

    @JsonProperty
    @ApiModelProperty(dataType = "String")
    private String codigo;

    @JsonProperty
    @ApiModelProperty(dataType = "String"  , required = true )
    @NotEmpty
    private String descripcion;
    
    @JsonProperty
    @NotEmpty
    private List<MovementFile> movementFile;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<MovementFile> getMovementFile() {
		return movementFile;
	}

	public void setMovementFile(List<MovementFile> movementFile) {
		this.movementFile = movementFile;
	}
}
