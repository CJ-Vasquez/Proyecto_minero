import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ordenes-salida',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>Órdenes de Salida</h1>
      <p>Módulo en construcción...</p>
    </div>
  `,
  styles: [`
    .page-container { padding: 20px; }
    h1 { color: #2c3e50; margin-bottom: 20px; }
  `]
})
export class OrdenesSalidaComponent {}