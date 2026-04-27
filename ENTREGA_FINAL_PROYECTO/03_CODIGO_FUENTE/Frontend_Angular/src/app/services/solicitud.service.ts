import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleSolicitud {
  productoId: number;
  codigoProducto?: string;
  nombreProducto?: string;
  cantidadSolicitada: number;
  cantidadAprobada?: number;
  precioReferencial: number;
}

export interface SolicitudPedido {
  id: number;
  numeroPedido: string;
  origen: string;
  solicitante: string;
  oficina: string;
  glosa: string;
  destino: string;
  aprobador: string;
  almacen: string;
  fecha: string;
  estado: string;
  detalles: DetalleSolicitud[];
}

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {
  private apiUrl = 'http://localhost:8082/api/solicitudes-pedido';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getSolicitudes(): Observable<SolicitudPedido[]> {
    return this.http.get<SolicitudPedido[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getSolicitudesPorEstado(estado: string): Observable<SolicitudPedido[]> {
    return this.http.get<SolicitudPedido[]>(`${this.apiUrl}/estado/${estado}`, { headers: this.getHeaders() });
  }

  getSolicitudesPendientes(): Observable<SolicitudPedido[]> {
    return this.http.get<SolicitudPedido[]>(`${this.apiUrl}/pendientes`, { headers: this.getHeaders() });
  }

  getSolicitud(id: number): Observable<SolicitudPedido> {
    return this.http.get<SolicitudPedido>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  crearSolicitud(solicitud: any): Observable<SolicitudPedido> {
    return this.http.post<SolicitudPedido>(this.apiUrl, solicitud, { headers: this.getHeaders() });
  }

  enviarAAprobacion(id: number): Observable<SolicitudPedido> {
    return this.http.post<SolicitudPedido>(`${this.apiUrl}/${id}/enviar-aprobacion`, {}, { headers: this.getHeaders() });
  }

  aprobarSolicitud(id: number, cantidadAprobada?: number): Observable<SolicitudPedido> {
    let url = `${this.apiUrl}/${id}/aprobar`;
    if (cantidadAprobada) {
      url += `?cantidadAprobada=${cantidadAprobada}`;
    }
    return this.http.put<SolicitudPedido>(url, {}, { headers: this.getHeaders() });
  }

  rechazarSolicitud(id: number, motivo: string): Observable<SolicitudPedido> {
    return this.http.put<SolicitudPedido>(`${this.apiUrl}/${id}/rechazar?motivo=${encodeURIComponent(motivo)}`, {}, { headers: this.getHeaders() });
  }

  cancelarSolicitud(id: number): Observable<SolicitudPedido> {
    return this.http.put<SolicitudPedido>(`${this.apiUrl}/${id}/cancelar`, {}, { headers: this.getHeaders() });
  }

  eliminarSolicitud(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}