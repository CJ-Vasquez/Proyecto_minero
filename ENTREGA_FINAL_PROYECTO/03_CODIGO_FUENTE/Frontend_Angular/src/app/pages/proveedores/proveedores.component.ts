import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService, Proveedor } from '../../services/proveedor.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './proveedores.component.html',
  styleUrls: ['./proveedores.component.scss']
})
export class ProveedoresComponent implements OnInit {
  proveedores: Proveedor[] = [];
  proveedoresFiltrados: Proveedor[] = [];
  terminoBusqueda: string = '';
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  editando: boolean = false;
  proveedorSeleccionado: Proveedor | null = null;

  estados = ['ACTIVO', 'INACTIVO', 'VETADO'];

  proveedorForm = {
    codigo: '',
    razonSocial: '',
    ruc: '',
    nombreContacto: '',
    telefono: '',
    email: '',
    direccion: '',
    estado: 'ACTIVO'
  };

  constructor(private proveedorService: ProveedorService) {}

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
  this.proveedorService.getProveedores().subscribe({
    next: (data) => {
      this.proveedores = data;
      this.proveedoresFiltrados = [...data];  // ← Copiar los datos
    },
    error: (error) => {
      console.error('Error:', error);
      Swal.fire('Error', 'No se pudieron cargar los proveedores', 'error');
    }
  });
}

  aplicarFiltros(): void {
    let filtrados = [...this.proveedores];
    
    if (this.terminoBusqueda) {
      filtrados = filtrados.filter(p => 
        p.razonSocial.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
        p.ruc.includes(this.terminoBusqueda) ||
        (p.nombreContacto && p.nombreContacto.toLowerCase().includes(this.terminoBusqueda.toLowerCase()))
      );
    }
    
    if (this.estadoSeleccionado) {
      filtrados = filtrados.filter(p => p.estado === this.estadoSeleccionado);
    }
    
    this.proveedoresFiltrados = filtrados;
  }

  getPrioridadTexto(prioridad: number): string {
    if (prioridad === 1) return 'A';
    if (prioridad === 2) return 'B';
    if (prioridad === 3) return 'C';
    return '-';
  }

  getPrioridadClass(prioridad: number): string {
    if (prioridad === 1) return 'prioridad-a';
    if (prioridad === 2) return 'prioridad-b';
    if (prioridad === 3) return 'prioridad-c';
    return '';
  }

  abrirModalNuevo(): void {
    this.editando = false;
    this.proveedorForm = {
      codigo: '', razonSocial: '', ruc: '', nombreContacto: '',
      telefono: '', email: '', direccion: '', estado: 'ACTIVO'
    };
    this.mostrarModal = true;
  }

  abrirModalEditar(proveedor: Proveedor): void {
    this.editando = true;
    this.proveedorSeleccionado = proveedor;
    this.proveedorForm = {
      codigo: proveedor.codigo,
      razonSocial: proveedor.razonSocial,
      ruc: proveedor.ruc,
      nombreContacto: proveedor.nombreContacto || '',
      telefono: proveedor.telefono || '',
      email: proveedor.email || '',
      direccion: proveedor.direccion || '',
      estado: proveedor.estado
    };
    this.mostrarModal = true;
  }

  guardarProveedor(): void {
    if (!this.proveedorForm.razonSocial || !this.proveedorForm.ruc) {
      Swal.fire('Error', 'Razón Social y RUC son obligatorios', 'warning');
      return;
    }

    if (this.editando && this.proveedorSeleccionado) {
      this.proveedorService.actualizarProveedor(this.proveedorSeleccionado.id, this.proveedorForm).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Proveedor actualizado', 'success');
          this.cerrarModal();
          this.cargarProveedores();
        },
        error: () => Swal.fire('Error', 'No se pudo actualizar', 'error')
      });
    } else {
      this.proveedorService.crearProveedor(this.proveedorForm).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Proveedor creado', 'success');
          this.cerrarModal();
          this.cargarProveedores();
        },
        error: (err) => {
          let msg = err.error?.message || 'Error al crear proveedor';
          Swal.fire('Error', msg, 'error');
        }
      });
    }
  }

  abrirModalEvaluar(proveedor: Proveedor): void {
    Swal.fire({
      title: `Evaluar Proveedor: ${proveedor.razonSocial}`,
      html: `
        <input type="number" id="puntaje" class="swal2-input" placeholder="Puntaje (0-50)" min="0" max="50" step="0.5">
        <p>Prioridad: A (48-40) | B (39-36) | C (35-30)</p>
      `,
      showCancelButton: true,
      confirmButtonText: 'Evaluar',
      preConfirm: () => {
        const puntaje = (document.getElementById('puntaje') as HTMLInputElement).value;
        return parseFloat(puntaje) || 0;
      }
    }).then((result) => {
      if (result.isConfirmed && result.value > 0) {
        this.proveedorService.evaluarProveedor(proveedor.id, result.value).subscribe({
          next: () => {
            Swal.fire('Evaluado', 'Proveedor evaluado correctamente', 'success');
            this.cargarProveedores();
          },
          error: () => Swal.fire('Error', 'No se pudo evaluar', 'error')
        });
      }
    });
  }

  cambiarEstado(proveedor: Proveedor): void {
    const nuevoEstado = proveedor.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    Swal.fire({
      title: 'Cambiar Estado',
      text: `¿${nuevoEstado === 'ACTIVO' ? 'Activar' : 'Desactivar'} proveedor ${proveedor.razonSocial}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí'
    }).then((result) => {
      if (result.isConfirmed) {
        this.proveedorService.cambiarEstado(proveedor.id, nuevoEstado).subscribe({
          next: () => {
            Swal.fire('Éxito', `Proveedor ${nuevoEstado === 'ACTIVO' ? 'activado' : 'desactivado'}`, 'success');
            this.cargarProveedores();
          },
          error: () => Swal.fire('Error', 'No se pudo cambiar estado', 'error')
        });
      }
    });
  }

  eliminarProveedor(id: number, nombre: string): void {
    Swal.fire({
      title: '¿Eliminar?',
      text: `¿Estás seguro de eliminar ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.proveedorService.eliminarProveedor(id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Proveedor eliminado', 'success');
            this.cargarProveedores();
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.proveedorSeleccionado = null;
  }
}