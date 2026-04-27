import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecepcionService, Recepcion } from '../../services/recepcion.service';
import { OrdenCompraService, OrdenCompra } from '../../services/orden-compra.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-recepciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recepciones.component.html',
  styleUrls: ['./recepciones.component.scss']
})
export class RecepcionesComponent implements OnInit {
  recepciones: Recepcion[] = [];
  recepcionesFiltradas: Recepcion[] = [];
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  mostrarDetalleModal: boolean = false;
  recepcionSeleccionada: Recepcion | null = null;
  cargando: boolean = true;
  rol: string = '';

  // Paginación
  paginaActual: number = 1;
  itemsPorPagina: number = 10;
  totalPaginas: number = 1;
  totalRegistros: number = 0;
  Math = Math;

  // Datos para nueva recepción
  ordenesEnviadas: OrdenCompra[] = [];
  ordenSeleccionada: OrdenCompra | null = null;
  codigoOrden: string = '';
  busquedaRealizada: boolean = false;
  
  recepcionForm = {
    ordenCompraId: 0,
    fechaRecepcion: new Date().toISOString().split('T')[0],
    observaciones: '',
    detalles: [] as any[]
  };

  estados = ['PENDIENTE', 'CONFIRMADA', 'CANCELADA'];

  constructor(
    private recepcionService: RecepcionService,
    private ordenCompraService: OrdenCompraService
  ) {}

  ngOnInit(): void {
    const user = localStorage.getItem('user');
    if (user) {
      const userObj = JSON.parse(user);
      this.rol = userObj.rol || 'USUARIO';
    }
    this.cargarRecepciones();
    this.cargarOrdenesEnviadas();
  }

