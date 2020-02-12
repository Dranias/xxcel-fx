package xxcell.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.LogReport;


public class ModificarPreciosController implements Initializable {
    
    @FXML
    private JFXComboBox<String> cmbTipoprec;
    
    @FXML
    private JFXComboBox<String> cmbIdentificador;

    @FXML
    private JFXButton btnPrec;

    @FXML
    private JFXButton btnCosto;
    
    @FXML
    private JFXButton btnDistribuidor;

    @FXML
    private JFXTextField txtDistribuidor;

    @FXML
    private JFXTextField txtPrecio;
    
    @FXML
    private JFXTextField txtCosto;
    
    Conexion conn = new Conexion();
    LogReport log;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        conn.ConnectionOpen();
        
        inicializaCmb();
        cmbIdentificador.setVisible(false);
        
        
        cmbTipoprec.setOnAction(e -> {
            cmbIdentificador.getSelectionModel().clearSelection();
            cmbIdentificador.getItems().clear();
            cmbIdentificador.setVisible(true);
            String StmtSq = "Select DISTINCT Identificador FROM productos "; 
            StmtSq += "WHERE Tipo='"+cmbTipoprec.getValue()+"'";
            if(conn.QueryExecute(StmtSq))
            {
                try {
                        while(conn.setResult.next()){
                        cmbIdentificador.getItems().add(conn.setResult.getString("Identificador"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                    String msjHeader = "¡Error!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                }
            }
        });
        
        cmbIdentificador.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbIdentificador.getValue(), cmbIdentificador.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbIdentificador.setValue(s);
                }
            }
        });
        
        cmbTipoprec.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbTipoprec.getValue(), cmbTipoprec.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbTipoprec.setValue(s);
                }
            }
        });
        
        btnPrec.setOnAction((ActionEvent e) -> {
            if(txtPrecio.getText().length() != 0 && cmbTipoprec.getValue().length() != 0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Eliminar producto");
                alert.setContentText("¿Desea realmente modificar el Precio de Tipo?");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    modificarPrecio("precio");
                } 
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Modificar Costos");
                alert.setContentText("Debe Seleccionar el tipo a modificar e ingresar una Cantidad");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
            }
        });//FIN btnPrecio
        
        btnDistribuidor.setOnAction((ActionEvent e) -> { 
            if(txtDistribuidor.getText().length() != 0 && cmbTipoprec.getValue().length() != 0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modificar Costos");
                alert.setContentText("¿Desea realmente modificar el Precio a Distibuidor de Tipo?");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    modificarPrecio("distribuidor");
                } 
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Modificar Costos");
                alert.setContentText("Debe Seleccionar el tipo a modificar e ingresar una Cantidad");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
            }
        });//FIN btnDistribuidor
        
        btnCosto.setOnAction((ActionEvent e) -> { 
            if(txtCosto.getText().length() != 0 && cmbTipoprec.getValue().length() != 0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modificar Costos");
                alert.setContentText("¿Desea realmente modificar el Costo de Tipo?");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    modificarPrecio("costo");
                } 
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Modificar Costos");
                alert.setContentText("Debe Seleccionar el tipo a modificar e ingresar una Cantidad");
                alert.initOwner(btnPrec.getScene().getWindow());
                Optional<ButtonType> result = alert.showAndWait();
            }
        });//FIN btnPrecio
        
    }   
    
    public void modificarPrecio(String destino){
        String mensaje;
        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
        
        String query;
        if(destino.equals("precio")){
            if(cmbIdentificador.getValue().length() != 0){
                query = "Update productos SET PrecPub = " + txtPrecio.getText() + " WHERE Tipo = '" + cmbTipoprec.getValue() + "' AND Identificador = '"+ cmbIdentificador.getValue() +"'";
                System.out.println(query); 
                
            }else{
                query = "Update productos SET PrecPub = " + txtPrecio.getText() + " WHERE Tipo = '" + cmbTipoprec.getValue() + "'";
                System.out.println(query);
            }
            if(conn.QueryUpdate(query)){
                mensaje = "Precio Modificado \n";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }else{
                mensaje = "Precio no pudo ser Modificado \n Verifique conexion con base de datos";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }   
        }
        if(destino.equals("distribuidor")){
            if(cmbIdentificador.getValue().length() != 0){
                query = "Update productos SET PrecDist = " + txtDistribuidor.getText() + " WHERE Tipo = '" + cmbTipoprec.getValue() + "' AND Identificador = '"+ cmbIdentificador.getValue() +"'";
                System.out.println(query);
            }else{
                query = "Update productos SET PrecDist = " + txtDistribuidor.getText() + " WHERE Tipo = '" + cmbTipoprec.getValue() + "'";
                System.out.println(query);
            }
            if(conn.QueryUpdate(query)){
                mensaje = "Precio Distribuidor Modificado \n";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }else{
                mensaje = "Precio Distribuidor no pudo ser Modificado \n Verifique conexion con base de datos";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }
        }
        if(destino.equals("costo")){
            if(cmbIdentificador.getValue().length() != 0){
                query = "Update productos SET Costo = " + txtCosto.getText() + " WHERE Tipo = ' AND Identificador = '"+ cmbIdentificador.getValue() +"'";
                System.out.println(query);
            }else{
                query = "Update productos SET Costo = " + txtCosto.getText() + " WHERE Tipo = '" + cmbTipoprec.getValue() + "'";
                System.out.println(query);
            }
            if(conn.QueryUpdate(query)){
                mensaje = "Costo Modificado \n";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }else{
                mensaje = "Costo no pudo ser Modificado \n Verifique conexion con base de datos";
                incompleteAlert.setTitle("Modificacion de Costos");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnCosto.getScene().getWindow());
                incompleteAlert.showAndWait();
            }
        }
    }
    
    public void inicializaCmb(){
        String  query = "SELECT DISTINCT Tipo FROM productos ORDER BY Tipo";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbTipoprec.getItems().add(conn.setResult.getString("Tipo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }
    
}
