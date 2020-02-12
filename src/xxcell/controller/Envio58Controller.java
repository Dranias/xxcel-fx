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
public class Envio58Controller implements Initializable {

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
        System.out.println("-------------------");
        int i;
        
        conn.ConnectionOpen();
        queryUpdate.ConnectionOpen();
        connPrest.ConnectionOpen();
        
        String query = "UPDATE `productos` SET `CantidadActual`= 0 WHERE `ID` = 27533828";
        conn.QueryUpdate(query);
        
        //query = "DELETE from tblinventario WHERE `invfecha` >= '2009-07-21' and `invfecha` <= '2019-05-01'";
        //query = "DELETE from tblinventario WHERE `invfecha` >= '2009-07-21' and `invfecha` <= '2019-05-01' AND  NumLocal = L127";
        //DELETE from tblventas WHERE `ventaFecha` >= '2009-07-21' and `ventaFecha` <= '2019-05-01'
        //DELETE FROM `tblventadetalle` WHERE `ventaFolio` < 12199 AND `local` = 'L127'
        //DELETE FROM `tblventadetalle` WHERE `ventaFolio` < 2317 AND `local` = 'L58'
        //DELETE FROM `tblventadetalle` WHERE `ventaFolio` < 1773 AND `local` = 'L64'
        
        /*String query;
        for(i=10190;i<=10191;i++){
            query = "SELECT * FROM tblventadetalle WHERE ventaFolio = " + i;
            conn.QueryExecute(query);
            try {
               while(conn.setResult.next()){
                    System.out.print(conn.setResult.getString("ventaFolio") + " ");
                    System.out.print(conn.setResult.getString("productoCodigo") + " ");
                    System.out.print(conn.setResult.getInt("ventaCantidad") + " ");
                    System.out.print(conn.setResult.getString("productoPrecio") + " ");
                    System.out.println(conn.setResult.getString("local"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        /*String query = "SELECT * from tblventas WHERE tblventas.ventaFecha BETWEEN '2019-01-14' and '2019-01-15' AND NumLocal = 'L127'";
        conn.QueryExecute(query);
            try {
               while(conn.setResult.next()){
                    System.out.print(conn.setResult.getString("ventaFolio") + " ");
                    System.out.print(conn.setResult.getString("ventaProductos") + " ");
                    System.out.print(conn.setResult.getInt("ventaImporte") + " ");
                    System.out.print(conn.setResult.getString("NumLocal") + " ");
                    System.out.println(conn.setResult.getString("NumEmpleado"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
        /*String query = "SELECT * from tblventadetalle "
                + "INNER JOIN productos "
                +    "ON tblventadetalle.productoCodigo = productos.ID "
                + "INNER JOIN tblventas "
                +    "ON tblventas.ventaFolio = tblventadetalle.ventaFolio "
                + "WHERE tblventas.ventaFolio = 10136 AND tblventas.NumEmpleado = 1";
        System.out.println(query);
        conn.QueryExecute(query);
            try {
               while(conn.setResult.next()){
                   System.out.print(conn.setResult.getString("Marca") + " ");
                   System.out.print(conn.setResult.getString("Modelo") + " ");
                   System.out.print(conn.setResult.getString("Tipo") + " ");
                   System.out.print(conn.setResult.getString("Identificador") + " ");
                   System.out.print(conn.setResult.getString("productoCodigo") + " ");
                   System.out.print(conn.setResult.getString("ventaCantidad") + " ");
                   System.out.print(conn.setResult.getInt("productoPrecio") + " ");
                   System.out.print(conn.setResult.getInt("NumEmpleado") + " ");
                   System.out.println(conn.setResult.getString("local"));
                }
            } catch (SQLException ex) {
               Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
        /*String query;
        int ventas = 0;
        double precio = 0;
        query = "Select * from tblventas "
                + "INNER JOIN tblventadetalle "
                    + "ON tblventadetalle.ventaFolio = tblventas.ventaFolio "
                + "INNER JOIN productos "
                    + "ON tblventadetalle.productoCodigo = productos.ID"
                + "WHERE tblventas.NumEmpleado = 12 AND tblventas.ventaFecha BETWEEN '2019-01-13' and '2019-01-19' "
                + "AND tblventadetalle.local = tblventas.NumLocal";
        System.out.println(query);
        conn.QueryExecute(query);
        try {
           while(conn.setResult.next()){
               System.out.println(conn.setResult.getString("ventaFolio") + " ");
               //System.out.print(conn.setResult.getString("ventaProductos") + " ");
               //System.out.print(conn.setResult.getString("ventaImporte") + " ");
               //System.out.print(conn.setResult.getString("NumEmpleado") + " ");
               //System.out.println(conn.setResult.getString("NumLocal"));
               //System.out.println(conn.setResult.getString("local") + " ");
               //System.out.println(conn.setResult.getString("CodigoDistribuidor"));
               if(conn.setResult.getString("CodigoDistribuidor") == null){
                    ventas = ventas + conn.setResult.getInt("ventaProductos");
                    precio = precio + conn.setResult.getDouble("ventaImporte");
               }
            }
        } catch (SQLException ex) {
           Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Ventas = " + ventas);
        System.out.println("Comision = " + precio);*/
        
