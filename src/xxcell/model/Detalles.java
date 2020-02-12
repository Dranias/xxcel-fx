package xxcell.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Detalles {
    private IntegerProperty Folio;
    private StringProperty Codigo;
    private StringProperty NumEmpleado;
    private IntegerProperty Cantidad;
    private FloatProperty Total;
    private StringProperty TooltipText;
    private StringProperty Hora;
    private FloatProperty Utilidad;
    private StringProperty Distribuidor;
    
    public Detalles(){
        
    }
    
    //Este constructo es utilizado para EstadisticasUtilidad
    public Detalles(int Folio, int Cantidad, float Total, float Utilidad){
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Total = new SimpleFloatProperty(Total);
        this.Utilidad = new SimpleFloatProperty(Utilidad);
    }
    
    public Detalles(int Folio, String Codigo, String NumEmpleado, int Cantidad, float Total, String TooltipText, String hora, String Distribuidor){
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.NumEmpleado = new SimpleStringProperty(NumEmpleado);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Total = new SimpleFloatProperty(Total);
        this.TooltipText = new SimpleStringProperty(TooltipText);
        this.Hora = new SimpleStringProperty(hora);
        this.Distribuidor = new SimpleStringProperty(Distribuidor);
    }
    
    public Detalles(int Folio, String Codigo, String NumEmpleado, int Cantidad, float Total, String TooltipText, String hora){
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.NumEmpleado = new SimpleStringProperty(NumEmpleado);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Total = new SimpleFloatProperty(Total);
        this.TooltipText = new SimpleStringProperty(TooltipText);
        this.Hora = new SimpleStringProperty(hora);
    }
    
    public Detalles(int Folio, String Codigo, int Cantidad, float Total, String TooltipText){
        this.Folio = new SimpleIntegerProperty(Folio);
        this.Codigo = new SimpleStringProperty(Codigo);
        this.Cantidad = new SimpleIntegerProperty(Cantidad);
        this.Total = new SimpleFloatProperty(Total);
        this.NumEmpleado = new SimpleStringProperty(null);
        this.TooltipText = new SimpleStringProperty(TooltipText);
    }

    public void setHora(StringProperty Hora) {
        this.Hora = Hora;
    }

    public StringProperty getHora() {
        return Hora;
    }
    
    public FloatProperty UtilidadProperty(){
        return Utilidad;
    }
    
    public IntegerProperty FolioProperty(){
        return Folio;
    }
    
    public StringProperty CodigoProperty(){
        return Codigo;
    }
    
    public StringProperty NumEmpleadoProperty(){
        return NumEmpleado;
    }
    
    public StringProperty ToolTipProperty(){
        return TooltipText;
    }
    
    public IntegerProperty CantidadProperty(){
        return Cantidad;
    }
    
    public FloatProperty TotalProperty(){
        return Total;
    }
    
    public String getTooltipText(){
        return TooltipText.get();
    }

    public int getFolio() {
        return Folio.get();
    }

    public StringProperty getCodigo() {
        return Codigo;
    }

    public StringProperty getNumEmpleado() {
        return NumEmpleado;
    }
  
    public String getEmpleado() {
        return NumEmpleado.get();
    }

    public int getCantidad() {
        return Cantidad.get();
    }

    public FloatProperty getTotal() {
        return Total;
    }
    
    public String codigo(){
        return Codigo.get();
    }
    
    public float total(){
        return Total.get();
    }
    
    public StringProperty DistribuidorProperty(){
        return Distribuidor;
    }
}
