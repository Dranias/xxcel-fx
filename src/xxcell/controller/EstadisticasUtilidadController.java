package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import xxcell.Conexion.Conexion;
import xxcell.model.Detalles;

public class EstadisticasUtilidadController implements Initializable {
    
    Conexion conn = new Conexion();
    
    //Date Pickers para restringir los datos de una fecha X a una Y
    @FXML
    private JFXDatePicker datePInicio;
    @FXML
    private JFXDatePicker datePFinal;
    
    //Tabla para visualizar las ventas según las fechas
    @FXML
    private TableView<Detalles> tblVentas;
    @FXML
    private TableColumn<Detalles, Number> colFolio;
    @FXML
    private TableColumn<Detalles, Number> colCantidadProd;
    @FXML
    private TableColumn<Detalles, Number> colPrecioVenta;
    @FXML
    private TableColumn<Detalles, Number> colUtilidad;

    //Botón para inicar el llenado de la tabla y mostrar utilidad y botón para reiniciar
    @FXML
    private JFXButton btnAceptar;
    @FXML
    private JFXButton btnReiniciar;

    //Label para poner en pantalla el calculo de ventas totales de folio y la utilidad total
    @FXML
    private Label lblVenta;
    @FXML
    private Label lblUtilidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
        colFolio.setCellValueFactory(cellData -> cellData.getValue().FolioProperty());
        colCantidadProd.setCellValueFactory(cellData -> cellData.getValue().CantidadProperty());
        colPrecioVenta.setCellValueFactory(cellData -> cellData.getValue().TotalProperty());
        colUtilidad.setCellValueFactory(cellData -> cellData.getValue().UtilidadProperty());
        
        btnAceptar.setOnAction((ActionEvent e) -> { 
            if(datePInicio.getValue() != null && datePFinal.getValue() != null){
                try {
                    btnAceptar.setDisable(true);
                    tblVentas.refresh();
                    tblVentas.setItems(agregarDatosATablar());
                } catch (SQLException ex) {
                    Logger.getLogger(EstadisticasUtilidadController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al selecciónar fechas.");
                alert.setContentText("Debe selecciónar las fechas de inicio y final");
                alert.showAndWait();
            }
        }); 
        
        btnReiniciar.setOnAction((ActionEvent e) -> { 
            btnAceptar.setDisable(false);
            tblVentas.refresh();
            tblVentas.setItems(null);
            datePFinal.setValue(null);
            datePInicio.setValue(null);
        }); 
    }    
    
    public ObservableList<Detalles> agregarDatosATablar() throws SQLException{
        ObservableList<Detalles> DetalleUtilidad = FXCollections.observableArrayList();
        float importe = 0, utilidad = 0;
        LocalDate FechaInit = datePInicio.getValue(), FinalFecha = datePFinal.getValue();
        String query = "SELECT * FROM tblventas WHERE tblventas.ventaFecha BETWEEN '"+FechaInit+"' and '"+FinalFecha.plusDays(1)+"'";
        System.out.println(query);
        if(conn.QueryExecute(query)){
            while (conn.setResult.next()){
                int folio = conn.setResult.getInt("ventaFolio");
                int productos = conn.setResult.getInt("ventaProductos");
                float ventaImporte = conn.setResult.getFloat("ventaImporte");
                float ventaUtilidad = conn.setResult.getFloat("ventaUtilidad");
                importe = importe + ventaImporte;
                utilidad = utilidad + ventaUtilidad;
                System.out.println("Folio: " + folio + " Productos: " + productos + " Importe: " + ventaImporte + " Utilidad: " + ventaUtilidad);
                DetalleUtilidad.add(new Detalles (folio, productos, ventaImporte, ventaUtilidad));
            }
        }
        lblVenta.setText(String.valueOf(importe));
        lblUtilidad.setText(String.valueOf(utilidad));
        return DetalleUtilidad;
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}
