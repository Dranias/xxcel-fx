/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.LogReport;
import xxcell.model.Productos;
import xxcell.model.Salidas;

/**
 * FXML Controller class
 *
 * @author Dranias
 */
public class RastreoController implements Initializable {

    
    @FXML
    private JFXTextField txtCodigo;
    
    @FXML
    private TableView<Salidas> tblRastreo;
    
    @FXML
    private TableColumn<Salidas, String> colFecha;

    @FXML
    private TableColumn<Salidas, Number> colFolio;

    @FXML
    private TableColumn<Salidas, Number> colCantidad;

    @FXML
    private TableColumn<Salidas, String> colDescrip;
    
    LogReport log;
    
    Conexion conn = new Conexion();
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConnectionOpen();

        //Se pide Focus de txtID
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtCodigo.requestFocus();
            }
        });
        
        
        //Inicializan Columnas del Tableview
        colFolio.setCellValueFactory(cellData -> cellData.getValue().getFolio());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().getCantidad());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().getFecha());
        colDescrip.setCellValueFactory(cellData -> cellData.getValue().getNotas());
    }
    
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        Alert alert;
        if(event.getCode() == KeyCode.ENTER){ 
            String query = "SELECT * FROM productos WHERE ID = '"+txtCodigo.getText()+"'";
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
                    tblRastreo.setItems(ObtenerSalidas());
                    txtCodigo.clear();
                    txtCodigo.requestFocus();
                   // queryUpdate.ConnectionClose();
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
    
    public ObservableList<Salidas> ObtenerSalidas() throws SQLException{
        ObservableList<Salidas> salidas = FXCollections.observableArrayList();
        String Descripcion;
        int Cantidad, Folio;
        Date Fecha;
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                
        String query = "SELECT ventafolio, invfecha, invcantidad, invdescripcion FROM tblinventario WHERE productocodigo = " + txtCodigo.getText();
        
        if(conn.QueryExecute(query))
        {
            while (conn.setResult.next())
            {
                System.out.println("---------------");
                Fecha = conn.setResult.getTimestamp("invfecha");
                
                Descripcion = conn.setResult.getString("invdescripcion");
                System.out.println(Descripcion);
                
                Cantidad = conn.setResult.getInt("invcantidad");
                System.out.println(Cantidad);
                
                Folio = conn.setResult.getInt("ventafolio");
                System.out.println(Folio);
                
                String fechaFormat;
                fechaFormat = formatter.format(Fecha);
                System.out.println(fechaFormat);
                
                salidas.add(new Salidas(Folio, Cantidad, Descripcion, fechaFormat));
            }
            conn.setResult.close();
        }
        return salidas;
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
    
}
