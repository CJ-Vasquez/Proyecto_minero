import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrdenCompraService, OrdenCompra } from '../../services/orden-compra.service';
import { CotizacionService, Cotizacion } from '../../services/cotizacion.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-ordenes-compra',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ordenes-compra.component.html',
  styleUrls: ['./ordenes-compra.component.scss']
})
export class OrdenesCompraComponent implements OnInit {
  ordenes: OrdenCompra[] = [];
  ordenesFiltradas: OrdenCompra[] = [];
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  mostrarDetalleModal: boolean = false;
  ordenSeleccionada: OrdenCompra | null = null;
  cargando: boolean = true;
  rol: string = '';

  // Paginación
  paginaActual: number = 1;
  itemsPorPagina: number = 10;
  totalPaginas: number = 1;
  totalRegistros: number = 0;
  Math = Math;

  // Datos para nueva orden
  cotizacionesAprobadas: Cotizacion[] = [];
  cotizacionSeleccionada: Cotizacion | null = null;
  codigoCotizacion: string = '';
  busquedaRealizada: boolean = false;
  
  ordenForm = {
    cotizacionId: 0,
    fechaEmision: new Date().toISOString().split('T')[0],
    fechaEntrega: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    observaciones: ''
  };

  estados = ['PENDIENTE', 'ENVIADA', 'RECIBIDA', 'CANCELADA'];

  constructor(
    private ordenCompraService: OrdenCompraService,
    private cotizacionService: CotizacionService
  ) {}

  ngOnInit(): void {
    const user = localStorage.getItem('user');
    if (user) {
      const userObj = JSON.parse(user);
      this.rol = userObj.rol || 'USUARIO';
    }
    this.cargarOrdenes();
    this.cargarCotizacionesAprobadas();
  }

  // ============================================
  // CARGAR ÓRDENES
  // ============================================

  cargarOrdenes(): void {
    this.cargando = true;
    this.ordenCompraService.getOrdenes().subscribe({
      next: (data) => {
        console.log('Órdenes desde backend:', data);
        if (data && data.length > 0) {
          this.ordenes = data;
        } else {
          this.cargarOrdenesEjemplo();
        }
        this.ordenesFiltradas = [...this.ordenes];
        this.calcularPaginacion();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar órdenes:', err);
        this.cargarOrdenesEjemplo();
        this.ordenesFiltradas = [...this.ordenes];
        this.calcularPaginacion();
        this.cargando = false;
      }
    });
  }

  cargarOrdenesEjemplo(): void {
    this.ordenes = [
      {
        id: 1,
        numeroOrden: 'OC-202501001',
        cotizacionId: 1,
        numeroCotizacion: 'COT-202501001',
        proveedorId: 1,
        nombreProveedor: '3M Perú',
        fechaEmision: '2025-01-10',
        fechaEntrega: '2025-01-25',
        montoTotal: 12500,
        estado: 'ENVIADA',
        observaciones: 'Entregar en almacén de Lima',
        detalles: [
          { productoId: 1, codigoProducto: 'EPP-001', nombreProducto: 'Casco de Seguridad', cantidad: 50, precioUnitario: 45, subtotal: 2250 },
          { productoId: 2, codigoProducto: 'EPP-002', nombreProducto: 'Lentes de Seguridad', cantidad: 100, precioUnitario: 25, subtotal: 2500 },
          { productoId: 3, codigoProducto: 'EPP-003', nombreProducto: 'Guantes de Cuero', cantidad: 80, precioUnitario: 35, subtotal: 2800 },
          { productoId: 4, codigoProducto: 'EPP-004', nombreProducto: 'Botas de Seguridad', cantidad: 40, precioUnitario: 120, subtotal: 4800 }
        ]
      },
      {
        id: 2,
        numeroOrden: 'OC-202501002',
        cotizacionId: 2,
        numeroCotizacion: 'COT-202501002',
        proveedorId: 2,
        nombreProveedor: 'Ferreyros S.A.',
        fechaEmision: '2025-01-15',
        fechaEntrega: '2025-01-30',
        montoTotal: 5000,
        estado: 'PENDIENTE',
        observaciones: 'Urgente',
        detalles: [
          { productoId: 5, codigoProducto: 'HERR-001', nombreProducto: 'Taladro Industrial', cantidad: 10, precioUnitario: 500, subtotal: 5000 }
        ]
      }
    ];
  }

  // ============================================
  // CARGAR COTIZACIONES APROBADAS
  // ============================================

  cargarCotizacionesAprobadas(): void {
    this.cotizacionService.getCotizaciones().subscribe({
      next: (data) => {
        this.cotizacionesAprobadas = data.filter(c => c.estado === 'APROBADO');
        console.log('Cotizaciones aprobadas:', this.cotizacionesAprobadas);
      },
      error: (err) => {
        console.error('Error cargando cotizaciones:', err);
        this.cotizacionesAprobadas = [];
      }
    });
  }

  // ============================================
  // BÚSQUEDA POR CÓDIGO DE COTIZACIÓN
  // ============================================

  buscarCotizacionPorCodigo(): void {
    this.busquedaRealizada = true;
    
    if (!this.codigoCotizacion || this.codigoCotizacion.trim() === '') {
      this.cotizacionSeleccionada = null;
      this.ordenForm.cotizacionId = 0;
      return;
    }
    
    const encontrada = this.cotizacionesAprobadas.find(
      c => c.numeroCotizacion === this.codigoCotizacion
    );
    
    if (encontrada) {
      this.cotizacionSeleccionada = encontrada;
      this.ordenForm.cotizacionId = encontrada.id;
      this.ordenForm.fechaEmision = new Date().toISOString().split('T')[0];
      console.log('Cotización encontrada:', encontrada);
    } else {
      this.cotizacionSeleccionada = null;
      this.ordenForm.cotizacionId = 0;
    }
  }

