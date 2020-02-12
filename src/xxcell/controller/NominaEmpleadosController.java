package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.Detalles;
import xxcell.model.Empleados;

public class NominaEmpleadosController implements Initializable {

    //Varibales para las conexiones y los querys
    Conexion conn = new Conexion();

    float TotalVenta, totalvendido = 0;
    
    Date fechaHoy = new Date();
    
    Empleados aux = new Empleados();
    int cantidadproductos = 0;
    
    //Buttons
    @FXML
    private JFXButton ObtenerSalario;
    
    //textfield
    @FXML
    private JFXTextField txtExtra;
    @FXML
    private JFXTextField txtDescuento;
    
    // Labels
    @FXML
    private Label lblCantidad;
    @FXML
    private Label lblPorcentaje;
    @FXML
    private Label NombreEmpleado;
    @FXML
    private Label NumeroEmpleado;
    @FXML
    private Label lblSueldo;
    @FXML
    private Label lblFechaFin;
    @FXML
    private Label lblError;
    
    //CheckBox
    @FXML
    private JFXCheckBox Lunes;
    @FXML
    private JFXCheckBox Martes;
    @FXML
    private JFXCheckBox Miercoles;
    @FXML
    private JFXCheckBox Jueves;
    @FXML
    private JFXCheckBox Viernes;
    @FXML
    private JFXCheckBox Sabado;
    @FXML
    private JFXCheckBox Domingo;
    
    //DatePickers
    @FXML
    private JFXDatePicker FechaInicial;
    @FXML
    private JFXDatePicker FechaFinal;
    
    
    @FXML
    private TableView<Detalles> TablaVentas;
    @FXML
    private TableColumn<Detalles, Number> Folio;
    @FXML
    private TableColumn<Detalles, String> colCodigo;
    @FXML
    private TableColumn<Detalles, Number> Cantidad;
    @FXML
    private TableColumn<Detalles, Number> Total;
    
    //Lista para Llenar la tabla de Detalles;
    ObservableList<Detalles> detalles = FXCollections.observableArrayList();
    
    //Variables de TableView para la Tabla Empleados
    @FXML
    private TableView<Empleados> TablaEmpleados;
    @FXML
    private TableColumn<Empleados, String> Nombre;
    @FXML
    private TableColumn<Empleados, String> Apellido;  
    
    //Lista para Llenar la tabla de Empleados;
    ObservableList<Empleados> empleados = FXCollections.observableArrayList();
    
