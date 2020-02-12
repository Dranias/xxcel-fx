package xxcell.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Distribuidores {
    
    private StringProperty Nombre;
    private StringProperty Apellido;
    private StringProperty Telefono;
    private StringProperty FechaAlta;
    private StringProperty UltimaCompra;
    private StringProperty Codigo;
    private StringProperty RFC;
    private StringProperty RazonSocial;
    private StringProperty Direccion;
    private StringProperty status;
    
    public Distribuidores(){
        this(null,null,null,null,null,null,null,null,null,null);
    }
      
    public Distribuidores(String _codigo, String _nombre, String _apellido, String _status){
        this.Codigo = new SimpleStringProperty(_codigo);
        this.Nombre = new SimpleStringProperty(_nombre);
        this.Apellido = new SimpleStringProperty(_apellido);
        this.status = new SimpleStringProperty(_status);
    }
      
    //Constructor para las Tablas Distribuidores
    public Distribuidores(String Nombre, String Apellido, String FechaAlta, String UltimaCompra, String Status, String Codigo){
        this.Nombre = new SimpleStringProperty(Nombre);
        this.Apellido = new SimpleStringProperty(Apellido);
        this.Telefono = new SimpleStringProperty(null);
        this.FechaAlta = new SimpleStringProperty(FechaAlta);
        this.UltimaCompra = new SimpleStringProperty(UltimaCompra);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.RFC = new SimpleStringProperty(null);
        this.RazonSocial = new SimpleStringProperty(null);
        this.Direccion = new SimpleStringProperty(null);
        this.status = new SimpleStringProperty(Status);
    }
    
    //Constructor si el Distribuidor no factura
    public Distribuidores(String Nombre, String Apellido, String Telefono, String FechaAlta, String UltimaCompra, String Codigo, String Status){
        this.Nombre = new SimpleStringProperty(Nombre);
        this.Apellido = new SimpleStringProperty(Apellido);
        this.Telefono = new SimpleStringProperty(Telefono);
        this.FechaAlta = new SimpleStringProperty(FechaAlta);
        this.UltimaCompra = new SimpleStringProperty(UltimaCompra);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.RFC = new SimpleStringProperty(null);
        this.RazonSocial = new SimpleStringProperty(null);
        this.Direccion = new SimpleStringProperty(null);
        this.status = new SimpleStringProperty(Status);
    }
    
    //Contructor si el Distribuidor da datos para Facturar
    public Distribuidores(String Nombre, String Apellido, String Telefono, String FechaAlta, String UltimaCompra, String Codigo, String RFC, String RazonSocial, String Direccion, String Status){
        this.Nombre = new SimpleStringProperty(Nombre);
        this.Apellido = new SimpleStringProperty(Apellido);
        this.Telefono = new SimpleStringProperty(Telefono);
        this.FechaAlta = new SimpleStringProperty(FechaAlta);
        this.UltimaCompra = new SimpleStringProperty(UltimaCompra);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.RFC = new SimpleStringProperty(RFC);
        this.RazonSocial = new SimpleStringProperty(RazonSocial);
        this.Direccion = new SimpleStringProperty(Direccion);
        this.status = new SimpleStringProperty(Status);
    }
    //Funciones Para el Nombre
    public StringProperty NombreProperty(){
        return Nombre;
    }
    public String getNombre(){
        return Nombre.get();
    }
    
    //Funciones Apellido
    public StringProperty ApellidoProperty(){
        return Apellido;
    }
    public String getApellido(){
        return Apellido.get();
    }
    
    //Funciones Telefono
    public String getTelefono(){
        return Telefono.get();
    }
    
    //Funciones FechaAlta
    public StringProperty FechaAltaProperty(){
        return FechaAlta;
    }
    public String getFechaAlta(){
        return FechaAlta.get();
    }
    
    //Funciones UltimaCompra
    public StringProperty UltimaCompraProperty(){
        return UltimaCompra;
    }
    public String getUltimaCompra(){
        return UltimaCompra.get();
    }
    
    //Funciones Codigo
    public StringProperty CodigoProperty(){
        return Codigo;
    }
    public String getCodigo(){
        return Codigo.get();
    }
    
    //Funciones RazonSocial
    public String getRazonSocial(){
        return RazonSocial.get();
    }
    
    //Funciones Direccion
    public String getDireccion(){
        return Direccion.get();
    }
    
    //Funciones Status
    public StringProperty statusProperty(){
        return status;
    }
    public String getStatus(){
        return status.get();
    }
    
}
