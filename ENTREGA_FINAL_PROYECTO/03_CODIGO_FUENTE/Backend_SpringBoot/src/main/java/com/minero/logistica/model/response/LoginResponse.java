package com.minero.logistica.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String nombres;
    private String apellidos;
    private String rol;
    
    public LoginResponse(String token, Long id, String username, String email, 
                         String nombres, String apellidos, String rol) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
    }
}