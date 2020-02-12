/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import xxcell.Conexion.Conexion;
import xxcell.model.Detalles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import xxcell.model.PrinterService;


public class VentasDiaController implements Initializable {

    Conexion conn = new Conexion();
    Conexion connCuenta = new Conexion();
    LocalDate fechaHoy =  LocalDate.now();
    DateTimeFormatter formato;
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    String local = Variables_Globales.local;
    String LocalBusqueda;
    
    @FXML
    private TableView<Detalles> tblDetalle;
    @FXML
    private TableColumn<Detalles, Number> colFolio;
    @FXML
    private TableColumn<Detalles, String> colHora;
    @FXML
    private TableColumn<Detalles, Number> colCantidad;
    @FXML
    private TableColumn<Detalles, Number> colTotal;
    @FXML
    private TableColumn<Detalles, String> colNumEmp;
    @FXML
    private TableColumn<Detalles, String> colDistribuidor;

    @FXML
    private Label lblCantidad;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblFecha;
    
    //Botones
    @FXML
    private JFXButton btnL58;
    @FXML
    private JFXButton btnL64;
    @FXML
    private JFXButton btnL127;
    @FXML
    private JFXButton btnConsultarVentas;
    
    @FXML
    private JFXDatePicker dateSeleccion;
    
    //Variables SidePane Detalles
    @FXML
    private TableView<Detalles> tblDescripcion;

    @FXML
    private TableColumn<Detalles, Number> colCantidadDetalle;

    @FXML
    private TableColumn<Detalles, String> colDescripcionDetalle;

    @FXML
    private TableColumn<Detalles, Number> colImporteDetalle;

    @FXML
    private JFXButton btnDevolver;

    @FXML
    private Label lblTotalDetalle;

    @FXML
    private Label lblNumTicketDetalle;

    @FXML
    private Label lblCajeroDetalle;

    @FXML
    private Label lblFechaDetalle;

    @FXML
    private JFXButton btnCancelarCompra;

    @FXML
    private JFXButton btnImprimirCopia;
    
    ObservableList<Detalles> detalles = FXCollections.observableArrayList();
    ObservableList<Detalles> descripcion = FXCollections.observableArrayList();
    
    Detalles aux;
    Detalles auxDescripcion;
    
    private Alert alert;
    