     //Función que obtendrá los objetos empleados para ingresarlos al tableview
    public ObservableList<Empleados> ObtenerEmpleados() throws SQLException{
        String qry = "SELECT usuario, Nombre, Apellido, NumEmpleado, Nivel FROM empleado";
        String nom, apel, usuar;
        int numEmp;
                
        if(conn.QueryExecute(qry))
        {
            while(conn.setResult.next())
            {
                usuar = conn.setResult.getString("usuario");
                nom = conn.setResult.getString("Nombre");
                apel = conn.setResult.getString("Apellido");
                numEmp = conn.setResult.getInt("NumEmpleado");
                if(conn.setResult.getInt("Nivel") != 0){
                    empleados.add(new Empleados(usuar, nom, apel, numEmp, conn.setResult.getInt("Nivel")));
                }
            }
        }
        return empleados;
    }
    
    
    //Función que obtendrá los objetos detalle de Ventas para ingresarlos al tableview
    public ObservableList<Detalles> ObtenerDetalles() throws SQLException{
        String codigo, cadena, STSQL, TooltipText;
        int folio, cantidad, producto;
        LocalDate FechaInit, FinalFecha;
        FechaInit = FechaInicial.getValue();
        FinalFecha = FechaFinal.getValue();
        float total;
       
        STSQL = "Select * from tblventas "
                + "INNER JOIN tblventadetalle "
                    + "ON tblventadetalle.ventaFolio = tblventas.ventaFolio "
                + "INNER JOIN productos "
                    + "ON tblventadetalle.productoCodigo = productos.ID "
                + "WHERE tblventas.NumEmpleado = "+aux.getNumEmp()+" AND tblventas.ventaFecha BETWEEN '"+ FechaInit +"' and '"+FinalFecha+"' "
                + "AND tblventadetalle.local = tblventas.NumLocal";
        
        System.out.println(STSQL);
        
        if(conn.QueryExecute(STSQL))
        {
            while (conn.setResult.next())
            {
                if(conn.setResult.getString("CodigoDistribuidor") != null){
                    System.out.println(conn.setResult.getInt("ventaCantidad"));
                }
                if(conn.setResult.getString("CodigoDistribuidor") == null){
                    TooltipText = conn.setResult.getString("Marca");
                    TooltipText += " " + conn.setResult.getString("Modelo");
                    TooltipText += " " + conn.setResult.getString("Tipo");
                    TooltipText += " " + conn.setResult.getString("Identificador");
                    folio = conn.setResult.getInt("ventaFolio");
                    codigo = conn.setResult.getString("ID");
                    cantidad = conn.setResult.getInt("ventaCantidad");
                    total = conn.setResult.getFloat("productoPrecio");
                    //cantidadproductos = cantidadproductos + cantidad;
                    //totalvendido = totalvendido + total;
                    cadena = aux.getNombre();
                    cadena += " " + aux.getApellido();
                    TotalVenta = totalvendido;
                    NombreEmpleado.setText(cadena);
                    NumeroEmpleado.setText(String.valueOf(aux.getNumEmp()));
                    detalles.add(new Detalles(folio, codigo, cantidad, total, TooltipText));
                }
            } 
        }  
        
        STSQL = "SELECT * FROM tblventas WHERE tblventas.NumEmpleado = "+aux.getNumEmp()+" AND tblventas.ventaFecha BETWEEN '"+ FechaInit +"' and '"+FinalFecha+"'";
        conn.QueryExecute(STSQL);
        while(conn.setResult.next()){
            if(conn.setResult.getString("CodigoDistribuidor") == null){
                cantidadproductos = cantidadproductos + conn.setResult.getInt("ventaProductos");
                totalvendido = totalvendido + conn.setResult.getFloat("ventaImporte");
            }
        }
        lblCantidad.setText(String.valueOf(cantidadproductos));
        return detalles;
    }
    
