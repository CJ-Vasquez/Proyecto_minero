import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CotizacionService, Cotizacion } from '../../services/cotizacion.service';
import { SolicitudService, SolicitudPedido } from '../../services/solicitud.service';
import { ProveedorService, Proveedor } from '../../services/proveedor.service';
import { ProductoService, Producto } from '../../services/producto.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cotizaciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cotizaciones.component.html',
  styleUrls: ['./cotizaciones.component.scss']
})
export class CotizacionesComponent implements OnInit {
  cotizaciones: Cotizacion[] = [];
  cotizacionesFiltradas: Cotizacion[] = [];
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  mostrarDetalleModal: boolean = false;
  mostrarModalEvaluacion: boolean = false;
  cotizacionSeleccionada: Cotizacion | null = null;
  cotizacionEnEvaluacion: Cotizacion | null = null;
  motivoEvaluacion: string = '';
  cargando: boolean = true;
  cargandoDetalles: boolean = false;
  rol: string = '';

  // Datos para nueva cotización
  solicitudesAprobadas: SolicitudPedido[] = [];
  proveedores: Proveedor[] = [];
  productos: Producto[] = [];

  cotizacionForm = {
    solicitudPedidoId: 0,
    proveedorId: 0,
    fechaCotizacion: new Date().toISOString().split('T')[0],
    fechaValidez: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    detalles: [] as any[]
  };

  // Respuesta de proveedor
  respuestaForm = {
    precioTotal: 0,
    observaciones: ''
  };

  estados = ['PENDIENTE', 'ENVIADO', 'RESPONDIDO', 'APROBADO', 'RECHAZADO'];

  constructor(
    private cotizacionService: CotizacionService,
    private solicitudService: SolicitudService,
    private proveedorService: ProveedorService,
    private productoService: ProductoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.rol = user?.rol || '';
    this.cargarCotizaciones();
    this.cargarSolicitudesAprobadas();
    this.cargarProveedores();
    this.cargarProductos();
  }

  cargarCotizaciones(): void {
  this.cargando = true;
  this.cotizacionService.getCotizaciones().subscribe({
    next: (data) => {
      this.cotizaciones = data;
      this.cotizacionesFiltradas = [...data];
      this.calcularPaginacion();  // ← AGREGAR ESTA LÍNEA
      this.cargando = false;
    },
    error: (error) => {
      console.error('Error:', error);
      this.cargando = false;
      Swal.fire('Error', 'No se pudieron cargar las cotizaciones', 'error');
    }
  });
}

  cargarSolicitudesAprobadas(): void {
    this.solicitudService.getSolicitudesPorEstado('APROBADO').subscribe({
      next: (data) => {
        this.solicitudesAprobadas = data;
        console.log('Solicitudes aprobadas:', this.solicitudesAprobadas);
      },
      error: (err) => console.error('Error cargando solicitudes:', err)
    });
  }

