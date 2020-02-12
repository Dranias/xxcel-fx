package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.EnvioCorreo;
import xxcell.model.LogReport;
import xxcell.model.Productos;

public class EstadisticasProductosController implements Initializable {
    
    //Conexión a la base de Datos
    Conexion conn = new Conexion();
    
    LogReport log;
    
    //Strings para Mandar las Estadisticas por correo.
    String MensajeEstadisticaProducto;
    String MensakeEstadisticaMarca;
    
    //Date pickers, entre que fecha y que fecha se darán las estadisticas
    @FXML
    private JFXDatePicker DatePInicio;
    @FXML
    private JFXDatePicker DatePFin;
    LocalDate FechaInit, FinalFecha;

    //Combobox
    @FXML
    private JFXComboBox<String> cmbMarca;
    @FXML
    private JFXComboBox<String> cmbModelo;
    @FXML
    private JFXComboBox<String> cmbTipo;

    //Botones
    @FXML
    private JFXButton btnReset;
    @FXML
    private JFXButton btnAceptar;
    @FXML
    private JFXButton btnImprimir;

    //PieChart, muestrá de forma gráfica las estadisticas
    @FXML
    private PieChart pieChartProductos;
    ObservableList<PieChart.Data> DataTipopieChart = FXCollections.observableArrayList();
    //PieChart
    @FXML
    private PieChart pieChartMarca;
    ObservableList<PieChart.Data> DataMarcapieChart = FXCollections.observableArrayList();

    //Tabla para mostrar el número de ventas que se realizo por producto
    @FXML
    private TableView<Productos> tblProductos;
    @FXML
    private TableColumn<Productos, String> colCodigo;
    @FXML
    private TableColumn<Productos, String> colModelo;
    @FXML
    private TableColumn<Productos, String> colTipo;
    @FXML
    private TableColumn<Productos, String> colIdentificador;
    @FXML
    private TableColumn<Productos, Number> colNumVentas;
    @FXML
    private TableColumn<Productos, String> colMarca;
    @FXML
    private TableColumn<Productos, Number> colDispon;
    //Lista para Llenar la tabla de productos;
    ObservableList<Productos> productos = FXCollections.observableArrayList();
    //Lista para llenar el PieChart
    ObservableList<Productos> dataProductosTipo = FXCollections.observableArrayList();
    ObservableList<Productos> dataProductosMarca = FXCollections.observableArrayList();
    