  cargarRecepciones(): void {
    this.cargando = true;
    this.recepcionService.getRecepciones().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.recepciones = data;
        } else {
          this.cargarRecepcionesEjemplo();
        }
        this.recepcionesFiltradas = [...this.recepciones];
        this.calcularPaginacion();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.cargarRecepcionesEjemplo();
        this.recepcionesFiltradas = [...this.recepciones];
        this.calcularPaginacion();
        this.cargando = false;
      }
    });
  }

  cargarRecepcionesEjemplo(): void {
    this.recepciones = [
      {
        id: 1,
        numeroRecepcion: 'REC-202501001',
        ordenCompraId: 1,
        numeroOrden: 'OC-202501001',
        proveedorId: 1,
        nombreProveedor: '3M Perú',
        fechaRecepcion: '2025-01-20',
        estado: 'CONFIRMADA',
        observaciones: 'Recepción completa',
        detalles: [
          { productoId: 1, codigoProducto: 'EPP-001', nombreProducto: 'Casco de Seguridad', cantidadSolicitada: 50, cantidadRecibida: 50, cantidadPendiente: 0, precioUnitario: 45, subtotal: 2250, observaciones: '' },
          { productoId: 2, codigoProducto: 'EPP-002', nombreProducto: 'Lentes de Seguridad', cantidadSolicitada: 100, cantidadRecibida: 100, cantidadPendiente: 0, precioUnitario: 25, subtotal: 2500, observaciones: '' }
        ]
      },
      {
        id: 2,
        numeroRecepcion: 'REC-202501002',
        ordenCompraId: 2,
        numeroOrden: 'OC-202501002',
        proveedorId: 2,
        nombreProveedor: 'Ferreyros S.A.',
        fechaRecepcion: '2025-01-25',
        estado: 'PENDIENTE',
        observaciones: 'Recepción parcial',
        detalles: [
          { productoId: 5, codigoProducto: 'HERR-001', nombreProducto: 'Taladro Industrial', cantidadSolicitada: 10, cantidadRecibida: 5, cantidadPendiente: 5, precioUnitario: 500, subtotal: 2500, observaciones: 'Faltan 5 unidades' }
        ]
      }
    ];
  }

  cargarOrdenesEnviadas(): void {
    this.ordenCompraService.getOrdenes().subscribe({
      next: (data) => {
        this.ordenesEnviadas = data.filter(o => o.estado === 'ENVIADA');
        console.log('Órdenes enviadas:', this.ordenesEnviadas);
      },
      error: (err) => {
        console.error('Error:', err);
        this.ordenesEnviadas = [];
      }
    });
  }

  buscarOrdenPorCodigo(): void {
    this.busquedaRealizada = true;
    
    if (!this.codigoOrden || this.codigoOrden.trim() === '') {
      this.ordenSeleccionada = null;
      this.recepcionForm.ordenCompraId = 0;
      this.recepcionForm.detalles = [];
      return;
    }
    
    const encontrada = this.ordenesEnviadas.find(
      o => o.numeroOrden === this.codigoOrden
    );
    
    if (encontrada) {
      this.ordenSeleccionada = encontrada;
      this.recepcionForm.ordenCompraId = encontrada.id;
      this.recepcionForm.detalles = (encontrada.detalles || []).map(d => ({
        productoId: d.productoId,
        codigoProducto: d.codigoProducto,
        nombreProducto: d.nombreProducto,
        cantidadSolicitada: d.cantidad,
        cantidadRecibida: 0,
        cantidadPendiente: d.cantidad,
        precioUnitario: d.precioUnitario,
        subtotal: 0,
        observaciones: ''
      }));
      console.log('Orden encontrada:', encontrada);
    } else {
      this.ordenSeleccionada = null;
      this.recepcionForm.ordenCompraId = 0;
      this.recepcionForm.detalles = [];
    }
  }

  actualizarCantidadRecibida(index: number, cantidad: number): void {
    const detalle = this.recepcionForm.detalles[index];
    detalle.cantidadRecibida = cantidad;
    detalle.cantidadPendiente = detalle.cantidadSolicitada - cantidad;
    detalle.subtotal = cantidad * detalle.precioUnitario;
  }

  calcularTotal(): number {
    return this.recepcionForm.detalles.reduce((sum, d) => sum + d.subtotal, 0);
  }

  calcularPaginacion(): void {
    this.totalRegistros = this.recepcionesFiltradas.length;
    this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = Math.max(1, this.totalPaginas);
    }
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
  }

  getRecepcionesPaginadas(): Recepcion[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.recepcionesFiltradas.slice(inicio, inicio + this.itemsPorPagina);
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

  aplicarFiltros(): void {
    if (this.estadoSeleccionado) {
      this.recepcionesFiltradas = this.recepciones.filter(r => r.estado === this.estadoSeleccionado);
    } else {
      this.recepcionesFiltradas = [...this.recepciones];
    }
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  limpiarFiltros(): void {
    this.estadoSeleccionado = '';
    this.recepcionesFiltradas = [...this.recepciones];
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  getEstadoClass(estado: string): string {
    switch(estado) {
      case 'CONFIRMADA': return 'success';
      case 'CANCELADA': return 'danger';
      default: return 'warning';
    }
  }

  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'CONFIRMADA': return 'Confirmada';
      case 'CANCELADA': return 'Cancelada';
      default: return 'Pendiente';
    }
  }

  abrirModalNuevo(): void {
    this.codigoOrden = '';
    this.busquedaRealizada = false;
    this.recepcionForm = {
      ordenCompraId: 0,
      fechaRecepcion: new Date().toISOString().split('T')[0],
      observaciones: '',
      detalles: []
    };
    this.ordenSeleccionada = null;
    this.mostrarModal = true;
  }

  abrirDetalle(recepcion: Recepcion): void {
    this.recepcionSeleccionada = recepcion;
    this.mostrarDetalleModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mostrarDetalleModal = false;
    this.recepcionSeleccionada = null;
    this.ordenSeleccionada = null;
  }

  guardarRecepcion(): void {
    if (!this.recepcionForm.ordenCompraId) {
      Swal.fire('Error', 'Seleccione una orden de compra enviada', 'warning');
      return;
    }

    const productosRecibidos = this.recepcionForm.detalles.filter(d => d.cantidadRecibida > 0);
    if (productosRecibidos.length === 0) {
      Swal.fire('Error', 'Debe ingresar al menos una cantidad recibida', 'warning');
      return;
    }

    const dataToSend = {
      ordenCompraId: this.recepcionForm.ordenCompraId,
      fechaRecepcion: this.recepcionForm.fechaRecepcion,
      observaciones: this.recepcionForm.observaciones,
      detalles: this.recepcionForm.detalles.filter(d => d.cantidadRecibida > 0)
    };

    Swal.fire({
      title: 'Guardando Recepción',
      text: 'Por favor espere...',
      allowOutsideClick: false,
      didOpen: () => { Swal.showLoading(); }
    });

    this.recepcionService.crearRecepcion(dataToSend).subscribe({
      next: (recepcion) => {
        Swal.fire({
          icon: 'success',
          title: 'Éxito',
          text: `Recepción ${recepcion.numeroRecepcion} creada correctamente`,
          timer: 2000
        });
        this.cerrarModal();
        this.cargarRecepciones();
      },
      error: (error) => {
        console.error('Error:', error);
        Swal.fire('Error', error.error?.message || 'No se pudo crear la recepción', 'error');
      }
    });
  }

  confirmarRecepcion(id: number, numero: string): void {
    Swal.fire({
      title: 'Confirmar Recepción',
      text: `¿Confirmar la recepción ${numero}? Esto actualizará el stock.`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, confirmar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.recepcionService.confirmarRecepcion(id).subscribe({
          next: () => {
            Swal.fire('Confirmada', 'Recepción confirmada y stock actualizado', 'success');
            this.cargarRecepciones();
          },
          error: (error) => {
            console.error('Error:', error);
            Swal.fire('Error', 'No se pudo confirmar la recepción', 'error');
          }
        });
      }
    });
  }

  calcularTotalRecepcion(): number {
    if (!this.recepcionSeleccionada || !this.recepcionSeleccionada.detalles) return 0;
    return this.recepcionSeleccionada.detalles.reduce((sum, d) => sum + (d.subtotal || 0), 0);
  }

  imprimirRecepcion(): void {
    window.print();
  }
}