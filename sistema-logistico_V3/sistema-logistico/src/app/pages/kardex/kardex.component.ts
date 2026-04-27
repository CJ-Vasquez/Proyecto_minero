import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KardexService, MovimientoKardex } from '../../services/kardex.service';
import { ProductoService, Producto } from '../../services/producto.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import 'jspdf-autotable';

@Component({
  selector: 'app-kardex',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './kardex.component.html',
  styleUrls: ['./kardex.component.scss']
})
export class KardexComponent implements OnInit {
  movimientos: MovimientoKardex[] = [];
  movimientosFiltrados: MovimientoKardex[] = [];
  productos: Producto[] = [];
  resumenProductos: any[] = [];
  
  // Filtros
  productoSeleccionadoId: number = 0;
  fechaInicio: string = '';
  fechaFin: string = '';
  tipoMovimiento: string = '';
  
  // Estado
  cargando: boolean = true;
  mostrarResumen: boolean = true;
  mostrarDetalle: boolean = false;
  movimientoSeleccionado: MovimientoKardex | null = null;
  
  // Paginación
  paginaActual: number = 1;
  itemsPorPagina: number = 15;
  totalPaginas: number = 1;
  totalRegistros: number = 0;
  Math = Math;

  tiposMovimiento = ['ENTRADA', 'SALIDA'];

  constructor(
    private kardexService: KardexService,
    private productoService: ProductoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: (err) => console.error('Error cargando productos:', err)
    });
    
    this.kardexService.getMovimientos().subscribe({
      next: (data) => {
        this.movimientos = data;
        this.movimientosFiltrados = [...data];
        this.calcularPaginacion();
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error:', error);
        this.cargarMovimientosEjemplo();
        this.cargando = false;
      }
    });
    
    this.kardexService.getResumenProductos().subscribe({
      next: (data) => {
        this.resumenProductos = data;
      },
      error: (err) => {
        console.error('Error:', err);
        this.cargarResumenEjemplo();
      }
    });
  }

  cargarMovimientosEjemplo(): void {
    this.movimientos = [
      {
        id: 1,
        fecha: '2025-01-15',
        productoId: 1,
        codigoProducto: 'PROD-001',
        nombreProducto: 'Producto Ejemplo 1',
        tipoMovimiento: 'ENTRADA',
        documentoReferencia: 'OC-001',
        cantidad: 50,
        precioUnitario: 100,
        subtotal: 5000,
        saldoAnterior: 0,
        saldoNuevo: 50,
        almacen: 'LIMA',
        usuario: 'Admin',
        observaciones: 'Compra inicial'
      }
    ];
    this.movimientosFiltrados = [...this.movimientos];
    this.calcularPaginacion();
  }

  cargarResumenEjemplo(): void {
    this.resumenProductos = [
      { productoId: 1, codigo: 'PROD-001', nombre: 'Producto Ejemplo 1', entradas: 50, salidas: 0, saldoActual: 50, valorizado: 5000 }
    ];
  }

  aplicarFiltros(): void {
    let filtrados = [...this.movimientos];
    
    if (this.productoSeleccionadoId !== 0) {
      filtrados = filtrados.filter(m => m.productoId === this.productoSeleccionadoId);
    }
    
    if (this.fechaInicio) {
      filtrados = filtrados.filter(m => m.fecha >= this.fechaInicio);
    }
    
    if (this.fechaFin) {
      filtrados = filtrados.filter(m => m.fecha <= this.fechaFin);
    }
    
    if (this.tipoMovimiento) {
      filtrados = filtrados.filter(m => m.tipoMovimiento === this.tipoMovimiento);
    }
    
    this.movimientosFiltrados = filtrados;
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  limpiarFiltros(): void {
    this.productoSeleccionadoId = 0;
    this.fechaInicio = '';
    this.fechaFin = '';
    this.tipoMovimiento = '';
    this.movimientosFiltrados = [...this.movimientos];
    this.paginaActual = 1;
    this.calcularPaginacion();
  }

  calcularPaginacion(): void {
    this.totalRegistros = this.movimientosFiltrados.length;
    this.totalPaginas = Math.ceil(this.totalRegistros / this.itemsPorPagina);
    if (this.paginaActual > this.totalPaginas) {
      this.paginaActual = Math.max(1, this.totalPaginas);
    }
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
  }

  getMovimientosPaginados(): MovimientoKardex[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.movimientosFiltrados.slice(inicio, inicio + this.itemsPorPagina);
  }

  get paginas(): number[] {
    const paginas: number[] = [];
    const maxPaginas = 5;
    let inicio = Math.max(1, this.paginaActual - Math.floor(maxPaginas / 2));
    let fin = Math.min(this.totalPaginas, inicio + maxPaginas - 1);
    
    if (fin - inicio + 1 < maxPaginas) {
      inicio = Math.max(1, fin - maxPaginas + 1);
    }
    
    for (let i = inicio; i <= fin; i++) {
      paginas.push(i);
    }
    return paginas;
  }

  getTipoMovimientoClass(tipo: string): string {
    return tipo === 'ENTRADA' ? 'badge-success' : 'badge-danger';
  }

  verDetalle(movimiento: MovimientoKardex): void {
    this.movimientoSeleccionado = movimiento;
    this.mostrarDetalle = true;
  }

  cerrarDetalle(): void {
    this.mostrarDetalle = false;
    this.movimientoSeleccionado = null;
  }

  calcularTotalValorizado(): number {
    return this.resumenProductos.reduce((sum, p) => sum + (p.valorizado || 0), 0);
  }

  exportarExcel(): void {
    const data = this.movimientosFiltrados.map(m => ({
      'Fecha': m.fecha,
      'Código': m.codigoProducto,
      'Producto': m.nombreProducto,
      'Tipo': m.tipoMovimiento,
      'Documento': m.documentoReferencia,
      'Cantidad': m.cantidad,
      'Precio Unitario': m.precioUnitario,
      'Subtotal': m.subtotal,
      'Saldo Anterior': m.saldoAnterior,
      'Saldo Nuevo': m.saldoNuevo,
      'Almacén': m.almacen,
      'Usuario': m.usuario
    }));

    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Kardex');
    XLSX.writeFile(wb, `kardex_${new Date().toISOString().split('T')[0]}.xlsx`);
    
    Swal.fire('Exportado', 'Archivo Excel generado correctamente', 'success');
  }

  exportarPDF(): void {
    const doc = new jsPDF('landscape');
    
    doc.setFontSize(16);
    doc.text('Reporte de Kardex', 14, 15);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString()}`, 14, 25);
    
    const tableColumn = ['Fecha', 'Código', 'Producto', 'Tipo', 'Cantidad', 'Precio', 'Subtotal', 'Saldo'];
    const tableRows = this.movimientosFiltrados.slice(0, 50).map(m => [
      m.fecha,
      m.codigoProducto,
      m.nombreProducto.substring(0, 20),
      m.tipoMovimiento,
      m.cantidad.toString(),
      `S/ ${m.precioUnitario.toFixed(2)}`,
      `S/ ${m.subtotal.toFixed(2)}`,
      m.saldoNuevo.toString()
    ]);
    
    (doc as any).autoTable({
      head: [tableColumn],
      body: tableRows,
      startY: 35,
      theme: 'striped',
      styles: { fontSize: 8 },
      headStyles: { fillColor: [102, 126, 234] }
    });
    
    doc.save(`kardex_${new Date().toISOString().split('T')[0]}.pdf`);
    Swal.fire('Exportado', 'Archivo PDF generado correctamente', 'success');
  }
}