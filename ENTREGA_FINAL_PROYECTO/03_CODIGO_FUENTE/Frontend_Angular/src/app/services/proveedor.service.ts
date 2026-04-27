import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Proveedor {
  id: number;
  codigo: string;
  razonSocial: string;
  ruc: string;
  nombreContacto: string;
  telefono: string;
  email: string;
  direccion: string;
  estado: string;
  prioridad: number;
  puntajeEvaluacion: number;
  fechaRegistro: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {
  private apiUrl = 'http://localhost:8082/api/proveedores';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getProveedores(): Observable<Proveedor[]> {
    console.log('Token en proveedor service:', localStorage.getItem('token'));
    return this.http.get<Proveedor[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getProveedoresActivos(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${this.apiUrl}/activos`, { headers: this.getHeaders() });
  }

  getProveedoresPreferentes(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${this.apiUrl}/preferentes`, { headers: this.getHeaders() });
  }

  getProveedor(id: number): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  crearProveedor(proveedor: Partial<Proveedor>): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.apiUrl, proveedor, { headers: this.getHeaders() });
  }

  actualizarProveedor(id: number, proveedor: Partial<Proveedor>): Observable<Proveedor> {
    return this.http.put<Proveedor>(`${this.apiUrl}/${id}`, proveedor, { headers: this.getHeaders() });
  }

  evaluarProveedor(id: number, puntaje: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/evaluar?puntaje=${puntaje}`, {}, { headers: this.getHeaders() });
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado?estado=${estado}`, {}, { headers: this.getHeaders() });
  }

  eliminarProveedor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}