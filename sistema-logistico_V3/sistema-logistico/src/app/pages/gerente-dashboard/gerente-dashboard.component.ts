import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService, SolicitudPedido } from '../../services/solicitud.service';
import { CotizacionService, Cotizacion } from '../../services/cotizacion.service';
import { ProductoService, Producto } from '../../services/producto.service';
import { ProveedorService, Proveedor } from '../../services/proveedor.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-gerente-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gerente-dashboard.component.html',
  styleUrls: ['./gerente-dashboard.component.scss']
})
export class GerenteDashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('barChart') barChart: any;
  
  // KPIs
  totalSolicitudes: number = 0;
  solicitudesPendientes: number = 0;
  solicitudesAprobadas: number = 0;
  totalCotizaciones: number = 0;
  cotizacionesPendientes: number = 0;
  cotizacionesAprobadas: number = 0;
  totalProveedores: number = 0;
  totalProductos: number = 0;
  
  // Datos para tablas
  solicitudesLista: SolicitudPedido[] = [];
  cotizacionesLista: Cotizacion[] = [];
  
  // Modales
  mostrarModalSolicitudes: boolean = false;
  mostrarModalCotizaciones: boolean = false;
  mostrarDetalleSolicitud: boolean = false;
  mostrarDetalleCotizacion: boolean = false;
  solicitudSeleccionada: SolicitudPedido | null = null;
  cotizacionSeleccionada: Cotizacion | null = null;
  
  // Evaluación
  mostrarModalEvaluacion: boolean = false;
  solicitudEnEvaluacion: SolicitudPedido | null = null;
  cotizacionEnEvaluacion: Cotizacion | null = null;
  motivoEvaluacion: string = '';
  
  rol: string = '';

  constructor(
    private solicitudService: SolicitudService,
    private cotizacionService: CotizacionService,
    private productoService: ProductoService,
    private proveedorService: ProveedorService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.rol = user?.rol || '';
    this.cargarDatos();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.inicializarGrafico();
    }, 500);
  }

  cargarDatos(): void {
    // Cargar solicitudes
    this.solicitudService.getSolicitudes().subscribe({
      next: (data) => {
        this.solicitudesLista = data;
        this.totalSolicitudes = data.length;
        this.solicitudesPendientes = data.filter(s => s.estado === 'PENDIENTE_APROBACION').length;
        this.solicitudesAprobadas = data.filter(s => s.estado === 'APROBADO').length;
        this.inicializarGrafico();
      },
      error: (err) => console.error('Error:', err)
    });
    
    // Cargar cotizaciones
    this.cotizacionService.getCotizaciones().subscribe({
      next: (data) => {
        this.cotizacionesLista = data;
        this.totalCotizaciones = data.length;
        this.cotizacionesPendientes = data.filter(c => c.estado === 'RESPONDIDA').length;
        this.cotizacionesAprobadas = data.filter(c => c.estado === 'APROBADO').length;
        this.inicializarGrafico();
      },
      error: (err) => console.error('Error:', err)
    });
    
    // Cargar proveedores y productos
    this.proveedorService.getProveedores().subscribe({
      next: (data) => this.totalProveedores = data.length,
      error: (err) => console.error('Error:', err)
    });
    
    this.productoService.getProductos().subscribe({
      next: (data) => this.totalProductos = data.length,
      error: (err) => console.error('Error:', err)
    });
  }

  inicializarGrafico(): void {
    const canvas = document.getElementById('barChart') as HTMLCanvasElement;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    // Destruir gráfico existente si hay
    if ((window as any).myChart) {
      (window as any).myChart.destroy();
    }
    
    (window as any).myChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Solicitudes', 'Cotizaciones'],
        datasets: [
          {
            label: 'Pendientes',
            data: [this.solicitudesPendientes, this.cotizacionesPendientes],
            backgroundColor: '#ffc107',
            borderColor: '#ffc107',
            borderWidth: 1
          },
          {
            label: 'Aprobadas',
            data: [this.solicitudesAprobadas, this.cotizacionesAprobadas],
            backgroundColor: '#28a745',
            borderColor: '#28a745',
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          legend: { position: 'top' }
        },
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  // ============================================
  // MÉTODOS AUXILIARES
  // ============================================
  
  calcularTotalSolicitud(solicitud: SolicitudPedido): number {
    if (!solicitud.detalles) return 0;
    return solicitud.detalles.reduce((sum, d) => sum + (d.cantidadSolicitada * d.precioReferencial), 0);
  }
  
  getEstadoClass(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'badge bg-success';
      case 'RECHAZADO': return 'badge bg-danger';
      case 'PENDIENTE_APROBACION': return 'badge bg-warning text-dark';
      default: return 'badge bg-secondary';
    }
  }
  
  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'Aprobado';
      case 'RECHAZADO': return 'Rechazado';
      case 'PENDIENTE_APROBACION': return 'Pendiente';
      default: return estado;
    }
  }

  // ============================================
  // MODALES
  // ============================================
  
  verSolicitudesPendientes(): void {
    this.mostrarModalSolicitudes = true;
  }
  
  verCotizacionesPendientes(): void {
    this.mostrarModalCotizaciones = true;
  }
  
  verDetalleSolicitud(solicitud: SolicitudPedido): void {
    this.solicitudSeleccionada = solicitud;
    this.mostrarDetalleSolicitud = true;
  }
  
  verDetalleCotizacion(cotizacion: Cotizacion): void {
    this.cotizacionSeleccionada = cotizacion;
    this.mostrarDetalleCotizacion = true;
  }
  
  evaluarSolicitud(solicitud: SolicitudPedido): void {
    Swal.fire({
      title: 'Evaluar Solicitud',
      html: `
        <p><strong>N° Pedido:</strong> ${solicitud.numeroPedido}</p>
        <p><strong>Solicitante:</strong> ${solicitud.solicitante}</p>
        <p><strong>Productos:</strong> ${solicitud.detalles?.length || 0} items</p>
        <hr>
        <label>Decisión:</label>
        <select id="decision" class="swal2-select">
          <option value="aprobar">Aprobar</option>
          <option value="rechazar">Rechazar</option>
        </select>
        <label id="motivoLabel" style="display:none; margin-top:10px">Motivo:</label>
        <textarea id="motivo" class="swal2-textarea" placeholder="Ingrese motivo si es rechazo" style="display:none"></textarea>
      `,
      showCancelButton: true,
      confirmButtonText: 'Confirmar',
      preConfirm: () => {
        const decision = (document.getElementById('decision') as HTMLSelectElement).value;
        const motivo = (document.getElementById('motivo') as HTMLTextAreaElement).value;
        return { decision, motivo };
      },
      didOpen: () => {
        const decisionSelect = document.getElementById('decision') as HTMLSelectElement;
        const motivoLabel = document.getElementById('motivoLabel');
        const motivoText = document.getElementById('motivo');
        
        decisionSelect.addEventListener('change', () => {
          if (decisionSelect.value === 'rechazar') {
            motivoLabel!.style.display = 'block';
            motivoText!.style.display = 'block';
          } else {
            motivoLabel!.style.display = 'none';
            motivoText!.style.display = 'none';
          }
        });
      }
    }).then((result) => {
      if (result.isConfirmed) {
        if (result.value.decision === 'aprobar') {
          this.solicitudService.aprobarSolicitud(solicitud.id).subscribe({
            next: () => {
              Swal.fire('Aprobada', 'Solicitud aprobada', 'success');
              this.cargarDatos();
              this.cerrarModales();
            },
            error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
          });
        } else {
          if (!result.value.motivo) {
            Swal.fire('Error', 'Debe ingresar un motivo', 'warning');
            return;
          }
          this.solicitudService.rechazarSolicitud(solicitud.id, result.value.motivo).subscribe({
            next: () => {
              Swal.fire('Rechazada', 'Solicitud rechazada', 'success');
              this.cargarDatos();
              this.cerrarModales();
            },
            error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
          });
        }
      }
    });
  }
  
  evaluarCotizacion(cotizacion: Cotizacion): void {
    Swal.fire({
      title: 'Evaluar Cotización',
      html: `
        <p><strong>N° Cotización:</strong> ${cotizacion.numeroCotizacion}</p>
        <p><strong>Proveedor:</strong> ${cotizacion.nombreProveedor}</p>
        <p><strong>Monto Total:</strong> S/ ${cotizacion.montoTotal}</p>
        <hr>
        <label>Decisión:</label>
        <select id="decision" class="swal2-select">
          <option value="aprobar">Aprobar</option>
          <option value="rechazar">Rechazar</option>
        </select>
        <label id="motivoLabel" style="display:none; margin-top:10px">Motivo:</label>
        <textarea id="motivo" class="swal2-textarea" placeholder="Ingrese motivo si es rechazo" style="display:none"></textarea>
      `,
      showCancelButton: true,
      confirmButtonText: 'Confirmar',
      preConfirm: () => {
        const decision = (document.getElementById('decision') as HTMLSelectElement).value;
        const motivo = (document.getElementById('motivo') as HTMLTextAreaElement).value;
        return { decision, motivo };
      },
      didOpen: () => {
        const decisionSelect = document.getElementById('decision') as HTMLSelectElement;
        const motivoLabel = document.getElementById('motivoLabel');
        const motivoText = document.getElementById('motivo');
        
        decisionSelect.addEventListener('change', () => {
          if (decisionSelect.value === 'rechazar') {
            motivoLabel!.style.display = 'block';
            motivoText!.style.display = 'block';
          } else {
            motivoLabel!.style.display = 'none';
            motivoText!.style.display = 'none';
          }
        });
      }
    }).then((result) => {
      if (result.isConfirmed) {
        if (result.value.decision === 'aprobar') {
          this.cotizacionService.aprobarCotizacion(cotizacion.id).subscribe({
            next: () => {
              Swal.fire('Aprobada', 'Cotización aprobada', 'success');
              this.cargarDatos();
              this.cerrarModales();
            },
            error: () => Swal.fire('Error', 'No se pudo aprobar', 'error')
          });
        } else {
          if (!result.value.motivo) {
            Swal.fire('Error', 'Debe ingresar un motivo', 'warning');
            return;
          }
          this.cotizacionService.rechazarCotizacion(cotizacion.id, result.value.motivo).subscribe({
            next: () => {
              Swal.fire('Rechazada', 'Cotización rechazada', 'success');
              this.cargarDatos();
              this.cerrarModales();
            },
            error: () => Swal.fire('Error', 'No se pudo rechazar', 'error')
          });
        }
      }
    });
  }
  
  cerrarModales(): void {
    this.mostrarModalSolicitudes = false;
    this.mostrarModalCotizaciones = false;
    this.mostrarDetalleSolicitud = false;
    this.mostrarDetalleCotizacion = false;
    this.mostrarModalEvaluacion = false;
    this.solicitudSeleccionada = null;
    this.cotizacionSeleccionada = null;
    this.solicitudEnEvaluacion = null;
    this.cotizacionEnEvaluacion = null;
  }
}