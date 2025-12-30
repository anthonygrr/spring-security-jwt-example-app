package com.rawr.springsecutiryapp.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//Establecer orden de respuesta
@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponse(
        String username,
        String message,
        String jwt,
        Boolean status
) {
}
