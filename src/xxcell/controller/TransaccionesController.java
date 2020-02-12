package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.Transaccion;


public class TransaccionesController implements Initializable {

    Conexion conn = new Conexion();
    
    Transaccion aux = new Transaccion();
    
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    
    //TDate Picker
    @FXML
    private JFXDatePicker DateInicio;
    
    //Variables para la Tabla de prestamos, estos serán Editables para modificar su contendido desde la tabla
    @FXML
    private TableView<Transaccion> tblPrestamos;
    @FXML
    private TableColumn<Transaccion, String> colCodigo;
    @FXML
    private TableColumn<Transaccion, Number> colCantidad;
    @FXML
    private TableColumn<Transaccion, String> colProcedencia;
    @FXML
    private TableColumn<Transaccion, String> colDestino;
    @FXML
    private TableColumn<Transaccion, String> colFecha;
    @FXML
    private TableColumn<Transaccion, String> colNotas;

    //Textfields
    @FXML
    private JFXTextField txtCantidad;
    @FXML
    private JFXTextField txtNotas;
    @FXML
    private JFXTextField txtCodigo;
    
    //Botones
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnTransaccion;
    @FXML
    private JFXButton btnSalir;

    @FXML
    private JFXComboBox<String> cmbLocalDestino;
    
    //Labels de Erorr
    @FXML
    private Label lblErrCodigo;
    @FXML
    private Label lblErrCantidad;
    @FXML
    private Label lblErrCmbLocalDestino;
    @FXML
    private Label lblErrNotas;
        