        /*String query = "Select * from tblventas WHERE ventaFolio = 154";
        conn.QueryExecute(query);
        try {
           while(conn.setResult.next()){
               System.out.println(conn.setResult.getString("ventaFolio") + " ");
               System.out.print(conn.setResult.getString("ventaProductos") + " ");
               System.out.print(conn.setResult.getString("ventaImporte") + " ");
               //System.out.print(conn.setResult.getString("NumEmpleado") + " ");
               System.out.println(conn.setResult.getString("NumLocal"));
               //System.out.println(conn.setResult.getString("local") + " ");
               //System.out.println(conn.setResult.getString("CodigoDistribuidor"));
            }
        } catch (SQLException ex) {
           Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        /*String query = "UPDATE tblventas SET ventaImporte = 0 WHERE ventaFolio = 154 AND ventaImporte = 50";
        System.out.println(query);
        conn.QueryUpdate(query);*/
        
        /*String query = "Select * from prestamos "
                    + "INNER JOIN productos "
                    + "ON productos.ID = prestamos.Codigo "
                    + "WHERE Notas = 'ADRIAN'";
        conn.QueryExecute(query);
        try {
           while(conn.setResult.next()){
               System.out.println(conn.setResult.getString("Codigo") + " ");
               System.out.print(conn.setResult.getString("Marca") + " ");
               System.out.print(conn.setResult.getString("Modelo") + " ");
               System.out.print(conn.setResult.getString("Tipo") + " ");
               System.out.println(conn.setResult.getString("Identificador"));
            }
        } catch (SQLException ex) {
           Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        /*String query = "Select * from salidas WHERE Local = 'L64'";
        conn.QueryExecute(query);
        try {
           while(conn.setResult.next()){
               System.out.println(conn.setResult.getString("Codigo") + " ");
               System.out.print(conn.setResult.getString("Local") + " ");
               System.out.print(conn.setResult.getString("NumEmpleado") + " ");
               System.out.print(conn.setResult.getString("Motivo") + " ");
               System.out.print(conn.setResult.getString("Folio") + " ");
            }
        } catch (SQLException ex) {
           Logger.getLogger(Envio58Controller.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        /*String query = "ALTER TABLE tblinventario MODIFY COLUMN invdescripcion TEXT";
        conn.QueryUpdate(query);*/
        
    }   
    
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query, producto, query2, fecha;
        fecha = formato.format(fechaHoy);
        Alert alert;
        int cantidadLocal, cantidad58;
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
                    lblEnvio.setText(producto + "   Enviado a 58");
                    cantidadLocal = conn.setResult.getInt(Variables_Globales.local) - 1;
                    cantidad58 = conn.setResult.getInt("L58") + 1;
                    query = "UPDATE productos SET `"+Variables_Globales.local+"` = "+cantidadLocal+", L58 = "+cantidad58+" WHERE `ID` = '"+txtCodigo.getText()+"'";
                    System.out.println(query);
                    queryUpdate.QueryUpdate(query);
                                        
                    query2 = "Insert into prestamos (Codigo, Cantidad, Procedencia, Destino, FechaHora, Notas) ";
                    query2 += "values (?,?,?,?,?,?)";

                    connPrest.preparedStatement(query2);
                    connPrest.stmt.setString(1, txtCodigo.getText());
                    connPrest.stmt.setInt(2, 1);
                    connPrest.stmt.setString(3, Variables_Globales.local);
                    connPrest.stmt.setString(4, "L58");
                    connPrest.stmt.setString(5, fecha);
                    connPrest.stmt.setString(6, "Envio a L60");
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