    public ObservableList<Productos> ObtenerProd() throws SQLException{
        String STSQL = "SELECT productos.ID, productos.Modelo, productos.Tipo, productos.Marca, productos.CantidadActual, "
                + "productos.Identificador, tblventadetalle.ventaCantidad "
                + "FROM productos " 
                + "INNER JOIN tblventadetalle "
                + "ON productos.ID = tblventadetalle.productoCodigo "
                + "INNER JOIN tblventas "
                + "ON tblventas.ventaFolio = tblventadetalle.ventaFolio "
                + "WHERE tblventas.ventaFecha BETWEEN '"+FechaInit+"' and '"+FinalFecha.plusDays(1)+"' "
                + "ORDER BY tblventadetalle.ventaCantidad DESC ";
        System.out.println(STSQL);
        boolean acceselse, accesMarca, accessTipo;
        String Mod, Marc, DI, Nom, Tip;   
        int VentaCantidad, Disponibilidad;
        if(conn.QueryExecute(STSQL))
        {
            while (conn.setResult.next())
            {
                DI = conn.setResult.getString("ID");
                Marc = conn.setResult.getString("Marca");
                Mod = conn.setResult.getString("Modelo");
                Nom = conn.setResult.getString("Identificador");
                Tip = conn.setResult.getString("Tipo");
                VentaCantidad = conn.setResult.getInt("ventaCantidad");
                Disponibilidad = conn.setResult.getInt("CantidadActual");
                if(productos.isEmpty()){
                    productos.add(new Productos(DI, Marc, Mod, Nom, Tip, VentaCantidad, Disponibilidad));
                }else{
                    acceselse = true;
                    for(int i = 0 ; i<productos.size() ; i++){
                        if(productos.get(i).getID().equals(DI)){
                           productos.get(i).setventasTotales(VentaCantidad);
                           acceselse = false;
                        }  
                    }
                    if(acceselse){
                        productos.add(new Productos(DI, Marc, Mod, Nom, Tip, VentaCantidad, Disponibilidad));
                    }
                }
                //Función para llenar el dataProductos para Piechart Tipo
                if(dataProductosTipo.isEmpty()){
                    dataProductosTipo.add(new Productos(Tip, VentaCantidad));
                }else{
                    accesMarca = true;
                    for(int i = 0 ; i<dataProductosTipo.size() ; i++){
                        if(dataProductosTipo.get(i).getTipo().equals(Tip)){
                           dataProductosTipo.get(i).setventasTotales(VentaCantidad);
                           accesMarca = false;
                        }  
                    }
                    if(accesMarca){
                        dataProductosTipo.add(new Productos(Tip, VentaCantidad));
                    }
                }
                //Función para llenar el dataProductos para Piechart Marca
                if(dataProductosMarca.isEmpty()){
                    dataProductosMarca.add(new Productos(VentaCantidad, Marc));
                }else{
                    accessTipo = true;
                    for(int i = 0 ; i<dataProductosMarca.size() ; i++){
                        if(dataProductosMarca.get(i).getMarca().equals(Marc)){
                           dataProductosMarca.get(i).setventasTotales(VentaCantidad);
                           accessTipo = false;
                        }  
                    }
                    if(accessTipo){
                        dataProductosMarca.add(new Productos(VentaCantidad, Marc));
                    }
                }
            }//Para evitar confusiones: ¡Fin While!    
        }
        for(int i = 0 ; i<dataProductosTipo.size() ; i++){
            DataTipopieChart.add(new PieChart.Data(dataProductosTipo.get(i).getTipo(), dataProductosTipo.get(i).getventasTotales()));      
        }
        for(int i = 0 ; i<dataProductosMarca.size() ; i++){
            DataMarcapieChart.add(new PieChart.Data(dataProductosMarca.get(i).getMarca(), dataProductosMarca.get(i).getventasTotales()));      
        }       
        int x;
        DataTipopieChart.forEach(data ->
            data.nameProperty().bind(
                Bindings.concat(
                    data.getName(), " ", data.pieValueProperty(), ""
                )
            )
              
        );
        DataMarcapieChart.forEach(data ->
            data.nameProperty().bind(
                Bindings.concat(
                    data.getName(), " ", data.pieValueProperty(), ""
                )
            )
        );
        return productos; 
    }
    
