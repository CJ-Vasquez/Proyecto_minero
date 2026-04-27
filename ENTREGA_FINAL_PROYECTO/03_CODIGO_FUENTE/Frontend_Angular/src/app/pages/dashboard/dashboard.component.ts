import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SolicitudService } from '../../services/solicitud.service';
import { CotizacionService } from '../../services/cotizacion.service';
import { OrdenCompraService } from '../../services/orden-compra.service';
import { AuthService } from '../../services/auth.service';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  userName: string = '';
  fechaActual: string = '';
  chartPeriodo: string = 'semana';
  Math = Math;
  
  kpis = [
    { label: 'Solicitudes', value: 0, icon: 'fas fa-clipboard-list', color: 'primary', trend: 12 },
    { label: 'Cotizaciones', value: 0, icon: 'fas fa-file-invoice-dollar', color: 'success', trend: 8 },
    { label: 'Órdenes de Compra', value: 0, icon: 'fas fa-shopping-cart', color: 'warning', trend: -3 },
    { label: 'Proveedores', value: 0, icon: 'fas fa-truck', color: 'info', trend: 5 }
  ];
  
  solicitudesRecientes: any[] = [];
  cotizacionesRecientes: any[] = [];
  private trendChart: any;

  constructor(
    private solicitudService: SolicitudService,
    private cotizacionService: CotizacionService,
    private ordenCompraService: OrdenCompraService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.userName = user?.nombres || 'Administrador';
    this.fechaActual = new Date().toLocaleDateString('es-PE', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
    this.cargarDatos();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.inicializarGrafico();
    }, 500);
  }

  cargarDatos(): void {
    this.solicitudService.getSolicitudes().subscribe({
      next: (data) => {
        this.kpis[0].value = data.length;
        this.solicitudesRecientes = data.slice(0, 5);
        this.actualizarGrafico();
      },
      error: (err) => console.error('Error:', err)
    });
    
    this.cotizacionService.getCotizaciones().subscribe({
      next: (data) => {
        this.kpis[1].value = data.length;
        this.cotizacionesRecientes = data.slice(0, 5);
        this.actualizarGrafico();
      },
      error: (err) => console.error('Error:', err)
    });
    
    this.ordenCompraService.getOrdenes().subscribe({
      next: (data) => this.kpis[2].value = data.length,
      error: (err) => console.error('Error:', err)
    });
    
    this.kpis[3].value = 12;
  }

  refreshData(): void {
    this.cargarDatos();
  }

  cambiarPeriodo(periodo: string): void {
    this.chartPeriodo = periodo;
    this.actualizarGrafico();
  }

  inicializarGrafico(): void {
    const canvas = document.getElementById('trendChart') as HTMLCanvasElement;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    
    this.trendChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
        datasets: [{
          label: 'Solicitudes',
          data: [5, 8, 12, 7, 15, 4, 3],
          borderColor: '#667eea',
          backgroundColor: 'rgba(102, 126, 234, 0.1)',
          tension: 0.4,
          fill: true,
          pointBackgroundColor: '#667eea',
          pointBorderColor: 'white',
          pointRadius: 4,
          pointHoverRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          legend: { display: false },
          tooltip: { backgroundColor: '#1a1a2e', titleColor: '#fff', bodyColor: '#ccc' }
        },
        scales: {
          y: { beginAtZero: true, grid: { color: '#e9ecef' } },
          x: { grid: { display: false } }
        }
      }
    });
  }

  actualizarGrafico(): void {
    if (!this.trendChart) return;
    
    let datos = [];
    if (this.chartPeriodo === 'semana') {
      datos = [5, 8, 12, 7, 15, 4, 3];
      this.trendChart.data.labels = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
    } else if (this.chartPeriodo === 'mes') {
      datos = [32, 45, 38, 52, 48, 61, 55, 49, 58, 62, 48, 45, 52, 49, 55, 60, 58, 62, 65, 58, 52, 48, 45, 42, 38, 35, 40, 45, 50, 48];
      this.trendChart.data.labels = Array.from({ length: 30 }, (_, i) => `${i + 1}`);
    } else {
      datos = [120, 145, 138, 162, 158, 171, 165, 159, 168, 172, 158, 155];
      this.trendChart.data.labels = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    }
    
    this.trendChart.data.datasets[0].data = datos;
    this.trendChart.update();
  }

  getEstadoClass(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'bg-success';
      case 'RECHAZADO': return 'bg-danger';
      case 'PENDIENTE_APROBACION': return 'bg-warning';
      default: return 'bg-secondary';
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
}