package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import xxcell.Conexion.Conexion;
import xxcell.model.LogReport;
import xxcell.model.Productos;

public class BusquedaVentaController implements Initializable {
    
    Conexion conn = new Conexion();
    String sqlStmt;
    String query;
    
    //Variables para la Tabla creada
    @FXML private TableView<Productos> Tabla;
    @FXML private TableColumn<Productos, String> IDCol; //COLUMNA ID
    @FXML private TableColumn<Productos, String> ModCol; //COLUMNA MODELO
    @FXML private TableColumn<Productos, String> MarcaCol; //COLUMNA MARCA
    @FXML private TableColumn<Productos, String> TipoCol; //COLUMNA TIPO
    @FXML private TableColumn<Productos, String> NomCol; //COLUMNA NOMBRE
    
    //Lista para Llenar la tabla de productos;
    ObservableList<Productos> productos = FXCollections.observableArrayList();
    
    @FXML private JFXComboBox<String> cmbTipo;

    @FXML private JFXComboBox<String> cmbMarca;
    
    @FXML private JFXComboBox<String> cmbIdentificador;

    @FXML private JFXTextField txtModelo;

    @FXML private JFXButton btnFiltrar;

    @FXML private JFXButton btnReset;
    
    private AutoCompletionBinding<String> autoCompletModelo;
    
    //Lista para el autocompletado de JFXTextField buscar
    Set<String> hs_autocomplete = new HashSet<>();
    
    LogReport log;
    Window window;
    
    //Función para llenar el TableView con los datos que el usuario indique
    public ObservableList<Productos> ObtenerProd(String STSQL) throws SQLException{
        String Mod, Marc, DI, Nom, Tip, Dep;   
        int Disp, l58, l64, l127;
        double PPub, PDist;
        if(conn.QueryExecute(STSQL))
        {
            while (conn.setResult.next())
            {
                DI = conn.setResult.getString("ID");
                Marc = conn.setResult.getString("Marca");
                Mod = conn.setResult.getString("Modelo");
                Nom = conn.setResult.getString("Identificador");
                PPub = conn.setResult.getDouble("PrecPub");
                PDist = conn.setResult.getDouble("PrecDist");
                Tip = conn.setResult.getString("Tipo");
                Dep = conn.setResult.getString("Descrip");
                Disp = conn.setResult.getInt("CantidadActual");
                l58 = conn.setResult.getInt("L58");
                l64 = conn.setResult.getInt("L64");
                l127 = conn.setResult.getInt("L127");
                productos.add(new Productos(DI,Marc,Mod,Nom,PPub,PDist,Tip,Dep,Disp,l58,l64,l127));
            }    
        }
        return productos;
    }
    
    public void Autocompletar()
    {
        query = "SELECT DISTINCT Modelo FROM productos ORDER BY Modelo";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_autocomplete.add(conn.setResult.getString("Modelo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }    
    
    public void iniciarComboBox()
    {
        //inicia el combobox Marca
        query = "SELECT DISTINCT Marca FROM productos ORDER BY Marca";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbMarca.getItems().add(conn.setResult.getString("Marca"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        
        query = "SELECT DISTINCT Tipo FROM productos ORDER BY Tipo";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbTipo.getItems().add(conn.setResult.getString("Tipo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }
    
    public void iniciarcmbIdentificador(){
        cmbIdentificador.getSelectionModel().clearSelection();
        cmbIdentificador.getItems().clear();
        cmbIdentificador.setVisible(true);
        query = "SELECT DISTINCT Identificador FROM productos WHERE Tipo = '"+cmbTipo.getValue()+"' ORDER BY Identificador";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                   cmbIdentificador.getItems().add(conn.setResult.getString("Identificador"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {      
        Autocompletar();
        
        TextFields.bindAutoCompletion(txtModelo, hs_autocomplete);
        
        Platform.runLater(() -> {
            window = btnFiltrar.getScene().getWindow();             
            log = new LogReport(window);
            txtModelo.requestFocus();
        });
        
        iniciarComboBox();
        IDCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        MarcaCol.setCellValueFactory(cellData -> cellData.getValue().marcaProperty());
        ModCol.setCellValueFactory(cellData -> cellData.getValue().modeloProperty());
        TipoCol.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        NomCol.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        //Función para el comboBox Marca; Esté llenara el TableView dependiendo la Opción que el usuario Ingrese
        cmbMarca.setOnAction(e -> {
            query = "SELECT * FROM productos "; 
            query += "WHERE Marca='"+cmbMarca.getValue()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(query));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        });//FIN DE LAMBDA MARCA   
        
        txtModelo.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            query = "SELECT * FROM productos ";
            query += "WHERE Modelo='"+txtModelo.getText()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(query));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        });   
        
        //Función para el combobox Tipo
        cmbTipo.setOnAction(e -> {
            iniciarcmbIdentificador();
            query = "SELECT * FROM productos "; 
            query += "WHERE Tipo='"+cmbTipo.getValue()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(query));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            } 
        });//FIN DE LAMBDA
        
        btnFiltrar.setOnAction((ActionEvent e) -> {
            if(txtModelo.getText().length()>0 && cmbMarca.getValue() == null
               && cmbTipo.getValue() != null && cmbIdentificador.getValue() != null){
                query =  "SELECT * FROM productos ";
                query +=  "WHERE Modelo='"+txtModelo.getText()+"' "
                        + "AND Tipo = '"+cmbTipo.getValue()+"' "
                        + "AND Identificador = '"+cmbIdentificador.getValue()+"'"; 
            }
            if(txtModelo.getText().length()>0 && cmbMarca.getValue() != null
               && cmbTipo.getValue() != null && cmbIdentificador.getValue() != null){
                query =  "SELECT * FROM productos ";
                query +=  "WHERE Modelo='"+txtModelo.getText()+"' "
                        + "AND Marca = '"+cmbMarca.getValue()+"' "
                        + "AND Tipo = '"+cmbTipo.getValue()+"' "
                        + "AND Identificador = '"+cmbIdentificador.getValue()+"'"; 
            }  
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(query));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        });
        
        
        btnReset.setOnAction((ActionEvent e) -> { 
            cmbMarca.getSelectionModel().clearSelection();
            cmbMarca.getItems().clear();
            cmbTipo.getSelectionModel().clearSelection();
            cmbTipo.getItems().clear();
            txtModelo.clear();
            iniciarComboBox();
            Tabla.refresh();
            productos.removeAll(productos);
            //iniciarComboBox();
        });//FIN RESET
        
        Tabla.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Variables_Globales.BusquedaVenta = Tabla.getSelectionModel().getSelectedItem();
                Stage stage;
                stage = (Stage) Tabla.getScene().getWindow();
                stage.close();
            }
        });
        
        Tabla.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER){
                Variables_Globales.BusquedaVenta = Tabla.getSelectionModel().getSelectedItem();
                Stage stage;
                stage = (Stage) Tabla.getScene().getWindow();
                stage.close();
            }
        });
    }    
}
