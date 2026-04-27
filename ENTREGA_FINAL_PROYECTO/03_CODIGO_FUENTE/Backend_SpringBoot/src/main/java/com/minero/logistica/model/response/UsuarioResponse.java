package com.minero.logistica.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private String nombres;
    private String apellidos;
    private String cargo;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
}