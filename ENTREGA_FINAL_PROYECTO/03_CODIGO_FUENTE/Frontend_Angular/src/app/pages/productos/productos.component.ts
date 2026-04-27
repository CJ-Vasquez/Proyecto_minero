import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService, Producto } from '../../services/producto.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="productos-container">
      <div class="header">
        <h1>Gestión de Productos</h1>
        <button class="btn-primary" (click)="abrirModalNuevo()">
          <i class="fas fa-plus"></i> Nuevo Producto
        </button>
      </div>

      <div class="filtros">
        <input type="text" placeholder="Buscar..." [(ngModel)]="terminoBusqueda" (input)="aplicarFiltros()">
        <select [(ngModel)]="categoriaSeleccionada" (change)="aplicarFiltros()">
          <option value="">Todas las categorías</option>
          <option *ngFor="let cat of categorias" [value]="cat">{{ cat }}</option>
        </select>
      </div>

      <div class="tabla-container">
        <table class="tabla-productos">
          <thead>
            <tr>
              <th>Código</th><th>Nombre</th><th>Categoría</th><th>Stock</th><th>Precio</th><th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let p of productosFiltrados">
              <td>{{ p.codigo }}</td>
              <td>{{ p.nombre }}</td>
              <td>{{ p.categoria }}</td>
              <td [class.stock-critico]="p.stockActual <= p.stockMinimo">{{ p.stockActual }}</td>
              <td>{{ p.precioReferencial | currency:'PEN':'symbol':'1.2-2' }}</td>
              <td class="acciones">
                <button (click)="abrirModalStock(p)" title="Ajustar Stock"><i class="fas fa-boxes"></i></button>
                <button (click)="abrirModalEditar(p)" title="Editar"><i class="fas fa-edit"></i></button>
                <button (click)="eliminarProducto(p.id, p.nombre)" title="Eliminar"><i class="fas fa-trash"></i></button>
              </td>
            </tr>
            <tr *ngIf="productosFiltrados.length === 0">
              <td colspan="6" class="text-center">No hay productos registrados</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Nuevo/Editar -->
    <div class="modal" [class.show]="mostrarModal" (click)="cerrarModal()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>{{ editando ? 'Editar Producto' : 'Nuevo Producto' }}</h3>
          <button class="close" (click)="cerrarModal()">&times;</button>
        </div>
        <div class="modal-body">
          <input type="text" placeholder="Código" [(ngModel)]="productoForm.codigo">
          <input type="text" placeholder="Nombre" [(ngModel)]="productoForm.nombre">
          <textarea placeholder="Descripción" [(ngModel)]="productoForm.descripcion"></textarea>
          <select [(ngModel)]="productoForm.categoria">
            <option *ngFor="let cat of categorias" [value]="cat">{{ cat }}</option>
          </select>
          <input type="number" placeholder="Precio Referencial" [(ngModel)]="productoForm.precioReferencial">
          <input type="number" placeholder="Stock Mínimo" [(ngModel)]="productoForm.stockMinimo">
          <input type="text" placeholder="Ubicación Física" [(ngModel)]="productoForm.ubicacionFisica">
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" (click)="cerrarModal()">Cancelar</button>
          <button class="btn-primary" (click)="guardarProducto()">Guardar</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .productos-container { padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .btn-primary { background: #667eea; color: white; padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; }
    .filtros { display: flex; gap: 10px; margin-bottom: 20px; }
    .filtros input, .filtros select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px; }
    .tabla-container { overflow-x: auto; background: white; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
    th { background: #f8f9fa; font-weight: 600; }
    tr:hover { background: #f8f9fa; }
    .stock-critico { color: #ffc107; font-weight: bold; }
    .acciones { display: flex; gap: 10px; }
    .acciones button { background: none; border: none; cursor: pointer; font-size: 16px; }
    .acciones button:hover { color: #667eea; }
    .text-center { text-align: center; }
    .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); align-items: center; justify-content: center; z-index: 1000; }
    .modal.show { display: flex; }
    .modal-content { background: white; border-radius: 12px; width: 500px; max-width: 90%; }
    .modal-header { display: flex; justify-content: space-between; padding: 15px 20px; border-bottom: 1px solid #eee; }
    .modal-body { padding: 20px; }
    .modal-body input, .modal-body select, .modal-body textarea { width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 15px 20px; border-top: 1px solid #eee; }
    .btn-secondary { background: #6c757d; color: white; padding: 8px 16px; border: none; border-radius: 6px; cursor: pointer; }
    .close { background: none; border: none; font-size: 24px; cursor: pointer; }
  `]
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  terminoBusqueda: string = '';
  categoriaSeleccionada: string = '';
  mostrarModal: boolean = false;
  editando: boolean = false;
  productoSeleccionado: Producto | null = null;

  categorias = ['EPP', 'REPUESTOS', 'HERRAMIENTAS', 'LUBRICANTES', 'PERFORACION'];

  productoForm = {
    codigo: '',
    nombre: '',
    descripcion: '',
    categoria: 'EPP',
    unidadMedida: 'UNIDAD',
    precioReferencial: 0,
    stockMinimo: 0,
    stockActual: 0,
    ubicacionFisica: ''
  };

  constructor(private productoService: ProductoService) {}

  ngOnInit(): void {
  console.log('ProductosComponent inicializado');
  this.cargarProductos();
}

cargarProductos(): void {
  console.log('Llamando a getProductos()');
  this.productoService.getProductos().subscribe({
    next: (data) => {
      console.log('Datos recibidos:', data);
      this.productos = data;
      this.productosFiltrados = data;
      console.log('Productos asignados:', this.productos.length);
    },
    error: (error) => {
      console.error('Error:', error);
    }
  });
}

  aplicarFiltros(): void {
    let filtrados = [...this.productos];
    if (this.terminoBusqueda) {
      filtrados = filtrados.filter(p => 
        p.nombre.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
        p.codigo.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
      );
    }
    if (this.categoriaSeleccionada) {
      filtrados = filtrados.filter(p => p.categoria === this.categoriaSeleccionada);
    }
    this.productosFiltrados = filtrados;
  }

  abrirModalNuevo(): void {
    this.editando = false;
    this.productoForm = {
      codigo: '', nombre: '', descripcion: '', categoria: 'EPP',
      unidadMedida: 'UNIDAD', precioReferencial: 0, stockMinimo: 0,
      stockActual: 0, ubicacionFisica: ''
    };
    this.mostrarModal = true;
  }

  abrirModalEditar(producto: Producto): void {
    this.editando = true;
    this.productoSeleccionado = producto;
    this.productoForm = {
      codigo: producto.codigo,
      nombre: producto.nombre,
      descripcion: producto.descripcion || '',
      categoria: producto.categoria,
      unidadMedida: producto.unidadMedida,
      precioReferencial: producto.precioReferencial,
      stockMinimo: producto.stockMinimo,
      stockActual: producto.stockActual,
      ubicacionFisica: producto.ubicacionFisica || ''
    };
    this.mostrarModal = true;
  }

  guardarProducto(): void {
    if (this.editando && this.productoSeleccionado) {
      this.productoService.actualizarProducto(this.productoSeleccionado.id, this.productoForm).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Producto actualizado', 'success');
          this.cerrarModal();
          this.cargarProductos();
        },
        error: () => Swal.fire('Error', 'No se pudo actualizar', 'error')
      });
    } else {
      this.productoService.crearProducto(this.productoForm).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Producto creado', 'success');
          this.cerrarModal();
          this.cargarProductos();
        },
        error: () => Swal.fire('Error', 'No se pudo crear', 'error')
      });
    }
  }

  abrirModalStock(producto: Producto): void {
    Swal.fire({
      title: 'Ajustar Stock',
      html: `<input type="number" id="cantidad" class="swal2-input" placeholder="Cantidad (+ o -)" value="0">`,
      showCancelButton: true,
      confirmButtonText: 'Aplicar',
      preConfirm: () => {
        const cantidad = (document.getElementById('cantidad') as HTMLInputElement).value;
        return parseInt(cantidad) || 0;
      }
    }).then((result) => {
      if (result.isConfirmed && result.value !== 0) {
        this.productoService.actualizarStock(producto.id, result.value).subscribe({
          next: () => {
            Swal.fire('Éxito', `Stock ${result.value > 0 ? 'aumentado' : 'reducido'}`, 'success');
            this.cargarProductos();
          },
          error: () => Swal.fire('Error', 'No se pudo ajustar stock', 'error')
        });
      }
    });
  }

  eliminarProducto(id: number, nombre: string): void {
    Swal.fire({
      title: '¿Eliminar?',
      text: `¿Estás seguro de eliminar ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productoService.eliminarProducto(id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Producto eliminado', 'success');
            this.cargarProductos();
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.productoSeleccionado = null;
  }
}