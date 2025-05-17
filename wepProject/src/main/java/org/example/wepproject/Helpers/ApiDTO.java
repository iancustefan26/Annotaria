package org.example.wepproject.Helpers;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiDTO {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public ApiDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}