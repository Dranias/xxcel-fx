package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.Distribuidores;
import xxcell.model.GeneraCodigos;
import xxcell.model.generaCodigoBarras;

public class DistribuidoresController implements Initializable {

    Conexion conn = new Conexion();
    Distribuidores distribuidor;
    boolean bandera = false;
    boolean banderaSQL;
    
    //Textfields
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextField txtApellido;
    @FXML
    private JFXTextField txtTelefono;
    @FXML
    private JFXTextField txtRFC;
    @FXML
    private JFXTextField txtRazonSocial;
    @FXML
    private JFXTextField txtDireccion;

    //Label
    @FXML
    private Label lblCodigo;
    @FXML
    private Label lblErrNombre;
    @FXML
    private Label lblErrApellido;
    @FXML
    private Label lblErrTelefono;
    @FXML
    private Label lblErrRFc;
    @FXML
    private Label lblErrRazonSocial;
    @FXML
    private Label lblErrDireccion;
    @FXML
    private Label lblErrAlta;
    @FXML
    private Label lblErrUltimaCompra;
    @FXML
    private Label lblErrStatus;
    
    //DatePicker
    @FXML
    private JFXDatePicker DateAlta;
    @FXML
    private JFXDatePicker dateUltimaCompra;
    LocalDate fecha;

    //CheckBox
    @FXML
    private JFXCheckBox cbFacturacion;
    
    //Botones
    @FXML
    private JFXButton btnAgregar;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnModificar;
    @FXML
    private JFXButton btnReiniciar;
    
    //ComboBox
    @FXML
    private JFXComboBox<String> cmbStatus;
    
    //Tabla Distribuidores
    
    @FXML
    private TableView<Distribuidores> tblDistribuidores;
    @FXML
    private TableColumn<Distribuidores, String> colNombre;
    @FXML
    private TableColumn<Distribuidores, String> colApellido;
    @FXML
    private TableColumn<Distribuidores, String> colAlta;
    @FXML
    private TableColumn<Distribuidores, String> colUltimaCompra;  
    @FXML
    private TableColumn<Distribuidores, String> colStatus;
    @FXML
    private TableColumn<Distribuidores, String> colCodigo;
    
    ObservableList<Distribuidores> OLdistribuidores = FXCollections.observableArrayList();
    
