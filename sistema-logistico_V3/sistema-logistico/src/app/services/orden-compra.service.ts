import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleOrdenCompra {
  id?: number;
  productoId: number;
  codigoProducto?: string;
  nombreProducto?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal?: number;
}

export interface OrdenCompra {
  id: number;
  numeroOrden: string;
  cotizacionId: number;
  numeroCotizacion: string;
  proveedorId: number;
  nombreProveedor: string;
  fechaEmision: string;
  fechaEntrega: string;
  montoTotal: number;
  estado: string;
  observaciones?: string;
  detalles: DetalleOrdenCompra[];
}

@Injectable({
  providedIn: 'root'
})
export class OrdenCompraService {
  private apiUrl = 'http://localhost:8082/api/ordenes-compra';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getOrdenes(): Observable<OrdenCompra[]> {
    return this.http.get<OrdenCompra[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getOrden(id: number): Observable<OrdenCompra> {
    return this.http.get<OrdenCompra>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  // ✅ CORREGIDO: Enviar los datos correctamente al backend
  crearOrden(cotizacionId: number, fechaEntrega: string, observaciones: string): Observable<OrdenCompra> {
    const body = {
      cotizacionId: cotizacionId,
      fechaEntrega: fechaEntrega,
      observaciones: observaciones
    };
    console.log('Enviando al backend:', body);
    return this.http.post<OrdenCompra>(this.apiUrl, body, { headers: this.getHeaders() });
  }

  enviarOrden(id: number): Observable<OrdenCompra> {
    return this.http.put<OrdenCompra>(`${this.apiUrl}/${id}/enviar`, {}, { headers: this.getHeaders() });
  }

  recibirOrden(id: number): Observable<OrdenCompra> {
    return this.http.put<OrdenCompra>(`${this.apiUrl}/${id}/recibir`, {}, { headers: this.getHeaders() });
  }

  cancelarOrden(id: number): Observable<OrdenCompra> {
    return this.http.put<OrdenCompra>(`${this.apiUrl}/${id}/cancelar`, {}, { headers: this.getHeaders() });
  }

  // Método adicional si quieres usar el endpoint dedicado
  crearDesdeCotizacion(cotizacionId: number): Observable<OrdenCompra> {
    return this.http.post<OrdenCompra>(`${this.apiUrl}/desde-cotizacion/${cotizacionId}`, {}, { headers: this.getHeaders() });
  }
}