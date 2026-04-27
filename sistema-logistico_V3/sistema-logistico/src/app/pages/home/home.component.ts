import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  servicios = [
    {
      icon: 'fas fa-shopping-cart',
      titulo: 'Gestión de Compras',
      descripcion: 'Solicitudes, cotizaciones y órdenes de compra con flujo de aprobación gerencial integrado.'
    },
    {
      icon: 'fas fa-warehouse',
      titulo: 'Almacenamiento',
      descripcion: 'Recepción de materiales, control de stock por sede (Lima, Trujillo, Mina) y trazabilidad completa.'
    },
    {
      icon: 'fas fa-truck',
      titulo: 'Logística y Distribución',
      descripcion: 'Órdenes de salida, despacho a operaciones mineras y kárdex valorizado en tiempo real.'
    },
    {
      icon: 'fas fa-shield-alt',
      titulo: 'Seguridad y Auditoría',
      descripcion: 'Roles diferenciados, autenticación JWT y registro de auditoría de cada operación crítica.'
    }
  ];

  modulos = [
    { icon: 'fas fa-boxes', nombre: 'Productos' },
    { icon: 'fas fa-handshake', nombre: 'Proveedores' },
    { icon: 'fas fa-clipboard-list', nombre: 'Solicitudes' },
    { icon: 'fas fa-file-invoice-dollar', nombre: 'Cotizaciones' },
    { icon: 'fas fa-shopping-cart', nombre: 'Órdenes de Compra' },
    { icon: 'fas fa-truck-loading', nombre: 'Recepciones' },
    { icon: 'fas fa-chart-line', nombre: 'Kárdex' },
    { icon: 'fas fa-chart-pie', nombre: 'Reportes' }
  ];

  scrollTo(id: string): void {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}
