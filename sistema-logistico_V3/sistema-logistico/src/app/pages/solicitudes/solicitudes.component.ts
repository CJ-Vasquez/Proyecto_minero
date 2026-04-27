import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService, SolicitudPedido } from '../../services/solicitud.service';
import { ProductoService, Producto } from '../../services/producto.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

// Interfaces para la evaluación de stock
interface StockPorAlmacen {
  almacenId: number;
  almacenNombre: string;
  cantidad: number;
}

interface ProductoEvaluado {
  productoId: number;
  codigo: string;
  nombre: string;
  cantidadSolicitada: number;
  stockTotal: number;
  stockPorAlmacen: StockPorAlmacen[];
  suficiente: boolean;
  deficit: number;
  accion: 'TRASLADAR' | 'COMPRAR' | 'MIXTO';
  almacenesOrigen: { almacenId: number; cantidad: number }[];
  necesitaCompra: number;
}

@Component({
  selector: 'app-solicitudes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './solicitudes.component.html',
  styleUrls: ['./solicitudes.component.scss']
})
export class SolicitudesComponent implements OnInit {
  solicitudes: SolicitudPedido[] = [];
  solicitudesFiltradas: SolicitudPedido[] = [];
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  mostrarDetalleModal: boolean = false;
  mostrarModalEvaluacion: boolean = false;
  solicitudSeleccionada: SolicitudPedido | null = null;
  solicitudEnEvaluacion: SolicitudPedido | null = null;
  productosEvaluados: ProductoEvaluado[] = [];
  motivoRechazo: string = '';
  cargando: boolean = true;
  cargandoStock: boolean = false;
  rol: string = '';

  estados = ['CREADO', 'PENDIENTE_APROBACION', 'APROBADO', 'RECHAZADO', 'CANCELADO'];
  productos: Producto[] = [];

  solicitudForm = {
    origen: 'MINA',
    solicitante: '',
    oficina: '',
    glosa: '',
    destino: 'LIMA',
    aprobador: '',
    almacen: 'LIMA',
    fecha: new Date().toISOString().split('T')[0],
    detalles: [] as any[]
  };

  nuevoProducto = {
    productoId: 0,
    cantidad: 0
  };

  constructor(
    private solicitudService: SolicitudService,
    private productoService: ProductoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.rol = user?.rol || '';
    this.cargarSolicitudes();
    this.cargarProductos();
  }

