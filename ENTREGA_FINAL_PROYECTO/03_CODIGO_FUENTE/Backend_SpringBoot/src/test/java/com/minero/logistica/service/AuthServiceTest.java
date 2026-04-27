package com.minero.logistica.service;

import com.minero.logistica.entity.UsuarioEntity;
import com.minero.logistica.exception.UnauthorizedException;
import com.minero.logistica.model.request.LoginRequest;
import com.minero.logistica.model.response.LoginResponse;
import com.minero.logistica.repository.UsuarioRepository;
import com.minero.logistica.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de AuthService.
 * Verifica que el login funcione correctamente cuando las contrasenas
 * estan cifradas con BCryptPasswordEncoder (requerimiento del curso).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de AuthService con BCrypt")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthService authService;

    private UsuarioEntity usuarioBD;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, tokenProvider, passwordEncoder);

        usuarioBD = new UsuarioEntity();
        usuarioBD.setId(1L);
        usuarioBD.setUsername("admin");
        usuarioBD.setPassword(passwordEncoder.encode("admin123"));
        usuarioBD.setEmail("admin@minero.com");
        usuarioBD.setNombres("Administrador");
        usuarioBD.setApellidos("Sistema");
        usuarioBD.setRol("ADMIN");
        usuarioBD.setActivo(true);
        usuarioBD.setBloqueado(false);
        usuarioBD.setIntentosFallidos(0);
    }

    @Test
    @DisplayName("LOGIN CORRECTO: debe generar token cuando las credenciales son validas")
    void login_debeGenerarTokenConCredencialesValidas() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioBD));
        when(tokenProvider.generateToken(any(UsuarioEntity.class))).thenReturn("token-jwt-simulado");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioBD);

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token-jwt-simulado");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRol()).isEqualTo("ADMIN");
        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("LOGIN FALLIDO: debe rechazar password incorrecto (BCrypt no coincide)")
    void login_debeRechazarPasswordIncorrecto() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("passwordIncorrecto");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioBD));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Usuario o contraseña incorrectos");
    }

    @Test
    @DisplayName("LOGIN FALLIDO: debe rechazar usuario inexistente")
    void login_debeRechazarUsuarioInexistente() {
        LoginRequest request = new LoginRequest();
        request.setUsername("noexiste");
        request.setPassword("cualquier");

        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Usuario o contraseña incorrectos");
    }

    @Test
    @DisplayName("LOGIN FALLIDO: debe rechazar usuario inactivo")
    void login_debeRechazarUsuarioInactivo() {
        usuarioBD.setActivo(false);
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioBD));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    @DisplayName("LOGIN FALLIDO: debe rechazar usuario bloqueado")
    void login_debeRechazarUsuarioBloqueado() {
        usuarioBD.setBloqueado(true);
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioBD));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("bloqueado");
    }

    @Test
    @DisplayName("BCRYPT: verifica que el password NO se almacena en texto plano")
    void bcrypt_verificaQuePasswordEstaCifrado() {
        String passwordOriginal = "miPasswordSecreto";
        String hash = passwordEncoder.encode(passwordOriginal);

        assertThat(hash).startsWith("$2a$");
        assertThat(hash).isNotEqualTo(passwordOriginal);
        assertThat(passwordEncoder.matches(passwordOriginal, hash)).isTrue();
        assertThat(passwordEncoder.matches("otroPassword", hash)).isFalse();
    }
}
