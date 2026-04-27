package com.minero.logistica.repository;

import com.minero.logistica.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de acceso a datos (Repository) para UsuarioEntity.
 * Cubre las operaciones: INSERTAR, LISTAR, ACTUALIZAR y ELIMINAR.
 * Verifica adicionalmente que las contrasenas se almacenen cifradas con BCrypt.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Pruebas CRUD de UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioEntity usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        usuarioBase = new UsuarioEntity();
        usuarioBase.setUsername("jperez");
        usuarioBase.setPassword(passwordEncoder.encode("password123"));
        usuarioBase.setEmail("jperez@minero.com");
        usuarioBase.setNombres("Juan");
        usuarioBase.setApellidos("Perez");
        usuarioBase.setCargo("Asistente");
        usuarioBase.setRol("ASISTENTE_ALMACEN");
        usuarioBase.setActivo(true);
        usuarioBase.setBloqueado(false);
        usuarioBase.setIntentosFallidos(0);
    }

    @Test
    @DisplayName("INSERTAR: debe guardar un usuario con password cifrado con BCrypt")
    void insertar_debeGuardarUsuarioConPasswordCifrado() {
        UsuarioEntity guardado = usuarioRepository.save(usuarioBase);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull().isPositive();
        assertThat(guardado.getUsername()).isEqualTo("jperez");

        assertThat(guardado.getPassword()).startsWith("$2a$");
        assertThat(guardado.getPassword()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", guardado.getPassword())).isTrue();
    }

    @Test
    @DisplayName("LISTAR: debe recuperar todos los usuarios registrados")
    void listar_debeRecuperarTodosLosUsuarios() {
        usuarioRepository.save(usuarioBase);

        UsuarioEntity admin = new UsuarioEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@minero.com");
        admin.setNombres("Administrador");
        admin.setApellidos("Sistema");
        admin.setRol("ADMIN");
        admin.setActivo(true);
        admin.setBloqueado(false);
        admin.setIntentosFallidos(0);
        usuarioRepository.save(admin);

        UsuarioEntity gerente = new UsuarioEntity();
        gerente.setUsername("gerente");
        gerente.setPassword(passwordEncoder.encode("gerente123"));
        gerente.setEmail("gerente@minero.com");
        gerente.setNombres("Luis");
        gerente.setApellidos("Gerente");
        gerente.setRol("GERENTE");
        gerente.setActivo(true);
        gerente.setBloqueado(false);
        gerente.setIntentosFallidos(0);
        usuarioRepository.save(gerente);

        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        assertThat(usuarios).hasSize(3);
        assertThat(usuarios).extracting(UsuarioEntity::getUsername)
                .containsExactlyInAnyOrder("jperez", "admin", "gerente");
    }

    @Test
    @DisplayName("ACTUALIZAR: debe modificar los datos de un usuario existente")
    void actualizar_debeModificarUsuarioExistente() {
        UsuarioEntity guardado = usuarioRepository.save(usuarioBase);
        Long id = guardado.getId();

        guardado.setNombres("Juan Carlos");
        guardado.setEmail("jcperez@minero.com");
        guardado.setRol("JEFE_ALMACEN");
        guardado.setCargo("Jefe de Turno");
        usuarioRepository.save(guardado);

        Optional<UsuarioEntity> actualizado = usuarioRepository.findById(id);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombres()).isEqualTo("Juan Carlos");
        assertThat(actualizado.get().getEmail()).isEqualTo("jcperez@minero.com");
        assertThat(actualizado.get().getRol()).isEqualTo("JEFE_ALMACEN");
        assertThat(actualizado.get().getUsername()).isEqualTo("jperez");
    }

    @Test
    @DisplayName("ELIMINAR: debe borrar un usuario de la base de datos")
    void eliminar_debeBorrarUsuarioDeLaBaseDeDatos() {
        UsuarioEntity guardado = usuarioRepository.save(usuarioBase);
        Long id = guardado.getId();
        assertThat(usuarioRepository.findById(id)).isPresent();

        usuarioRepository.deleteById(id);

        Optional<UsuarioEntity> eliminado = usuarioRepository.findById(id);
        assertThat(eliminado).isEmpty();
        assertThat(usuarioRepository.count()).isZero();
    }

    @Test
    @DisplayName("BUSCAR POR USERNAME: debe encontrar usuario por su nombre de usuario")
    void findByUsername_debeEncontrarUsuarioExistente() {
        usuarioRepository.save(usuarioBase);

        Optional<UsuarioEntity> encontrado = usuarioRepository.findByUsername("jperez");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("jperez@minero.com");
    }

    @Test
    @DisplayName("VERIFICAR PASSWORD: debe validar correctamente con BCrypt")
    void verificar_passwordDebeCoincidirAlCifrarYCompararConBCrypt() {
        UsuarioEntity guardado = usuarioRepository.save(usuarioBase);

        assertThat(passwordEncoder.matches("password123", guardado.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("passwordIncorrecto", guardado.getPassword())).isFalse();
    }
}