    //Funcion usada para obtener los datos y llenar las tablas, 
    //localBtn - Se usa para saber desde que local se quiere acceder
    //origen - Se usa para distinguir si se ocupa el llenado de la tabla detalles o la tabla descripcion
    public ObservableList<Detalles> ObtenerDetalles(String localBtn, boolean origen, int numFolio, LocalDate fechaSeleccionada, String Numempleado) throws SQLException{
        String codigo, empleado = null, STSQL, TooltipText, Local, distribuidor;
        String fechaFormat = null;
        String hora = null;
        int folio = 0, cantidad, cantidadDetalle, cantidadproductos = 0;
        int cantidadTotal = 0;
        float total, totalvendido = 0;
        Timestamp fecha;
        
        System.out.println("REcibo de localBtn  " + localBtn);
        
        if(localBtn.equals("1")){
            Local = Variables_Globales.local; 
        }else {
            Local = localBtn;
        }
        
        System.out.println("Numero de empleado: " + Numempleado);
        
        //Llena la tabla detalles
        if(!origen){
            
            STSQL = "SELECT tblventas.ventaFolio, tblventas.NumEmpleado, tblventas.CodigoDistribuidor, "
                    + "tblventas.ventaImporte, "
                    + "tblventas.ventaFecha, tblventas.ventaProductos "
                    + "FROM tblventas ";
            STSQL += "WHERE tblventas.ventaFecha > '"+fechaSeleccionada+"' ";
            STSQL += "AND tblventas.ventaFecha < '"+fechaSeleccionada.plusDays(1)+"'"
                    + "AND tblventas.NumLocal = '"+Local+"' "
                    + "GROUP BY ventaFolio";
            if(conn.QueryExecute(STSQL)) {
                while (conn.setResult.next()) {
                    folio = conn.setResult.getInt("ventaFolio");
                    empleado = conn.setResult.getString("NumEmpleado");
                    cantidad = conn.setResult.getInt("ventaProductos");
                    total = (conn.setResult.getFloat("ventaImporte"));
                    fecha = conn.setResult.getTimestamp("ventaFecha");
                    Format horaformatter = new SimpleDateFormat("k:mm");
                    
                    if(conn.setResult.getString("CodigoDistribuidor") != null ){
                       distribuidor =  conn.setResult.getString("CodigoDistribuidor");
                    }else{
                       distribuidor = "";
                    }
                    hora = horaformatter.format(fecha);
                    cantidadTotal += getCantidadDetalle(folio);
                    totalvendido = totalvendido + total;
                    detalles.add(new Detalles(folio, null, empleado, cantidad, total, null,hora, distribuidor));
                }
                formato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
                lblFecha.setText(Local+" - "+formato.format(fechaSeleccionada));
            }
            STSQL = "SELECT * FROM tblventas WHERE ventaFecha > '"+fechaSeleccionada+"' ";
            STSQL += "AND ventaFecha < '"+fechaSeleccionada.plusDays(1)+"' "
                   + "AND tblventas.NumLocal = '"+Local+"' ";
            total = 0;
            cantidad = 0;
            
            if(conn.QueryExecute(STSQL)) {
                while (conn.setResult.next()) {
                    System.out.println("-----");
                    System.out.println(conn.setResult.getInt("ventaFolio"));
                    System.out.println(conn.setResult.getFloat("ventaImporte"));
                    System.out.println(conn.setResult.getInt("ventaProductos"));
                    total = total + (conn.setResult.getFloat("ventaImporte"));
                    cantidad = cantidad + conn.setResult.getInt("ventaProductos");
                }
            }
            
            lblCantidad.setText(String.valueOf(cantidad));
            lblTotal.setText(String.valueOf("$ "+total));
            
            
            return detalles;
        } else {    //Llena la tabla descripcion
            System.out.println("abajo");
            STSQL = "SELECT * from tblventadetalle "
                + "INNER JOIN productos "
                +    "ON tblventadetalle.productoCodigo = productos.ID "
                + "INNER JOIN tblventas "
                +    "ON tblventas.ventaFolio = tblventadetalle.ventaFolio "
                + "WHERE tblventas.ventaFolio = "+numFolio+" AND tblventas.NumEmpleado = " + Numempleado + " "
                + "AND tblventadetalle.local = '" +local+"' "
                + "AND tblventas.ventaFecha > '"+fechaSeleccionada+"' "
                + "AND tblventas.ventaFecha < '"+fechaSeleccionada.plusDays(1)+"' ";    
            
            System.out.println(STSQL);
            if(conn.QueryExecute(STSQL))
            {
                while (conn.setResult.next())
                {
                    TooltipText = conn.setResult.getString("Marca");
                    TooltipText += " " + conn.setResult.getString("Modelo");
                    TooltipText += " " + conn.setResult.getString("Tipo");
                    TooltipText += " " + conn.setResult.getString("Identificador");
                    folio = conn.setResult.getInt("ventaFolio");
                    codigo = conn.setResult.getString("ID");
                    empleado = conn.setResult.getString("NumEmpleado");
                    cantidad = conn.setResult.getInt("ventaCantidad");
                    total = (conn.setResult.getFloat("productoPrecio"));
                    Format formatter = new SimpleDateFormat("EEEE d MMMM yyyy hh:mm aaa");
                    fecha = conn.setResult.getTimestamp("ventaFecha");
                    Format horaformatter = new SimpleDateFormat("hh:mm aaa");
                    hora = horaformatter.format(fecha);
                    fechaFormat = formatter.format(fecha);
                    cantidadproductos = cantidadproductos + cantidad;
                    totalvendido = totalvendido + total;
                    descripcion.add(new Detalles(folio, codigo, empleado, cantidad, total, TooltipText,hora));
                }
                lblNumTicketDetalle.setText(String.valueOf(folio));
                lblCajeroDetalle.setText(empleado);
                lblFechaDetalle.setText(fechaFormat);
                lblTotalDetalle.setText(String.valueOf(totalvendido));
//                lblCantidad.setText(String.valueOf(cantidadproductos));
//                lblTotal.setText(String.valueOf("$ "+totalvendido));
            }
            conn.sqlStmt.close();
            return descripcion;
        }
    }
    
