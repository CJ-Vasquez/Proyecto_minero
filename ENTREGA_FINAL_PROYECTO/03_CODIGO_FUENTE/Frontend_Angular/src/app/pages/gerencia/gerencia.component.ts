import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService, SolicitudPedido } from '../../services/solicitud.service';
import { CotizacionService, Cotizacion } from '../../services/cotizacion.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gerencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gerencia.component.html',
  styleUrls: ['./gerencia.component.scss']
})
export class GerenciaComponent implements OnInit {
  // Solicitudes
  solicitudes: SolicitudPedido[] = [];
  solicitudesPendientes: SolicitudPedido[] = [];
  solicitudSeleccionada: SolicitudPedido | null = null;
  mostrarModalSolicitud: boolean = false;
  
  // Cotizaciones
  cotizaciones: Cotizacion[] = [];
  cotizacionesPendientes: Cotizacion[] = [];
  cotizacionSeleccionada: Cotizacion | null = null;
  mostrarModalCotizacion: boolean = false;
  
  // Estado
  pestanaActiva: string = 'solicitudes';
  cargando: boolean = true;
  rol: string = '';
  motivoRechazo: string = '';

  constructor(
    private solicitudService: SolicitudService,
    private cotizacionService: CotizacionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.rol = user?.rol || '';
    this.cargarDatos();
  }

  cargarDatos(): void {
  this.cargando = true;
  
  // Cargar solicitudes
  this.solicitudService.getSolicitudes().subscribe({
    next: (data) => {
      this.solicitudes = data;
      this.solicitudesPendientes = data.filter(s => s.estado === 'PENDIENTE_APROBACION');
      console.log('Solicitudes pendientes:', this.solicitudesPendientes.length);
    },
    error: (err) => console.error('Error cargando solicitudes:', err)
  });
  
  // Cargar cotizaciones - CORREGIDO
  this.cotizacionService.getCotizaciones().subscribe({
    next: (data) => {
      this.cotizaciones = data;
      console.log('Todas las cotizaciones:', data);
      
      // IMPORTANTE: Tu backend usa estado 'PENDIENTE', no 'RESPONDIDO'
      // Las cotizaciones pendientes de aprobación son las que están en estado 'PENDIENTE'
      this.cotizacionesPendientes = data.filter(c => c.estado === 'PENDIENTE');
      
      console.log('Cotizaciones pendientes (PENDIENTE):', this.cotizacionesPendientes.length);
      this.cargando = false;
    },
    error: (err) => {
      console.error('Error cargando cotizaciones:', err);
      this.cargando = false;
    }
  });
}

  // ============================================
  // MÉTODOS PARA SOLICITUDES
  // ============================================
  
  verDetalleSolicitud(solicitud: SolicitudPedido): void {
    this.solicitudSeleccionada = solicitud;
    this.mostrarModalSolicitud = true;
  }
  
  aprobarSolicitud(id: number): void {
    Swal.fire({
      title: 'Aprobar Solicitud',
      text: '¿Estás seguro de aprobar esta solicitud?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, aprobar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.solicitudService.aprobarSolicitud(id).subscribe({
          next: () => {
            Swal.fire('Aprobada', 'Solicitud aprobada correctamente', 'success');
            this.cargarDatos();
            this.cerrarModales();
          },
          error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
        });
      }
    });
  }
  
  rechazarSolicitud(id: number): void {
    Swal.fire({
      title: 'Rechazar Solicitud',
      input: 'textarea',
      inputLabel: 'Motivo del rechazo',
      inputPlaceholder: 'Ingrese el motivo...',
      showCancelButton: true,
      confirmButtonText: 'Rechazar'
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.solicitudService.rechazarSolicitud(id, result.value).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Solicitud rechazada', 'success');
            this.cargarDatos();
            this.cerrarModales();
          },
          error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
        });
      }
    });
  }

  // ============================================
  // MÉTODOS PARA COTIZACIONES
  // ============================================
  
  verDetalleCotizacion(cotizacion: Cotizacion): void {
    this.cotizacionSeleccionada = cotizacion;
    this.mostrarModalCotizacion = true;
  }
  
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
            Swal.fire('Aprobada', 'Cotización aprobada correctamente', 'success');
            this.cargarDatos();
            this.cerrarModales();
          },
          error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
        });
      }
    });
  }
  
  rechazarCotizacion(id: number): void {
    Swal.fire({
      title: 'Rechazar Cotización',
      input: 'textarea',
      inputLabel: 'Motivo del rechazo',
      inputPlaceholder: 'Ingrese el motivo...',
      showCancelButton: true,
      confirmButtonText: 'Rechazar'
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.cotizacionService.rechazarCotizacion(id, result.value).subscribe({
          next: () => {
            Swal.fire('Rechazada', 'Cotización rechazada', 'success');
            this.cargarDatos();
            this.cerrarModales();
          },
          error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
        });
      }
    });
  }

  cerrarModales(): void {
    this.mostrarModalSolicitud = false;
    this.mostrarModalCotizacion = false;
    this.solicitudSeleccionada = null;
    this.cotizacionSeleccionada = null;
    this.motivoRechazo = '';
  }
  
  getEstadoClass(estado: string): string {
  switch(estado) {
    case 'APROBADO': return 'badge bg-success';
    case 'RECHAZADO': return 'badge bg-danger';
    case 'PENDIENTE_APROBACION': return 'badge bg-warning text-dark';
    case 'PENDIENTE': return 'badge bg-info text-dark';  // ← CORREGIDO
    default: return 'badge bg-secondary';
  }
}

getEstadoTexto(estado: string): string {
  switch(estado) {
    case 'APROBADO': return 'Aprobado';
    case 'RECHAZADO': return 'Rechazado';
    case 'PENDIENTE_APROBACION': return 'Pendiente Aprobación';
    case 'PENDIENTE': return 'Pendiente';  // ← CORREGIDO
    default: return estado;
  }
}

  calcularTotalSolicitud(solicitud: SolicitudPedido): number {
  if (!solicitud.detalles) return 0;
  return solicitud.detalles.reduce((sum, d) => sum + (d.cantidadSolicitada * d.precioReferencial), 0);
}
}