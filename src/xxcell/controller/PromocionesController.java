package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.Productos;

public class PromocionesController implements Initializable {

    Conexion conn = new Conexion();
    
    @FXML
    private JFXTextField txtCodigoPromo;

    @FXML
    private JFXTextField txtPrecio;

    @FXML
    private JFXTextField txtCodigo;

    @FXML
    private JFXButton btnCancelar;

    @FXML
    private JFXButton btnAgregar;
    
    @FXML
    private TableView<Productos> tblProductos;

    @FXML
    private TableColumn<Productos, String> colCodigo;

    @FXML
    private TableColumn<Productos, String> colProducto;
    
    @FXML
    private Label lblErrorPrecio;

    @FXML
    private Label lblErrorPromo;
    
    @FXML
    private Label lblErrorTabla;
    
    //Lista para Llenar la tabla de productos;
    ObservableList<Productos> productos = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        TextFormatter<String> txtFormatterICodigo = new TextFormatter<>(getFilter());
        txtCodigoPromo.setTextFormatter(txtFormatterICodigo);
        
        tblProductos.setPlaceholder(new Label("---XXCELL---"));
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colProducto.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        btnAgregar.setOnAction((ActionEvent e) -> {
            boolean flag = true;
            String queryCodigoPromo;
            
            if(!esnumero(txtPrecio.getText())){
                flag=false;
                lblErrorPromo.setText("Debe ingresar un número.");
            }
            else
                lblErrorPromo.setText("");
            
            if(txtCodigoPromo.getText().length() == 0 || txtCodigoPromo.getText().length() > 15) {
                lblErrorPrecio.setText("Tipo debe ser mayor a 0 y Menor a 15 caracteres");
                flag = false;
            }
            else
                lblErrorPrecio.setText("");
            
            if(productos.size() < 2){
                lblErrorTabla.setText("La tabla debe conetener al menos 2 productos");
                flag = false;
            }
            else
                lblErrorTabla.setText("");
            
            queryCodigoPromo = "Select * from promociones where CodigoPromocion = '"+txtCodigoPromo.getText()+"'";
            conn.QueryExecute(queryCodigoPromo);
            try {
                if(conn.setResult.first()) {
                    String mensaje = "¡El Código de Promocion ya existe! \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
                    incompleteAlert.setTitle("Promociones");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(btnAgregar.getScene().getWindow());
                    incompleteAlert.showAndWait();
                }else{
                    if(flag)
                        AgregarSQL();
                } 
            } catch (SQLException ex) {
                Logger.getLogger(PromocionesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnCancelar.setOnAction((ActionEvent e) -> {
            Stage stage;
            stage = (Stage) btnAgregar.getScene().getWindow();
            stage.close();
        });
    }    
    
    //funcion para añadir a la base de datos las promociones
    public void AgregarSQL(){
        String query;
        double preciopromocion;
        int accesosValidos = 0;
        
        preciopromocion = Double.parseDouble(txtPrecio.getText());
        
        for(int i = 0 ; i < productos.size() ; i++){
            query = "INSERT INTO promociones "
                + "(CodigoPromocion, PrecioPromocion, CodigoProducto) "
                + "VALUES "
                + "('"+ txtCodigoPromo.getText() +"', '"+preciopromocion+"', '"+productos.get(i).getID()+"')";
            preciopromocion = 0;
            if(conn.QueryUpdate(query)){
                accesosValidos++;
            }
        }
        
        if(accesosValidos == productos.size()){
            String mensaje = "Promocion Añadida \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Gestión de Productos");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnAgregar.getScene().getWindow());
            incompleteAlert.showAndWait();
            limpiarVentana();
        }else{
            String mensaje = "Promocion NO PUDO SER añadida \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
            incompleteAlert.setTitle("Gestión de Productos");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnAgregar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
    }
    
    //MAYUSCULA TEXTFIELD CODIGO
    private UnaryOperator<TextFormatter.Change> getFilter() {
        return change -> {
            String text = change.getText();
            change.setText(text.toUpperCase());
            return change;
        };
    }
    
    public void limpiarVentana(){
        txtCodigoPromo.clear();
        txtPrecio.clear();
        tblProductos.refresh();
        productos.removeAll(productos);
    }
    
    @FXML
    void MPressedTxtCodigo(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Parent principal;
            String CodigoProducto, nombre;
            principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
            Stage principalStage = new Stage();
            scene = new Scene(principal);
            principalStage.setScene(scene);
            principalStage.initModality(Modality.APPLICATION_MODAL);
            principalStage.initOwner(btnAgregar.getScene().getWindow());
            principalStage.showAndWait(); 
            if(Variables_Globales.BusquedaVenta.getID() != null){
                CodigoProducto = Variables_Globales.BusquedaVenta.getID();
                nombre = Variables_Globales.BusquedaVenta.getMarca();
                nombre += " " + Variables_Globales.BusquedaVenta.getModelo();
                nombre += " " + Variables_Globales.BusquedaVenta.getTipo();
                nombre += " " + Variables_Globales.BusquedaVenta.getNombre();
                productos.add(new Productos (CodigoProducto, nombre));
                tblProductos.refresh();
                tblProductos.setItems(productos);
                Variables_Globales.BusquedaVenta = new Productos();
            }
        }
    }
        
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException {  
        ObservableList<Productos> productoSeleccionado,todosProductos;
        
        if(event.getCode() == KeyCode.F10){
            Parent principal;
            String CodigoProducto, nombre;
            principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
            Stage principalStage = new Stage();
            scene = new Scene(principal);
            principalStage.setScene(scene);
            principalStage.initModality(Modality.APPLICATION_MODAL);
            principalStage.initOwner(btnAgregar.getScene().getWindow());
            principalStage.showAndWait(); 
            if(Variables_Globales.BusquedaVenta.getID() != null){
                CodigoProducto = Variables_Globales.BusquedaVenta.getID();
                nombre = Variables_Globales.BusquedaVenta.getMarca();
                nombre += " " + Variables_Globales.BusquedaVenta.getModelo();
                nombre += " " + Variables_Globales.BusquedaVenta.getTipo();
                nombre += " " + Variables_Globales.BusquedaVenta.getNombre();
                productos.add(new Productos (CodigoProducto, nombre));
                tblProductos.refresh();
                tblProductos.setItems(productos);
                Variables_Globales.BusquedaVenta = new Productos();
            }
        }
        if(event.getCode() == KeyCode.DELETE){ 
            System.out.println("HE dado delete");
            productoSeleccionado = tblProductos.getSelectionModel().getSelectedItems();
            todosProductos = tblProductos.getItems();
            productoSeleccionado.forEach(todosProductos::remove);
            
            tblProductos.refresh();
            tblProductos.setItems(todosProductos);
        }
    }
    
    //FUNCIÓN PARA VALIDAR SI LOS DATOS INGRESADOS EN "MERCANCIA ENTRANTE, Y LOS 3 LOCALES SON NUMEROS
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
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}