    //Funcion para llenar los Prestamos
    public ObservableList<Transaccion> ObenerPrestamo(boolean datePickerflag) throws SQLException{
        String query = "SELECT * FROM prestamos ";
        String codigo, procedencia, destino, notas, producto;
        int cantidad, folio;
        Timestamp fecha;
        LocalDate datePicker;
        
        if(datePickerflag){
            datePicker = DateInicio.getValue();
            query += "WHERE FechaHora > '"+datePicker+"' ";
        }else{
            query += "WHERE FechaHora > '"+today+"' ";
        }
        query += "AND FechaHora < '"+tomorrow+"' AND Procedencia = '"+Variables_Globales.local+"'";
        //Lista para Llenar la tabla de Prestamos si es que hay;
        ObservableList<Transaccion> prestamos = FXCollections.observableArrayList();
        
        if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                codigo = conn.setResult.getString("Codigo");
                cantidad = conn.setResult.getInt("Cantidad");
                procedencia = conn.setResult.getString("Procedencia");
                destino = conn.setResult.getString("Destino");
                fecha = conn.setResult.getTimestamp("FechaHora");
                notas = conn.setResult.getString("Notas");
                folio = conn.setResult.getInt("Folio");
                Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
               
                String s;
                s = formatter.format(fecha);
                prestamos.add(new Transaccion(codigo, cantidad, procedencia, destino, s, notas, folio));
            }
        }
        return prestamos;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        DateInicio.setOnAction((ActionEvent e) -> {
            try {
                tblPrestamos.refresh();
                tblPrestamos.setItems(ObenerPrestamo(true));
            } catch (SQLException ex) {
                Logger.getLogger(TransaccionesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        iniciarComboBox();
        btnCancelar.setDisable(true);
        //Inicio de las columnas
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().CantidadProperty());
        colProcedencia.setCellValueFactory(cellData -> cellData.getValue().ProcedenciaProperty());
        colDestino.setCellValueFactory(cellData -> cellData.getValue().DestinoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().FechaProperty());
        colNotas.setCellValueFactory(cellData -> cellData.getValue().NotasProperty());
        try {
            tblPrestamos.setItems(ObenerPrestamo(false));
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tblPrestamos.setRowFactory(tv -> new TableRow<Transaccion>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            public void updateItem(Transaccion Prod, boolean empty) {
                super.updateItem(Prod, empty);
                if (Prod == null) {
                    setTooltip(null);
                } else {
                    if(Prod.getProducto().length() > 0){
                        tooltip.setText(Prod.getProducto());
                        setTooltip(tooltip);
                    }
                }
            }
        });
        
        btnTransaccion.setOnAction((ActionEvent e) -> {   
            boolean flag = true, ExisteCodigo = false;
            String query, Producto="";
            int cantidadLocal=-1;
//**************************************** Validación de Código ******************************************** 
            query = "SELECT * FROM productos WHERE ID = '"+txtCodigo.getText()+"'";
            conn.QueryExecute(query);
            try {
                if(!conn.setResult.first()){
                    lblErrCodigo.setText("Producto no encontrado");
                    flag = false;
                }
                else{
                    cantidadLocal = conn.setResult.getInt(Variables_Globales.local);
                    Producto = conn.setResult.getString("Marca");
                    Producto += conn.setResult.getString("Modelo");
                    Producto += conn.setResult.getString("Tipo");
                    Producto += conn.setResult.getString("Identificador");
                    ExisteCodigo = true;
                    lblErrCodigo.setText("");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TransaccionesController.class.getName()).log(Level.SEVERE, null, ex);
            }
//**************************************** Validación de CANTIDAD ******************************************** 
            //Si se válido el textfield Codigo
            if(ExisteCodigo){
                if(cantidadLocal < Integer.valueOf(txtCantidad.getText())){
                    lblErrCantidad.setText("No cuenta con suficientes "+Producto);
                    flag = false;
                }else{
                    lblErrCantidad.setText("");
                }
            }
            else{
                lblErrCantidad.setText("Ingrese un Código Válido");
                flag = false;
            }
//**************************************** Validación de ComboBox ********************************************      
            if(cmbLocalDestino.getValue() == null){
                lblErrCmbLocalDestino.setText("Seleccione un Destino de Transferencia");
                flag = false;
            }else
                lblErrCmbLocalDestino.setText("");
//**************************************** Validación de Notas ********************************************
            if(txtNotas.getText().length() == 0){
                lblErrNotas.setText("Ingrese referencias con respecto a la Transacción");
                flag = false;
            }
            else{
                lblErrNotas.setText("");
            }
//**************************************** Entra al query ********************************************            
            if(flag){
                try {
                    Transefir(cmbLocalDestino.getValue());
                } catch (SQLException ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Transacciones");
                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
                    alert.setContentText("TRANSACCION NO PUDO SER REALIZADA");
                    alert.initOwner(btnTransaccion.getScene().getWindow());
                    alert.showAndWait();
                }
            }
        }); 
        
        btnSalir.setOnAction((ActionEvent e) -> {       
            Stage stage;
            stage = (Stage) btnSalir.getScene().getWindow();
            stage.close();
        }); 
        
        btnCancelar.setOnAction((ActionEvent e) -> {
            try {
                EliminarPedido();
            } catch (SQLException ex) {
                Logger.getLogger(TransaccionesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });    
    } //Fin Init
    
    @FXML
    void MPressedTxtCodProducto(MouseEvent event) throws IOException {
        
        if(event.getClickCount() == 2){
            Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
        Stage principalStage = new Stage();
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(btnSalir.getScene().getWindow());
        principalStage.showAndWait(); 
        if(Variables_Globales.BusquedaVenta.getID() != null){
            txtCodigo.setText(Variables_Globales.BusquedaVenta.getID());
            txtCodigo.requestFocus();
            }
        }
    }
    
    public void iniciarComboBox()
    {
        //inicia el combobox Nivel
        if(Variables_Globales.local.equals("L58")){
            ObservableList<String> options = FXCollections.observableArrayList("L64","L127", "Otros");
            cmbLocalDestino.getItems().addAll(options);
        }
        if(Variables_Globales.local.equals("L64")){
            ObservableList<String> options = FXCollections.observableArrayList("L58","L127", "Otros");
            cmbLocalDestino.getItems().addAll(options);
        }
        if(Variables_Globales.local.equals("L127")){
            ObservableList<String> options = FXCollections.observableArrayList("L58","L64", "Otros");
            cmbLocalDestino.getItems().addAll(options);
        }
    }
    
    public void Transefir(String Local) throws SQLException{
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query, fecha;
        fecha = formato.format(fechaHoy);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Transacciones");
        if(!Local.equals("Otros")){
            query = "UPDATE productos set "+Variables_Globales.local+" = "+Variables_Globales.local+" - "+txtCantidad.getText()+", ";
            query += ""+cmbLocalDestino.getValue()+" = "+cmbLocalDestino.getValue()+" + "+txtCantidad.getText();
            query += " WHERE ID = '"+txtCodigo.getText()+"'";
            if(conn.QueryUpdate(query)){
                query =  "INSERT into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas)";
                query += "Values ('"+txtCodigo.getText()+"', '"+txtCantidad.getText()+"', '"+Variables_Globales.local+"', '"+cmbLocalDestino.getValue()+"', '"+fecha+"', '"+txtNotas.getText()+"')";
                if(conn.QueryUpdate(query)){
                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
                    alert.setContentText("TRANSACCION REALIZADA CON EXITO");
                    alert.initOwner(btnTransaccion.getScene().getWindow());
                    alert.showAndWait();
                }
                else{
                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
                    alert.setContentText("TRANSACCION NO PUDO SER REALIZADA");
                    alert.initOwner(btnTransaccion.getScene().getWindow());
                    alert.showAndWait();
                    conn.RollBack();
                }
            }
        }else{
            query = "Insert into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas) ";
            query += "values (?,?,?,?,?,?)";
            conn.preparedStatement(query);
            conn.stmt.setString(1, txtCodigo.getText());
            conn.stmt.setInt(2, Integer.valueOf(txtCantidad.getText()));
            conn.stmt.setString(3, Variables_Globales.local);
            conn.stmt.setString(4, cmbLocalDestino.getValue());
            conn.stmt.setString(5, fecha);
            conn.stmt.setString(6, txtNotas.getText());
            conn.stmt.executeUpdate();
            conn.Commit();
            alert.setHeaderText("Transacción de "+Variables_Globales.local+" A Otros");
            alert.setContentText("TRANSACCION REALIZADA CON EXITO");
            alert.initOwner(btnTransaccion.getScene().getWindow());
            alert.showAndWait();
        }
        tblPrestamos.refresh();
        tblPrestamos.setItems(ObenerPrestamo(false));
    }
    
    public void EliminarPedido() throws SQLException{
        String query = "DELETE FROM prestamos where Folio = '"+aux.getFolio()+"'";
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Transacciones");
        
        if(conn.QueryUpdate(query)){
            alert.setHeaderText("Prestamo: ");
            alert.setContentText("¡Cancelado!");
            alert.initOwner(btnTransaccion.getScene().getWindow());
            alert.showAndWait();
        }
        else{
            alert.setHeaderText("Transacción:");
            alert.setContentText(" ¡No pudo ser Cancelado! Verifique conexión con la base de datos");
            alert.initOwner(btnTransaccion.getScene().getWindow());
            alert.showAndWait();
            conn.RollBack();
        }
        tblPrestamos.refresh();
        tblPrestamos.setItems(ObenerPrestamo(false));
    }
    
    public void Reinicio(){
        txtCodigo.clear();
        txtCantidad.clear();
        txtNotas.clear();
        cmbLocalDestino.getSelectionModel().clearSelection();
        cmbLocalDestino.getItems().clear();
    }
}
