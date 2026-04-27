package com.minero.logistica.config;

import com.minero.logistica.entity.UsuarioEntity;
import com.minero.logistica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        migrarPasswordsExistentes();
        crearUsuariosPorDefecto();
    }

    private void migrarPasswordsExistentes() {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        int migrados = 0;
        for (UsuarioEntity usuario : usuarios) {
            String password = usuario.getPassword();
            if (password != null && !esPasswordBCrypt(password)) {
                log.warn("Migrando password en texto plano a BCrypt para usuario: {}", usuario.getUsername());
                usuario.setPassword(passwordEncoder.encode(password));
                usuarioRepository.save(usuario);
                migrados++;
            }
        }
        if (migrados > 0) {
            log.info("Migracion BCrypt completada: {} usuarios actualizados", migrados);
        }
    }

    private boolean esPasswordBCrypt(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    private void crearUsuariosPorDefecto() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        log.info("Creando usuarios por defecto (primer arranque)");
        crearUsuario("admin", "admin123", "admin@minero.com", "Administrador", "Sistema", "Jefe TI", "ADMIN");
        crearUsuario("gerente", "gerente123", "gerente@minero.com", "Luis", "Perez", "Gerente de Compras", "GERENTE");
        crearUsuario("jefe", "jefe123", "jefe@minero.com", "Carlos", "Ramirez", "Jefe de Almacen", "JEFE_ALMACEN");
        crearUsuario("compras", "compras123", "compras@minero.com", "Ana", "Lopez", "Asistente de Compras", "ASISTENTE_COMPRAS");
        crearUsuario("almacen", "almacen123", "almacen@minero.com", "Jorge", "Torres", "Asistente de Almacen", "ASISTENTE_ALMACEN");
    }

    private void crearUsuario(String username, String password, String email,
                              String nombres, String apellidos, String cargo, String rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setCargo(cargo);
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setUltimoCambioPassword(LocalDateTime.now());
        usuarioRepository.save(usuario);
        log.info("Usuario por defecto creado: {} / {}", username, rol);
    }
}