    //Función para llenar el ObservableList: OLdistribuidores.
    public ObservableList<Distribuidores> ObtenerDistribuidor() throws SQLException{
        String query = "SELECT * FROM distribuidores";
        String Nombre, Apellido, status, codigo, NombreCompleto;
        String FechaAlta = "", UltimaCompra = "";

        if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                Nombre = conn.setResult.getString("Nombre");
                Apellido = conn.setResult.getString("Apellido");
                FechaAlta = conn.setResult.getString("FechaAlta");
                UltimaCompra = conn.setResult.getString("UltimaCompra");
                status = conn.setResult.getString("Status");
                codigo = conn.setResult.getString("Codigo");
                if(!esnumero(codigo)){
                    GeneraCodigos generador = new GeneraCodigos();
                    String ID = generador.GeneraCodigoDistribuidor(codigo);
                    generador.ValidacionCadenaDist(codigo, ID);
                    System.out.println("Entro a Genmerar: " + ID + " -- Con: -- " + codigo);
                    codigo = ID;
                }
                NombreCompleto = Nombre + " " + Apellido + ".png";
                System.out.println("Nombre: " + NombreCompleto + " Codigo: " + codigo);
                GeneraCodigo(NombreCompleto, codigo);
                OLdistribuidores.add(new Distribuidores(Nombre, Apellido, FechaAlta, UltimaCompra, status, codigo));
            }
        }
        return OLdistribuidores;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Iniciar();
        //Obtener un distribuidor al dar dobleClick
        tblDistribuidores.setOnMousePressed(new EventHandler<MouseEvent>() {
             @Override 
             public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    distribuidor = tblDistribuidores.getSelectionModel().getSelectedItem();
                    llenado(distribuidor); 
                }
            }
        });
        
        //Llenado de Tabla
        colNombre.setCellValueFactory(cellData -> cellData.getValue().NombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().ApellidoProperty());
        colAlta.setCellValueFactory(cellData -> cellData.getValue().FechaAltaProperty());
        colUltimaCompra.setCellValueFactory(cellData -> cellData.getValue().UltimaCompraProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        try{
            tblDistribuidores.setItems(ObtenerDistribuidor());
        }catch (SQLException ex) {
            Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //CheckBox Facturacion
        cbFacturacion.setOnAction((ActionEvent e) -> {
            //Se habilitan
            if(bandera == false){
                HabilitarTextFields(bandera);
                bandera = true;
            }
            //Se desabilidan
            else{
                HabilitarTextFields(bandera);
                bandera = false;
                lblErrDireccion.setText("");
                lblErrRFc.setText("");
                lblErrRazonSocial.setText("");
            }
        });
        
        btnAgregar.setOnAction((ActionEvent e) -> {       
            //Validaciones
            banderaSQL = true;
//************************************  Validacion Nombre ***********************************
            if(txtNombre.getLength() == 0 || txtNombre.getLength() > 50){
                lblErrNombre.setText("No debe ser MAYOR a 50 o VACIO");
                banderaSQL = false;
            }else{
                lblErrNombre.setText("");
            }
//************************************  Validacion Apellido ***********************************
            if(txtApellido.getLength() == 0 || txtApellido.getLength() > 50){
                lblErrApellido.setText("No debe ser MAYOR a 50 o VACIO");
                banderaSQL = false;
            }else{
                lblErrApellido.setText("");
            }
//************************************  Validacion Telefono ***********************************
            if(txtTelefono.getLength() == 0 || txtTelefono.getLength() > 20){
                lblErrApellido.setText("No debe ser MAYOR a 20 o VACIO");
                banderaSQL = false;
            }else{
                lblErrTelefono.setText("");
            }
//************************************  Validacion FechaAlta ***********************************
            if(DateAlta.getValue() == null){
                lblErrAlta.setText("Seleccione una Fecha");
                banderaSQL = false;
            }else{
                lblErrAlta.setText("");
            }
//************************************  Validacion FechaAlta ***********************************
            if(dateUltimaCompra.getValue() == null){
                if(DateAlta.getValue() == null){
                    lblErrAlta.setText("Seleccione una Fecha");
                    banderaSQL = false;
                }
            }
//************************************  Validacion ComboBox Status ***********************************
            if(cmbStatus.getValue() == null){
                lblErrStatus.setText("Seleccione un Status");
                banderaSQL = false;
            }
            else{
                lblErrStatus.setText("");
            }
            
            //Si El CheckBox esta habilitado entrará a hacer las demas validaciones
            if(bandera){
//************************************  Validacion RFC ***********************************
                if(txtRFC.getLength() == 0 || txtRFC.getLength() > 30){
                    lblErrRFc.setText("No debe ser MAYOR a 30 o VACIO");
                    banderaSQL = false;
                }else{
                    lblErrRFc.setText("");
                }
//************************************  Validacion Razon Social ***********************************
                if(txtRazonSocial.getLength() == 0){
                    lblErrRazonSocial.setText("No debe ser VACIO");
                    banderaSQL = false;
                }else{
                    lblErrRazonSocial.setText("");
                }
//************************************  Validacion Razon Social ***********************************
                if(txtDireccion.getLength() == 0){
                    lblErrDireccion.setText("No debe ser VACIO");
                    banderaSQL = false;
                }else{
                    lblErrDireccion.setText("");
                }
            }
            if(banderaSQL == true){
                try {
                    AgregarSQL(bandera);
                    try{
                        tblDistribuidores.refresh();
                        OLdistribuidores.removeAll(OLdistribuidores);
                        tblDistribuidores.setItems(ObtenerDistribuidor());
                    }catch (SQLException ex) {
                        Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    reinicio();
                } catch (SQLException ex) {
                    Logger.getLogger(DistribuidoresController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }); 
        
        btnEliminar.setOnAction((ActionEvent e) -> {   
            if(tblDistribuidores.getSelectionModel().isEmpty()){
                String mensaje = "No se ha seleccionado ningun Distribuidor. \n";
                Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                incompleteAlert.setTitle("Distribuidores");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnEliminar.getScene().getWindow());
                incompleteAlert.showAndWait();
            } 
            else{
                EliminarEmpleado();
                try{
                    tblDistribuidores.refresh();
                    OLdistribuidores.removeAll(OLdistribuidores);
                    tblDistribuidores.setItems(ObtenerDistribuidor());
                }catch (SQLException ex) {
                    Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                }
                reinicio();
            }
        }); 
        
        btnModificar.setOnAction((ActionEvent e) -> {       
            if(tblDistribuidores.getSelectionModel().isEmpty()){
                String mensaje = "No se ha seleccionado ningun Distribuidor. \n";
                Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                incompleteAlert.setTitle("Distribuidores");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnEliminar.getScene().getWindow());
                incompleteAlert.showAndWait();
            } 
            else{
                try {
                    ModificarDistribuidor(bandera);
                } catch (SQLException ex) {
                    Logger.getLogger(DistribuidoresController.class.getName()).log(Level.SEVERE, null, ex);
                }
                try{
                    tblDistribuidores.refresh();
                    OLdistribuidores.removeAll(OLdistribuidores);
                    tblDistribuidores.setItems(ObtenerDistribuidor());
                }catch (SQLException ex) {
                    Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                }
                reinicio();
            }
        });   
        
        btnReiniciar.setOnAction((ActionEvent e) -> {       
            reinicio();
        });
    }

    //Cuando el CheckButton "Facturación" se presione, 
    //habilitará o Desabilitara los textfields para los datos de Facturacion
    public void HabilitarTextFields(boolean flag){
        txtRFC.setDisable(flag);
        txtRazonSocial.setDisable(flag);
        txtDireccion.setDisable(flag);
    }
    
    //Iniciará los ComboBox e Iniciará los Textfields de facturación
    public void Iniciar(){
        txtRFC.setDisable(true);
        txtRazonSocial.setDisable(true);
        txtDireccion.setDisable(true);
        ObservableList<String> options = FXCollections.observableArrayList("Activo","Inactivo");
       cmbStatus.getItems().addAll(options);
    }
    
    //Función para Agregar a la Base de datos
    public void AgregarSQL(boolean flag) throws SQLException{
        String codigo, FechaAlta, FechaUltimaCompra;
        String Query;
        LocalDate fechaAlta, fechaUltimacompra;
        Alert succesAlert = new Alert(Alert.AlertType.INFORMATION);
        
        codigo = generarCodigo();
        System.out.println("Codigo: " + codigo);
        
        fechaAlta = DateAlta.getValue();
        FechaAlta = fechaAlta.toString();
        
        if(dateUltimaCompra.getValue() == null){
            fechaUltimacompra = DateAlta.getValue();
            FechaUltimaCompra = fechaUltimacompra.toString();
        }
        else{
            fechaUltimacompra = dateUltimaCompra.getValue();
            FechaUltimaCompra = fechaUltimacompra.toString();
        }
       
        if(flag){
            Query = "INSERT INTO distribuidores (RFC, RazonSocial, Direccion, Nombre, ";
            Query += "Apellido, Telefono, FechaAlta, UltimaCompra, Status, Codigo) ";
            Query += "VALUES (?,?,?,?,?,?,?,?,?,?)";
            conn.preparedStatement(Query);
            conn.stmt.setString(1, txtRFC.getText());
            conn.stmt.setString(2, txtRazonSocial.getText());
            conn.stmt.setString(3, txtDireccion.getText());
            conn.stmt.setString(4, txtNombre.getText());
            conn.stmt.setString(5, txtApellido.getText());
            conn.stmt.setString(6, txtTelefono.getText());
            conn.stmt.setString(7, FechaAlta);
            conn.stmt.setString(8, FechaUltimaCompra);
            conn.stmt.setString(9, cmbStatus.getValue());
            conn.stmt.setString(10, codigo);  
        }
        else{
            Query = "INSERT INTO distribuidores (Nombre, Apellido, Telefono, ";
            Query += "FechaAlta, UltimaCompra, Status, Codigo) ";
            Query += "VALUES (?,?,?,?,?,?,?)";
            conn.preparedStatement(Query);
            conn.stmt.setString(1, txtNombre.getText());
            conn.stmt.setString(2, txtApellido.getText());
            conn.stmt.setString(3, txtTelefono.getText());
            conn.stmt.setString(4, FechaAlta);
            conn.stmt.setString(5, FechaUltimaCompra);
            conn.stmt.setString(6, cmbStatus.getValue());
            conn.stmt.setString(7, codigo);  
        }
        conn.stmt.executeUpdate();
        conn.Commit();
        succesAlert.setTitle("Alta satisfactoria");
        succesAlert.setHeaderText(null);
        succesAlert.initOwner(btnAgregar.getScene().getWindow());
        succesAlert.setContentText("Distribuidor registrado correctamente");
        succesAlert.showAndWait();
    }
    
    public void ModificarDistribuidor(boolean flag) throws SQLException{
        String codigo, FechaAlta, FechaUltimaCompra;
        String Query;
        LocalDate fechaAlta, fechaUltimacompra;
        Alert succesAlert = new Alert(Alert.AlertType.INFORMATION);
        
        codigo = generarCodigo();
        System.out.println("Codigo: " + codigo);
        
        fechaAlta = DateAlta.getValue();
        FechaAlta = fechaAlta.toString();
        
        if(dateUltimaCompra.getValue() == null){
            fechaUltimacompra = DateAlta.getValue();
            FechaUltimaCompra = fechaUltimacompra.toString();
        }
        else{
            fechaUltimacompra = dateUltimaCompra.getValue();
            FechaUltimaCompra = fechaUltimacompra.toString();
        }
       
        if(flag){
            Query = "UPDATE distribuidores SET RFC=?, RazonSocial=?, Direccion=?, Nombre=?, ";
            Query += "Apellido=?, Telefono=?, FechaAlta=?, UltimaCompra=?, Status=?, Codigo=? ";
            Query += "WHERE Codigo = '"+distribuidor.getCodigo()+"'";
            conn.preparedStatement(Query);
            conn.stmt.setString(1, txtRFC.getText());
            conn.stmt.setString(2, txtRazonSocial.getText());
            conn.stmt.setString(3, txtDireccion.getText());
            conn.stmt.setString(4, txtNombre.getText());
            conn.stmt.setString(5, txtApellido.getText());
            conn.stmt.setString(6, txtTelefono.getText());
            conn.stmt.setString(7, FechaAlta);
            conn.stmt.setString(8, FechaUltimaCompra);
            conn.stmt.setString(9, cmbStatus.getValue());
            conn.stmt.setString(10, codigo);  
        }
        else{
            Query = "UPDATE distribuidores SET Nombre=?, Apellido=?, Telefono=?, ";
            Query += "FechaAlta=?, UltimaCompra=?, Status=?, Codigo=? ";
            Query += "WHERE Codigo = '"+distribuidor.getCodigo()+"'";
            conn.preparedStatement(Query);
            conn.stmt.setString(1, txtNombre.getText());
            conn.stmt.setString(2, txtApellido.getText());
            conn.stmt.setString(3, txtTelefono.getText());
            conn.stmt.setString(4, FechaAlta);
            conn.stmt.setString(5, FechaUltimaCompra);
            conn.stmt.setString(6, cmbStatus.getValue());
            conn.stmt.setString(7, codigo);  
        }
        conn.stmt.executeUpdate();
        conn.Commit();
        succesAlert.setTitle("Modificacion Satisfactoria");
        succesAlert.setHeaderText(null);
        succesAlert.initOwner(btnAgregar.getScene().getWindow());
        succesAlert.setContentText("Distribuidor Modificado correctamente");
        succesAlert.showAndWait();
    }
    
    public void EliminarEmpleado(){
        String Query = "DELETE FROM distribuidores WHERE Codigo = '"+distribuidor.getCodigo()+"'";
        if(conn.QueryUpdate(Query))
        {
            String mensaje = "Distribuidor Eliminado \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Eliminar Distribuidor");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnEliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
        else
        {
            String mensaje = "Distribuidor no se ha podido eliminar \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Eliminar Distribuidor");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnEliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
        
    }
    
    public String generarCodigo() throws SQLException{
        String Codigo = "", fechaAlta;
        int identificador=0;
        LocalDate FechaAlta;
        String Query = "Select * from distribuidores";
        
        if(conn.QueryExecute(Query)){
            if(!conn.setResult.next()){
                identificador = 1;
            }
            else{
                while(conn.setResult.next()){
                    identificador = conn.setResult.getInt("claveUnica");
                    break;
                }
            }
            System.out.println("Identificador: " + identificador);
        }
        
        FechaAlta = DateAlta.getValue();
        fechaAlta = FechaAlta.toString();
        Codigo = fechaAlta.substring(2, 4);
        Codigo += txtNombre.getText().substring(0, 2);
        Codigo += fechaAlta.substring(5, 7);
        Codigo += txtApellido.getText().substring(0, 2);
        Codigo += fechaAlta.substring(8, 10);
        Codigo += txtTelefono.getText().substring(0, 3);
        System.out.println("Identificador : " + String.valueOf(identificador));
        Codigo += String.valueOf(identificador);
        
        return Codigo;
    }
    
    public void llenado(Distribuidores dist){
        Date Alta, UltimaCompra;
        LocalDate FAlta, FUltima;
        String Query = "Select * FROM distribuidores WHERE Codigo = '"+dist.getCodigo()+"'";
        conn.QueryExecute(Query);
        try{
            if(conn.setResult.first()) {
                txtNombre.setText(conn.setResult.getString("Nombre"));
                txtApellido.setText(conn.setResult.getString("Apellido"));
                txtTelefono.setText(conn.setResult.getString("Telefono"));
                if(conn.setResult.getString("RFC")!= null){
                    cbFacturacion.setSelected(true);
                    HabilitarTextFields(false);
                    bandera = true;
                    txtRFC.setText(conn.setResult.getString("RFC"));
                    txtRazonSocial.setText(conn.setResult.getString("Razonsocial"));
                    txtDireccion.setText(conn.setResult.getString("Direccion")); 
                }
                else{
                    cbFacturacion.setSelected(false);
                    HabilitarTextFields(true);
                    bandera = false;
                    txtRFC.clear();
                    txtRazonSocial.clear();
                    txtDireccion.clear();
                }
                if(conn.setResult.getString("Status").equals("Activo"))
                    cmbStatus.setValue("Activo");
                else
                    cmbStatus.setValue("Inactivo");
                //Conversiones de Date a LocalDate
                Alta = conn.setResult.getDate("FechaAlta");
                FAlta = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(Alta));
                DateAlta.setValue(FAlta);
                UltimaCompra = conn.setResult.getDate("UltimaCompra");
                FUltima = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(UltimaCompra));
                dateUltimaCompra.setValue(FUltima);
                lblCodigo.setText(conn.setResult.getString("Codigo"));
            }
        } catch (SQLException e) {
                
                //No pondré nada aquí, me piro.
        }    
    }
    
    public void reinicio(){
        txtApellido.clear();
        txtDireccion.clear();
        txtNombre.clear();
        txtRFC.clear();
        txtRazonSocial.clear();
        txtTelefono.clear();
        lblCodigo.setText("");
        lblErrAlta.setText("");
        lblErrApellido.setText("");
        lblErrDireccion.setText("");
        lblErrNombre.setText("");
        lblErrRFc.setText("");
        lblErrRazonSocial.setText("");
        lblErrStatus.setText("");
        lblErrTelefono.setText("");
        cmbStatus.getSelectionModel().clearSelection();
        DateAlta.setValue(null);
        dateUltimaCompra.setValue(null);
    }
    
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
    
    public void GeneraCodigo (String Nombre, String Codigo){
        generaCodigoBarras codigoBarras = new generaCodigoBarras();
        codigoBarras.createBarCode128(Nombre, Codigo);
    }    
}
