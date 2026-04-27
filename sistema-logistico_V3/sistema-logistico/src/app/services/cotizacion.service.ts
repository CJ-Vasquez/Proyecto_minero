import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleCotizacion {
  productoId: number;
  codigoProducto?: string;
  nombreProducto?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal?: number;
}

export interface Cotizacion {
  id: number;
  numeroCotizacion: string;
  solicitudPedidoId: number;
  numeroPedido: string;
  proveedorId: number;
  nombreProveedor: string;
  fechaCotizacion: string;
  fechaValidez: string;
  montoTotal: number;
  estado: string;
  observaciones?: string;
  detalles: DetalleCotizacion[];
}

@Injectable({
  providedIn: 'root'
})
export class CotizacionService {
  private apiUrl = 'http://localhost:8082/api/cotizaciones';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getCotizaciones(): Observable<Cotizacion[]> {
    return this.http.get<Cotizacion[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getCotizacionesAprobadas(): Observable<Cotizacion[]> {
    return this.http.get<Cotizacion[]>(`${this.apiUrl}/aprobadas`, { headers: this.getHeaders() });
  }

  getCotizacionesPendientes(): Observable<Cotizacion[]> {
    return this.http.get<Cotizacion[]>(`${this.apiUrl}/pendientes`, { headers: this.getHeaders() });
  }

  getCotizacionesPorSolicitud(solicitudId: number): Observable<Cotizacion[]> {
    return this.http.get<Cotizacion[]>(`${this.apiUrl}/solicitud/${solicitudId}`, { headers: this.getHeaders() });
  }

  getCotizacion(id: number): Observable<Cotizacion> {
    return this.http.get<Cotizacion>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  crearCotizacion(cotizacion: any): Observable<Cotizacion> {
    return this.http.post<Cotizacion>(this.apiUrl, cotizacion, { headers: this.getHeaders() });
  }

  enviarAProveedor(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/enviar-proveedor`, {}, { headers: this.getHeaders() });
  }

  recibirRespuesta(id: number, precioTotal: number, observaciones: string): Observable<Cotizacion> {
    return this.http.put<Cotizacion>(`${this.apiUrl}/${id}/recibir-respuesta?precioTotal=${precioTotal}&observaciones=${encodeURIComponent(observaciones)}`, {}, { headers: this.getHeaders() });
  }

  aprobarCotizacion(id: number): Observable<Cotizacion> {
    return this.http.put<Cotizacion>(`${this.apiUrl}/${id}/aprobar`, {}, { headers: this.getHeaders() });
  }

  rechazarCotizacion(id: number, motivo: string): Observable<Cotizacion> {
    return this.http.put<Cotizacion>(`${this.apiUrl}/${id}/rechazar?motivo=${encodeURIComponent(motivo)}`, {}, { headers: this.getHeaders() });
  }

  eliminarCotizacion(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}