    public void Salario() throws SQLException{
        int cantidadMeta, faltas;
        float descuentofaltas = 0, sueldo = 0, txtdescuento=0, txtaumento = 0;
        boolean flag = true;
        String mensaje = "";
        
        if(FechaFinal.getValue() == null && FechaFinal.getValue() == null){
            flag = false;
            mensaje += "¡Error! Seleccione Fechas  \n";

        }
        if(TablaEmpleados.getSelectionModel().isEmpty()){
            flag = false;
            mensaje += "¡Error! Seleccione un empleado\n ";
        }
        if(!flag){
            Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
            incompleteAlert.setTitle("Nomimas");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(ObtenerSalario.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
        if(flag){
            String qrySueldo = "SELECT * FROM sueldos where Nivel = '"+aux.getNivel()+"'";
            conn.QueryExecute(qrySueldo);
            if(conn.setResult.first()){
                faltas = obtenerFaltas();
                if(txtDescuento.getText().length() == 0){
                    txtdescuento = 0;
                }
                else{
                    txtdescuento = Float.valueOf(txtDescuento.getText());
                }
                
                if(txtExtra.getText().length() == 0){
                    txtaumento = 0;
                }
                else{
                    txtaumento = Float.valueOf(txtExtra.getText());
                }
                
                if(faltas>1){
                    faltas--;
                    descuentofaltas = faltas * conn.setResult.getFloat("DescuentoFalta");
                }
                if(faltas == 0){
                    sueldo += conn.setResult.getFloat("DiaExtra");
                }
                cantidadMeta = conn.setResult.getInt("NumVentas");
                if(cantidadproductos >= cantidadMeta){
                    lblPorcentaje.setText(".04%");
                    sueldo = (totalvendido * conn.setResult.getFloat("ComisionMeta")) + sueldo;
                }
                else{
                    lblPorcentaje.setText(".02%");               
                    sueldo = (totalvendido * conn.setResult.getFloat("ComisionBase"))+sueldo;
                }
                sueldo = sueldo + conn.setResult.getFloat("Base");
                sueldo = sueldo - descuentofaltas;
                sueldo = sueldo + txtaumento;
                sueldo = sueldo - txtdescuento;
                lblSueldo.setText(Float.toString(sueldo));
            } 
        }
        
    }
    
    private int obtenerFaltas() {
        int faltas=0;
        if(Lunes.selectedProperty().get() == true){
            faltas++;
        }
        if(Martes.selectedProperty().get() == true){
            faltas++;
        }
        if(Miercoles.selectedProperty().get() == true){
            faltas++;
        }
        if(Jueves.selectedProperty().get() == true){
            faltas++;
        }
        if(Viernes.selectedProperty().get() == true){
            faltas++;
        }
        if(Sabado.selectedProperty().get() == true){
            faltas++;
        }
        if(Domingo.selectedProperty().get() == true){
            faltas++;
        }
        return faltas;
    }
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        lblFechaFin.setText(formato.format(fechaHoy));

        Nombre.setCellValueFactory(cellData -> cellData.getValue().NombreProperty());
        Apellido.setCellValueFactory(cellData -> cellData.getValue().ApellidoProperty());
        try {
            TablaEmpleados.setItems(ObtenerEmpleados());
        } catch (SQLException ex) {
            Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Folio.setCellValueFactory(cellData -> cellData.getValue().FolioProperty());
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        Cantidad.setCellValueFactory(cellData -> cellData.getValue().CantidadProperty());
        Total.setCellValueFactory(cellData -> cellData.getValue().TotalProperty());
        
        Tooltip tooltip = new Tooltip();
        TablaVentas.setRowFactory(tv -> new TableRow<Detalles>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            public void updateItem(Detalles detalle, boolean empty) {
                super.updateItem(detalle, empty);
                if (detalle == null) {
                    setTooltip(null);
                } else {
                    tooltip.setText(detalle.getTooltipText());
                    setTooltip(tooltip);
                }
            }
        });
        
        //Seleccionar un objeto con doble click
        TablaEmpleados.setOnMousePressed(new EventHandler<MouseEvent>() {
             @Override 
             public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    if(TablaEmpleados.getSelectionModel().getSelectedItem() != null ){
                        aux = TablaEmpleados.getSelectionModel().getSelectedItem();
                        try {
                            if(FechaInicial.getValue() == null && FechaFinal.getValue() == null){
                                lblError.setText("Seleccione las fechas");
                            }
                            else{
                                lblError.setText("");
                                TablaVentas.refresh();
                                detalles.removeAll(detalles);
                                cantidadproductos = 0;
                                TotalVenta = 0;
                                totalvendido = 0;
                                TablaVentas.setItems(ObtenerDetalles());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        
        ObtenerSalario.setOnAction((ActionEvent e) -> {
            boolean ban = true;
            String error = "";
            if(!esnumero(txtDescuento.getText())){
                ban = false;
                error = "El Descuento debe ser Numero";
            }
            if(!esnumero(txtExtra.getText())){
                ban = false;
                error = "El pago Extra debe ser Numero";
            }
            if(ban){
                try {
                    Salario();
                } catch (SQLException ex) {
                    Logger.getLogger(NominaEmpleadosController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
                incompleteAlert.setTitle("Nomina");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(error);
                incompleteAlert.initOwner(ObtenerSalario.getScene().getWindow());
                incompleteAlert.showAndWait();
            }
        });
        
    }//FIN INITIALIZE      
    
    boolean esnumero (String x)
    {  
        double number = 0;
        if(x.isEmpty())
        {
            return true;
        }
        else
        {
            try {
                number = Double.parseDouble(x);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }
}