    private int getCantidadDetalle(int folio) throws SQLException{
        String query;
        int cantidadDetalle = 0;
        
        query = "SELECT tblventadetalle.ventaCantidad FROM tblventadetalle "
                + "WHERE tblventadetalle.ventaFolio = '"+folio+"' ";

        if(connCuenta.QueryExecute(query)) {
            while (connCuenta.setResult.next()){
                cantidadDetalle += connCuenta.setResult.getInt("ventaCantidad");
            }
        }
        return cantidadDetalle;
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConnectionOpen();
        LocalBusqueda = Variables_Globales.local;
        Platform.runLater(() -> {
            colCantidadDetalle.prefWidthProperty().bind(tblDescripcion.widthProperty().divide(4)); // w * 1/4
            colDescripcionDetalle.prefWidthProperty().bind(tblDescripcion.widthProperty().divide(2)); // w * 2/4
            colImporteDetalle.prefWidthProperty().bind(tblDescripcion.widthProperty().divide(4)); // w * 1/4
            
            colFolio.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            colHora.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            colCantidad.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            colTotal.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            colNumEmp.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            colDistribuidor.prefWidthProperty().bind(tblDetalle.widthProperty().divide(6)); // w * 1/5
            
            dateSeleccion.setValue(today);
        });
        
        if(Variables_Globales.Rol.equals("0")){
            btnL127.setDisable(false);
            btnL58.setDisable(false);
            btnL64.setDisable(false);
        }else{
            btnCancelarCompra.setVisible(false);
            btnCancelarCompra.setDisable(true);
            btnDevolver.setVisible(false);
            btnDevolver.setDisable(true);
            btnL127.setVisible(false);
            btnL127.setDisable(true);
            btnL58.setVisible(false);
            btnL58.setDisable(true);
            btnL64.setVisible(false);
            btnL64.setDisable(true);
        }
        
        formato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        lblFecha.setText(Variables_Globales.local+" - "+formato.format(fechaHoy));
                
        colCantidadDetalle.setCellValueFactory(cellData -> cellData.getValue().CantidadProperty());
        colImporteDetalle.setCellValueFactory(cellData -> cellData.getValue().TotalProperty());
        colDescripcionDetalle.setCellValueFactory(cellData -> cellData.getValue().ToolTipProperty());
      
        colFolio.setCellValueFactory(cellData -> cellData.getValue().FolioProperty());
        colHora.setCellValueFactory(cellData -> cellData.getValue().getHora());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().CantidadProperty());
        colNumEmp.setCellValueFactory(cellData -> cellData.getValue().NumEmpleadoProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().TotalProperty());
        colDistribuidor.setCellValueFactory(cellData -> cellData.getValue().DistribuidorProperty());
        
