import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReporteSolicitudes {
  total: number;
  aprobadas: number;
  rechazadas: number;
  pendientes: number;
  porMes: { mes: string; cantidad: number }[];
}

export interface ReporteCompras {
  totalCompras: number;
  montoTotal: number;
  porProveedor: { proveedor: string; monto: number; cantidad: number }[];
  porMes: { mes: string; monto: number }[];
}

export interface ReporteInventario {
  totalProductos: number;
  valorTotal: number;
  productosBajoStock: { producto: string; stock: number; minimo: number }[];
  movimientos: { tipo: string; cantidad: number }[];
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = 'http://localhost:8082/api/reportes';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getReporteSolicitudes(fechaInicio?: string, fechaFin?: string): Observable<ReporteSolicitudes> {
    let url = `${this.apiUrl}/solicitudes`;
    if (fechaInicio && fechaFin) {
      url += `?inicio=${fechaInicio}&fin=${fechaFin}`;
    }
    return this.http.get<ReporteSolicitudes>(url, { headers: this.getHeaders() });
  }

  getReporteCompras(fechaInicio?: string, fechaFin?: string): Observable<ReporteCompras> {
    let url = `${this.apiUrl}/compras`;
    if (fechaInicio && fechaFin) {
      url += `?inicio=${fechaInicio}&fin=${fechaFin}`;
    }
    return this.http.get<ReporteCompras>(url, { headers: this.getHeaders() });
  }

  getReporteInventario(): Observable<ReporteInventario> {
    return this.http.get<ReporteInventario>(`${this.apiUrl}/inventario`, { headers: this.getHeaders() });
  }
}