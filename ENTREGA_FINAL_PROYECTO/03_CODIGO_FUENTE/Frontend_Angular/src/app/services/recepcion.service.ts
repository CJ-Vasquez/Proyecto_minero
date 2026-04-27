import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleRecepcion {
  productoId: number;
  codigoProducto: string;
  nombreProducto: string;
  cantidadSolicitada: number;
  cantidadRecibida: number;
  cantidadPendiente: number;
  precioUnitario: number;
  subtotal: number;
  observaciones?: string;
}

export interface Recepcion {
  id: number;
  numeroRecepcion: string;
  ordenCompraId: number;
  numeroOrden: string;
  proveedorId: number;
  nombreProveedor: string;
  fechaRecepcion: string;
  estado: string;
  observaciones?: string;
  detalles: DetalleRecepcion[];
}

@Injectable({
  providedIn: 'root'
})
export class RecepcionService {
  private apiUrl = 'http://localhost:8082/api/recepciones';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getRecepciones(): Observable<Recepcion[]> {
    return this.http.get<Recepcion[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getRecepcion(id: number): Observable<Recepcion> {
    return this.http.get<Recepcion>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  crearRecepcion(recepcion: any): Observable<Recepcion> {
    return this.http.post<Recepcion>(this.apiUrl, recepcion, { headers: this.getHeaders() });
  }

  confirmarRecepcion(id: number): Observable<Recepcion> {
    return this.http.put<Recepcion>(`${this.apiUrl}/${id}/confirmar`, {}, { headers: this.getHeaders() });
  }

  cancelarRecepcion(id: number): Observable<Recepcion> {
    return this.http.put<Recepcion>(`${this.apiUrl}/${id}/cancelar`, {}, { headers: this.getHeaders() });
  }
}