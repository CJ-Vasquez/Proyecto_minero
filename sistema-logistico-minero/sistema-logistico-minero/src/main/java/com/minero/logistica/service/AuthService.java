package com.minero.logistica.service;

import com.minero.logistica.entity.UsuarioEntity;
import com.minero.logistica.exception.UnauthorizedException;
import com.minero.logistica.model.request.LoginRequest;
import com.minero.logistica.model.response.LoginResponse;
import com.minero.logistica.repository.UsuarioRepository;
import com.minero.logistica.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login: {}", request.getUsername());

        UsuarioEntity usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new UnauthorizedException("El usuario se encuentra inactivo");
        }

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new UnauthorizedException("El usuario se encuentra bloqueado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            int intentos = usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos();
            usuario.setIntentosFallidos(intentos + 1);
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
        }

        usuario.setIntentosFallidos(0);
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = tokenProvider.generateToken(usuario);

        return new LoginResponse(
            token, usuario.getId(), usuario.getUsername(),
            usuario.getEmail(), usuario.getNombres(),
            usuario.getApellidos(), usuario.getRol()
        );
    }
}