  // ============================================
  // PAGINACIÓN
  // ============================================

  calcularPaginacion(): void {
    this.totalRegistros = this.ordenesFiltradas.length;
    this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = Math.max(1, this.totalPaginas);
    }
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
  }

  getOrdenesPaginadas(): OrdenCompra[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.ordenesFiltradas.slice(inicio, inicio + this.itemsPorPagina);
  }

  get paginas(): number[] {
    const paginas: number[] = [];
    const maxPaginas = 5;
    let inicio = Math.max(1, this.paginaActual - Math.floor(maxPaginas / 2));
    let fin = Math.min(this.totalPaginas, inicio + maxPaginas - 1);
    
    if (fin - inicio + 1 < maxPaginas) {
      inicio = Math.max(1, fin - maxPaginas + 1);
    }
    
    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }
    return paginas;
  }

  // ============================================
  // FILTROS
  // ============================================

  aplicarFiltros(): void {
    if (this.estadoSeleccionado) {
      this.ordenesFiltradas = this.ordenes.filter(o => o.estado === this.estadoSeleccionado);
    } else {
      this.ordenesFiltradas = [...this.ordenes];
    }
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  limpiarFiltros(): void {
    this.estadoSeleccionado = '';
    this.ordenesFiltradas = [...this.ordenes];
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  // ============================================
  // ESTADOS
  // ============================================

  getEstadoClass(estado: string): string {
    switch(estado) {
      case 'RECIBIDA': return 'success';
      case 'ENVIADA': return 'info';
      case 'CANCELADA': return 'danger';
      default: return 'warning';
    }
  }

  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'RECIBIDA': return 'Recibida';
      case 'ENVIADA': return 'Enviada';
      case 'CANCELADA': return 'Cancelada';
      default: return 'Pendiente';
    }
  }

  // ============================================
  // MODALES
  // ============================================

  abrirModalNuevo(): void {
    this.codigoCotizacion = '';
    this.busquedaRealizada = false;
    this.ordenForm = {
      cotizacionId: 0,
      fechaEmision: new Date().toISOString().split('T')[0],
      fechaEntrega: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      observaciones: ''
    };
    this.cotizacionSeleccionada = null;
    this.mostrarModal = true;
  }

  abrirDetalle(orden: OrdenCompra): void {
    this.ordenSeleccionada = orden;
    this.mostrarDetalleModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mostrarDetalleModal = false;
    this.ordenSeleccionada = null;
    this.cotizacionSeleccionada = null;
  }

  // ============================================
  // CRUD OPERACIONES
  // ============================================

  guardarOrden(): void {
  if (!this.ordenForm.cotizacionId) {
    Swal.fire('Error', 'Seleccione una cotización aprobada', 'warning');
    return;
  }

  Swal.fire({
    title: 'Generando Orden',
    text: 'Por favor espere...',
    allowOutsideClick: false,
    didOpen: () => { Swal.showLoading(); }
  });

  // Usar el endpoint dedicado
  this.ordenCompraService.crearDesdeCotizacion(this.ordenForm.cotizacionId).subscribe({
    next: (orden) => {
      Swal.fire('Éxito', `Orden ${orden.numeroOrden} creada`, 'success');
      this.cerrarModal();
      this.cargarOrdenes();
    },
    error: (error) => {
      console.error('Error:', error);
      Swal.fire('Error', error.error?.message || 'No se pudo crear', 'error');
    }
  });
}

  

  enviarOrden(id: number, numero: string): void {
    Swal.fire({
      title: 'Enviar Orden',
      text: `¿Enviar la orden ${numero} al proveedor?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, enviar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.ordenCompraService.enviarOrden(id).subscribe({
          next: () => {
            Swal.fire('Enviada', 'Orden enviada al proveedor', 'success');
            this.cargarOrdenes();
          },
          error: (error) => {
            console.error('Error:', error);
            const orden = this.ordenes.find(o => o.id === id);
            if (orden) {
              orden.estado = 'ENVIADA';
              this.aplicarFiltros();
            }
            Swal.fire('Enviada', 'Orden enviada (modo local)', 'success');
          }
        });
      }
    });
  }

  recibirOrden(id: number, numero: string): void {
    Swal.fire({
      title: 'Recibir Orden',
      text: `¿Confirmar recepción de la orden ${numero}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, recibir'
    }).then((result) => {
      if (result.isConfirmed) {
        this.ordenCompraService.recibirOrden(id).subscribe({
          next: () => {
            Swal.fire('Recibida', 'Orden recibida correctamente', 'success');
            this.cargarOrdenes();
          },
          error: (error) => {
            console.error('Error:', error);
            const orden = this.ordenes.find(o => o.id === id);
            if (orden) {
              orden.estado = 'RECIBIDA';
              this.aplicarFiltros();
            }
            Swal.fire('Recibida', 'Orden recibida (modo local)', 'success');
          }
        });
      }
    });
  }

  cancelarOrden(id: number, numero: string): void {
    Swal.fire({
      title: 'Cancelar Orden',
      text: `¿Cancelar la orden ${numero}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.ordenCompraService.cancelarOrden(id).subscribe({
          next: () => {
            Swal.fire('Cancelada', 'Orden cancelada', 'success');
            this.cargarOrdenes();
          },
          error: (error) => {
            console.error('Error:', error);
            const orden = this.ordenes.find(o => o.id === id);
            if (orden) {
              orden.estado = 'CANCELADA';
              this.aplicarFiltros();
            }
            Swal.fire('Cancelada', 'Orden cancelada (modo local)', 'success');
          }
        });
      }
    });
  }

  imprimirOrden(): void {
    window.print();
  }
}