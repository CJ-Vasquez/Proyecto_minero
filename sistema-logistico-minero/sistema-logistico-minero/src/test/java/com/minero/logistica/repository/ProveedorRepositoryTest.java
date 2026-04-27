package com.minero.logistica.repository;

import com.minero.logistica.entity.ProveedorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de acceso a datos (Repository) para ProveedorEntity.
 * Cubre las operaciones: INSERTAR, LISTAR, ACTUALIZAR y ELIMINAR.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Pruebas CRUD de ProveedorRepository")
class ProveedorRepositoryTest {

    @Autowired
    private ProveedorRepository proveedorRepository;

    private ProveedorEntity proveedorBase;

    @BeforeEach
    void setUp() {
        proveedorRepository.deleteAll();

        proveedorBase = new ProveedorEntity();
        proveedorBase.setCodigo("PROV-001");
        proveedorBase.setRazonSocial("Minera Tauro S.A.C.");
        proveedorBase.setRuc("20123456789");
        proveedorBase.setNombreContacto("Maria Gonzales");
        proveedorBase.setTelefono("987654321");
        proveedorBase.setEmail("contacto@tauro.com");
        proveedorBase.setDireccion("Av. Industrial 123, Lima");
        proveedorBase.setEstado("ACTIVO");
        proveedorBase.setPrioridad(1);
        proveedorBase.setPuntajeEvaluacion(45.0);
    }

    @Test
    @DisplayName("INSERTAR: debe guardar un proveedor nuevo y asignar ID y fecha de registro")
    void insertar_debeGuardarProveedorYAsignarId() {
        ProveedorEntity guardado = proveedorRepository.save(proveedorBase);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull().isPositive();
        assertThat(guardado.getCodigo()).isEqualTo("PROV-001");
        assertThat(guardado.getRuc()).isEqualTo("20123456789");
        assertThat(guardado.getFechaRegistro()).isNotNull();

        Optional<ProveedorEntity> recuperado = proveedorRepository.findById(guardado.getId());
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getRazonSocial()).isEqualTo("Minera Tauro S.A.C.");
    }

    @Test
    @DisplayName("LISTAR: debe recuperar todos los proveedores registrados")
    void listar_debeRecuperarTodosLosProveedores() {
        proveedorRepository.save(proveedorBase);

        ProveedorEntity prov2 = new ProveedorEntity();
        prov2.setCodigo("PROV-002");
        prov2.setRazonSocial("Suministros del Sur S.A.");
        prov2.setRuc("20987654321");
        prov2.setEmail("ventas@suministrosur.com");
        prov2.setEstado("ACTIVO");
        prov2.setPrioridad(2);
        prov2.setPuntajeEvaluacion(35.0);
        proveedorRepository.save(prov2);

        ProveedorEntity prov3 = new ProveedorEntity();
        prov3.setCodigo("PROV-003");
        prov3.setRazonSocial("Equipos Mineros EIRL");
        prov3.setRuc("10456789012");
        prov3.setEmail("info@equiposmineros.com");
        prov3.setEstado("INACTIVO");
        prov3.setPrioridad(3);
        prov3.setPuntajeEvaluacion(20.0);
        proveedorRepository.save(prov3);

        List<ProveedorEntity> proveedores = proveedorRepository.findAll();

        assertThat(proveedores).hasSize(3);
        assertThat(proveedores).extracting(ProveedorEntity::getRuc)
                .containsExactlyInAnyOrder("20123456789", "20987654321", "10456789012");
    }

    @Test
    @DisplayName("ACTUALIZAR: debe modificar los datos de un proveedor existente")
    void actualizar_debeModificarProveedorExistente() {
        ProveedorEntity guardado = proveedorRepository.save(proveedorBase);
        Long id = guardado.getId();

        guardado.setRazonSocial("Minera Tauro Mejorada S.A.C.");
        guardado.setNombreContacto("Ricardo Salinas");
        guardado.setEstado("INACTIVO");
        guardado.setPuntajeEvaluacion(48.5);
        proveedorRepository.save(guardado);

        Optional<ProveedorEntity> actualizado = proveedorRepository.findById(id);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getRazonSocial()).isEqualTo("Minera Tauro Mejorada S.A.C.");
        assertThat(actualizado.get().getNombreContacto()).isEqualTo("Ricardo Salinas");
        assertThat(actualizado.get().getEstado()).isEqualTo("INACTIVO");
        assertThat(actualizado.get().getPuntajeEvaluacion()).isEqualTo(48.5);
        assertThat(actualizado.get().getRuc()).isEqualTo("20123456789");
    }

    @Test
    @DisplayName("ELIMINAR: debe borrar un proveedor de la base de datos")
    void eliminar_debeBorrarProveedorDeLaBaseDeDatos() {
        ProveedorEntity guardado = proveedorRepository.save(proveedorBase);
        Long id = guardado.getId();
        assertThat(proveedorRepository.findById(id)).isPresent();

        proveedorRepository.deleteById(id);

        Optional<ProveedorEntity> eliminado = proveedorRepository.findById(id);
        assertThat(eliminado).isEmpty();
        assertThat(proveedorRepository.count()).isZero();
    }

    @Test
    @DisplayName("QUERY: debe verificar existencia por RUC")
    void existsByRuc_debeRetornarTruePorRucExistente() {
        proveedorRepository.save(proveedorBase);

        boolean existe = proveedorRepository.existsByRuc("20123456789");
        boolean noExiste = proveedorRepository.existsByRuc("99999999999");

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
