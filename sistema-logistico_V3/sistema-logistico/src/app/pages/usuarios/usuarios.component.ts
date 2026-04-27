import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService, Usuario, UsuarioRequest } from '../../services/usuario.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.scss']
})
export class UsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];
  rolSeleccionado: string = '';
  estadoSeleccionado: string = '';
  mostrarModal: boolean = false;
  mostrarDetalleModal: boolean = false;
  mostrarPasswordModal: boolean = false;
  usuarioSeleccionado: Usuario | null = null;
  cargando: boolean = true;
  esEdicion: boolean = false;

  // Paginación
  paginaActual: number = 1;
  itemsPorPagina: number = 10;
  totalPaginas: number = 1;
  totalRegistros: number = 0;
  Math = Math;

  // Formulario
  usuarioForm: UsuarioRequest = {
    username: '',
    password: '',
    nombres: '',
    apellidos: '',
    email: '',
    rol: 'USUARIO'
  };

  nuevaPassword: string = '';
  confirmarPassword: string = '';

  roles = ['ADMIN', 'GERENTE', 'USUARIO'];
  estados = ['ACTIVO', 'INACTIVO'];

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.usuariosFiltrados = [...data];
        this.calcularPaginacion();
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error:', error);
        this.cargarUsuariosEjemplo();
        this.cargando = false;
      }
    });
  }

  cargarUsuariosEjemplo(): void {
    this.usuarios = [
      { id: 1, username: 'admin', nombres: 'Admin', apellidos: 'Sistema', email: 'admin@minalog.com', rol: 'ADMIN', activo: true, fechaCreacion: '2025-01-01' },
      { id: 2, username: 'gerente', nombres: 'Carlos', apellidos: 'Lopez', email: 'gerente@minalog.com', rol: 'GERENTE', activo: true, fechaCreacion: '2025-01-02' },
      { id: 3, username: 'usuario', nombres: 'Juan', apellidos: 'Perez', email: 'usuario@minalog.com', rol: 'USUARIO', activo: true, fechaCreacion: '2025-01-03' }
    ];
    this.usuariosFiltrados = [...this.usuarios];
    this.calcularPaginacion();
  }

  aplicarFiltros(): void {
    let filtrados = [...this.usuarios];
    
    if (this.rolSeleccionado) {
      filtrados = filtrados.filter(u => u.rol === this.rolSeleccionado);
    }
    
    if (this.estadoSeleccionado) {
      const activo = this.estadoSeleccionado === 'ACTIVO';
      filtrados = filtrados.filter(u => u.activo === activo);
    }
    
    this.usuariosFiltrados = filtrados;
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  limpiarFiltros(): void {
    this.rolSeleccionado = '';
    this.estadoSeleccionado = '';
    this.usuariosFiltrados = [...this.usuarios];
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  calcularPaginacion(): void {
    this.totalRegistros = this.usuariosFiltrados.length;
    this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = Math.max(1, this.totalPaginas);
    }
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
  }

  getUsuariosPaginados(): Usuario[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.usuariosFiltrados.slice(inicio, inicio + this.itemsPorPagina);
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

  getRolClass(rol: string): string {
    switch(rol) {
      case 'ADMIN': return 'badge-danger';
      case 'GERENTE': return 'badge-warning';
      default: return 'badge-info';
    }
  }

  abrirModalNuevo(): void {
    this.esEdicion = false;
    this.usuarioForm = {
      username: '',
      password: '',
      nombres: '',
      apellidos: '',
      email: '',
      rol: 'USUARIO'
    };
    this.mostrarModal = true;
  }

  abrirModalEditar(usuario: Usuario): void {
    this.esEdicion = true;
    this.usuarioSeleccionado = usuario;
    this.usuarioForm = {
      username: usuario.username,
      password: '',
      nombres: usuario.nombres,
      apellidos: usuario.apellidos,
      email: usuario.email,
      rol: usuario.rol
    };
    this.mostrarModal = true;
  }

  abrirDetalle(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.mostrarDetalleModal = true;
  }

  abrirPasswordModal(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.nuevaPassword = '';
    this.confirmarPassword = '';
    this.mostrarPasswordModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mostrarDetalleModal = false;
    this.mostrarPasswordModal = false;
    this.usuarioSeleccionado = null;
  }

  guardarUsuario(): void {
    if (!this.usuarioForm.username || !this.usuarioForm.nombres || !this.usuarioForm.apellidos || !this.usuarioForm.email) {
      Swal.fire('Error', 'Todos los campos son obligatorios', 'warning');
      return;
    }

    if (!this.esEdicion && !this.usuarioForm.password) {
      Swal.fire('Error', 'La contraseña es obligatoria', 'warning');
      return;
    }

    if (this.usuarioForm.password && this.usuarioForm.password.length < 4) {
      Swal.fire('Error', 'La contraseña debe tener al menos 4 caracteres', 'warning');
      return;
    }

    Swal.fire({
      title: this.esEdicion ? 'Actualizando usuario' : 'Creando usuario',
      text: 'Por favor espere...',
      allowOutsideClick: false,
      didOpen: () => { Swal.showLoading(); }
    });

    if (this.esEdicion && this.usuarioSeleccionado) {
      this.usuarioService.actualizarUsuario(this.usuarioSeleccionado.id, {
        nombres: this.usuarioForm.nombres,
        apellidos: this.usuarioForm.apellidos,
        email: this.usuarioForm.email,
        rol: this.usuarioForm.rol
      }).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Usuario actualizado correctamente', 'success');
          this.cerrarModal();
          this.cargarUsuarios();
        },
        error: (error) => {
          console.error('Error:', error);
          Swal.fire('Error', error.error?.message || 'No se pudo actualizar', 'error');
        }
      });
    } else {
      this.usuarioService.crearUsuario(this.usuarioForm).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Usuario creado correctamente', 'success');
          this.cerrarModal();
          this.cargarUsuarios();
        },
        error: (error) => {
          console.error('Error:', error);
          Swal.fire('Error', error.error?.message || 'No se pudo crear', 'error');
        }
      });
    }
  }

  cambiarEstado(usuario: Usuario): void {
    const nuevoEstado = !usuario.activo;
    const mensaje = nuevoEstado ? 'activar' : 'desactivar';
    
    Swal.fire({
      title: `¿${mensaje} usuario?`,
      text: `¿Estás seguro de ${mensaje} a ${usuario.nombres} ${usuario.apellidos}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: `Sí, ${mensaje}`,
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.usuarioService.cambiarEstado(usuario.id, nuevoEstado).subscribe({
          next: () => {
            Swal.fire('Éxito', `Usuario ${mensaje}do correctamente`, 'success');
            this.cargarUsuarios();
          },
          error: (error) => {
            console.error('Error:', error);
            Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
          }
        });
      }
    });
  }

  cambiarPassword(): void {
    if (!this.nuevaPassword) {
      Swal.fire('Error', 'Ingrese una nueva contraseña', 'warning');
      return;
    }

    if (this.nuevaPassword.length < 4) {
      Swal.fire('Error', 'La contraseña debe tener al menos 4 caracteres', 'warning');
      return;
    }

    if (this.nuevaPassword !== this.confirmarPassword) {
      Swal.fire('Error', 'Las contraseñas no coinciden', 'warning');
      return;
    }

    Swal.fire({
      title: 'Cambiando contraseña',
      text: 'Por favor espere...',
      allowOutsideClick: false,
      didOpen: () => { Swal.showLoading(); }
    });

    this.usuarioService.cambiarPassword(this.usuarioSeleccionado!.id, this.nuevaPassword).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Contraseña cambiada correctamente', 'success');
        this.cerrarModal();
      },
      error: (error) => {
        console.error('Error:', error);
        Swal.fire('Error', 'No se pudo cambiar la contraseña', 'error');
      }
    });
  }

  eliminarUsuario(id: number, nombre: string): void {
    Swal.fire({
      title: '¿Eliminar usuario?',
      text: `¿Estás seguro de eliminar a ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.usuarioService.eliminarUsuario(id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Usuario eliminado correctamente', 'success');
            this.cargarUsuarios();
          },
          error: (error) => {
            console.error('Error:', error);
            Swal.fire('Error', 'No se pudo eliminar', 'error');
          }
        });
      }
    });
  }
}