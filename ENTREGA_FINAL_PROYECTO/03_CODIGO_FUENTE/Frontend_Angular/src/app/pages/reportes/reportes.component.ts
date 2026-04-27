import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import 'jspdf-autotable';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.scss']
})
export class ReportesComponent implements OnInit {
  // Datos de reportes
  reporteSolicitudes: any = null;
  reporteCompras: any = null;
  reporteInventario: any = null;
  
  // Filtros
  fechaInicio: string = '';
  fechaFin: string = '';
  
  // Estado
  pestanaActiva: string = 'solicitudes';
  cargando: boolean = false;

  ngOnInit(): void {
    this.cargarDatosEjemplo();
  }

  cargarDatosEjemplo(): void {
    // Datos de ejemplo para Solicitudes
    this.reporteSolicitudes = {
      total: 45,
      aprobadas: 28,
      rechazadas: 7,
      pendientes: 10,
      porMes: [
        { mes: 'Enero', cantidad: 12 },
        { mes: 'Febrero', cantidad: 15 },
        { mes: 'Marzo', cantidad: 18 }
      ]
    };

    // Datos de ejemplo para Compras
    this.reporteCompras = {
      totalCompras: 32,
      montoTotal: 156800,
      porProveedor: [
        { proveedor: '3M Perú', monto: 45000, cantidad: 8 },
        { proveedor: 'Ferreyros S.A.', monto: 68000, cantidad: 12 },
        { proveedor: 'Bosch Perú', monto: 43800, cantidad: 12 }
      ],
      porMes: [
        { mes: 'Enero', monto: 35000 },
        { mes: 'Febrero', monto: 42000 },
        { mes: 'Marzo', monto: 79800 }
      ]
    };

    // Datos de ejemplo para Inventario
    this.reporteInventario = {
      totalProductos: 156,
      valorTotal: 245800,
      productosBajoStock: [
        { producto: 'Casco de Seguridad', stock: 5, minimo: 10 },
        { producto: 'Guantes de Cuero', stock: 8, minimo: 15 },
        { producto: 'Lentes de Seguridad', stock: 12, minimo: 20 }
      ],
      movimientos: [
        { tipo: 'ENTRADA', cantidad: 450 },
        { tipo: 'SALIDA', cantidad: 320 }
      ]
    };
  }

  getPorcentajeAprobacion(): number {
    if (!this.reporteSolicitudes || this.reporteSolicitudes.total === 0) return 0;
    return (this.reporteSolicitudes.aprobadas / this.reporteSolicitudes.total) * 100;
  }

  obtenerTotalMovimientos(): number {
    if (!this.reporteInventario || !this.reporteInventario.movimientos) return 0;
    return this.reporteInventario.movimientos.reduce((sum: number, m: any) => sum + m.cantidad, 0);
  }

  // ============================================
  // EXPORTAR A EXCEL
  // ============================================

  exportarExcelSolicitudes(): void {
    const data = [
      { Indicador: 'Total Solicitudes', Valor: this.reporteSolicitudes.total },
      { Indicador: 'Aprobadas', Valor: this.reporteSolicitudes.aprobadas },
      { Indicador: 'Rechazadas', Valor: this.reporteSolicitudes.rechazadas },
      { Indicador: 'Pendientes', Valor: this.reporteSolicitudes.pendientes },
      { Indicador: 'Tasa de Aprobación', Valor: `${this.getPorcentajeAprobacion().toFixed(1)}%` }
    ];
    
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Reporte Solicitudes');
    XLSX.writeFile(wb, `reporte_solicitudes_${new Date().toISOString().split('T')[0]}.xlsx`);
    Swal.fire('Exportado', 'Archivo Excel generado correctamente', 'success');
  }

  exportarExcelCompras(): void {
    const data = [
      { Indicador: 'Total Compras', Valor: this.reporteCompras.totalCompras },
      { Indicador: 'Monto Total', Valor: `S/ ${this.reporteCompras.montoTotal.toFixed(2)}` }
    ];
    
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Reporte Compras');
    XLSX.writeFile(wb, `reporte_compras_${new Date().toISOString().split('T')[0]}.xlsx`);
    Swal.fire('Exportado', 'Archivo Excel generado correctamente', 'success');
  }

  exportarExcelInventario(): void {
    const data = [
      { Indicador: 'Total Productos', Valor: this.reporteInventario.totalProductos },
      { Indicador: 'Valor Total Inventario', Valor: `S/ ${this.reporteInventario.valorTotal.toFixed(2)}` }
    ];
    
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Reporte Inventario');
    XLSX.writeFile(wb, `reporte_inventario_${new Date().toISOString().split('T')[0]}.xlsx`);
    Swal.fire('Exportado', 'Archivo Excel generado correctamente', 'success');
  }

  // ============================================
  // EXPORTAR A PDF
  // ============================================

  exportarPDFSolicitudes(): void {
    const doc = new jsPDF();
    
    doc.setFontSize(18);
    doc.text('Reporte de Solicitudes', 14, 20);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString()}`, 14, 30);
    
    const data = [
      ['Total Solicitudes', this.reporteSolicitudes.total.toString()],
      ['Aprobadas', this.reporteSolicitudes.aprobadas.toString()],
      ['Rechazadas', this.reporteSolicitudes.rechazadas.toString()],
      ['Pendientes', this.reporteSolicitudes.pendientes.toString()],
      ['Tasa de Aprobación', `${this.getPorcentajeAprobacion().toFixed(1)}%`]
    ];
    
    (doc as any).autoTable({
      head: [['Indicador', 'Valor']],
      body: data,
      startY: 40,
      theme: 'striped',
      headStyles: { fillColor: [102, 126, 234] }
    });
    
    doc.save(`reporte_solicitudes_${new Date().toISOString().split('T')[0]}.pdf`);
    Swal.fire('Exportado', 'Archivo PDF generado correctamente', 'success');
  }

  exportarPDFCompras(): void {
    const doc = new jsPDF();
    
    doc.setFontSize(18);
    doc.text('Reporte de Compras', 14, 20);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString()}`, 14, 30);
    
    const data = [
      ['Total Compras', this.reporteCompras.totalCompras.toString()],
      ['Monto Total', `S/ ${this.reporteCompras.montoTotal.toFixed(2)}`]
    ];
    
    (doc as any).autoTable({
      head: [['Indicador', 'Valor']],
      body: data,
      startY: 40,
      theme: 'striped',
      headStyles: { fillColor: [102, 126, 234] }
    });
    
    doc.save(`reporte_compras_${new Date().toISOString().split('T')[0]}.pdf`);
    Swal.fire('Exportado', 'Archivo PDF generado correctamente', 'success');
  }

  exportarPDFInventario(): void {
    const doc = new jsPDF();
    
    doc.setFontSize(18);
    doc.text('Reporte de Inventario', 14, 20);
    doc.setFontSize(10);
    doc.text(`Generado: ${new Date().toLocaleString()}`, 14, 30);
    
    const data = [
      ['Total Productos', this.reporteInventario.totalProductos.toString()],
      ['Valor Total Inventario', `S/ ${this.reporteInventario.valorTotal.toFixed(2)}`]
    ];
    
    (doc as any).autoTable({
      head: [['Indicador', 'Valor']],
      body: data,
      startY: 40,
      theme: 'striped',
      headStyles: { fillColor: [102, 126, 234] }
    });
    
    doc.save(`reporte_inventario_${new Date().toISOString().split('T')[0]}.pdf`);
    Swal.fire('Exportado', 'Archivo PDF generado correctamente', 'success');
  }
}