    public ObservableList<Productos> ObtenerProdComboBox(String combobox, int indicador) throws SQLException{
        String STSQL = "SELECT productos.ID, productos.Modelo, productos.Tipo, productos.Marca, productos.CantidadActual, "
                + "productos.Identificador, tblventadetalle.ventaCantidad "
                + "FROM productos " 
                + "INNER JOIN tblventadetalle "
                + "ON productos.ID = tblventadetalle.productoCodigo "
                + "INNER JOIN tblventas "
                + "ON tblventas.ventaFolio = tblventadetalle.ventaFolio ";
        if(indicador == 1){
            STSQL += "AND productos.Marca = '"+combobox+"'";
        }
        if(indicador == 2){
            STSQL += "AND productos.Tipo = '"+combobox+"'";
        }
        if(indicador == 3){
            STSQL += "AND productos.Modelo = '"+combobox+"'";
        }
        STSQL += " WHERE tblventas.ventaFecha BETWEEN '"+FechaInit+"' and '"+FinalFecha.plusDays(1)+"'";
        
        boolean acceselse, accesMarca, accessTipo;
        String Mod, Marc, DI, Nom, Tip;   
        int VentaCantidad, Disponibilidad;
        if(conn.QueryExecute(STSQL))
        {
            while (conn.setResult.next())
            {
                DI = conn.setResult.getString("ID");
                Marc = conn.setResult.getString("Marca");
                Mod = conn.setResult.getString("Modelo");
                Nom = conn.setResult.getString("Identificador");
                Tip = conn.setResult.getString("Tipo");
                VentaCantidad = conn.setResult.getInt("ventaCantidad");
                Disponibilidad = conn.setResult.getInt("CantidadActual");
                if(productos.size() == 0){
                    productos.add(new Productos(DI, Marc, Mod, Nom, Tip, VentaCantidad, Disponibilidad));
                }else{
                    acceselse = true;
                    for(int i = 0 ; i<productos.size() ; i++){
                        if(productos.get(i).getID().equals(DI)){
                           productos.get(i).setventasTotales(VentaCantidad);
                           acceselse = false;
                        }  
                    }
                    if(acceselse){
                        productos.add(new Productos(DI, Marc, Mod, Nom, Tip, VentaCantidad, Disponibilidad));
                    }
                } 
                //Función para llenar el dataProductos para Piechart Tipo
                if(dataProductosTipo.isEmpty()){
                    dataProductosTipo.add(new Productos(Tip, VentaCantidad));
                }else{
                    accesMarca = true;
                    for(int i = 0 ; i<dataProductosTipo.size() ; i++){
                        if(dataProductosTipo.get(i).getTipo().equals(Tip)){
                           dataProductosTipo.get(i).setventasTotales(VentaCantidad);
                           accesMarca = false;
                        }  
                    }
                    if(accesMarca){
                        dataProductosTipo.add(new Productos(Tip, VentaCantidad));
                    }
                }
                //Función para llenar el dataProductos para Piechart Marca
                if(dataProductosMarca.isEmpty()){
                    dataProductosMarca.add(new Productos(VentaCantidad, Marc));
                }else{
                    accessTipo = true;
                    for(int i = 0 ; i<dataProductosMarca.size() ; i++){
                        if(dataProductosMarca.get(i).getMarca().equals(Marc)){
                           dataProductosMarca.get(i).setventasTotales(VentaCantidad);
                           accessTipo = false;
                        }  
                    }
                    if(accessTipo){
                        dataProductosMarca.add(new Productos(VentaCantidad, Marc));
                    }
                }
            }    
        }
        for(int i = 0 ; i<dataProductosTipo.size() ; i++){
            DataTipopieChart.add(new PieChart.Data(dataProductosTipo.get(i).getTipo(), dataProductosTipo.get(i).getventasTotales()));      
        }
        for(int i = 0 ; i<dataProductosMarca.size() ; i++){
            DataMarcapieChart.add(new PieChart.Data(dataProductosMarca.get(i).getMarca(), dataProductosMarca.get(i).getventasTotales()));      
        }       
        int x;
        DataTipopieChart.forEach(data ->
            data.nameProperty().bind(
                Bindings.concat(
                    data.getName(), " ", data.pieValueProperty(), ""
                )
            )
              
        );
        DataMarcapieChart.forEach(data ->
            data.nameProperty().bind(
                Bindings.concat(
                    data.getName(), " ", data.pieValueProperty(), ""
                )
            )
        );
        return productos;
    }
    
    public void iniciarComboBox()
    {
        inicializar(true);
        String query = "SELECT DISTINCT Marca FROM productos ORDER BY Marca";
        
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbMarca.getItems().add(conn.setResult.getString("Marca"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
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
            }
        }
    }
    
