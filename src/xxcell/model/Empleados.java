package xxcell.model;

import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Empleados {
    
    private StringProperty Usuario;
    private StringProperty Contra;
    private StringProperty Nombre;
    private StringProperty Apellido;
    private StringProperty Direccion;
    private StringProperty Telefono;
    private IntegerProperty NumEmp; //Numero de empleado
    private IntegerProperty Nivel;
    private Date FechaAlta;
    private StringProperty Referencia;
    private StringProperty Faltas;
    
    public Empleados(){    
    }
    
    public Empleados(String Usuario, String Nombre, String Apellido, int NumEmpleado, int Nivel){
        this.Usuario = new SimpleStringProperty(Usuario);
        this.Nombre = new SimpleStringProperty(Nombre);
        this.Apellido = new SimpleStringProperty(Apellido);
        this.NumEmp = new SimpleIntegerProperty(NumEmpleado);
        this.Nivel = new SimpleIntegerProperty(Nivel);
    }
    
    public Empleados(String Usuario, String Contra, String Nombre, String Apellido, String Direccion, String Telefono, int NumEmp, int Nivel, Date FechaAlta, String Referencia){
        this.Usuario = new SimpleStringProperty(Usuario);
        this.Contra = new SimpleStringProperty(Contra);
        this.Nombre = new SimpleStringProperty(Nombre);
        this.Apellido = new SimpleStringProperty(Apellido);
        this.Direccion = new SimpleStringProperty(Direccion);
        this.Telefono = new SimpleStringProperty(Telefono);
        this.NumEmp = new SimpleIntegerProperty(NumEmp);
        this.Nivel = new SimpleIntegerProperty(Nivel);
        this.FechaAlta = FechaAlta;
        this.Referencia = new SimpleStringProperty(Referencia);
    }
    
    //FUNCIONES PARA USURIO
      //***FUNCIÓN PARA QUE EL TABLEVIEW RECIBA EL NOMBRE DE USUARIO***//
    public StringProperty UsuarioProperty()
    {
        return Usuario;
    }
    public String getUsuario(){
        return Usuario.get();
    }
    
    //FUNCIONES PARA LA CONTRASEÑA
    public String getContra(){
        return Contra.get();
    }
    
    //FUNCIONES PARA EL NOMBRE DEL EMPLEADO
    public StringProperty NombreProperty()
    {
        return Nombre;
    }
    public String getNombre(){
        return Nombre.get();
    }
    
    //FUNCIONES PARA EL APELLIDO DEL EMPLEADO
    public StringProperty ApellidoProperty()
    {
        return Apellido;
    }
    public String getApellido(){
        return Apellido.get();
    }
    
    //FUNCIONES PARA LA DIRECCION DEL EMPLEADO
    public String getDireccion(){
        return Direccion.get();
    }
    
    //FUNCIONES PARA EL TELEOFNO DEL EMPLEADO
    public String getTelefono(){
        return Telefono.get();
    }
    
    //FUNCIONES PARA EL NUMERO DE EMPLEADO
    public IntegerProperty NumEmpProperty()
    {
        return NumEmp;
    }
    public int getNumEmp(){
        return NumEmp.get();
    }
    
    //FUNCIONES PARA LA FECHA DE CONTRATACION
    
    
    //FUNCIONES PARA LAS REFERENCIAS DEL EMPLEADO
    public String getReferencia(){
        return Referencia.get();
    }
    
    //FUNCIONES PARA NIVEL DE USUARIO
    public IntegerProperty NivelProperty(){
        return Nivel;
    }
    public int getNivel(){
        return Nivel.get();
    }
}
