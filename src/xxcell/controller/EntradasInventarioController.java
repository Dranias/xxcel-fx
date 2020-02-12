/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.Productos;


public class EntradasInventarioController implements Initializable {
   
    @FXML
    private JFXTextField txtCosto;
    @FXML
    private JFXTextField txtPublico;
    @FXML
    private JFXTextField txtDistribuidor;
    @FXML
    private JFXTextField txtCodigo;   
    @FXML
    private JFXTextField txtLocal127;
    @FXML
    private JFXTextField txtLocal58;
    @FXML
    private JFXTextField txtLocal64;
    @FXML
    private JFXTextField txtCantidad;

    @FXML
    private Label lblDescripcion;
    @FXML
    private Label lblCantActual;
    @FXML
    private Label lblEsuma;
    @FXML
    private Label lblECantidadEntrante;
    @FXML
    private Label lblCostoActual;
    @FXML
    private Label lbldistactual;
    @FXML
    private Label lblpublicoactual;
    @FXML
    private Label lblErrorCosto;
    @FXML
    private Label lblActual127;
    @FXML
    private Label lblActual58;
    @FXML
    private Label lblActual64;
    
    
    
    @FXML
    private JFXButton btnAgregar;
    
    Conexion conn = new Conexion();
    
    private Alert alert;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        Platform.runLater(() -> {
            txtCodigo.requestFocus();
            inicializa();
        });
        
        StringConverter<String> formatter;
        //TextFormatter para permitir solo el uso de numeros en los campos Telefono y Numero de empleado
        formatter = new StringConverter<String>() {
            @Override
            public String fromString(String string) {
                if (string.length() == 13)
                   return string;
                else
                if (string.length() == 12 && string.indexOf('-') == -1)
                   return string.substring(0, 4) + "-" + 
                          string.substring(4);
                else
                   return "";
             }

            @Override
            public String toString(String object) {
               if (object == null)   // only null when called from 
                  return ""; // TextFormatter constructor 
                                     // without default
               return object;
            }
         };
        
        UnaryOperator<TextFormatter.Change> filter;
        filter = (TextFormatter.Change change) -> {
            String text = change.getText();
            for (int i = 0; i < text.length(); i++)
                if (!Character.isDigit(text.charAt(i)))
                    return null;
            return change;
        };
        
