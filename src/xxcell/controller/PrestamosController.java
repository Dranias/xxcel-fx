package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;
import xxcell.model.Transaccion;

public class PrestamosController implements Initializable {

    Conexion conn = new Conexion();
    
    Transaccion aux = new Transaccion();
    
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    
    @FXML
    private TableView<Transaccion> tblPrestamos;
    @FXML
    private TableColumn<Transaccion, String> colCodigo;
    @FXML
    private TableColumn<Transaccion, String> colProcedencia;
    @FXML
    private TableColumn<Transaccion, String> colDestino;
    @FXML
    private TableColumn<Transaccion, String> colFecha;
    @FXML
    private TableColumn<Transaccion, String> colNotas;
    
    @FXML
    private JFXTextField txtNotas;
    @FXML
    private JFXTextField txtCodigo;
    
    @FXML
    private DatePicker DateInicio;
    
    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXButton btnSalir;
    @FXML
    private JFXButton btnBuscar;
    @FXML
    private JFXButton btncopiar;
    
    @FXML
    private Label lblErrCodigo;
    
    public ObservableList<Transaccion> ObenerPrestamo(boolean datePickerflag) throws SQLException{
        String query = "SELECT * FROM prestamos JOIN productos ON prestamos.Codigo = productos.ID ";
                query += "AND Procedencia = '"+Variables_Globales.local+"' ";
        String codigo, procedencia, destino, notas, producto;
        int cantidad, folio;
        Timestamp fecha;
        LocalDate datePicker;
        
        if(datePickerflag){
            datePicker = DateInicio.getValue();
            query += "WHERE prestamos.FechaHora > '"+datePicker+"' ";
            query += "AND prestamos.FechaHora < '"+datePicker.plusDays(1)+"' ";
        }else{
            query += "WHERE prestamos.FechaHora > '"+today+"' ";
            query += "AND prestamos.FechaHora < '"+tomorrow+"' ";
        }
        //query+= "OR Destino = '"+Variables_Globales.localPublico+"' ";
        query += "ORDER BY prestamos.FechaHora";
        System.out.println(query);
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
                producto = conn.setResult.getString("Marca") + " " + conn.setResult.getString("Modelo")
                        +" " + conn.setResult.getString("Tipo")+ " " + conn.setResult.getString("Identificador");
                Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                String s;
                s = formatter.format(fecha);
                prestamos.add(new Transaccion(codigo, cantidad, procedencia, destino, s, notas, folio, producto));
            }
        }
        
        //Llenar registro de Llegadas
        query = "SELECT * FROM prestamos JOIN productos ON prestamos.Codigo = productos.ID ";
        query+= "AND Destino = '"+Variables_Globales.localPublico+"' ";
        if(datePickerflag){
            datePicker = DateInicio.getValue();
            query += "WHERE prestamos.FechaHora > '"+datePicker+"' ";
            query += "AND prestamos.FechaHora < '"+datePicker.plusDays(1)+"' ";
        }else{
            query += "WHERE prestamos.FechaHora > '"+today+"' ";
            query += "AND prestamos.FechaHora < '"+tomorrow+"' ";
        }
        //query+= "OR Destino = '"+Variables_Globales.localPublico+"' ";
        query += "ORDER BY prestamos.FechaHora";
        System.out.println(query);
        if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                codigo = conn.setResult.getString("Codigo");
                cantidad = conn.setResult.getInt("Cantidad");
                procedencia = conn.setResult.getString("Procedencia");
                destino = conn.setResult.getString("Destino");
                fecha = conn.setResult.getTimestamp("FechaHora");
                notas = conn.setResult.getString("Notas");
                folio = conn.setResult.getInt("Folio");
                producto = conn.setResult.getString("Marca") + " " + conn.setResult.getString("Modelo")
                        +" " + conn.setResult.getString("Tipo")+ " " + conn.setResult.getString("Identificador");
                Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                String s;
                s = formatter.format(fecha);
                prestamos.add(new Transaccion(codigo, cantidad, procedencia, destino, s, notas, folio, producto));
            }
        }
        conn.setResult.close();
        return prestamos;
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        Platform.runLater(() -> {
            
            btncopiar.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), new Runnable() {
            @Override
            public void run() {
                btncopiar.fire();
                System.out.println("Imprime CÃ³digo");
            }
        });
            
        });
        
        if(!Variables_Globales.Rol.equals("0")){
            btnCancelar.setDisable(true);
        }else{
            btnCancelar.setDisable(false);
        }
        
        
        
        tblPrestamos.refresh();
        try {
            tblPrestamos.setItems(ObenerPrestamo(false));
        } catch (SQLException ex) {
            Logger.getLogger(PrestamosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Inicio de las columnas
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        colProcedencia.setCellValueFactory(cellData -> cellData.getValue().ProcedenciaProperty());
        colDestino.setCellValueFactory(cellData -> cellData.getValue().DestinoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().FechaProperty());
        colNotas.setCellValueFactory(cellData -> cellData.getValue().NotasProperty());
        
        //Función: ToolTip para mostrar la descripción de producto
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
        
        btnBuscar.setOnAction((ActionEvent e) -> {
            try {
                tblPrestamos.refresh();
                ObservableList<Transaccion> prestamos = FXCollections.observableArrayList();
                tblPrestamos.setItems(prestamos);
                tblPrestamos.setItems(ObenerPrestamo(true));
            } catch (SQLException ex) {
                Logger.getLogger(TransaccionesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    
    
    @FXML
    void Actioncopiar(ActionEvent event) throws SQLException, Exception {
        aux = tblPrestamos.getSelectionModel().getSelectedItem();
        String myString = aux.getCodigo();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        Conexion key = new Conexion();
        String query;
        boolean acceso = true;
        if(event.getCode() == KeyCode.ENTER){
                query = "SELECT * from productos WHERE ID = '"+txtCodigo.getText()+"'";
                System.out.println(query);

                key.QueryExecute(query);
                if(!key.setResult.first()){
                    lblErrCodigo.setText("No existe el producto");
                    acceso = false;
                }else{
                    if(key.setResult.getInt("CantidadActual") == 0){
                        acceso = false;
                        lblErrCodigo.setText("No hay productos en el inventario");
                    }
                    if(key.setResult.getInt(Variables_Globales.local) == 0){
                        acceso = false;
                        lblErrCodigo.setText("No hay productos en el Local");
                    }
                }
                conn.setResult.close();
                if(acceso = true){
                    lblErrCodigo.setText("");
                    Transfefir();
                }
            }
        }   
    
    public void Transfefir() throws SQLException{
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query, fecha, Otros = "Otros";
        int Cantidad = 1;
        fecha = formato.format(fechaHoy);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transacciones");
        query = "Insert into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas) ";
        query += "values (?,?,?,?,?,?)";
        conn.preparedStatement(query);
        conn.stmt.setString(1, txtCodigo.getText());
        conn.stmt.setInt(2, Cantidad);
        conn.stmt.setString(3, Variables_Globales.local);
        conn.stmt.setString(4, Otros);
        conn.stmt.setString(5, fecha);
        conn.stmt.setString(6, txtNotas.getText());
        conn.stmt.executeUpdate();
        conn.Commit();
        txtCodigo.clear();
        txtCodigo.requestFocus();
        tblPrestamos.refresh();
        tblPrestamos.setItems(ObenerPrestamo(false));
    }
    
    public void EliminarPedido() throws SQLException{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transacciones");
        aux = tblPrestamos.getSelectionModel().getSelectedItem();
        System.out.println(aux.getDestino());
        if(aux.getDestino().equals("Otros")){
            String query = "DELETE FROM prestamos where Folio = '"+aux.getFolio()+"'";
            if(conn.QueryUpdate(query)){
                alert.setHeaderText("Prestamo: ");
                alert.setContentText("¡Cancelado!");
                alert.initOwner(btnCancelar.getScene().getWindow());
                alert.showAndWait();
            }
            else{
                alert.setHeaderText("Transacción:");
                alert.setContentText(" ¡No pudo ser Cancelado! Verifique conexión con la base de datos");
                alert.initOwner(btnCancelar.getScene().getWindow());
                alert.showAndWait();
                conn.RollBack();
            }
            tblPrestamos.refresh();
            tblPrestamos.setItems(ObenerPrestamo(false));
        }
        else{
                alert.setHeaderText("Transacción:");
                alert.setContentText(" ¡No pudo ser Cancelado! Solo se puede eliminar prestamos\n a locales ajenos ");
                alert.initOwner(btnCancelar.getScene().getWindow());
                alert.showAndWait();
                conn.RollBack();
            }
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}


/*
Se elimino un botón desde el FXML entre la linea 97 y 103
 
                  <JFXButton fx:id="btnVentaEspera" layoutX="488.0" layoutY="1.0" maxHeight="1.7976931348623157E308" onAction="#ActionVentaEspera" prefHeight="80.0" prefWidth="80.0" style="-fx-background-color: #757575;" styleClass="text-button" text="Venta  en&#10;espera" textAlignment="CENTER" textFill="WHITE" AnchorPane.bottomAnchor="20.0" AnchorPane.rightAnchor="435.0" AnchorPane.topAnchor="20.0">
                     <font>
                        <Font size="14.0" />
                     </font>
                  </JFXButton>

 
*/