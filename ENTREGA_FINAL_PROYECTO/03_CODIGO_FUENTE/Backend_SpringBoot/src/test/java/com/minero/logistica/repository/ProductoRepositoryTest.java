package com.minero.logistica.repository;

import com.minero.logistica.entity.ProductoEntity;
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
 * Pruebas de la capa de acceso a datos (Repository) para ProductoEntity.
 * Cubre las operaciones: INSERTAR, LISTAR, ACTUALIZAR y ELIMINAR.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Pruebas CRUD de ProductoRepository")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    private ProductoEntity productoBase;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        productoBase = new ProductoEntity();
        productoBase.setCodigo("PRD-001");
        productoBase.setNombre("Casco Minero");
        productoBase.setDescripcion("Casco de seguridad con lampara");
        productoBase.setCategoria("EPP");
        productoBase.setUnidadMedida("UND");
        productoBase.setPrecioReferencial(85.50);
        productoBase.setStockMinimo(10);
        productoBase.setStockActual(50);
        productoBase.setUbicacionFisica("Almacen A - Estante 3");
        productoBase.setActivo(true);
        productoBase.setStockLima(20);
        productoBase.setStockTrujillo(15);
        productoBase.setStockMina(15);
    }

    @Test
    @DisplayName("INSERTAR: debe guardar un producto nuevo y asignar ID")
    void insertar_debeGuardarProductoYAsignarId() {
        ProductoEntity guardado = productoRepository.save(productoBase);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull().isPositive();
        assertThat(guardado.getCodigo()).isEqualTo("PRD-001");
        assertThat(guardado.getNombre()).isEqualTo("Casco Minero");
        assertThat(guardado.getStockActual()).isEqualTo(50);

        Optional<ProductoEntity> recuperado = productoRepository.findById(guardado.getId());
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getCategoria()).isEqualTo("EPP");
    }

    @Test
    @DisplayName("LISTAR: debe recuperar todos los productos almacenados")
    void listar_debeRecuperarTodosLosProductos() {
        productoRepository.save(productoBase);

        ProductoEntity producto2 = new ProductoEntity();
        producto2.setCodigo("PRD-002");
        producto2.setNombre("Guantes Industriales");
        producto2.setCategoria("EPP");
        producto2.setUnidadMedida("PAR");
        producto2.setPrecioReferencial(25.00);
        producto2.setStockActual(120);
        producto2.setStockMinimo(20);
        producto2.setActivo(true);
        productoRepository.save(producto2);

        ProductoEntity producto3 = new ProductoEntity();
        producto3.setCodigo("PRD-003");
        producto3.setNombre("Taladro Neumatico");
        producto3.setCategoria("HERRAMIENTAS");
        producto3.setUnidadMedida("UND");
        producto3.setPrecioReferencial(1500.00);
        producto3.setStockActual(5);
        producto3.setStockMinimo(2);
        producto3.setActivo(true);
        productoRepository.save(producto3);

        List<ProductoEntity> productos = productoRepository.findAll();

        assertThat(productos).hasSize(3);
        assertThat(productos).extracting(ProductoEntity::getCodigo)
                .containsExactlyInAnyOrder("PRD-001", "PRD-002", "PRD-003");
    }

    @Test
    @DisplayName("ACTUALIZAR: debe modificar los datos de un producto existente")
    void actualizar_debeModificarProductoExistente() {
        ProductoEntity guardado = productoRepository.save(productoBase);
        Long id = guardado.getId();

        guardado.setNombre("Casco Minero PRO");
        guardado.setPrecioReferencial(120.00);
        guardado.setStockActual(100);
        productoRepository.save(guardado);

        Optional<ProductoEntity> actualizado = productoRepository.findById(id);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombre()).isEqualTo("Casco Minero PRO");
        assertThat(actualizado.get().getPrecioReferencial()).isEqualTo(120.00);
        assertThat(actualizado.get().getStockActual()).isEqualTo(100);
        assertThat(actualizado.get().getCodigo()).isEqualTo("PRD-001");
    }

    @Test
    @DisplayName("ELIMINAR: debe borrar un producto de la base de datos")
    void eliminar_debeBorrarProductoDeLaBaseDeDatos() {
        ProductoEntity guardado = productoRepository.save(productoBase);
        Long id = guardado.getId();
        assertThat(productoRepository.findById(id)).isPresent();

        productoRepository.deleteById(id);

        Optional<ProductoEntity> eliminado = productoRepository.findById(id);
        assertThat(eliminado).isEmpty();
        assertThat(productoRepository.count()).isZero();
    }

    @Test
    @DisplayName("QUERY: debe encontrar productos con stock critico")
    void listarProductosConStockCritico_debeRetornarProductosBajoMinimo() {
        productoBase.setStockActual(5);
        productoBase.setStockMinimo(10);
        productoRepository.save(productoBase);

        ProductoEntity productoOk = new ProductoEntity();
        productoOk.setCodigo("PRD-OK");
        productoOk.setNombre("Producto OK");
        productoOk.setCategoria("EPP");
        productoOk.setUnidadMedida("UND");
        productoOk.setStockActual(100);
        productoOk.setStockMinimo(10);
        productoOk.setActivo(true);
        productoRepository.save(productoOk);

        List<ProductoEntity> criticos = productoRepository.findProductosConStockCritico();

        assertThat(criticos).hasSize(1);
        assertThat(criticos.get(0).getCodigo()).isEqualTo("PRD-001");
    }
}