  cargarSolicitudes(): void {
  this.cargando = true;
  this.solicitudService.getSolicitudes().subscribe({
    next: (data) => {
      this.solicitudes = data;
      this.solicitudesFiltradas = [...data];
      this.calcularPaginacion();  // ← AGREGAR ESTA LÍNEA
      this.cargando = false;
      console.log('Solicitudes cargadas:', this.solicitudes.length);
    },
    error: (error) => {
      console.error('Error:', error);
      this.cargando = false;
      Swal.fire('Error', 'No se pudieron cargar las solicitudes', 'error');
    }
  });
}

  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
        console.log('Productos cargados:', this.productos.length);
      },
      error: (err) => {
        console.error('Error cargando productos:', err);
        Swal.fire('Error', 'No se pudieron cargar los productos', 'error');
      }
    });
  }

  aplicarFiltros(): void {
  if (this.estadoSeleccionado) {
    this.solicitudesFiltradas = this.solicitudes.filter(s => s.estado === this.estadoSeleccionado);
  } else {
    this.solicitudesFiltradas = [...this.solicitudes];
  }
  this.paginaActual = 1;
  this.calcularPaginacion();
}

  getEstadoClass(estado: string): string {
  switch(estado) {
    case 'APROBADO': return 'success';
    case 'RECHAZADO': return 'danger';
    case 'PENDIENTE_APROBACION': return 'warning';
    case 'CANCELADO': return 'secondary';
    default: return 'info';
  }
}

  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'Aprobado';
      case 'RECHAZADO': return 'Rechazado';
      case 'PENDIENTE_APROBACION': return 'Pendiente Aprobación';
      case 'CANCELADO': return 'Cancelado';
      default: return 'Creado';
    }
  }

  abrirModalNuevo(): void {
    this.solicitudForm = {
      origen: 'MINA',
      solicitante: '',
      oficina: '',
      glosa: '',
      destino: 'LIMA',
      aprobador: '',
      almacen: 'LIMA',
      fecha: new Date().toISOString().split('T')[0],
      detalles: []
    };
    this.nuevoProducto = { productoId: 0, cantidad: 0 };
    this.mostrarModal = true;
  }

  seleccionarSolicitud(solicitud: SolicitudPedido): void {
    this.solicitudSeleccionada = solicitud;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mostrarDetalleModal = false;
    this.mostrarModalEvaluacion = false;
    this.solicitudSeleccionada = null;
    this.solicitudEnEvaluacion = null;
    this.productosEvaluados = [];
  }

  // ============================================
  // MÉTODOS DE EVALUACIÓN INTELIGENTE
  // ============================================

  private obtenerStockPorAlmacen(productoId: number): StockPorAlmacen[] {
    // Datos simulados - En producción, llamar a inventarioService
    const stockSimulado: { [key: number]: StockPorAlmacen[] } = {
      1: [
        { almacenId: 1, almacenNombre: 'LIMA', cantidad: 15 },
        { almacenId: 2, almacenNombre: 'MINA', cantidad: 5 },
        { almacenId: 3, almacenNombre: 'TRUJILLO', cantidad: 8 }
      ],
      2: [
        { almacenId: 1, almacenNombre: 'LIMA', cantidad: 50 },
        { almacenId: 2, almacenNombre: 'MINA', cantidad: 10 },
        { almacenId: 3, almacenNombre: 'TRUJILLO', cantidad: 20 }
      ]
    };
    
    return stockSimulado[productoId] || [
      { almacenId: 1, almacenNombre: 'LIMA', cantidad: 0 },
      { almacenId: 2, almacenNombre: 'MINA', cantidad: 0 },
      { almacenId: 3, almacenNombre: 'TRUJILLO', cantidad: 0 }
    ];
  }

  private evaluarProducto(detalle: any): ProductoEvaluado {
    const stockPorAlmacen = this.obtenerStockPorAlmacen(detalle.productoId);
    const stockTotal = stockPorAlmacen.reduce((sum, s) => sum + s.cantidad, 0);
    const cantidadSolicitada = detalle.cantidadSolicitada;
    const suficiente = stockTotal >= cantidadSolicitada;
    const deficit = suficiente ? 0 : cantidadSolicitada - stockTotal;
    
    let accion: 'TRASLADAR' | 'COMPRAR' | 'MIXTO' = 'COMPRAR';
    let almacenesOrigen: { almacenId: number; cantidad: number }[] = [];
    let necesitaCompra = cantidadSolicitada;
    
    if (suficiente) {
      accion = 'TRASLADAR';
      necesitaCompra = 0;
      let restante = cantidadSolicitada;
      for (const stock of [...stockPorAlmacen].sort((a, b) => b.cantidad - a.cantidad)) {
        if (restante <= 0) break;
        const tomar = Math.min(stock.cantidad, restante);
        if (tomar > 0) {
          almacenesOrigen.push({ almacenId: stock.almacenId, cantidad: tomar });
          restante -= tomar;
        }
      }
    } else if (stockTotal > 0) {
      accion = 'MIXTO';
      necesitaCompra = deficit;
      for (const stock of stockPorAlmacen) {
        if (stock.cantidad > 0) {
          almacenesOrigen.push({ almacenId: stock.almacenId, cantidad: stock.cantidad });
        }
      }
    }
    
    const producto = this.productos.find(p => p.id === detalle.productoId);
    
    return {
      productoId: detalle.productoId,
      codigo: producto?.codigo || '',
      nombre: producto?.nombre || '',
      cantidadSolicitada,
      stockTotal,
      stockPorAlmacen,
      suficiente,
      deficit,
      accion,
      almacenesOrigen,
      necesitaCompra
    };
  }

  evaluarSolicitud(solicitud: SolicitudPedido): void {
    this.solicitudEnEvaluacion = solicitud;
    this.motivoRechazo = '';
    this.cargandoStock = true;
    this.mostrarModalEvaluacion = true;
    
    setTimeout(() => {
      if (solicitud.detalles) {
        this.productosEvaluados = solicitud.detalles.map(d => this.evaluarProducto(d));
      }
      this.cargandoStock = false;
    }, 500);
  }

  cambiarAccionProducto(index: number, accion: 'TRASLADAR' | 'COMPRAR'): void {
    const producto = this.productosEvaluados[index];
    if (producto.suficiente && accion === 'COMPRAR') {
      Swal.fire({
        title: '¿Estás seguro?',
        text: 'Hay stock suficiente. ¿Prefieres comprar en lugar de trasladar?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, comprar'
      }).then((result) => {
        if (result.isConfirmed) {
          producto.accion = accion;
          producto.necesitaCompra = producto.cantidadSolicitada;
          producto.almacenesOrigen = [];
        }
      });
    } else if (!producto.suficiente && accion === 'TRASLADAR') {
      Swal.fire('No disponible', 'No hay suficiente stock en almacenes', 'warning');
    } else {
      producto.accion = accion;
      if (accion === 'TRASLADAR') {
        producto.necesitaCompra = 0;
        let restante = producto.cantidadSolicitada;
        producto.almacenesOrigen = [];
        for (const stock of [...producto.stockPorAlmacen].sort((a, b) => b.cantidad - a.cantidad)) {
          if (restante <= 0) break;
          const tomar = Math.min(stock.cantidad, restante);
          if (tomar > 0) {
            producto.almacenesOrigen.push({ almacenId: stock.almacenId, cantidad: tomar });
            restante -= tomar;
          }
        }
      } else if (accion === 'COMPRAR') {
        producto.necesitaCompra = producto.cantidadSolicitada;
        producto.almacenesOrigen = [];
      }
    }
  }

  aprobarSolicitudConEvaluacion(): void {
    if (!this.solicitudEnEvaluacion) return;
    
    const productosATrasladar = this.productosEvaluados.filter(p => p.accion === 'TRASLADAR' || p.accion === 'MIXTO');
    const productosAComprar = this.productosEvaluados.filter(p => p.accion === 'COMPRAR' || (p.accion === 'MIXTO' && p.necesitaCompra > 0));
    
    let mensaje = '';
    if (productosATrasladar.length > 0) {
      mensaje += `📦 Traslado: ${productosATrasladar.length} productos\n`;
    }
    if (productosAComprar.length > 0) {
      mensaje += `🛒 Compra: ${productosAComprar.length} productos\n`;
    }
    
    Swal.fire({
      title: 'Confirmar Aprobación',
      text: `¿Aprobar esta solicitud?\n\n${mensaje}`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, Aprobar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.aprobarSolicitud(this.solicitudEnEvaluacion!.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Solicitud Aprobada',
              html: `${mensaje}`,
              timer: 3000
            });
            this.cerrarModal();
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
        });
      }
    });
  }

  rechazarSolicitudConEvaluacion(): void {
    Swal.fire({
      title: 'Rechazar Solicitud',
      input: 'textarea',
      inputLabel: 'Motivo del rechazo',
      inputPlaceholder: 'Ingrese el motivo...',
      showCancelButton: true,
      confirmButtonText: 'Rechazar'
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.solicitudService.rechazarSolicitud(this.solicitudEnEvaluacion!.id, result.value).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Solicitud rechazada', 'success');
            this.cerrarModal();
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
        });
      }
    });
  }

  getProductosTrasladar(): number {
    return this.productosEvaluados.filter(p => p.accion === 'TRASLADAR' || p.accion === 'MIXTO').length;
  }

  getProductosComprar(): number {
    return this.productosEvaluados.filter(p => p.accion === 'COMPRAR' || p.accion === 'MIXTO').length;
  }

  getTotalComprarUnidades(): number {
    return this.productosEvaluados.reduce((sum, p) => sum + (p.necesitaCompra || 0), 0);
  }

  getNombreAlmacen(almacenId: number): string {
    const almacenes: { [key: number]: string } = {
      1: 'LIMA',
      2: 'MINA',
      3: 'TRUJILLO'
    };
    return almacenes[almacenId] || 'Desconocido';
  }

  // ============================================
  // MÉTODOS EXISTENTES
  // ============================================

  agregarProducto(): void {
    console.log('=== AGREGAR PRODUCTO ===');
    console.log('Producto ID seleccionado:', this.nuevoProducto.productoId);
    console.log('Cantidad:', this.nuevoProducto.cantidad);
    console.log('Lista de productos disponibles:', this.productos);
    
    if (!this.nuevoProducto.productoId || this.nuevoProducto.productoId === 0) {
      Swal.fire('Error', 'Seleccione un producto', 'warning');
      return;
    }
    
    if (!this.nuevoProducto.cantidad || this.nuevoProducto.cantidad <= 0) {
      Swal.fire('Error', 'Ingrese una cantidad válida', 'warning');
      return;
    }

    const productoIdNum = Number(this.nuevoProducto.productoId);
    const producto = this.productos.find(p => p.id === productoIdNum);
    
    console.log('Producto encontrado:', producto);
    
    if (!producto) {
      Swal.fire('Error', 'Producto no encontrado', 'error');
      return;
    }
    
    const existe = this.solicitudForm.detalles.some(d => d.productoId === this.nuevoProducto.productoId);
    if (existe) {
      Swal.fire('Error', 'El producto ya está agregado', 'warning');
      return;
    }
    
    const nuevoDetalle = {
      productoId: producto.id,
      codigoProducto: producto.codigo,
      nombreProducto: producto.nombre,
      cantidadSolicitada: this.nuevoProducto.cantidad,
      precioReferencial: producto.precioReferencial
    };
    
    this.solicitudForm.detalles.push(nuevoDetalle);
    this.nuevoProducto = { productoId: 0, cantidad: 0 };
    
    Swal.fire({
      icon: 'success',
      title: 'Producto agregado',
      text: `${producto.nombre} - Cantidad: ${nuevoDetalle.cantidadSolicitada}`,
      timer: 1500,
      showConfirmButton: false
    });
  }

  eliminarProducto(index: number): void {
    const producto = this.solicitudForm.detalles[index];
    Swal.fire({
      title: '¿Eliminar producto?',
      text: `¿Eliminar ${producto.nombreProducto}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudForm.detalles.splice(index, 1);
        Swal.fire('Eliminado', 'Producto eliminado', 'success');
      }
    });
  }

  calcularTotal(): number {
    return this.solicitudForm.detalles.reduce((sum, item) => {
      return sum + (item.cantidadSolicitada * item.precioReferencial);
    }, 0);
  }

  calcularTotalDetalle(): number {
    if (!this.solicitudSeleccionada || !this.solicitudSeleccionada.detalles) return 0;
    return this.solicitudSeleccionada.detalles.reduce((sum, item) => {
      return sum + (item.cantidadSolicitada * item.precioReferencial);
    }, 0);
  }

  guardarSolicitud(): void {
    if (!this.solicitudForm.solicitante) {
      Swal.fire('Error', 'El solicitante es obligatorio', 'warning');
      return;
    }

    if (this.solicitudForm.detalles.length === 0) {
      Swal.fire('Error', 'Debe agregar al menos un producto', 'warning');
      return;
    }

    console.log('Guardando solicitud:', this.solicitudForm);

    this.solicitudService.crearSolicitud(this.solicitudForm).subscribe({
      next: (response) => {
        console.log('Solicitud creada:', response);
        Swal.fire('Éxito', 'Solicitud creada correctamente', 'success');
        this.cerrarModal();
        this.cargarSolicitudes();
      },
      error: (error) => {
        console.error('Error detallado:', error);
        Swal.fire('Error', error.error?.message || 'No se pudo crear la solicitud', 'error');
      }
    });
  }

  enviarAAprobacion(id: number): void {
    Swal.fire({
      title: 'Enviar a aprobación',
      text: '¿Estás seguro de enviar esta solicitud a aprobación?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, enviar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.enviarAAprobacion(id).subscribe({
          next: () => {
            Swal.fire('Enviado', 'Solicitud enviada a aprobación', 'success');
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo enviar', 'error')
        });
      }
    });
  }

  aprobarSolicitud(id: number): void {
    Swal.fire({
      title: 'Aprobar solicitud',
      text: '¿Estás seguro de aprobar esta solicitud?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, aprobar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.aprobarSolicitud(id).subscribe({
          next: () => {
            Swal.fire('Aprobada', 'Solicitud aprobada', 'success');
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
        });
      }
    });
  }

  rechazarSolicitud(id: number): void {
    Swal.fire({
      title: 'Rechazar solicitud',
      input: 'text',
      inputLabel: 'Motivo del rechazo',
      inputPlaceholder: 'Ingrese el motivo...',
      showCancelButton: true,
      confirmButtonText: 'Rechazar'
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.solicitudService.rechazarSolicitud(id, result.value).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Solicitud rechazada', 'success');
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
        });
      }
    });
  }

  cancelarSolicitud(id: number): void {
    Swal.fire({
      title: 'Cancelar solicitud',
      text: '¿Estás seguro de cancelar esta solicitud?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.cancelarSolicitud(id).subscribe({
          next: () => {
            Swal.fire('Cancelada', 'Solicitud cancelada', 'success');
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo cancelar', 'error')
        });
      }
    });
  }

  eliminarSolicitud(id: number, numero: string): void {
    Swal.fire({
      title: '¿Eliminar solicitud?',
      text: `¿Estás seguro de eliminar la solicitud ${numero}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.eliminarSolicitud(id).subscribe({
          next: () => {
            Swal.fire('Eliminada', 'Solicitud eliminada', 'success');
            this.cargarSolicitudes();
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }

  abrirDetalle(solicitud: SolicitudPedido): void {
    this.solicitudSeleccionada = solicitud;
    this.mostrarDetalleModal = true;
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
  this.totalRegistros = this.solicitudesFiltradas.length;
  this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
  if (this.paginaActual > this.totalPaginas) {
    this.paginaActual = Math.max(1, this.totalPaginas);
  }
}

// Cambiar página
cambiarPagina(pagina: number): void {
  this.paginaActual = pagina;
}

// Obtener solicitudes paginadas
getSolicitudesPaginadas(): SolicitudPedido[] {
  const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
  return this.solicitudesFiltradas.slice(inicio, inicio + this.itemsPorPagina);
}

// Limpiar filtros
limpiarFiltros(): void {
  this.estadoSeleccionado = '';
  this.solicitudesFiltradas = [...this.solicitudes];
  this.paginaActual = 1;
  this.calcularPaginacion();
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