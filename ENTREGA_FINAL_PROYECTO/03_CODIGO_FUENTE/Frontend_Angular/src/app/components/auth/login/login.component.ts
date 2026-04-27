import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  rememberMe: boolean = false;
  isLoading: boolean = false;
  usernameError: boolean = false;
  passwordError: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.usernameError = !this.username;
    this.passwordError = !this.password;
    
    if (!this.username || !this.password) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos incompletos',
        text: 'Por favor ingrese usuario y contraseña',
        confirmButtonColor: '#667eea'
      });
      return;
    }
    
    this.isLoading = true;
    
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response) => {
        this.isLoading = false;
        
        if (this.rememberMe) {
          localStorage.setItem('rememberMe', 'true');
        }
        
        Swal.fire({
          icon: 'success',
          title: `¡Bienvenido ${response.nombres}!`,
          text: 'Redirigiendo al dashboard...',
          timer: 1500,
          showConfirmButton: false,
          background: '#fff',
          customClass: {
            popup: 'animated fadeInUp faster'
          }
        });
        
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);
      },
      error: (error) => {
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error de autenticación',
          text: error.error?.message || 'Usuario o contraseña incorrectos',
          confirmButtonColor: '#667eea'
        });
      }
    });
  }
}