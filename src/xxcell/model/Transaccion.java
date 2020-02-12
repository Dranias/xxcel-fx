package xxcell.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaccion {
    StringProperty Codigo;
    IntegerProperty Cantidad;
    StringProperty Procedencia;
    StringProperty Destino;
    StringProperty Fecha;
    StringProperty Notas;
    IntegerProperty Folio;
    StringProperty Producto;
   
    public Transaccion(){
       
    } 
   
    public Transaccion(String Codigo, int Cantidad, String Procedencia, String Destino, String Fecha, String Notas, int Folio){
        this.Codigo = new SimpleStringProperty(Codigo);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Procedencia = new SimpleStringProperty(Procedencia);
        this.Destino = new SimpleStringProperty(Destino);
        this.Fecha = new SimpleStringProperty(Fecha);
        this.Notas = new SimpleStringProperty(Notas); 
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Producto = new SimpleStringProperty("");
    }
    
    public Transaccion(String Codigo, int Cantidad, String Procedencia, String Destino, String Fecha, String Notas, int Folio, String Producto){
        this.Codigo = new SimpleStringProperty(Codigo);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Procedencia = new SimpleStringProperty(Procedencia);
        this.Destino = new SimpleStringProperty(Destino);
        this.Fecha = new SimpleStringProperty(Fecha);
        this.Notas = new SimpleStringProperty(Notas); 
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Producto = new SimpleStringProperty(Producto);
    }
   
   //Funciones Property para la Tabla
    public StringProperty CodigoProperty(){
        return Codigo;
    }
    public IntegerProperty CantidadProperty(){
        return Cantidad;
    }
    public StringProperty ProcedenciaProperty(){
        return Procedencia;
    }
    public StringProperty DestinoProperty(){
        return Destino;
    }
    public StringProperty FechaProperty(){
        return Fecha;
    }
    public StringProperty NotasProperty(){
        return Notas;
    }
    public IntegerProperty FolioProperty(){
        return Folio;
    }
    //Get's
    public String getDestino(){
        return Destino.get();
    }
    public int getFolio(){
        return Folio.get();
    }
    public String getProducto()
    {
        return Producto.get();
    }
    
    public String getCodigo()
    {
        return Codigo.get();
    }

}