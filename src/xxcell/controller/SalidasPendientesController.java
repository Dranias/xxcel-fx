package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
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
import xxcell.model.Salidas;

public class SalidasPendientesController implements Initializable {

    Conexion conn = new Conexion();
    
    Salidas aux = new Salidas();
    
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    
    //Variables para la Tabla de prestamos, estos serán Editables para modificar su contendido desde la tabla
    @FXML
    private TableView<Salidas> tblSalidas;
    @FXML
    private TableColumn<Salidas, String> colCodigo;
    @FXML
    private TableColumn<Salidas, String> colProcedencia;
    @FXML
    private TableColumn<Salidas, String> colFecha;
    @FXML
    private TableColumn<Salidas, String> colNotas;
    @FXML
    private TableColumn<Salidas, String> colNumEmpleado;

    //Textfields
    @FXML
    private JFXTextField txtNumEmpleado;
    @FXML
    private JFXTextField txtCodigo;
    
    
    @FXML
    private JFXTextArea txtNotas;
    
    //Botones
    @FXML
    private JFXButton btnAutorizar;
    @FXML
    private JFXButton btnRegistrarSalida;
    @FXML
    private JFXButton btnSalir;

    @FXML
    private JFXComboBox<String> cmbLocalDestino;
    
    //Labels de Erorr
    @FXML
    private Label lblErrCodigo;
    @FXML
    private Label lblErrNumEmpleado;
    @FXML
    private Label lblErrCmbLocalDestino;
    @FXML
    private Label lblErrNotas;
    
    private Alert alert;
        
