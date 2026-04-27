import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = this.authService.getUser();
    const userRole = user?.rol || '';
    const allowedRoles = route.data['roles'] as Array<string>;
    
    // Si no se especifican roles, cualquiera puede acceder
    if (!allowedRoles || allowedRoles.length === 0) {
      return true;
    }
    
    // Verificar si el rol del usuario está permitido
    if (allowedRoles.includes(userRole)) {
      return true;
    }
    
    // Si no tiene permiso, redirigir al dashboard
    this.router.navigate(['/dashboard']);
    return false;
  }
}