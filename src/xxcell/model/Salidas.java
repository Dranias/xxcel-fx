/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author snak0
 */
public class Salidas {
    
    StringProperty Codigo;
    StringProperty Procedencia;
    StringProperty NumEmpleado;
    StringProperty Fecha;
    StringProperty Notas;
    StringProperty Producto;
    IntegerProperty Folio;
    IntegerProperty Cantidad;
    Date FechaSalida;

    public Salidas(String codigo, String procedencia, String numEmpleado, String fechaFormat, String notas, int folio, Date fechaSalida, String Producto) {
        this.Codigo = new SimpleStringProperty(codigo);
        this.Procedencia = new SimpleStringProperty(procedencia);
        this.NumEmpleado = new SimpleStringProperty(numEmpleado);
        this.Fecha = new SimpleStringProperty(fechaFormat);
        this.Notas = new SimpleStringProperty(notas);
        this.Folio = new SimpleIntegerProperty(folio);
        this.FechaSalida = fechaSalida;
        this.Producto = new SimpleStringProperty(Producto);
    }
    
    public Salidas(int folio, int Cantidad, String Notas, String Fecha) {
        this.Folio = new SimpleIntegerProperty(folio);
        this.Fecha = new SimpleStringProperty(Fecha);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Notas = new SimpleStringProperty(Notas);
    }

    public Salidas() {
       
    }

    public void setCodigo(StringProperty Codigo) {
        this.Codigo = Codigo;
    }

    public void setProcedencia(StringProperty Procedencia) {
        this.Procedencia = Procedencia;
    }

    public void setNumEmpleado(StringProperty NumEmpleado) {
        this.NumEmpleado = NumEmpleado;
    }

    public void setFecha(StringProperty Fecha) {
        this.Fecha = Fecha;
    }

    public void setNotas(StringProperty Notas) {
        this.Notas = Notas;
    }

    public void setFolio(IntegerProperty Folio) {
        this.Folio = Folio;
    }
    
    public StringProperty getCodigo() {
        return Codigo;
    }

    public StringProperty getProcedencia() {
        return Procedencia;
    }

    public StringProperty getNumEmpleado() {
        return NumEmpleado;
    }

    public StringProperty getFecha() {
        return Fecha;
    }

    public StringProperty getNotas() {
        return Notas;
    }

    public IntegerProperty getFolio() {
        return Folio;
    }

    public IntegerProperty getCantidad() {
        return Cantidad;
    }
    
    public Date getFechaSalida() {
        return FechaSalida;
    }
    
    public String codigo(){
        return Codigo.get();
    }
    
    public String procedencia(){
        return Procedencia.get();
    }
    
    public String numEmpleado(){
        return NumEmpleado.get();
    }
    
    public String notas(){
        return Notas.get();
    }
    
    public int folio(){
        return Folio.get();
    }
    
    public String getProducto()
    {
        return Producto.get();
    }
}
