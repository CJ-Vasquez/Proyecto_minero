import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MovimientoKardex {
  id: number;
  fecha: string;
  productoId: number;
  codigoProducto: string;
  nombreProducto: string;
  tipoMovimiento: 'ENTRADA' | 'SALIDA';
  documentoReferencia: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  saldoAnterior: number;
  saldoNuevo: number;
  almacen: string;
  usuario: string;
  observaciones?: string;
}

@Injectable({
  providedIn: 'root'
})
export class KardexService {
  private apiUrl = 'http://localhost:8082/api/kardex';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getMovimientos(): Observable<MovimientoKardex[]> {
    return this.http.get<MovimientoKardex[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getMovimientosPorProducto(productoId: number): Observable<MovimientoKardex[]> {
    return this.http.get<MovimientoKardex[]>(`${this.apiUrl}/producto/${productoId}`, { headers: this.getHeaders() });
  }

  getResumenProductos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/resumen`, { headers: this.getHeaders() });
  }

  registrarMovimiento(movimiento: any): Observable<MovimientoKardex> {
    return this.http.post<MovimientoKardex>(this.apiUrl, movimiento, { headers: this.getHeaders() });
  }

  
}