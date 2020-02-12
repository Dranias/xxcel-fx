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
public class Envio64Controller implements Initializable {

    @FXML
    private Label lblEnvio;

    @FXML
    private JFXTextField txtCodigo;
    
    LogReport log;
    
    Conexion conn = new Conexion();
    Conexion queryUpdate = new Conexion();
    Conexion connPrest = new Conexion();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        conn.ConnectionOpen();
        queryUpdate.ConnectionOpen();
        connPrest.ConnectionOpen();
        
        // TODO
        /*int contador = 0;
        String query = "Select ID FROM productos";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    System.out.println(conn.setResult.getString("ID"));
                    contador++;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Envio64Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(contador);*/
        
        /*String query = "SELECT productoCodigo, ventaCantidad, productoPrecio from tblventadetalle WHERE ventaFolio = 9642";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    System.out.println("Producto Codigo: " + conn.setResult.getString("productoCodigo"));
                    System.out.println("Venta Cantidad:  " + conn.setResult.getInt("ventaCantidad"));
                    System.out.println("producto Precio: " + conn.setResult.getFloat("productoPrecio"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Envio64Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        
        /*String query = "UPDATE tblventadetalle set ventaFolio = 9643 WHERE productoCodigo = 4551615 AND ventaFolio = 9642";
        if(conn.QueryUpdate(query)){
            System.out.println("Listo");
        }*/
        
        /*String query = "INSERT INTO `tblventadetalle`(`ventaFolio`, `productoCodigo`, `ventaCantidad`, `productoPrecio`, `VentaCancelada`) VALUES (7250,61142775,1,45.00,null)";   
        if(conn.QueryUpdate(query)){
            System.out.println("Insert Listo");
        }*/
        
        /*String query = "Update productos set CantidadActual =  14 WHERE ID = 305320348";
        if(conn.QueryUpdate(query)){
            System.out.println("Listo");
        }*/
        
        /*String query = "UPDATE productos SET L58 = 0, L64 = 0, L127 = 6 WHERE ID = 842799550768";
        conn.QueryUpdate(query);*/
        
        
        /*int i = 1;
        String query;
        for(i=1;i<10;i++){
            query = "DELETE FROM tblventas WHERE ventaFolio = " + i + " AND NumLocal = 'L58'" ;
            System.out.println(query);
            conn.QueryUpdate(query);
        }/*
        
        /*String query = "Select * from tblinventario";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    System.out.println(conn.setResult.getString("ventafolio"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Envio64Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    */
       
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
                    lblEnvio.setText(producto + "   Enviado a 64");
                    cantidadLocal = conn.setResult.getInt(Variables_Globales.local) - 1;
                    cantidad64 = conn.setResult.getInt("L64") + 1;
                    query = "UPDATE productos SET `"+Variables_Globales.local+"` = "+cantidadLocal+", L64 = "+cantidad64+" WHERE `ID` = '"+txtCodigo.getText()+"'";
                    System.out.println(query);

                    queryUpdate.QueryUpdate(query);
                    
                    query2 = "Insert into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas) ";
                    query2 += "values (?,?,?,?,?,?)";

                    connPrest.preparedStatement(query2);
                    connPrest.stmt.setString(1, txtCodigo.getText());
                    connPrest.stmt.setInt(2, 1);
                    connPrest.stmt.setString(3, Variables_Globales.local);
                    connPrest.stmt.setString(4, "L64");
                    connPrest.stmt.setString(5, fecha);
                    connPrest.stmt.setString(6, "Envio a L64");
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