        try {
            tblDetalle.setItems(ObtenerDetalles("1",false,0,today, ""));
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        btnL58.setOnAction((ActionEvent e) -> {       
            try {
                local = btnL58.getAccessibleText();
                DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
                LocalDate fechalocal = dateSeleccion.getValue();
                lblFecha.setText("L58"+" - "+dateFormato.format(fechalocal));
                inicializar();
                tblDescripcion.refresh();
                descripcion.clear();
                tblDetalle.refresh();
                detalles.removeAll(detalles);
                tblDetalle.setItems(ObtenerDetalles("L58",false,0,fechalocal, ""));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnL64.setOnAction((ActionEvent e) -> {       
            try {
                local = btnL64.getAccessibleText();
                DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
                LocalDate fechalocal = dateSeleccion.getValue();
                lblFecha.setText("L64"+" - "+dateFormato.format(fechalocal));
                inicializar();
                tblDescripcion.refresh();
                descripcion.clear();
                tblDetalle.refresh();
                detalles.removeAll(detalles);
                tblDetalle.setItems(ObtenerDetalles("L64",false,0,fechalocal, ""));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnL127.setOnAction((ActionEvent e) -> {       
            try {
                local = btnL127.getAccessibleText();
                DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
                LocalDate fechalocal = dateSeleccion.getValue();
                lblFecha.setText("L127"+" - "+dateFormato.format(fechalocal));
                inicializar();
                tblDescripcion.refresh();
                descripcion.clear();
                tblDetalle.refresh();
                detalles.removeAll(detalles);
                tblDetalle.setItems(ObtenerDetalles("L127",false,0,fechalocal, ""));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnImprimirCopia.setOnAction((ActionEvent e) -> {
            Conexion conection = new Conexion();
            Conexion conectionID = new Conexion();
            String query, mensaje, IDproducto, nombreProducto;
            int cantidad;
            float precio, total = 0;
            Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
            incompleteAlert.setTitle("Imprimir Copia");
            incompleteAlert.setHeaderText(null);
            
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
            Date fechaHoy = new Date();
            String fecha = formato.format(fechaHoy);
            PrinterService printerService = new PrinterService();
            
            if(tblDetalle.getSelectionModel().isEmpty()){
                mensaje = "No se ha seleccionado ningun folio. \n";
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnImprimirCopia.getScene().getWindow());
                incompleteAlert.showAndWait();
            }else{
                aux = tblDetalle.getSelectionModel().getSelectedItem();
                query = "SELECT * from tblventadetalle WHERE ventaFolio = '"+aux.getFolio()+"'";
                //System.out.println(aux.getFolio());
                if(conection.QueryExecute(query)){
                    try {
                        printerService.printString(Variables_Globales.impresora, "  COPIA  \n");
                        printerService.printString(Variables_Globales.impresora, " XXCELL - Reparacion y Accesorios \n");
                        printerService.printString(Variables_Globales.impresora, " 10 pnte y 5 de Mayo, Centro \n");
                        printerService.printString(Variables_Globales.impresora, "     "+fecha+" \n");
                        printerService.printString(Variables_Globales.impresora, " --------------------------\n");
                        printerService.printString(Variables_Globales.impresora, " Cantidad  Producto  Precio\n");
                        printerService.printString(Variables_Globales.impresora, " --------------------------\n");
                        while(conection.setResult.next()){
                            try {
                                nombreProducto = "";
                                IDproducto = conection.setResult.getString("productoCodigo");
                                cantidad = conection.setResult.getInt("ventaCantidad");
                                precio = conection.setResult.getFloat("productoPrecio");
                                total += precio;
                                query = "SELECT Marca, Modelo, Tipo, Identificador FROM productos WHERE ID = '"+IDproducto+"'";
                                if(conectionID.QueryExecute(query)){
                                    if(conectionID.setResult.first()){
                                        nombreProducto += conectionID.setResult.getString("Marca") + " ";
                                        nombreProducto += conectionID.setResult.getString("Modelo") + " ";
                                        nombreProducto += conectionID.setResult.getString("Tipo") + " ";
                                        nombreProducto += conectionID.setResult.getString("Identificador") + " ";
                                    }
                                }
                                printerService.printString(Variables_Globales.impresora, "\n"+ cantidad+ "  "
                                                                      + nombreProducto);
                                printerService.printString(Variables_Globales.impresora, "\n Codigo: " + IDproducto + 
                                                                            "                  $"+precio+"\n");
                                /*System.out.println("Cantidad: " + cantidad);
                                System.out.println("Producto: " + nombreProducto);
                                System.out.println("Precio: " + precio);*/
                            } catch (SQLException ex) {
                                Logger.getLogger(VentasDiaController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        printerService.printString(Variables_Globales.impresora, " -----------------\n");
                        printerService.printString(Variables_Globales.impresora, "  Total:     $"+total+"\n");
                        printerService.printString(Variables_Globales.impresora, " -----------------\n");
                        printerService.printString(Variables_Globales.impresora, "  Gracias por su preferencia \n ");
                        printerService.printString(Variables_Globales.impresora, "      LOF"+ aux.getFolio() +"PMExxCO"+Variables_Globales.local+" \n\n\n\n");
                        byte[] cutP = new byte[] { 0x1d, 'V', 1 };
                        printerService.printBytes(Variables_Globales.impresora, cutP);

//System.out.println("Total: " + total);
                    } catch (SQLException ex) {
                        Logger.getLogger(VentasDiaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    mensaje = "Verifique conexión con la base de datos. \n";
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(btnImprimirCopia.getScene().getWindow());
                    incompleteAlert.showAndWait();
                }
            }
            try {
                conection.setResult.close();
                conectionID.setResult.close();
            } catch (SQLException ex) {
                Logger.getLogger(VentasDiaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }   
    
    @FXML
    void MPressedTblDetalle(MouseEvent event) throws SQLException {
        String empleado;
        int folio;
        if(!tblDetalle.getSelectionModel().isEmpty()){
            aux = tblDetalle.getSelectionModel().getSelectedItem();
            folio = aux.getFolio();
            empleado = aux.getEmpleado();
            tblDescripcion.refresh();
            descripcion.clear();
            if(dateSeleccion.getValue().equals(today))
                tblDescripcion.setItems(ObtenerDetalles(Variables_Globales.local, true,folio,today, empleado));
            else
                tblDescripcion.setItems(ObtenerDetalles(Variables_Globales.local, true,folio,dateSeleccion.getValue(), empleado));
        }
    }
    
    @FXML
    void tblDetallesKeyPressed(KeyEvent event) throws SQLException {
        int folio; 
        String empleado;
        if(event.getCode() == KeyCode.ENTER){
            aux = tblDetalle.getSelectionModel().getSelectedItem();
            if(tblDetalle.getSelectionModel().getSelectedItem() != null){
                folio = aux.getFolio();
                empleado = aux.getEmpleado();
                tblDescripcion.refresh();
                descripcion.clear();
                if(dateSeleccion.getValue().equals(today))
                    tblDescripcion.setItems(ObtenerDetalles(Variables_Globales.local, true,folio,today, empleado));
                else
                    tblDescripcion.setItems(ObtenerDetalles(Variables_Globales.local, true,folio,dateSeleccion.getValue(), empleado));
            }
        }
    }
    
    @FXML
    void ActionBtnConsultarVentas(ActionEvent event) throws SQLException {
        DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        LocalDate fechalocal = dateSeleccion.getValue();
        lblFecha.setText(local+" - "+dateFormato.format(fechalocal));
        inicializar();
        tblDescripcion.refresh();
        descripcion.clear();
        tblDetalle.refresh();
        detalles.removeAll(detalles);
        tblDetalle.setItems(ObtenerDetalles("1",false,0,dateSeleccion.getValue(),""));
    }
    
    @FXML
    void ActionDevolver(ActionEvent event) throws SQLException {
        int cantidadDevuelta;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmacion");
        alert.initOwner(btnDevolver.getScene().getWindow());
        if(!tblDescripcion.getSelectionModel().isEmpty()){
            auxDescripcion = tblDescripcion.getSelectionModel().getSelectedItem();
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Devolucion de artículo");
            dialog.setHeaderText(null);
            dialog.setContentText("Cantidad a devolver de "+auxDescripcion.getCantidad());
            dialog.initOwner(btnDevolver.getScene().getWindow());
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()) {
                if(esnumero(result.get())){
                     cantidadDevuelta= Integer.parseInt(result.get());   
                     if(cantidadDevuelta <= auxDescripcion.getCantidad() && cantidadDevuelta > 0)
                        RegistrarDevolucion(cantidadDevuelta);
                     else {
                         alert.setHeaderText(null);
                         alert.setContentText("La cantidad debe ser mayor a 0 y menor al total");
                         alert.showAndWait(); 
                     }
                }
            }
        } else{
            System.out.println("Hay error");
        }
    }
    
    public void inicializar(){
        lblCajeroDetalle.setText("");
        lblFechaDetalle.setText("");
        lblNumTicketDetalle.setText("");
        lblTotalDetalle.setText("");
    }
    
    boolean esnumero (String x) {
        double number = 0;
        if(x.isEmpty()) {
            return true;
        } else {
            try {
                number = Double.parseDouble(x);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }
    
    public void RegistrarDevolucion(int cantidadDevuelta) throws SQLException{
        String sqlStmt;
        String pass;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmacion");
        alert.initOwner(btnDevolver.getScene().getWindow());

        conn.AutoCommit(false);
        
        if(VentaActualiza(cantidadDevuelta))
            if(VentaDetalleActualiza(cantidadDevuelta))
                if(ProductoActualiza(true, cantidadDevuelta))
                    if(FolioIncrementa())
                        if(InventarioRegistra(true,cantidadDevuelta)){
                            if(VentaActualizaUtilidad(cantidadDevuelta)){
                                conn.Commit();
                                alert.setHeaderText(null);
                                alert.setContentText("Se ha registrado la devolucion con exito");
                                alert.showAndWait();
                                tblDescripcion.refresh();
                                descripcion.clear();
                                tblDetalle.refresh();
                                detalles.removeAll(detalles);
                                inicializar();
                                tblDetalle.setItems(ObtenerDetalles("1",false,0,today, ""));
                            }else
                                conn.RollBack();
                        } else
                            conn.RollBack();
                    else
                        conn.RollBack();
                else
                    conn.RollBack();
            else
                conn.RollBack();
        else
            conn.RollBack();
    }

    private boolean ProductoActualiza(boolean b, int cantidadDevuelta) {
        boolean result = false;
        String sqlStmt;
        String local = this.local;
        String codigo = auxDescripcion.codigo();
        sqlStmt = "Update productos set ";
        sqlStmt += "Entradas = Entradas + "+cantidadDevuelta+" ,";
        sqlStmt += local+" = "+local+" + "+cantidadDevuelta+" ,";
        sqlStmt += "CantidadActual = CantidadActual + "+cantidadDevuelta+"";
        sqlStmt += " Where ID"
                + " = '"+codigo+"'";
        if(conn.QueryUpdate(sqlStmt))
            result = true;
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
    
    private boolean InventarioRegistra(boolean b, int cantidadDevuelta) throws SQLException {
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        boolean result = false;
        String sqlStmt;
        String codigo = auxDescripcion.codigo();
        String notas = "Entrada por devolucion";

        sqlStmt = "Insert into tblinventario (invfolio,invmovimiento,invfecha,invcantidad,productocodigo,ventafolio,invdescripcion) ";
        sqlStmt += " Values ("+getFolio()+",";
        sqlStmt += "'Devolucion',";
        sqlStmt += "'"+formato.format(fechaHoy)+"',";
        sqlStmt += ""+cantidadDevuelta+",";
        sqlStmt += "'"+codigo+"',"+auxDescripcion.getFolio()+",";
        sqlStmt += "'"+notas+" ')";

        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;
    }
    
    private int getFolio() throws SQLException{
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
            alert.initOwner(btnCancelarCompra.getScene().getWindow());
            alert.setTitle("Punto de Venta - Error al obtener folio");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        conn.setResult.close();
        return result;		
    }

    private boolean VentaActualiza(int cantidadDevuelta) {
        boolean result = false;
        String sqlStmt;
        String local = this.local;
        int folio = auxDescripcion.getFolio();
        double precio = auxDescripcion.total()/auxDescripcion.getCantidad();
        precio = precio * cantidadDevuelta;
        sqlStmt = "Update tblventas set ";
        sqlStmt += "ventaImporte = ventaImporte - "+precio+" ";
        sqlStmt += " Where ventaFolio"
                + " = '"+folio+"' AND NumLocal = '"+local+"'";
        System.out.println(sqlStmt);
        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;        
    }

    private boolean VentaDetalleActualiza(int cantidadDevuelta) {
        boolean result = false;
        String sqlStmt;
        String local = this.local;
        int folio = auxDescripcion.getFolio();
        double precio = auxDescripcion.total()/auxDescripcion.getCantidad();
        precio = precio * cantidadDevuelta;
        sqlStmt = "Update tblventadetalle set ";
        sqlStmt += "ventaCantidad = ventaCantidad - "+cantidadDevuelta+", "
                + "productoPrecio = productoPrecio - "+precio+"";
        sqlStmt += " Where ventaFolio"
                + " = '"+folio+"' "
                + "AND productoCodigo = '"+auxDescripcion.codigo()+"' ";
        if(conn.QueryUpdate(sqlStmt))
            result = true;
        return result;                
    }
    
    private boolean VentaActualizaUtilidad(int cantidadDevuelta) throws SQLException{
        boolean result = false;
        Double utilidad = 0.0, NuevaUtilidad;
        Double costo = 0.0;
        double precio = auxDescripcion.total()/auxDescripcion.getCantidad();
        String queryProducto = "SELECT `Costo` FROM `productos` WHERE ID = "+auxDescripcion.codigo();
        conn.QueryExecute(queryProducto);
        if(conn.setResult.first()){
            costo = conn.setResult.getDouble("Costo");
        }
        System.out.println("Costo: " + costo);
        String queryUtilidad = "Select ventaUtilidad FROM tblventas WHERE ventaFolio = '"+auxDescripcion.getFolio()+"'";
        conn.QueryExecute(queryUtilidad);
        if(conn.setResult.first()){
            utilidad = conn.setResult.getDouble("ventaUtilidad");
        }
        System.out.println("Utilidad: " + utilidad);
        NuevaUtilidad = utilidad - ((precio - costo) * cantidadDevuelta);
        System.out.println("Nueva Utilidad: " + NuevaUtilidad);
        String query = "UPDATE `tblventas` SET `ventaUtilidad`= '"+NuevaUtilidad+"' WHERE ventaFolio = '"+auxDescripcion.getFolio()+"'";
        if(conn.QueryUpdate(query))
            result = true;
        conn.setResult.close();
        return result;
    }
   
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
    
}
