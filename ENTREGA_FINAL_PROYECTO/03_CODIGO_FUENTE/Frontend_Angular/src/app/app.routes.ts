import { Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login/login.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'productos', loadComponent: () => import('./pages/productos/productos.component').then(m => m.ProductosComponent) },
      { path: 'proveedores', loadComponent: () => import('./pages/proveedores/proveedores.component').then(m => m.ProveedoresComponent) },
      { path: 'solicitudes', loadComponent: () => import('./pages/solicitudes/solicitudes.component').then(m => m.SolicitudesComponent) },
      { path: 'cotizaciones', loadComponent: () => import('./pages/cotizaciones/cotizaciones.component').then(m => m.CotizacionesComponent) },
      { path: 'ordenes-compra', loadComponent: () => import('./pages/ordenes-compra/ordenes-compra.component').then(m => m.OrdenesCompraComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN', 'GERENTE'] } },
      { path: 'recepciones', 
  loadComponent: () => import('./pages/recepciones/recepciones.component').then(m => m.RecepcionesComponent) 
},
      { path: 'kardex', loadComponent: () => import('./pages/kardex/kardex.component').then(m => m.KardexComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN', 'GERENTE'] } },
      { path: 'usuarios', 
  loadComponent: () => import('./pages/usuarios/usuarios.component').then(m => m.UsuariosComponent),
  canActivate: [RoleGuard],
  data: { roles: ['ADMIN'] }
},
      { path: 'gerente-dashboard', loadComponent: () => import('./pages/gerente-dashboard/gerente-dashboard.component').then(m => m.GerenteDashboardComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN', 'GERENTE'] } },
      { path: 'gerencia', loadComponent: () => import('./pages/gerencia/gerencia.component').then(m => m.GerenciaComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN', 'GERENTE'] }
},
      { path: 'reportes', 
  loadComponent: () => import('./pages/reportes/reportes.component').then(m => m.ReportesComponent) 
},
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'dashboard' },
  { path: 'recepciones', 
  loadComponent: () => import('./pages/recepciones/recepciones.component').then(m => m.RecepcionesComponent) 
},
];