/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import xxcell.Conexion.Conexion;
import xxcell.model.Distribuidores;
import xxcell.model.Productos;

/**
 * FXML Controller class
 *
 * @author snak0
 */
public class BusquedaDistribuidorController implements Initializable {
    
    Conexion conn = new Conexion();
    String sqlStmt;
    String query;

    @FXML
    private TableView<Distribuidores> tblDistribuidores;
    @FXML
    private TableColumn<Distribuidores, String> tblColID;
    @FXML
    private TableColumn<Distribuidores, String> tblColNombre;
    @FXML
    private TableColumn<Distribuidores, String> tblApellido;
    @FXML
    private TableColumn<Distribuidores, String> tblColStatus;
    @FXML
    private JFXTextField txtBusqueda;
    
    ObservableList<Distribuidores> distribuidores = FXCollections.observableArrayList();
    
    Set<String> hsApellido = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    
    private AutoCompletionBinding<String> autoCompletApellido;
    
    FXRobot robot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            robot = FXRobotFactory.createRobot(tblDistribuidores.getScene());
        });
        tblColID.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        tblColNombre.setCellValueFactory(cellData -> cellData.getValue().NombreProperty());
        tblApellido.setCellValueFactory(cellData -> cellData.getValue().ApellidoProperty());
        tblColStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        try {
            sqlStmt = "SELECT * FROM distribuidores";
            tblDistribuidores.setItems(ObtenerDist(sqlStmt));
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtBusqueda.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            robot.keyPress(KeyCode.ESCAPE);
            Autocompletar();
            query = "Select * FROM distribuidores ";
            query += "WHERE Apellido='"+txtBusqueda.getText()+"'";
            try {   
                tblDistribuidores.refresh();
                distribuidores.removeAll(distribuidores);
                tblDistribuidores.setItems(ObtenerDist(query));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    
    
    public ObservableList<Distribuidores> ObtenerDist(String query) throws SQLException{
        String codigo, nombre, apellido, status;   
        if(conn.QueryExecute(query))
        {
            while (conn.setResult.next())
            {
                codigo = conn.setResult.getString("Codigo");
                nombre = conn.setResult.getString("Nombre");
                apellido = conn.setResult.getString("Apellido");
                status = conn.setResult.getString("Status");
                distribuidores.add(new Distribuidores(codigo,nombre,apellido,status));
            }    
        }
        return distribuidores;
    }
    
    @FXML
    void MPressedTblDistribuidor(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Variables_Globales.BusquedaDistribuidor = tblDistribuidores.getSelectionModel().getSelectedItem();
                Stage stage;
                stage = (Stage) tblDistribuidores.getScene().getWindow();
                stage.close();
            }
    }
    
    public void Autocompletar() {
        query = "SELECT * FROM distribuidores WHERE Apellido LIKE '%" + txtBusqueda.getText() + "%'";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hsApellido.add(conn.setResult.getString("Apellido"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(autoCompletApellido != null)
                autoCompletApellido.dispose();
            autoCompletApellido = TextFields.bindAutoCompletion(txtBusqueda, hsApellido);
        }
    }    
}