    public void iniciarComboboxOcultos(){
        String query;
        cmbModelo.getSelectionModel().clearSelection();
        cmbModelo.getItems().clear();
        cmbModelo.setVisible(true);
        query = "SELECT DISTINCT Modelo FROM productos Where Marca = '"+cmbMarca.getValue()+"' ORDER BY Modelo";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbModelo.getItems().add(conn.setResult.getString("Modelo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        btnImprimir.setDisable(true);
        //Iniciar Combobox
        iniciarComboBox();
        //Inicializa las columnas de la tabla
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colMarca.setCellValueFactory(cellData -> cellData.getValue().marcaProperty());
        colModelo.setCellValueFactory(cellData -> cellData.getValue().modeloProperty());
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colIdentificador.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colNumVentas.setCellValueFactory(cellData -> cellData.getValue().ventasTotalesProperty());
        colDispon.setCellValueFactory(cellData -> cellData.getValue().disponProperty());

        //Funciones de Combobox
        cmbMarca.setOnAction(e -> {
            clearPieChart();
            iniciarComboboxOcultos();
            try {   
                tblProductos.refresh();
                productos.removeAll(productos);
                tblProductos.setItems(ObtenerProdComboBox(cmbMarca.getValue(), 1));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        cmbModelo.setOnAction(e -> {
            clearPieChart();
            try {   
                tblProductos.refresh();
                productos.removeAll(productos);
                tblProductos.setItems(ObtenerProdComboBox(cmbModelo.getValue(), 3));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        cmbTipo.setOnAction(e -> {
            clearPieChart();
            try {   
                tblProductos.refresh();
                productos.removeAll(productos);
                tblProductos.setItems(ObtenerProdComboBox(cmbTipo.getValue(), 2));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        
        cmbModelo.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbModelo.getValue(), cmbModelo.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbModelo.setValue(s);
                }
            }
        });
        
        cmbMarca.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbMarca.getValue(), cmbMarca.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbMarca.setValue(s);
                }
            }
        });
        
        cmbTipo.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbTipo.getValue(), cmbTipo.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbTipo.setValue(s);
                }
            }
        });
        
        
        //Funcón para botón "Aceptar"
        btnAceptar.setOnAction((ActionEvent e) -> {       
            if(DatePInicio.getValue() != null & DatePFin.getValue() != null){
                btnAceptar.setDisable(true);
                btnImprimir.setDisable(false);
                FechaInit = DatePInicio.getValue();
                FinalFecha = DatePFin.getValue();
                inicializar(false);
                //Inicializa la Tabla y piecharts
                try {
                    tblProductos.setItems(ObtenerProd());
                    pieChartMarca.setData(DataMarcapieChart);
                    pieChartMarca.setLabelLineLength(10);
                    pieChartMarca.setLegendSide(Side.TOP);
                    pieChartProductos.setData(DataTipopieChart);
                    pieChartProductos.setLabelLineLength(10);
                    pieChartProductos.setLegendSide(Side.TOP);
                } catch (SQLException ex) {
                    Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }); 
        btnReset.setOnAction((ActionEvent e) -> {       
            reset();
            btnAceptar.setDisable(false);
            btnImprimir.setDisable(true);
        });
        btnImprimir.setOnAction((ActionEvent e) -> {
            MensajeEstadisticaProducto = "\t\tEstadisticas de producto de: " +FechaInit+ " al "+FinalFecha.plusDays(1) + "\n";
            MensajeEstadisticaProducto += "Codigo \t\t\t Producto \t\t\t Numero de Ventas \t\t\t Disponibilidad\n";
            for(int i = 0 ; i<productos.size() ; i++){
                MensajeEstadisticaProducto += String.format("%-15s", productos.get(i).getID()) + "\n" + String.format("%-50s", productos.get(i).getMarca() + " " + productos.get(i).getModelo() + " " + productos.get(i).getTipo() + " " + productos.get(i).getNombre() + "\t")
                         + String.format("%-15s", productos.get(i).getventasTotales()) + "\t"+ productos.get(i).getDispon() + "\n ------------------------------ \n";
            }
            System.out.println(MensajeEstadisticaProducto);
            EnvioCorreo envio = new EnvioCorreo();
            envio.EnviarCorreoEstadisticas(MensajeEstadisticaProducto);
        });
        
    }

    public void inicializar(boolean flag){
        cmbMarca.setDisable(flag);
        cmbModelo.setDisable(flag);
        cmbTipo.setDisable(flag);
        btnReset.setDisable(flag);
    }
    
    public void clearPieChart(){
        productos.removeAll(productos);
        
        DataTipopieChart.removeAll(DataTipopieChart);
        pieChartProductos.setData(DataTipopieChart);
        
        DataMarcapieChart.removeAll(DataMarcapieChart);
        pieChartMarca.setData(DataMarcapieChart);
        
        dataProductosMarca.removeAll(dataProductosMarca);
        dataProductosTipo.removeAll(dataProductosTipo);
    }
    
    public void reset(){
        inicializar(true);
        cmbMarca.getSelectionModel().clearSelection();
        cmbMarca.getItems().clear();
        cmbModelo.getSelectionModel().clearSelection();
        cmbModelo.getItems().clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbTipo.getItems().clear();
        iniciarComboBox();
        DatePFin.setValue(null);
        DatePInicio.setValue(null);
        tblProductos.refresh();
        clearPieChart();
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}