        txtCantidad.setTextFormatter(new TextFormatter<>(formatter,"",filter));
    }    
    
    @FXML
    void MPressedTxtCodigo(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Parent principal;
            String dato;
            dato = txtCodigo.getText().trim().toUpperCase();
            principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
            Stage principalStage = new Stage();
            scene = new Scene(principal);
            principalStage.setScene(scene);
            principalStage.initModality(Modality.APPLICATION_MODAL);
            principalStage.initOwner(btnAgregar.getScene().getWindow());
            principalStage.showAndWait(); 
            if(Variables_Globales.BusquedaVenta.getID() != null){
                txtCodigo.setText(Variables_Globales.BusquedaVenta.getID());
                Variables_Globales.BusquedaVenta = new Productos();
            }
        }
    }
    
    @FXML
    void ActionAgregar(ActionEvent event) {
        if(txtCantidad.getText().length() != 0){
            lblECantidadEntrante.setText("");
            if(Validar()){
                lblEsuma.setText("");
                if(esnumero(txtCosto.getText())){
                    if(esnumero(txtPublico.getText())){
                        if(esnumero(txtDistribuidor.getText())){
                            RegistraEntrada();
                            lblErrorCosto.setText("");
                        }
                        else
                            lblErrorCosto.setText("Debe ingresar un número");
                    }
                    else
                        lblErrorCosto.setText("Debe ingresar un número");
                } 
                else{
                    lblErrorCosto.setText("Debe ingresar un número");
                }   
            } else 
                lblEsuma.setText("La suma de los locales no puede ser mayor \n"
                        + "a la cantidad entrante.");
        } else
            lblECantidadEntrante.setText("Cantidad entrante no debe estar vacio");
    }

    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException {
        String query;
        String producto;        
        if(event.getCode() == KeyCode.F10){
            Parent principal;
            String dato;
            dato = txtCodigo.getText().trim().toUpperCase();
            principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
            Stage principalStage = new Stage();
            scene = new Scene(principal);
            principalStage.setScene(scene);
            principalStage.initModality(Modality.APPLICATION_MODAL);
            principalStage.initOwner(btnAgregar.getScene().getWindow());
            principalStage.showAndWait(); 
            if(Variables_Globales.BusquedaVenta.getID() != null){
                txtCodigo.setText(Variables_Globales.BusquedaVenta.getID());
                Variables_Globales.BusquedaVenta = new Productos();
            }
        }
        if(event.getCode() == KeyCode.ENTER){ 
            query = "SELECT * FROM productos WHERE ID = '"+txtCodigo.getText()+"'";
            conn.QueryExecute(query);
            try {
                if(!conn.setResult.first()){
                    alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Confirmacion");
                    alert.initOwner(btnAgregar.getScene().getWindow());
                    
                    alert.setHeaderText(null);
                    alert.setContentText("No hay coincidencias");
                    alert.showAndWait();
                    
                    //lblErrCodigo.setText("Producto no encontrado");
                }
                else{
                    txtCantidad.setDisable(false);
                    txtLocal127.setDisable(false);
                    txtLocal58.setDisable(false);
                    txtLocal64.setDisable(false);
                    btnAgregar.setDisable(false);
                    txtCosto.setDisable(false);
                    txtPublico.setDisable(false);
                    txtDistribuidor.setDisable(false);
                    producto = conn.setResult.getString("Marca");
                    producto += " "+conn.setResult.getString("Modelo");
                    producto += "\n"+conn.setResult.getString("Tipo");
                    producto += " "+conn.setResult.getString("Identificador");
                    lblDescripcion.setText(producto);
                    lblCantActual.setText(conn.setResult.getString("CantidadActual"));
                    lblCostoActual.setText(conn.setResult.getString("Costo"));
                    lblpublicoactual.setText(conn.setResult.getString("PrecPub"));
                    lbldistactual.setText(conn.setResult.getString("PrecDist"));
                    txtPublico.setText(conn.setResult.getString("PrecPub"));
                    txtDistribuidor.setText(conn.setResult.getString("PrecDist"));
                    lblActual127.setText(conn.setResult.getString("L127"));
                    lblActual58.setText(conn.setResult.getString("L58"));
                    lblActual64.setText(conn.setResult.getString("L64"));
                    txtCantidad.requestFocus();
                    lblCostoActual.setText(String.valueOf(conn.setResult.getFloat("Costo")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(EntradasInventarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private int getFolio(){
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
            alert.initOwner(btnAgregar.getScene().getWindow());
            alert.setTitle("Punto de Venta - Error al obtener folio");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return result;		
    }
    
    private void RegistraEntrada(){
        String sqlStmt;
        String pass;
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Confirmacion");
        alert.initOwner(btnAgregar.getScene().getWindow());

        conn.AutoCommit(false);

        if(ProductoActualiza(true))
            if(FolioIncrementa())
                if(ActualizarCostos())
                    if(InventarioRegistra(true)){
                        conn.Commit();
                        alert.setHeaderText(null);
                        alert.setContentText("Se ha registrado la entrada con exito");
                        alert.showAndWait();
                        inicializa();
                    } else
                        conn.RollBack();
                else
                    conn.RollBack();
            else
                conn.RollBack();
        else
            conn.RollBack();
    }

    private boolean ProductoActualiza(boolean b) {
        boolean result = false;
        String sqlStmt;
        sqlStmt = "Update productos set ";
        sqlStmt += "Entradas = Entradas + "+txtCantidad.getText()+",";
        sqlStmt += "L58 = L58 + "+txtLocal58.getText()+",";
        sqlStmt += "L64 = L64 + "+txtLocal64.getText()+",";
        sqlStmt += "L127 = L127 + "+txtLocal127.getText()+",";
        sqlStmt += "Costo = '"+txtCosto.getText()+"',";
        sqlStmt += "PrecDist = '"+txtDistribuidor.getText()+"',";
        sqlStmt += "PrecPub = '"+txtPublico.getText()+"',";
        sqlStmt += "CantidadActual = CantidadActual + "+txtCantidad.getText();
        sqlStmt += " Where ID"
                + " = '"+txtCodigo.getText()+"'";
        if(conn.QueryUpdate(sqlStmt))
            result = true;

        return result;
    }
    
    private boolean ActualizarCostos(){
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date fechaHoy = new Date();
        String sqlStmt;
        boolean result = false;
        if(!Objects.equals(Double.valueOf(lblCostoActual.getText()), Double.valueOf(txtCosto.getText()))){
           sqlStmt = "Insert into costos (CodigoProducto, Fecha, Costo) "
                + "values ('"+txtCodigo.getText()+"', '"+formato.format(fechaHoy)+"', '"+txtCosto.getText()+"')"; 
           if(conn.QueryUpdate(sqlStmt))
            result = true;
        }else{
            result = true;
        }
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

    private boolean InventarioRegistra(boolean b) {
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        boolean result = false;
        String sqlStmt;

        sqlStmt = "Insert into tblinventario (invfolio,invmovimiento,invfecha,invcantidad,productocodigo,ventafolio,invdescripcion) ";
        sqlStmt += " Values ("+getFolio()+",";
        sqlStmt += "'Entrada',";
        sqlStmt += "'"+formato.format(fechaHoy)+"',";
        sqlStmt += txtCantidad.getText()+",";
        sqlStmt += "'"+txtCodigo.getText()+"',0,";
        sqlStmt += "'Entrada por Inventario')";

        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;
    }
    
    private void inicializa(){
        txtCodigo.setText("");
        txtCantidad.setText("");
        txtCantidad.setDisable(true);
        lblDescripcion.setText("");
        lblCantActual.setText("");
        txtLocal127.setText("0");
        txtLocal127.setDisable(true);
        txtLocal58.setText("0");
        txtLocal58.setDisable(true);
        txtLocal64.setText("0");
        txtLocal64.setDisable(true);
        btnAgregar.setDisable(true);
        lblECantidadEntrante.setText("");
        lblEsuma.setText("");
        txtCosto.clear();
        txtCosto.setDisable(true);
        txtPublico.clear();
        txtPublico.setDisable(true);
        txtDistribuidor.clear();
        txtDistribuidor.setDisable(true);
        lblCostoActual.setText("");
        lblpublicoactual.setText("");
        lbldistactual.setText("");
    }
    
    private boolean Validar() {
        int totalCantidad = Integer.valueOf(txtCantidad.getText());
        int l127 = Integer.valueOf(txtLocal127.getText());
        int l58 = Integer.valueOf(txtLocal58.getText());
        int l64 = Integer.valueOf(txtLocal64.getText());
        int sumaLocales = l127 + l58 + l64;
        
        if(txtCosto.getText().isEmpty()){
            txtCosto.setText(lblCostoActual.getText());
        }
        
        return sumaLocales <= totalCantidad;
    }
    
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
