/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.LogReport;

public class AgregadoRapidoController implements Initializable {

    @FXML
    private JFXTextField txtCodigo;

    @FXML
    private Label lblProducto;

    @FXML
    private Label lblExistencia;
    
    LogReport log;
    
    Conexion conn = new Conexion();
    Conexion queryUpdate = new Conexion();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conn.ConnectionOpen();
        queryUpdate.ConnectionOpen();
    }

    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        String query, producto;
        int cantidadLocal, cantidadActual;
        Alert alert;
        if(event.getCode() == KeyCode.ENTER){ 
            query = "SELECT * FROM productos WHERE ID = '"+txtCodigo.getText()+"'";
            conn.QueryExecute(query);
            try {
                if(!conn.setResult.first()){
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Confirmacion");
                    alert.initOwner(txtCodigo.getScene().getWindow());
                    alert.setHeaderText(null);
                    alert.setContentText("No hay coincidencias");
                    alert.showAndWait();
                }
                else{
                    producto = conn.setResult.getString("Marca");
                    producto += " "+conn.setResult.getString("Modelo");
                    producto += "\n"+conn.setResult.getString("Tipo");
                    producto += " "+conn.setResult.getString("Identificador");
                    lblProducto.setText(producto);
                    cantidadLocal = conn.setResult.getInt(Variables_Globales.local) + 1;
                    cantidadActual = conn.setResult.getInt("CantidadActual") + 1;
                    lblExistencia.setText(String.valueOf(cantidadLocal));
                    query = "UPDATE productos SET `"+Variables_Globales.local+"` = "+cantidadLocal+", CantidadActual = "+cantidadActual+" WHERE `ID` = '"+txtCodigo.getText()+"'";
                    System.out.println(query);
                    queryUpdate.QueryUpdate(query);
                    txtCodigo.clear();
                    txtCodigo.requestFocus();
                }
            } catch (SQLException ex) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
                Logger.getLogger(EntradasInventarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
            conn.setResult.close();
        }
    }    
    
}
