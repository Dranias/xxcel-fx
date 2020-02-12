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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.LogReport;


/**
 * FXML Controller class
 *
 * @author Dranias
 */
public class Envio127Controller implements Initializable {

    Conexion conn = new Conexion();
    Conexion queryUpdate = new Conexion();
    Conexion connPrest = new Conexion();
        
    @FXML
    private Label lblEnvio;

    @FXML
    private JFXTextField txtCodigo;
    
    LogReport log;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        conn.ConnectionOpen();
        queryUpdate.ConnectionOpen();
        connPrest.ConnectionOpen();
    }   
    
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query, producto, query2, fecha;
        fecha = formato.format(fechaHoy);
        int cantidadLocal, cantidad64;
        Alert alert;
        if(event.getCode() == KeyCode.ENTER){
            System.out.println(txtCodigo.getText());
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
                    producto += " "+conn.setResult.getString("Tipo");
                    producto += " "+conn.setResult.getString("Identificador");
                    lblEnvio.setText(producto + "   Enviado a 127");
                    cantidadLocal = conn.setResult.getInt(Variables_Globales.local) - 1;
                    cantidad64 = conn.setResult.getInt("L127") + 1;
                    query = "UPDATE productos SET `"+Variables_Globales.local+"` = "+cantidadLocal+", L127 = "+cantidad64+" WHERE `ID` = '"+txtCodigo.getText()+"'";
                    System.out.println(query);
                    queryUpdate.QueryUpdate(query);
                    
                    query2 = "Insert into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas) ";
                    query2 += "values (?,?,?,?,?,?)";
                    connPrest.preparedStatement(query2);
                    connPrest.stmt.setString(1, txtCodigo.getText());
                    connPrest.stmt.setInt(2, 1);
                    connPrest.stmt.setString(3, Variables_Globales.local);
                    connPrest.stmt.setString(4, "L127");
                    connPrest.stmt.setString(5, fecha);
                    connPrest.stmt.setString(6, "Envio a 127");
                    connPrest.stmt.executeUpdate();
                    connPrest.Commit();
                    
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
