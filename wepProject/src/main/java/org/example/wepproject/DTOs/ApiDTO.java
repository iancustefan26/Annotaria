package org.example.wepproject.DTOs;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiDTO {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Object data;

    public ApiDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiDTO(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}