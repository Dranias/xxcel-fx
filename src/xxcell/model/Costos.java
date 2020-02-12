package xxcell.model;

import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Costos {
    private StringProperty Codigo;
    private StringProperty Producto;
    private DoubleProperty Costo;
    private StringProperty Fecha;
    
    public Costos(String Codigo, String Nombre, double Costo, String Fecha){
        this.Codigo = new SimpleStringProperty(Codigo);
        this.Producto = new SimpleStringProperty(Nombre);
        this.Costo = new SimpleDoubleProperty(Costo);
        this.Fecha = new SimpleStringProperty(Fecha);
    }
    
    public StringProperty CodigoProperty(){
        return Codigo;
    }
    public StringProperty NombreProperty(){
        return Producto;
    }
    public DoubleProperty CostoProperty(){
        return Costo;
    }
    public StringProperty FechaProrperty(){
        return Fecha;
    }
}
