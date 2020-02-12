package xxcell.controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.Costos;
import xxcell.model.Salidas;

public class EstadisticasCostosController implements Initializable {
    
    Conexion conn = new Conexion();
    
    @FXML
    private BarChart<String, Number> barChartCostos;

    @FXML
    private TableView<Costos> tblCostos;
    @FXML
    private TableColumn<Costos, String> colCodigo;
    @FXML
    private TableColumn<Costos, String> colProducto;
    @FXML
    private TableColumn<Costos, Number> colCosto;
    @FXML
    private TableColumn<Costos, String> colFecha;
    ObservableList<Costos> costos = FXCollections.observableArrayList();

    @FXML
    private JFXButton btnAceptar;
    @FXML
    private JFXButton btnReiniciar;
    @FXML
    private JFXTextField txtProducto;

    public ObservableList<Costos> ObtenerCostos() throws SQLException{
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormat, Producto, Codigo;
        Double costo;
        Date Fecha;
        String qry = "Select costos.CodigoProducto, costos.Fecha, costos.Costo, productos.Marca, productos.Modelo, productos.Tipo, productos.Identificador " +
                     "from costos " +
                     "	INNER JOIN productos " +
                     "    	ON costos.CodigoProducto = productos.ID "
                   + "Where costos.CodigoProducto = '"+txtProducto.getText()+"'";
        if(conn.QueryExecute(qry)){
            while(conn.setResult.next()){
                Codigo = conn.setResult.getString("CodigoProducto");
                Producto = conn.setResult.getString("Marca");
                Producto += " "+ conn.setResult.getString("Modelo");
                Producto += " "+ conn.setResult.getString("Tipo");
                Producto += " "+ conn.setResult.getString("Identificador");
                costo = conn.setResult.getDouble("Costo");
                Fecha = conn.setResult.getTimestamp("Fecha");
                fechaFormat = formatter.format(Fecha);
                costos.add(new Costos(Codigo, Producto, costo, fechaFormat)); 

            }
        }
        return costos;
    }
    
    public void ObtenerChart() throws SQLException{
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Costo");
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName(txtProducto.getText());
        Double costo;
        String Identificador = "";
        int i = 0;
        
        String qry = "Select Costo from costos where CodigoProducto = '"+txtProducto.getText()+"'";
        if(conn.QueryExecute(qry)){
            while(conn.setResult.next()){
                costo = conn.setResult.getDouble("Costo");
                Identificador = String.valueOf(i);
                dataSeries1.getData().add(new XYChart.Data(Identificador, costo));
                i++;
            }
        }
        barChartCostos.getData().add(dataSeries1);    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        colProducto.setCellValueFactory(cellData -> cellData.getValue().NombreProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().FechaProrperty());
        colCosto.setCellValueFactory(cellData -> cellData.getValue().CostoProperty());
        
        btnAceptar.setOnAction((ActionEvent e) -> { 
            txtProducto.setDisable(true);
            btnAceptar.setDisable(true);
            String SearchId = "Select ID from productos where ID = '"+txtProducto.getText()+"'";
            if(conn.QueryExecute(SearchId)){
                try {
                    if(conn.setResult.first()){
                        tblCostos.setItems(ObtenerCostos());
                        ObtenerChart();
                    }else{
                        String mensaje = "¡El ID de Producto NO existe! \n";
                        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                        incompleteAlert.setTitle("Estadistica de Costos");
                        incompleteAlert.setHeaderText(null);
                        incompleteAlert.setContentText(mensaje);
                        incompleteAlert.initOwner(btnAceptar.getScene().getWindow());
                        incompleteAlert.showAndWait();  
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(EstadisticasCostosController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }); 
        
        btnReiniciar.setOnAction((ActionEvent e) -> { 
            barChartCostos.getData().clear();
            costos.removeAll(costos);
            tblCostos.getItems().clear();
            txtProducto.setDisable(false);
            txtProducto.clear();
            txtProducto.requestFocus();
            btnAceptar.setDisable(false);
        }); 
    }

    @FXML
    void MPressedTxtCodProducto(MouseEvent event) throws IOException {
        if(event.getClickCount() == 2){
            Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
        Stage principalStage = new Stage();
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(btnAceptar.getScene().getWindow());
        principalStage.showAndWait(); 
        if(Variables_Globales.BusquedaVenta.getID() != null){
            txtProducto.setText(Variables_Globales.BusquedaVenta.getID());
            txtProducto.requestFocus();
            }
        }
    }    
    
}