  cargarProveedores(): void {
    this.proveedorService.getProveedoresActivos().subscribe({
      next: (data) => {
        this.proveedores = data;
      },
      error: (err) => console.error('Error cargando proveedores:', err)
    });
  }

  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: (err) => console.error('Error cargando productos:', err)
    });
  }

  aplicarFiltros(): void {
  if (this.estadoSeleccionado) {
    this.cotizacionesFiltradas = this.cotizaciones.filter(c => c.estado === this.estadoSeleccionado);
  } else {
    this.cotizacionesFiltradas = [...this.cotizaciones];
  }
  this.paginaActual = 1;
  this.calcularPaginacion();
}

  getEstadoClass(estado: string): string {
  switch(estado) {
    case 'APROBADO': return 'success';
    case 'RECHAZADO': return 'danger';
    case 'RESPONDIDO': return 'info';
    case 'ENVIADO': return 'warning';
    default: return 'secondary';
  }
}

  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'Aprobado';
      case 'RECHAZADO': return 'Rechazado';
      case 'RESPONDIDO': return 'Respondido';
      case 'ENVIADO': return 'Enviado';
      default: return 'Pendiente';
    }
  }

  abrirModalNuevo(): void {
    this.cotizacionForm = {
      solicitudPedidoId: 0,
      proveedorId: 0,
      fechaCotizacion: new Date().toISOString().split('T')[0],
      fechaValidez: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      detalles: []
    };
    this.mostrarModal = true;
  }

  // ============================================
  // MÉTODO onSolicitudChange - Carga productos de la solicitud
  // ============================================
  onSolicitudChange(): void {
    const solicitudId = this.cotizacionForm.solicitudPedidoId;
    console.log('=== onSolicitudChange ===');
    console.log('Solicitud ID seleccionado:', solicitudId);
    
    if (!solicitudId || solicitudId === 0) {
      this.cotizacionForm.detalles = [];
      return;
    }
    
    this.cargandoDetalles = true;
    
    this.solicitudService.getSolicitud(solicitudId).subscribe({
      next: (solicitud) => {
        this.cargandoDetalles = false;
        console.log('Solicitud completa:', solicitud);
        
        if (solicitud && solicitud.detalles && solicitud.detalles.length > 0) {
          this.cotizacionForm.detalles = solicitud.detalles.map(d => {
            const producto = this.productos.find(p => p.id === d.productoId);
            return {
              productoId: d.productoId,
              codigoProducto: producto?.codigo || d.codigoProducto,
              nombreProducto: producto?.nombre || d.nombreProducto,
              cantidad: d.cantidadSolicitada,
              precioUnitario: d.precioReferencial || 0,
              subtotal: (d.cantidadSolicitada || 0) * (d.precioReferencial || 0)
            };
          });
          console.log('Detalles cargados:', this.cotizacionForm.detalles);
          
          Swal.fire({
            icon: 'success',
            title: 'Productos cargados',
            text: `Se cargaron ${this.cotizacionForm.detalles.length} productos`,
            timer: 1500,
            showConfirmButton: false
          });
        } else {
          this.cotizacionForm.detalles = [];
          Swal.fire('Atención', 'La solicitud no tiene productos asociados', 'warning');
        }
      },
      error: (err) => {
        this.cargandoDetalles = false;
        console.error('Error:', err);
        Swal.fire('Error', 'No se pudo cargar la solicitud', 'error');
      }
    });
  }

  actualizarSubtotal(): void {
    this.cotizacionForm.detalles.forEach(d => {
      d.subtotal = d.cantidad * d.precioUnitario;
    });
  }

  guardarCotizacion(): void {
    if (!this.cotizacionForm.solicitudPedidoId || !this.cotizacionForm.proveedorId) {
      Swal.fire('Error', 'Solicitud y Proveedor son obligatorios', 'warning');
      return;
    }

    if (this.cotizacionForm.detalles.length === 0) {
      Swal.fire('Error', 'Debe agregar al menos un producto', 'warning');
      return;
    }

    const montoTotal = this.cotizacionForm.detalles.reduce((sum, d) => sum + (d.subtotal || 0), 0);
    
    const dataToSend = {
      solicitudPedidoId: this.cotizacionForm.solicitudPedidoId,
      proveedorId: this.cotizacionForm.proveedorId,
      fechaCotizacion: this.cotizacionForm.fechaCotizacion,
      fechaValidez: this.cotizacionForm.fechaValidez,
      detalles: this.cotizacionForm.detalles.map(d => ({
        productoId: d.productoId,
        cantidad: d.cantidad,
        precioUnitario: d.precioUnitario
      }))
    };

    console.log('Enviando cotización:', dataToSend);

    this.cotizacionService.crearCotizacion(dataToSend).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Cotización creada correctamente', 'success');
        this.cerrarModal();
        this.cargarCotizaciones();
      },
      error: (error) => {
        console.error('Error:', error);
        Swal.fire('Error', error.error?.message || 'No se pudo crear la cotización', 'error');
      }
    });
  }

  abrirDetalle(cotizacion: Cotizacion): void {
    this.cotizacionSeleccionada = cotizacion;
    this.mostrarDetalleModal = true;
  }

  enviarAProveedor(id: number): void {
    Swal.fire({
      title: 'Enviar a Proveedor',
      text: '¿Estás seguro de enviar esta cotización al proveedor?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, enviar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.enviarAProveedor(id).subscribe({
          next: () => {
            Swal.fire('Enviada', 'Cotización enviada al proveedor', 'success');
            this.cargarCotizaciones();
          },
          error: () => Swal.fire('Error', 'No se pudo enviar', 'error')
        });
      }
    });
  }

  abrirModalRespuesta(cotizacion: Cotizacion): void {
    Swal.fire({
      title: 'Registrar Respuesta del Proveedor',
      html: `
        <input type="number" id="precioTotal" class="swal2-input" placeholder="Precio Total" value="${cotizacion.montoTotal}">
        <textarea id="observaciones" class="swal2-textarea" placeholder="Observaciones"></textarea>
      `,
      showCancelButton: true,
      confirmButtonText: 'Guardar Respuesta',
      preConfirm: () => {
        const precioTotal = (document.getElementById('precioTotal') as HTMLInputElement).value;
        const observaciones = (document.getElementById('observaciones') as HTMLTextAreaElement).value;
        return { precioTotal: parseFloat(precioTotal) || 0, observaciones };
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.recibirRespuesta(cotizacion.id, result.value.precioTotal, result.value.observaciones).subscribe({
          next: () => {
            Swal.fire('Respuesta registrada', 'Respuesta del proveedor guardada', 'success');
            this.cargarCotizaciones();
          },
          error: () => Swal.fire('Error', 'No se pudo registrar la respuesta', 'error')
        });
      }
    });
  }

  // ============================================
  // MÉTODOS PARA EVALUACIÓN DE COTIZACIONES (GERENTE)
  // ============================================

  evaluarCotizacion(cotizacion: Cotizacion): void {
    this.cotizacionEnEvaluacion = cotizacion;
    this.motivoEvaluacion = '';
    this.mostrarModalEvaluacion = true;
  }

  cerrarModalEvaluacion(): void {
    this.mostrarModalEvaluacion = false;
    this.cotizacionEnEvaluacion = null;
    this.motivoEvaluacion = '';
  }

  aprobarCotizacionEvaluacion(): void {
    if (!this.cotizacionEnEvaluacion) return;
    
    Swal.fire({
      title: 'Aprobar Cotización',
      text: `¿Estás seguro de aprobar la cotización ${this.cotizacionEnEvaluacion.numeroCotizacion}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, aprobar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.aprobarCotizacion(this.cotizacionEnEvaluacion!.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Cotización Aprobada',
              text: 'La cotización ha sido aprobada. Ahora puede generar la Orden de Compra.',
              timer: 3000
            });
            this.cerrarModalEvaluacion();
            this.cargarCotizaciones();
          },
          error: (error) => {
            console.error('Error:', error);
            Swal.fire('Error', 'No se pudo aprobar la cotización', 'error');
          }
        });
      }
    });
  }

  rechazarCotizacionEvaluacion(): void {
    if (!this.cotizacionEnEvaluacion) return;
    
    if (!this.motivoEvaluacion.trim()) {
      Swal.fire('Error', 'Debe ingresar un motivo para rechazar la cotización', 'warning');
      return;
    }
    
    Swal.fire({
      title: 'Rechazar Cotización',
      text: `¿Estás seguro de rechazar la cotización ${this.cotizacionEnEvaluacion.numeroCotizacion}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, rechazar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.rechazarCotizacion(this.cotizacionEnEvaluacion!.id, this.motivoEvaluacion).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Cotización rechazada correctamente', 'success');
            this.cerrarModalEvaluacion();
            this.cargarCotizaciones();
          },
          error: (error) => {
            console.error('Error:', error);
            Swal.fire('Error', 'No se pudo rechazar la cotización', 'error');
          }
        });
      }
    });
  }

  // ============================================
  // MÉTODOS EXISTENTES (Aprobación/Rechazo directos)
  // ============================================

  aprobarCotizacion(id: number): void {
    Swal.fire({
      title: 'Aprobar Cotización',
      text: '¿Estás seguro de aprobar esta cotización?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, aprobar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.aprobarCotizacion(id).subscribe({
          next: () => {
            Swal.fire('Aprobada', 'Cotización aprobada', 'success');
            this.cargarCotizaciones();
          },
          error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
        });
      }
    });
  }

  rechazarCotizacion(id: number): void {
    Swal.fire({
      title: 'Rechazar Cotización',
      input: 'text',
      inputLabel: 'Motivo del rechazo',
      inputPlaceholder: 'Ingrese el motivo...',
      showCancelButton: true,
      confirmButtonText: 'Rechazar'
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.cotizacionService.rechazarCotizacion(id, result.value).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Cotización rechazada', 'success');
            this.cargarCotizaciones();
          },
          error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
        });
      }
    });
  }

  eliminarCotizacion(id: number, numero: string): void {
    Swal.fire({
      title: '¿Eliminar?',
      text: `¿Estás seguro de eliminar la cotización ${numero}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cotizacionService.eliminarCotizacion(id).subscribe({
          next: () => {
            Swal.fire('Eliminada', 'Cotización eliminada', 'success');
            this.cargarCotizaciones();
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mostrarDetalleModal = false;
    this.cotizacionSeleccionada = null;
    this.cargandoDetalles = false;
  }

  // ============================================
// MÉTODOS PARA PAGINACIÓN Y FILTROS
// ============================================

// Propiedades para paginación
paginaActual: number = 1;
itemsPorPagina: number = 10;
totalPaginas: number = 1;
totalRegistros: number = 0;
Math = Math;

// Calcular paginación
calcularPaginacion(): void {
  this.totalRegistros = this.cotizacionesFiltradas.length;
  this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
  if (this.paginaActual > this.totalPaginas) {
    this.paginaActual = Math.max(1, this.totalPaginas);
  }
}

// Cambiar página
cambiarPagina(pagina: number): void {
  this.paginaActual = pagina;
}

// Obtener cotizaciones paginadas
getCotizacionesPaginadas(): Cotizacion[] {
  const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
  return this.cotizacionesFiltradas.slice(inicio, inicio + this.itemsPorPagina);
}

// Limpiar filtros
limpiarFiltros(): void {
  this.estadoSeleccionado = '';
  this.cotizacionesFiltradas = [...this.cotizaciones];
  this.paginaActual = 1;
  this.calcularPaginacion();
}

// Calcular total de cotización
calcularTotalCotizacion(): number {
  return this.cotizacionForm.detalles.reduce((sum, d) => sum + (d.subtotal || 0), 0);
}

// Generar array de páginas para el paginador
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
}