    //Funcion para llenar los Prestamos
    public ObservableList<Salidas> ObtenerSalidasPendientes() throws SQLException{
        String query = "SELECT * FROM salidas JOIN productos ON salidas.Codigo = productos.ID";
        String codigo, procedencia, producto, notas, numEmpleado;
        int folio;
        Timestamp fecha;
        //Lista para Llenar la tabla de Prestamos si es que hay;
        ObservableList<Salidas> salidas = FXCollections.observableArrayList();
        Date fechaSalida;
        
        if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                codigo = conn.setResult.getString("Codigo");
                procedencia = conn.setResult.getString("Local");
                numEmpleado = conn.setResult.getString("NumEmpleado");
                fecha = conn.setResult.getTimestamp("Fecha");
                notas = conn.setResult.getString("Motivo");
                folio = conn.setResult.getInt("Folio");
                Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                fechaSalida = conn.setResult.getTimestamp("Fecha");
                producto = conn.setResult.getString("Marca") + " " + conn.setResult.getString("Modelo")
                        +" " + conn.setResult.getString("Tipo")+ " " + conn.setResult.getString("Identificador");
                String fechaFormat;
                fechaFormat = formatter.format(fecha);
                salidas.add(new Salidas(codigo, procedencia, numEmpleado, fechaFormat, notas, folio, fechaSalida, producto));
            }
            conn.setResult.close();
        }
        return salidas;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        Reinicio();
        if(!Variables_Globales.Rol.equals("0")){
            btnAutorizar.setVisible(false);
            btnAutorizar.setDisable(true);
        }
        //Inicio de las columnas
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().getCodigo());
        colProcedencia.setCellValueFactory(cellData -> cellData.getValue().getProcedencia());
        colNumEmpleado.setCellValueFactory(cellData -> cellData.getValue().getNumEmpleado());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().getFecha());
        colNotas.setCellValueFactory(cellData -> cellData.getValue().getNotas());
        try {
            tblSalidas.setItems(ObtenerSalidasPendientes());
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tblSalidas.setRowFactory(tv -> new TableRow<Salidas>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            public void updateItem(Salidas Prod, boolean empty) {
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
        
        //Seleccionar un objeto con doble click
        tblSalidas.setOnMousePressed(new EventHandler<MouseEvent>() {
             @Override 
             public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown()) {
                    if(tblSalidas.getSelectionModel().getSelectedItem() != null ){
                        aux = tblSalidas.getSelectionModel().getSelectedItem();
                        btnAutorizar.setDisable(false);
                        System.out.println(aux.folio());
                    }
                }
            }
        });
        
        btnRegistrarSalida.setOnAction((ActionEvent e) -> {   
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
            
            
            query = "Select * FROM empleado WHERE NumEmpleado = '"+txtNumEmpleado.getText()+"'";
            conn.QueryExecute(query);
            try {
                if(!conn.setResult.first()){
                    flag = false;
                    lblErrNumEmpleado.setText("Numero de usuario no valido");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SalidasPendientesController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                    RegistrarSalidaPendiente();
                } catch (SQLException ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Transacciones");
//                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
                    alert.setContentText("TRANSACCION NO PUDO SER REALIZADA");
                    alert.initOwner(btnSalir.getScene().getWindow());
                    alert.showAndWait();
                }
            } else System.out.println("Aqui hay error");
            try {
                conn.setResult.close();
            } catch (SQLException ex) {
                Logger.getLogger(SalidasPendientesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        
        btnSalir.setOnAction((ActionEvent e) -> {       
            Stage stage;
            stage = (Stage) btnSalir.getScene().getWindow();
            stage.close();
        }); 
        
        btnAutorizar.setOnAction((ActionEvent e) -> {
            try {
                RegistraSalida();
            } catch (SQLException ex) {
                Logger.getLogger(SalidasPendientesController.class.getName()).log(Level.SEVERE, null, ex);
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
        
    public void RegistrarSalidaPendiente() throws SQLException{
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query, fecha;
        fecha = formato.format(fechaHoy);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Salidas");
//        if(!Local.equals("Otros")){
//            query = "UPDATE productos set "+Variables_Globales.local+" = "+Variables_Globales.local+" - "+txtCantidad.getText()+", ";
//            query += ""+cmbLocalDestino.getValue()+" = "+cmbLocalDestino.getValue()+" + "+txtCantidad.getText();
//            query += " WHERE ID = '"+txtCodigo.getText()+"'";
//            if(conn.QueryUpdate(query)){
//                query =  "INSERT into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas)";
//                query += "Values ('"+txtCodigo.getText()+"', '"+txtCantidad.getText()+"', '"+Variables_Globales.local+"', '"+cmbLocalDestino.getValue()+"', '"+fecha+"', '"+txtNotas.getText()+"')";
//                if(conn.QueryUpdate(query)){
//                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
//                    alert.setContentText("TRANSACCION REALIZADA CON EXITO");
//                    alert.initOwner(btnSalir.getScene().getWindow());
//                    alert.showAndWait();
//                }
//                else{
//                    alert.setHeaderText("Transacción de "+Variables_Globales.local+" A "+cmbLocalDestino.getValue());
//                    alert.setContentText("TRANSACCION NO PUDO SER REALIZADA");
//                    alert.initOwner(btnSalir.getScene().getWindow());
//                    alert.showAndWait();
//                    conn.RollBack();
//                }
//            }
            query = "Insert into salidas (Codigo, Local, NumEmpleado, Motivo, Fecha) ";
            query += "values (?,?,?,?,?)";
            conn.preparedStatement(query);
            conn.stmt.setString(1, txtCodigo.getText());
            conn.stmt.setString(2, Variables_Globales.local);
            conn.stmt.setString(3, txtNumEmpleado.getText());
            conn.stmt.setString(4, txtNotas.getText());
            conn.stmt.setString(5, fecha);
            conn.stmt.executeUpdate();
            conn.Commit();
            alert.setHeaderText(null);
            alert.setContentText("Salida registrada con exito");
            alert.initOwner(btnSalir.getScene().getWindow());
            alert.showAndWait();
        tblSalidas.refresh();
        tblSalidas.setItems(ObtenerSalidasPendientes());
        Reinicio();
    }
    
    private void RegistraSalida() throws SQLException{
        String sqlStmt;
        String pass;
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Confirmacion");
        alert.initOwner(btnSalir.getScene().getWindow());

        conn.AutoCommit(false);

        if(ProductoActualiza(true))
            if(FolioIncrementa())
                if(InventarioRegistra(true)){
                    conn.Commit();
                    EliminarSalidaPendiente();
                    alert.setHeaderText(null);
                    alert.setContentText("Salida autorizada con exito");
                    alert.showAndWait();
                    Reinicio();
                } else
                    conn.RollBack();
            else
                conn.RollBack();
        else
            conn.RollBack();
    }

    private boolean ProductoActualiza(boolean b) {
        boolean result = false;
        String sqlStmt;
        String local = aux.procedencia();
        String codigo = aux.codigo();
        sqlStmt = "Update productos set ";
        sqlStmt += "Salidas = Salidas + 1 ,";
        sqlStmt += local+" = "+local+" - 1 ,";
        sqlStmt += "CantidadActual = CantidadActual - 1";
        sqlStmt += " Where ID"
                + " = '"+codigo+"'";
        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;
    }

    private boolean FolioIncrementa() {
        boolean result = false;
        String sqlStmt;


        sqlStmt = "Update tblfolios set folioinventario = folioinventario + '1'";
        if(conn.QueryUpdate(sqlStmt))
                result = true;
        return result;
    }

    private boolean InventarioRegistra(boolean b) throws SQLException {
        Date fechaHoy = aux.getFechaSalida();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        boolean result = false;
        String sqlStmt;
        String codigo = aux.codigo();
        String notas = "Numero de empleado: "+aux.numEmpleado()+" - "+aux.notas();

        sqlStmt = "Insert into tblinventario (invfolio,invmovimiento,invfecha,invcantidad,productocodigo,ventafolio,invdescripcion) ";
        sqlStmt += " Values ("+getFolio()+",";
        sqlStmt += "'Salida',";
        sqlStmt += "'"+formato.format(fechaHoy)+"',";
        sqlStmt += "1,";
        sqlStmt += "'"+codigo+"',0,";
        sqlStmt += "'"+notas+" ')";

        System.out.println(sqlStmt);
            
        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;
    }
    
    private int getFolio() throws SQLException{
        int result = 0;
        String query;
        
        query = "Select folioinventario from tblfolios";
        conn.QueryExecute(query);

        try{
            if(conn.setResult.next()){
                result = conn.setResult.getInt("folioinventario");
            }
        } catch(SQLException e){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(btnSalir.getScene().getWindow());
            alert.setTitle("Punto de Venta - Error al obtener folio");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        conn.setResult.close();
        return result;		
    }
    
    public void EliminarSalidaPendiente() throws SQLException{
        String query = "DELETE FROM salidas where Folio = "+aux.folio()+"";
        System.out.println(query);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Transacciones");
        
        if(conn.QueryUpdate(query)){
        }
        else{
            alert.setHeaderText("Transacción:");
            alert.setContentText(" ¡No pudo ser Cancelado! Verifique conexión con la base de datos");
            alert.initOwner(btnSalir.getScene().getWindow());
            alert.showAndWait();
            conn.RollBack();
        }
        tblSalidas.refresh();
        tblSalidas.setItems(ObtenerSalidasPendientes());
    }
    
    public void Reinicio(){
        txtCodigo.clear();
        txtNumEmpleado.clear();
        txtNotas.clear();
        btnAutorizar.setDisable(true);
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}
