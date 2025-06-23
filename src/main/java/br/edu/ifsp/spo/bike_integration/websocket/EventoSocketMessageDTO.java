package br.edu.ifsp.spo.bike_integration.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoSocketMessageDTO {

    private String action;
    private String message;
    private Object data;
    private String timestamp;
    private String userId;

}
