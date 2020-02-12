package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.LogReport;
import xxcell.model.PrinterService;
import xxcell.model.ProductoVenta;
import xxcell.model.Productos;

public class VentaController implements Initializable {

    @FXML
    private StackPane rootVenta;

    @FXML
    private BorderPane paneBotones;
    //*****************Botones del view ***************
    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnCancelar;

    @FXML
    private JFXButton btnCobrar;

    @FXML
    private JFXButton btnIniciar;

    @FXML
    private JFXButton btnVentaEspera;
    //****************Etiquetas del view *************
    @FXML
    private Label lblImporteLetras;

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblDescuento;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblProductosCant;

    @FXML
    private Label lblNomDistribuidor;

    //Lista para Llenar la tabla de productos;
    ObservableList<ProductoVenta> productosVenta = FXCollections.observableArrayList();
    ObservableList<ObservableList<ProductoVenta>> auxProductosVenta = FXCollections.observableArrayList();

    @FXML
    private TableView<ProductoVenta> tblProductos;

    @FXML
    private TableColumn<ProductoVenta, String> tblColCodigo;

    @FXML
    private TableColumn<ProductoVenta, String> tblColNombre;

    @FXML
    private TableColumn<ProductoVenta, String> tblColModelo;

    @FXML
    private TableColumn<ProductoVenta, String> tblColPrecio;

    @FXML
    private TableColumn<ProductoVenta, String> tblColCantidad;

    @FXML
    private TableColumn<ProductoVenta, String> tblColImporte;

    @FXML
    private TableColumn<ProductoVenta, String> tblColDescuento;

    @FXML
    private JFXTextField txtCodigoBarras;

    @FXML
    private JFXTextField txtCodDistribuidor;

    @FXML
    private JFXTextField txtDescuento;

    @FXML
    private Spinner<Integer> spnFolio;
    
    Node principal = null;

    boolean agruparProductos;
    boolean flagVentaEspera = false;
    int usuario;
    Conexion conn = new Conexion();
    Conexion connPromos = new Conexion();

    private Alert alert;
    private Dialog dialog;

    Funciones fun = new Funciones();
    double utilidad = 0;
    
    
    private Node NodePrincipal = null;
   
    LogReport log;
    Window window;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //btnVentaEspera.setDisable(false);
        //btnVentaEspera.setVisible(false);
        
        
        ConnectionOpen();
        
        //********** Shortcuts de los botones *************
        Platform.runLater(() -> {
            window = paneBotones.getScene().getWindow();             
            log = new LogReport(window);
            tblColCodigo.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8
            tblColNombre.prefWidthProperty().bind(tblProductos.widthProperty().divide(4)); // w * 1/4
            tblColModelo.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8
            tblColPrecio.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8
            tblColCantidad.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8
            tblColDescuento.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8
            tblColImporte.prefWidthProperty().bind(tblProductos.widthProperty().divide(8)); // w * 1/8

            paneBotones.requestFocus();
            btnSearch.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN), new Runnable() {
                @Override
                public void run() {
                    btnSearch.fire();
                }

            });

            btnIniciar.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), new Runnable() {
                @Override
                public void run() {
                    btnIniciar.fire();
                }
            });

            btnCobrar.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN), new Runnable() {
                @Override
                public void run() {
                    btnCobrar.fire();
                }
            });

            txtDescuento.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), new Runnable() {
                @Override
                public void run() {
                    txtDescuento.requestFocus();
                }
            });

            btnCancelar.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), new Runnable() {
                @Override
                public void run() {
                    btnCancelar.fire();
                }
            });
        });

        tblColCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tblColNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tblColModelo.setCellValueFactory(cellData -> cellData.getValue().modeloProperty());
        tblColCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty());
        tblColPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty());
        tblColDescuento.setCellValueFactory(cellData -> cellData.getValue().descuentoProperty());
        tblColImporte.setCellValueFactory(cellData -> cellData.getValue().importeProperty());

        //Vuelve editable la columna Cantidad
        tblColCantidad.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColCantidad.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductoVenta, String>>() {
            //Cambia los valores de los label Total y Productos al editar la celda
            @Override
            public void handle(TableColumn.CellEditEvent<ProductoVenta, String> event) {
                double total;
                double importe;
                int productos;
                String cantidad;
                String precio;
                String preimporte;
                String precantidad;
                String formato;
                String descuento;
                String preciobase;
                double ganancia;
                String costo;
                double descontado;

                DecimalFormat formateador = new DecimalFormat("###0.00");

                precantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();
                preimporte = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
                descuento = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getDescuento();
                descontado = Double.valueOf(descuento);

                if (esnumero(event.getNewValue()) && Integer.valueOf(event.getNewValue()) >= 0) {
                    ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCantidadProperty(event.getNewValue());
                } else {
                    System.out.println("No es numero");
                }

                cantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();
                precio = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPrecio();
                preciobase = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPreciobase();
                costo = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCosto();

                if (preciobase == precio) {
                    importe = Integer.valueOf(cantidad) * Double.valueOf(precio);
                    importe = importe - (descontado / 100 * importe);
                    formato = formateador.format(importe);
                    ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setImporteProperty(formato);
                } else {
                    importe = Integer.valueOf(cantidad) * Double.valueOf(precio);
                    formato = formateador.format(importe);
                    ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setImporteProperty(formato);
                }

                productos = (Integer.valueOf(lblProductosCant.getText()) + Integer.valueOf(cantidad)) - Integer.valueOf(precantidad);

                lblProductosCant.setText(String.valueOf(productos));
                formato = formateador.format(importe);
                importe = Double.valueOf(((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte());

                ganancia = Double.valueOf(costo) * (Integer.valueOf(cantidad) - Integer.valueOf(precantidad));
                System.out.println("Ganancia: " + ganancia);
                utilidad = utilidad + ((Double.valueOf(importe) - Double.valueOf(preimporte)) - ganancia);
                System.out.println("Utilidad: " + utilidad);

                total = (Double.valueOf(lblTotal.getText()) + importe) - Double.valueOf(preimporte);
                lblTotal.setText(formateador.format(total));

                tblProductos.refresh();
            }
        });

        //Vuelve editable la columna Costo
        tblColPrecio.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColPrecio.setOnEditCommit((TableColumn.CellEditEvent<ProductoVenta, String> event) -> {
            String cantidad;
            String precio;
            String preciobase;
            String preimporte;
            String descuento;
            String importeS;
            String costo;
            double descontado;
            double importe;
            double total;
            double ganancia;
            double preutilidad = utilidad;
            String mensaje;

            DecimalFormat formateadorPorc = new DecimalFormat("###0.##");
            DecimalFormat formateador = new DecimalFormat("####.00");

            preimporte = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
            preciobase = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPreciobase();
            costo = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCosto();

            importe = Double.valueOf(event.getNewValue());
            precio = formateador.format(importe);
            if (esnumero(event.getNewValue()) && importe >= 0) {
                ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPrecioProperty(precio);
            }

            precio = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPrecio();
            cantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();

            descontado = 100 - ((Double.valueOf(precio) / Double.valueOf(preciobase)) * 100);
            System.out.println(descontado);

            importe = Integer.valueOf(cantidad) * Double.valueOf(precio);
            importeS = formateador.format(importe);
            descuento = formateadorPorc.format(descontado);

            ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setImporteProperty(importeS);
            ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDescuentoProperty(descuento);

            ganancia = Double.valueOf(costo) * Integer.valueOf(cantidad);
            ganancia = Double.valueOf(preciobase) - ganancia;
            System.out.println("Ganancia: " + ganancia);
            if (importe < Double.valueOf(preimporte)) {
                mensaje = String.valueOf(utilidad);
                utilidad = utilidad - ganancia - (importe - ganancia);
                System.out.println(utilidad);
            } else if (importe > Double.valueOf(preimporte)) {
                utilidad = utilidad + ganancia + (Double.valueOf(preimporte) - ganancia);
                System.out.println(utilidad);
            }

            total = (Double.valueOf(lblTotal.getText()) + Double.valueOf(importe)) - Double.valueOf(preimporte);
            lblTotal.setText(formateador.format(total));

            tblProductos.refresh();
        } //Cambia los valores de los label Total y Productos al editar la celda
        );

        //Vuelve editable la columna Descuento
        tblColDescuento.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColDescuento.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductoVenta, String>>() {
            //Cambia los valores de los label Total y Productos al editar la celda
            @Override
            public void handle(TableColumn.CellEditEvent<ProductoVenta, String> event) {
                double importe;
                double total;
                double descontado;
                double util;
                double ganancia;
                int productos;
                String descuento;
                String cantidad;
                String precio;
                String preimporte;
                String precantidad;
                String preciobase;
                String importeS;
                String importeSt;
                String costo;

                DecimalFormat formateador = new DecimalFormat("###0.00");

                precantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();
                preimporte = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
                costo = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCosto();
                preciobase = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPreciobase();

                if (esnumero(event.getNewValue()) && Double.valueOf(event.getNewValue()) <= 100) {
                    ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDescuentoProperty(event.getNewValue());
                }

                descuento = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getDescuento();

                cantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();
                precio = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPreciobase();

                descontado = Integer.valueOf(descuento);
                importe = (Double.valueOf(preciobase) - ((descontado / 100) * Double.valueOf(preciobase))) * Integer.valueOf(cantidad);

                importeS = formateador.format(importe);

                ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setImporteProperty(importeS);

                importeSt = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
                ganancia = Double.valueOf(importe) - Double.valueOf(costo) * Integer.valueOf(cantidad);

                util = Double.valueOf(preimporte) - Double.valueOf(costo) * Integer.valueOf(cantidad);

                utilidad = utilidad - util + ganancia;
                System.out.println(utilidad);

                total = (Double.valueOf(lblTotal.getText()) + importe) - Double.valueOf(preimporte);
                lblTotal.setText(formateador.format(total));

                tblProductos.refresh();
            }
        });

        //Vuelve editable la columna Importe
        tblColImporte.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColImporte.setOnEditCommit((TableColumn.CellEditEvent<ProductoVenta, String> event) -> {
            String importe;
            double total;
            double util;
            double ganancia;
            String cantidad;
            String precio;
            String preimporte;
            String precantidad;
            String costo;
            String preciobase;

            DecimalFormat formateador = new DecimalFormat("###0.00");

            precantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();
            preimporte = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
            preciobase = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPreciobase();
            costo = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCosto();
            cantidad = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCantidad();

            total = Double.valueOf(event.getNewValue());
            precio = formateador.format(total);

            if (esnumero(event.getNewValue()) && total >= 0) {
                ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).setImporteProperty(precio);
            }

            importe = ((ProductoVenta) event.getTableView().getItems().get(event.getTablePosition().getRow())).getImporte();
            ganancia = Double.valueOf(importe) - Double.valueOf(costo) * Integer.valueOf(cantidad);

            util = Double.valueOf(preimporte) - Double.valueOf(costo) * Integer.valueOf(cantidad);

            utilidad = utilidad - util + ganancia;

            total = (Double.valueOf(lblTotal.getText()) + Double.valueOf(importe)) - Double.valueOf(preimporte);
            lblTotal.setText(formateador.format(total));

            tblProductos.refresh();
        } //Cambia los valores de los label Total y Productos al editar la celda
        );

        txtCodigoBarras.setOnKeyPressed((KeyEvent event) -> {
            ConnectionOpen();
            String dato;
            String query;
            boolean flagDistribuidor = false;
            dato = txtCodigoBarras.getText().trim().toUpperCase();
            if (event.getCode() == KeyCode.ENTER) {
                if (!dato.isEmpty()) {
                    if (!ProductoEnVenta()) {
                        query = "Select * from distribuidores Where Codigo = '" + txtCodDistribuidor.getText() + "' AND Status = 'Activo'";
                        conn.QueryExecute(query);
                        try {
                            if (conn.setResult.next()) {
                                flagDistribuidor = true;
                            }
                        } catch (SQLException ex) {
                            String msjHeader = "¡Error de SQL!";
                            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                        try {
                            conn.setResult.close();
                        } catch (SQLException ex) {
                            String msjHeader = "¡Error de SQL!";
                            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    try {
                        tblProductos.setItems(ObtenerProd(flagDistribuidor));
                    } catch (SQLException ex) {
                        String msjHeader = "¡Error de SQL!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(ex, msjHeader, msjText);
                    }
                }
            }
            if (event.getCode() == KeyCode.DOWN) {
                tblProductos.requestFocus();
                tblProductos.getSelectionModel().selectFirst();
            }
        });

        txtDescuento.setOnKeyPressed((KeyEvent event) -> {
            double importe;
            double total;
            double descontado;
            String descuento;
            String precio;
            String preimporte;
            String precantidad;
            String importeS;

            DecimalFormat formateadorPorc = new DecimalFormat("####.##");
            DecimalFormat formateador = new DecimalFormat("###0.00");

            if (event.getCode() == KeyCode.ENTER) {
                if (esnumero(txtDescuento.getText()) && Double.valueOf(txtDescuento.getText()) <= 100) {
                    for (int fila = 0; fila < productosVenta.size(); fila++) {
                        precantidad = productosVenta.get(fila).getCantidad();
                        preimporte = productosVenta.get(fila).getImporte();
                        
                        productosVenta.get(fila).setDescuentoProperty(txtDescuento.getText());

                        descuento = productosVenta.get(fila).getDescuento();
                        
                        System.out.println("Pre cantidad :" + precantidad);
                        System.out.println("Pre importe  :" + preimporte);
                        System.out.println("Descuento    :" + descuento);
                        
                        precio = productosVenta.get(fila).getPreciobase();
                        descontado = Integer.valueOf(descuento);
                        importe = Double.valueOf(preimporte) - (Double.valueOf(preimporte) * 10)/100;
                        System.out.println("Importe    :" + importe);
                        importeS = formateador.format(importe);

                        productosVenta.get(fila).setImporteProperty(importeS);

                        total = (Double.valueOf(lblTotal.getText()) + importe) - Double.valueOf(preimporte);
                        lblTotal.setText(formateador.format(total));
                    }
                    tblProductos.refresh();
                    txtCodigoBarras.requestFocus();
                } else {
                    System.out.println("Descuento no valido");
                }
            }

            if (txtDescuento.getText().isEmpty()) {
                txtDescuento.setText("0");
            }

        });

        lblTotal.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            String sEntero;
            String decimal;
            int longitud;
            int iEntero;

            longitud = lblTotal.getText().length();

            sEntero = lblTotal.getText().substring(0, longitud - 3);
            iEntero = Integer.valueOf(sEntero);

            decimal = lblTotal.getText().substring(longitud - lblTotal.getText().length()) + "/100 M.N";
            lblImporteLetras.setText(fun.getStringofNumber(iEntero) + " PESOS " + decimal);
        });

        tblProductos.setEditable(true);
        tblProductos.setPlaceholder(new Label(""));

        try {
            inicializa();
        } catch (SQLException ex) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }

        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yy-MM-dd");
        SimpleDateFormat formatohora = new SimpleDateFormat("hh:mm aa");
        lblFecha.setText(formato.format(fechaHoy) + "\n" + formatohora.format(fechaHoy));
    }

    @FXML
    void BusquedaTabla(ActionEvent event) throws IOException, SQLException {
        Parent principal;
        boolean flagDistribuidor = false;
        String dato;
        String query;
        dato = txtCodDistribuidor.getText().trim().toUpperCase();
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaVenta.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.setTitle("Busqueda de producto");
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(btnSearch.getScene().getWindow());
        principalStage.showAndWait();
        if (Variables_Globales.BusquedaVenta.getID() != null) {
            txtCodigoBarras.setText(Variables_Globales.BusquedaVenta.getID());
            if (!ProductoEnVenta()) {
                try {
                    if (!dato.isEmpty()) {
                        query = "Select * from distribuidores Where Codigo = '" + txtCodDistribuidor.getText() + "' AND Status = 'Activo'";
                        ConnectionOpen();
                        conn.QueryExecute(query);
                        try {
                            if (conn.setResult.next()) {
                                flagDistribuidor = true;
                            }
                        } catch (SQLException ex) {
                            String msjHeader = "¡Error de SQL!";
                            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    conn.setResult.close();
                    tblProductos.setItems(ObtenerProd(flagDistribuidor));
                    Variables_Globales.BusquedaVenta = new Productos();
                } catch (SQLException ex) {
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                }
            }
            txtCodigoBarras.requestFocus();
        }
    }

    @FXML
    void KeyPressedTxtDistribuidor(KeyEvent event) throws SQLException {
        String dato;
        String query;
        String nombreDist;
        dato = txtCodDistribuidor.getText().trim().toUpperCase();
        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.initOwner(btnIniciar.getScene().getWindow());
        if (event.getCode() == KeyCode.ENTER) {
            if (productosVenta.isEmpty()) {
                if (!dato.isEmpty()) {
                    query = "Select * from distribuidores Where Codigo = '" + txtCodDistribuidor.getText() + "' AND Status = 'Activo'";
                    ConnectionOpen();
                    conn.QueryExecute(query);
                    if (conn.setResult.next()) {
                        txtDescuento.setDisable(true);
                        tblColDescuento.setEditable(false);
                        txtCodigoBarras.requestFocus();
                        nombreDist = conn.setResult.getString("Nombre");
                        nombreDist += " " + conn.setResult.getString("Apellido");
                        txtCodDistribuidor.setDisable(true);
                        lblNomDistribuidor.setText(nombreDist);
                    } else {
                        alert.setTitle("Punto de Venta - Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Distribuidor no existe o \n su status es inactivo");
                        alert.showAndWait();
                    }
                } else {
                    txtDescuento.requestFocus();
                }
            } else {
                alert.setTitle("Punto de Venta - Venta iniciada");
                alert.setHeaderText(null);
                alert.setContentText("Hay productos que no se les ha aplicado precio de distribuidor."
                        + "\n Reinicie la venta e ingrese primero el codigo.");
                alert.showAndWait();
            }
            conn.setResult.close();
        }
    }

    @FXML
    void ActionIniciar(ActionEvent event) throws SQLException {
        String qry;
        String cadena;
        TextInputDialog dialog = new TextInputDialog("Numero de Empleado");
        dialog.setTitle("Punto de Venta XXCELL");
        dialog.initOwner(btnIniciar.getScene().getWindow());
        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Punto de venta XXCELL");
        alert.initOwner(btnIniciar.getScene().getWindow());
        dialog.setHeaderText(null);
        dialog.setContentText("Ingrese su numero de empleado");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (esnumero(result.get())) {
                usuario = Integer.parseInt(result.get());
                qry = "SELECT * FROM empleado where NumEmpleado = '" + usuario + "'";
                ConnectionOpen();
                conn.QueryExecute(qry);
                try {
                    if (conn.setResult.first()) {
                        cadena = conn.setResult.getString("Nombre");
                        cadena = cadena + " " + conn.setResult.getString("Apellido");

                        lblUsuario.setText("Vendedor: " + cadena);
                        HabilitaVenta(true);
                        txtCodigoBarras.requestFocus();
                    } else {
                        alert.setHeaderText(null);
                        alert.setContentText("Usuario no valido");
                        alert.showAndWait();
                    }
                } catch (Exception ex) {
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                }
            } else {
                System.out.println("No es número");
            }
        }
        conn.setResult.close();
    }

    @FXML
    void ActionCancelar(ActionEvent event) {
        System.out.println("Cancelar");
        if (!btnCancelar.isDisabled()) {
            try {
                inicializa();
            } catch (SQLException ex) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }

    @FXML
    void ActionCobrar(ActionEvent event) throws IOException {
        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Punto de venta XXCELL");
        alert.initOwner(btnIniciar.getScene().getWindow());
        if (productosVenta.isEmpty()) {
            alert.setHeaderText(null);
            alert.setContentText("Para cobrar es necesario introducir al menos un producto");
            alert.showAndWait();
            txtCodigoBarras.requestFocus();
        } else {
            Variables_Globales.totalVenta = lblTotal.getText();
            Variables_Globales.ventaRealizada = false;

            mostrarCobrar();

            if (Variables_Globales.ventaRealizada) {
                GrabarVenta(event);
            }
        }
    }

    @FXML
    void MPressedTxtCodDistribuidor(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Parent principal;
            principal = FXMLLoader.load(getClass().getResource("/xxcell/view/BusquedaDistribuidor.fxml"));
            Stage principalStage = new Stage();
            scene = new Scene(principal);
            principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
            principalStage.setScene(scene);
            principalStage.initModality(Modality.APPLICATION_MODAL);
            principalStage.initOwner(btnSearch.getScene().getWindow());
            principalStage.showAndWait();
            System.out.println(Variables_Globales.BusquedaDistribuidor.getCodigo());
            if (Variables_Globales.BusquedaDistribuidor.getCodigo() != null) {
                txtCodDistribuidor.setText(Variables_Globales.BusquedaDistribuidor.getCodigo());
                Variables_Globales.BusquedaVenta = new Productos();
            }
        }
    }

    @FXML
    void ActionVentaEspera(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Venta.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.setMaximized(true);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(btnCancelar.getScene().getWindow());
        principalStage.showAndWait();

    }

    //Accion para suprimir la fila seleccionada de la tabla con la tecla DELETE
    @FXML
    void ActionDelete(KeyEvent event) {
        ObservableList<ProductoVenta> productoSeleccionado, todosProductos;
        ProductoVenta producto;
        double importe;
        int productos;
        double total;
        String cantidad;
        String precio;
        String preimporte;
        double descontado;
        String formato;
        String descuento;
        String costo;
        double ganancia;
        DecimalFormat formateador = new DecimalFormat("###0.00");

        if (event.getCode() == KeyCode.DELETE) {
            //Si se presiona la tecla DELETE se deben actualizar los label Total y Productos y eliminar la fila seleccionada
            producto = tblProductos.getSelectionModel().getSelectedItem();
            todosProductos = tblProductos.getItems();
            productoSeleccionado = tblProductos.getSelectionModel().getSelectedItems();

            costo = producto.getCosto();
            preimporte = producto.getImporte();
            cantidad = producto.getCantidad();

            ganancia = Double.valueOf(costo) * Integer.valueOf(cantidad);
            System.out.println("Ganancia: " + ganancia);
            utilidad = utilidad - (Double.valueOf(preimporte) - ganancia);

            productos = (Integer.valueOf(lblProductosCant.getText()) - Integer.valueOf(cantidad));

            lblProductosCant.setText(String.valueOf(productos));

            total = Double.valueOf(lblTotal.getText()) - Double.valueOf(preimporte);
            lblTotal.setText(formateador.format(total));

            productoSeleccionado.forEach(todosProductos::remove);
            txtCodigoBarras.requestFocus();
        }

        if (event.getCode() == KeyCode.ADD) {
            producto = tblProductos.getSelectionModel().getSelectedItem();
            descuento = producto.getDescuento();
            cantidad = producto.getCantidad();
            descontado = Double.valueOf(descuento);
            precio = producto.getPrecio();
            preimporte = producto.getImporte();
            costo = producto.getCosto();

            productos = Integer.valueOf(cantidad) + 1;
            producto.setCantidadProperty(Integer.toString(productos));

            productos = Integer.valueOf(lblProductosCant.getText()) + 1;
            lblProductosCant.setText(String.valueOf(productos));

            //Importe de la venta
            if (producto.getPreciobase() == producto.getPrecio()) {
                total = (Double.valueOf(precio) * (Integer.valueOf(cantidad) + 1));
                total = total - (descontado / 100 * total);
                formato = formateador.format(total);
                producto.setImporteProperty(formato);
            } else {
                total = (Double.valueOf(precio) * (Integer.valueOf(cantidad) + 1));
                formato = formateador.format(total);
                producto.setImporteProperty(formato);
            }

            utilidad = utilidad + (Double.valueOf(precio) - Double.valueOf(costo));
            System.out.println("Utilidad: " + utilidad);

            total = (Double.valueOf(lblTotal.getText()) + total) - Double.valueOf(preimporte);
            lblTotal.setText(formateador.format(total));

            tblProductos.refresh();
            txtCodigoBarras.setText("");
        }

        if (event.getCode() == KeyCode.SUBTRACT) {
            producto = tblProductos.getSelectionModel().getSelectedItem();
            descuento = producto.getDescuento();
            descontado = Double.valueOf(descuento);
            cantidad = producto.getCantidad();
            preimporte = producto.getImporte();
            costo = producto.getCosto();

            if (Integer.valueOf(cantidad) > 1) {
                precio = producto.getPrecio();

                productos = Integer.valueOf(cantidad) - 1;
                producto.setCantidadProperty(Integer.toString(productos));

                productos = Integer.valueOf(lblProductosCant.getText()) - 1;
                lblProductosCant.setText(String.valueOf(productos));

                //Importe de la venta
                if (producto.getPreciobase() == producto.getPrecio()) {
                    total = (Double.valueOf(precio) * (Integer.valueOf(cantidad) - 1));
                    total = total - (descontado / 100 * total);
                    formato = formateador.format(total);
                    producto.setImporteProperty(formato);
                } else {
                    total = (Double.valueOf(precio) * (Integer.valueOf(cantidad) - 1));
                    formato = formateador.format(total);
                    producto.setImporteProperty(formato);
                }

                total = (Double.valueOf(lblTotal.getText()) + total) - Double.valueOf(preimporte);
                lblTotal.setText(formateador.format(total));

                utilidad = utilidad - (Double.valueOf(precio) - Double.valueOf(costo));
                System.out.println("Utilidad: " + utilidad);

                tblProductos.refresh();
                txtCodigoBarras.setText("");
            }
        }
    }

    public void inicializa() throws SQLException {
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yy-MM-dd");
        SimpleDateFormat formatohora = new SimpleDateFormat("hh:mm aa");
        lblFecha.setText(formato.format(fechaHoy) + "\n" + formatohora.format(fechaHoy));
        spnFolio.setValueFactory((new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000000, getFolioVenta())));
        spnFolio.getEditor().setStyle("-fx-font: 20pt 'verdana'; "
                + "-fx-text-fill: black; "
                + "-fx-alignment: CENTER_RIGHT;");
        lblProductosCant.setText("0");
        lblTotal.setText("0.00");
        productosVenta.clear();
        lblUsuario.setText(null);
        lblImporteLetras.setText(null);
        txtDescuento.setText("0");
        txtDescuento.setVisible(false);
        txtCodDistribuidor.setVisible(false);
        txtCodDistribuidor.setDisable(true);
        txtCodDistribuidor.setText("");
        lblNomDistribuidor.setText("");
        utilidad = 0;
        System.out.println("---- fin venta ----");
        HabilitaVenta(false);
    }

    private void HabilitaVenta(boolean venta) {
        //Habilitados en venta
        System.out.println(venta);
        tblProductos.setDisable(!venta);
        btnCobrar.setDisable(!venta);
        txtCodigoBarras.setDisable(!venta);
        btnIniciar.setDisable(venta);
        btnSearch.setDisable(!venta);
        try {
            spnFolio.setValueFactory((new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000000, getFolioVenta())));
        } catch (SQLException ex) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
        spnFolio.setEditable(!venta);
        txtDescuento.setDisable(!venta);
        txtDescuento.setVisible(venta);
        //btnVentaEspera.setDisable(venta);
        //btnVentaEspera.setVisible(!venta);
        txtCodDistribuidor.setVisible(venta);
        txtCodDistribuidor.setDisable(!venta);
    }

    private boolean ProductoEnVenta() {
        int fila;
        String dato = "";
        String precio;
        String cantidad;
        String costo;
        boolean result = false;
        int productos;
        double total;

        DecimalFormat formateador = new DecimalFormat("###0.00");

        dato = txtCodigoBarras.getText().trim().toUpperCase();

        if (!dato.isEmpty()) {
            for (fila = 0; fila < productosVenta.size(); fila++) {
                if (productosVenta.get(fila).getCodigo().toUpperCase().equals(dato)) {
                    cantidad = productosVenta.get(fila).getCantidad();
                    precio = productosVenta.get(fila).getPrecio();
                    costo = productosVenta.get(fila).getCosto();

                    productos = Integer.valueOf(cantidad) + 1;
                    productosVenta.get(fila).setCantidadProperty(Integer.toString(productos));

                    productos = Integer.valueOf(lblProductosCant.getText()) + 1;
                    lblProductosCant.setText(String.valueOf(productos));

                    //Importe de la venta
                    total = Double.valueOf(precio) * (Integer.valueOf(cantidad) + 1);
                    productosVenta.get(fila).setImporteProperty(Double.toString(total));

                    total = Double.valueOf(lblTotal.getText()) + Double.valueOf(precio);
                    lblTotal.setText(formateador.format(total));

                    utilidad = utilidad + (Double.valueOf(precio) - Double.valueOf(costo));
                    System.out.println("Utilidad: " + utilidad);

                    tblProductos.refresh();
                    txtCodigoBarras.setText("");
                    result = true;
                }
            }
        }
        return result;
    }

    public ObservableList<ProductoVenta> ObtenerProd(boolean flagDistribuidor) throws SQLException {
        double total;
        boolean continuar = true;
        boolean endPromo = false;
        int productos;
        String query, queryPromos;
        String dato = "";

        double precioDouble, costoDouble;

        String modelo, tipo, codigo, nombre, cantidad, preciobase, precio, precioDistribuidor, importe, descuento, costo;

        dato = txtCodigoBarras.getText().trim().toUpperCase();

        DecimalFormat formateador = new DecimalFormat("###0.00");

        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.initOwner(btnIniciar.getScene().getWindow());

        if (!dato.isEmpty()) {
            query = "Select * from productos Where ID = '" + txtCodigoBarras.getText() + "'";
            ConnectionOpen();
            conn.QueryExecute(query);
            try {
                if (conn.setResult.next()) {
                    if (conn.setResult.getInt("CantidadActual") <= 0) {
                        alert.setTitle("Punto de Venta - Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Producto sin existencias");
                        alert.showAndWait();
                        txtCodigoBarras.setText("");
                        continuar = false;
                    }
                    if (continuar && flagDistribuidor) {
                        codigo = conn.setResult.getString("ID");
                        nombre = conn.setResult.getString("Marca");
                        nombre += " " + conn.setResult.getString("Modelo");
                        nombre += " " + conn.setResult.getString("Tipo");
                        nombre += " " + conn.setResult.getString("Identificador");
                        modelo = conn.setResult.getString("Modelo");
                        cantidad = "1";
                        descuento = txtDescuento.getText();
                        preciobase = conn.setResult.getString("PrecDist");
                        importe = Double.toString(Double.valueOf(preciobase) - ((Double.valueOf(descuento) / 100) * Double.valueOf(preciobase)));
                        //importe = formateador.format(Double.valueOf(importe));
                        precio = conn.setResult.getString("PrecDist");
                        precioDistribuidor = conn.setResult.getString("PrecDist");
                        costo = conn.setResult.getString("Costo");

                        precioDouble = conn.setResult.getDouble("PrecDist");
                        costoDouble = conn.setResult.getDouble("Costo");
                        utilidad = utilidad + precioDouble - costoDouble;
                        productosVenta.add(new ProductoVenta(codigo, nombre, modelo, cantidad, preciobase, precio, precioDistribuidor, descuento, importe, costo));
                        txtCodigoBarras.setText("");

                        productos = Integer.valueOf(lblProductosCant.getText()) + 1;
                        lblProductosCant.setText(String.valueOf(productos));

                        total = Double.valueOf(lblTotal.getText()) + Double.valueOf(importe);
                        lblTotal.setText(formateador.format(total));
                    } else if (continuar && !flagDistribuidor) {
                        codigo = conn.setResult.getString("ID");
                        nombre = conn.setResult.getString("Marca");
                        nombre += " " + conn.setResult.getString("Modelo");
                        nombre += " " + conn.setResult.getString("Tipo");
                        nombre += " " + conn.setResult.getString("Identificador");
                        modelo = conn.setResult.getString("Modelo");
                        cantidad = "1";
                        descuento = txtDescuento.getText();
                        preciobase = conn.setResult.getString("PrecPub");
                        importe = Double.toString(Double.valueOf(preciobase) - ((Double.valueOf(descuento) / 100) * Double.valueOf(preciobase)));
                        importe = formateador.format(Double.valueOf(importe));
                        precio = conn.setResult.getString("PrecPub");
                        precioDistribuidor = conn.setResult.getString("PrecDist");
                        costo = conn.setResult.getString("Costo");

                        precioDouble = conn.setResult.getDouble("PrecPub");
                        costoDouble = conn.setResult.getDouble("Costo");
                        utilidad = utilidad + (precioDouble - costoDouble);
                        System.out.println("Utilidad: " + utilidad);
                        productosVenta.add(new ProductoVenta(codigo, nombre, modelo, cantidad, preciobase, precio, precioDistribuidor, descuento, importe, costo));
                        tblProductos.refresh();

                        txtCodigoBarras.setText("");

                        productos = Integer.valueOf(lblProductosCant.getText()) + 1;
                        lblProductosCant.setText(String.valueOf(productos));

                        total = Double.valueOf(lblTotal.getText()) + Double.valueOf(importe);
                        lblTotal.setText(formateador.format(total));
                    }
                } else { //Aqui va el query de busqueda en tblpromocions
                    query = "Select * from promociones Where CodigoPromocion = '" + txtCodigoBarras.getText() + "'";
                    ConnectionOpen();
                    conn.QueryExecute(query);
                    try {
                        if (conn.setResult.next()) {
                            endPromo = true;
                            System.out.println("Se ha encontrado la promo");
                            queryPromos = "SELECT productos.ID, productos.Identificador, "
                                    + "productos.Modelo, productos.Costo, "
                                    + "productos.Tipo, productos.CantidadActual, "
                                    + "promociones.PrecioPromocion, promociones.CodigoPromocion "
                                    + "FROM productos "
                                    + "INNER JOIN promociones "
                                    + "ON productos.ID = promociones.CodigoProducto "
                                    + "AND '"+txtCodigoBarras.getText()+"' = promociones.CodigoPromocion";
                            System.out.println(queryPromos);
                            connPromos.QueryExecute(queryPromos);
                            while(connPromos.setResult.next()){
                                if (connPromos.setResult.getInt("CantidadActual") <= 0) {
                                    alert.setTitle("Punto de Venta - Error");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Producto sin existencias");
                                    alert.showAndWait();
                                    txtCodigoBarras.setText("");
                                    continuar = false;
                                }
                                if (continuar) {
                                    codigo = connPromos.setResult.getString("ID");
                                    nombre = connPromos.setResult.getString("Identificador");
                                    nombre += " - " + connPromos.setResult.getString("Tipo");
                                    modelo = connPromos.setResult.getString("Modelo");
                                    cantidad = "1";
                                    importe = connPromos.setResult.getString("PrecioPromocion");
                                    costo = connPromos.setResult.getString("Costo");
                                    costoDouble = connPromos.setResult.getDouble("Costo");
                                    precioDouble = connPromos.setResult.getDouble("PrecioPromocion");
                                    utilidad = utilidad + precioDouble - costoDouble;
                                    System.out.println("Utilidad: " + utilidad);
                                    productosVenta.add(new ProductoVenta(codigo, nombre, modelo, cantidad, importe, importe, importe, "0", importe, costo));
                                    tblProductos.refresh();
                                    
                                    txtCodigoBarras.setText("");
                                    
                                    productos = Integer.valueOf(lblProductosCant.getText()) + 1; 
                                    lblProductosCant.setText(String.valueOf(productos));
                                    
                                    total = Double.valueOf(lblTotal.getText()) + Double.valueOf(importe);
                                    lblTotal.setText(formateador.format(total));
                                }
                            }
                        }
                        if (!endPromo) {
                            alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Producto no encontrado");
                            alert.setHeaderText(null);
                            alert.setContentText("El producto no esta registrado");
                            alert.initOwner(btnIniciar.getScene().getWindow());
                            alert.showAndWait();
                            txtCodigoBarras.setText("");
                        }
                    } catch (SQLException e) {
                        String msjHeader = "¡Error de SQL!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(e, msjHeader, msjText);
                    }

                }
                conn.setResult.close();
            } catch (SQLException e) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(e, msjHeader, msjText);
            }
        }
        return productosVenta;
    }

    boolean esnumero(String x) {
        double number = 0;
        if (x.isEmpty()) {
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

    private int getFolioVenta() throws SQLException {
        int result = 0;
        String query;
        
        if(Variables_Globales.local.equals("L127")){
            query = "Select folioventa from tblfolios";
            ConnectionOpen();
            conn.QueryExecute(query);
            try {
                if (conn.setResult.next()) {
                    result = conn.setResult.getInt("folioventa");
                }
            } catch (SQLException e) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(e, msjHeader, msjText);
            }
            try {
                conn.setResult.close();
            } catch (SQLException ex) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        
        if(Variables_Globales.local.equals("L58")){
            query = "Select folioventa58 from tblfolios";
            ConnectionOpen();
            conn.QueryExecute(query);
            try {
                if (conn.setResult.next()) {
                    result = conn.setResult.getInt("folioventa58");
                }
            } catch (SQLException e) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(e, msjHeader, msjText);
            }
            conn.setResult.close();
        }
        
        if(Variables_Globales.local.equals("L64")){
            query = "Select folioventa64 from tblfolios";
            ConnectionOpen();
            conn.QueryExecute(query);
            try {
                if (conn.setResult.next()) {
                    result = conn.setResult.getInt("folioventa64");
                }
            } catch (SQLException e) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(e, msjHeader, msjText);
            }
            conn.setResult.close();
        }
        return result;
    }

    private int getFolioInventario() throws SQLException {
        int result = 0;
        String query;
        query = "Select folioinventario from tblfolios";
        ConnectionOpen();
        conn.QueryExecute(query);

        try {
            if (conn.setResult.next()) {
                result = conn.setResult.getInt("folioinventario");
            }
        } catch (SQLException e) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(e, msjHeader, msjText);
        }
        conn.setResult.close();
        return result;
    }

    private void GrabarVenta(ActionEvent event) {
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Venta realizada");
        alert.initOwner(btnIniciar.getScene().getWindow());
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yy-MM-dd");
        SimpleDateFormat formatohora = new SimpleDateFormat("hh:mm aa");
        lblFecha.setText(formato.format(fechaHoy) + "\n" + formatohora.format(fechaHoy));

        boolean commit = false;

        ventaArchivo();
        
        conn.AutoCommit(false);
        if (GrabaVentaEncabezado()) {    
            try {
                if (GrabarVentaDetalle()) {  
                    try {
                        if (ActualizaFolioVenta()) {
                        alert.setContentText("Venta realizada con exito");
                        alert.showAndWait();
                        commit = true;
                        }
                    } catch (SQLException ex) {
                        String msjHeader = "¡Error de SQL!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(ex, msjHeader, msjText);
                    }
                }
            } catch (SQLException ex) {
                String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        
        if (commit) {
            //conn.Commit();
            PrinterService printerService = new PrinterService();
            if(printerService.PrinterConexion()){
                imprimetTicket();
                try {
                    mostrarBienvenida(event);
                } catch (IOException ex) {
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                }
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de Impresora");
                alert.initOwner(btnIniciar.getScene().getWindow());
                alert.setContentText("Error con la conexión a la impresora: " + Variables_Globales.impresora);
                alert.showAndWait();
            }
        } else {
            //conn.RollBack();
        }
        try {
            inicializa();
        } catch (SQLException ex) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
    }

    private void mostrarCobrar() throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Cobrar.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(btnCancelar.getScene().getWindow());
        principalStage.showAndWait();
    }

    private boolean GrabaVentaEncabezado() {
        boolean result = false;
        String sqlStmt;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        SimpleDateFormat formatoErr = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaHoy = new Date();
        String fecha = formato.format(fechaHoy);
        String fecha2 = formatoErr.format(fechaHoy);
        System.out.println(txtCodDistribuidor.getText());
        if (txtCodDistribuidor.getText().equals("")) {
            sqlStmt = "Insert into tblventas ";
            sqlStmt += "(ventafolio,";
            sqlStmt += "ventafecha,";
            sqlStmt += "ventaproductos,";
            sqlStmt += "ventaimporte,"
                    + "ventaUtilidad,";
            sqlStmt += "ventacancelada,";
            sqlStmt += "NumLocal,";
            sqlStmt += "NumEmpleado)";
            sqlStmt += " Values ";
            sqlStmt += "(" + spnFolio.getValue().toString() + ",";
            sqlStmt += "'" + fecha + "',";
            sqlStmt += lblProductosCant.getText() + ",";
            sqlStmt += lblTotal.getText() + ",";
            sqlStmt += utilidad + ",";
            sqlStmt += "'',";
            sqlStmt += "'" + Variables_Globales.local + "',";
            sqlStmt += usuario + ")";
        } else {
            sqlStmt = "Insert into tblventas ";
            sqlStmt += "(ventafolio,";
            sqlStmt += "ventafecha,";
            sqlStmt += "ventaproductos,";
            sqlStmt += "ventaimporte,"
                    + "ventaUtilidad,";
            sqlStmt += "ventacancelada,";
            sqlStmt += "NumLocal,";
            sqlStmt += "NumEmpleado, CodigoDistribuidor)";
            sqlStmt += " Values ";
            sqlStmt += "(" + spnFolio.getValue().toString() + ",";
            sqlStmt += "'" + fecha + "',";
            sqlStmt += lblProductosCant.getText() + ",";
            sqlStmt += lblTotal.getText() + ",";
            sqlStmt += utilidad + ",";
            sqlStmt += "'',";
            sqlStmt += "'" + Variables_Globales.local + "',";
            sqlStmt += usuario + ",'" + txtCodDistribuidor.getText() + "')";
        }
        System.out.println(sqlStmt);
        ConnectionOpen();
  
        if (conn.QueryUpdate(sqlStmt)) {
            result = true;
            System.out.println("Entro a Encabezado");
        }else{
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en Venta");
            alert.initOwner(btnIniciar.getScene().getWindow());
            alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error GrabaVentaEncabezado");
            alert.showAndWait();
        }
        return result;
    }

    private boolean GrabarVentaDetalle() throws SQLException {
        boolean result = true;
        int fila;
        String codigo;
        String nombre;
        String precio;
        String cantidad;
        String costo;

        String sqlStmt;

        for (fila = 0; fila < productosVenta.size(); fila++) {
            codigo = productosVenta.get(fila).getCodigo();
            nombre = productosVenta.get(fila).getNombre();
            precio = productosVenta.get(fila).getImporte();
            cantidad = productosVenta.get(fila).getCantidad();

            if (ActualizaExistenciaSalida(codigo, cantidad, false)) {
                if (RegistraMovimiento(codigo, cantidad, false)) {
                    sqlStmt = "Insert into tblventadetalle ";
                    sqlStmt += "(ventafolio, ";
                    sqlStmt += " productocodigo,";
                    sqlStmt += " ventacantidad,";
                    sqlStmt += " productoprecio, local, idVentasDetalle)";
                    sqlStmt += " Values ";
                    sqlStmt += "(" + spnFolio.getValue().toString() + ",";
                    sqlStmt += "'" + codigo + "',";
                    sqlStmt += cantidad + ",";
                    sqlStmt += precio + ",";
                    sqlStmt += "'"+Variables_Globales.localPublico + "', 0)";

                    System.out.println(sqlStmt);
                    ConnectionOpen();
                    if (!conn.QueryUpdate(sqlStmt)) {
                        Conexion connRespaldo = new Conexion();
                        if(!connRespaldo.QueryExecute(sqlStmt)){
                            result = false;
                            FileWriter fichero = null;
                            PrintWriter pw = null;
                            try
                            {
                                SimpleDateFormat formatoErr = new SimpleDateFormat("yyyy-MM-dd");
                                Date fechaHoy = new Date();
                                String fecha2 = formatoErr.format(fechaHoy);
                                fichero = new FileWriter(Variables_Globales.RutaImagenes+"DeteccionErrores"+fecha2+".txt", true);
                                pw = new PrintWriter(fichero);
                                pw.println("   GrabarVentaDetalle "); 
                                pw.println("VentaFolio: " + spnFolio.getValue().toString()); 
                                pw.println("ProductoCodigo: " + codigo); 
                                pw.println("Cantidad: " + cantidad); 
                                pw.println("Precio: " + precio); 
                            } catch (Exception e) {
                                String msjHeader = "¡Error de SQL!";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(e, msjHeader, msjText);
                            } finally {
                               try {
                               // Nuevamente aprovechamos el finally para 
                               // asegurarnos que se cierra el fichero.
                               if (null != fichero)
                                  fichero.close();
                               } catch (Exception e2) {
                                  String msjHeader = "¡Error de SQL!";
                                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                    log.SendLogReport(e2, msjHeader, msjText);
                               }
                            }
                        }
                        break;
                    }
                }else{
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error en Venta");
                    alert.initOwner(btnIniciar.getScene().getWindow());
                    alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error RegistraMovimiento");
                    alert.showAndWait();
                }
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error en Venta");
                alert.initOwner(btnIniciar.getScene().getWindow());
                alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error ActualizaExistenciaSalida");
                alert.showAndWait();
            }
        }
        return result;
    }

    private boolean ActualizaExistenciaSalida(String cod, String can, boolean cancel) {
        boolean result = false;
        String query;
        if (!cancel) {
            query = "Update productos set Salidas = Salidas + " + can + ",";
            query += " CantidadActual = CantidadActual - " + can + ",";
            query += " " + Variables_Globales.local + " = " + Variables_Globales.local + " - " + can;
        } else {
            query = "Update productos set Salidas = Salidas - " + can + ",";
            query += " CantidadActual = CantidadActual + " + can + ",";
            query += " " + Variables_Globales.local + " = " + Variables_Globales.local + " + " + can;
        }

        query += " Where ID='" + cod + "'";
        ConnectionOpen();
        if (conn.QueryUpdate(query)) {
            System.out.println("Entro a Existencia Salida");
            result = true;
        }
        else{
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en Venta");
            alert.initOwner(btnIniciar.getScene().getWindow());
            alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error ActualizaExistenciaSalida");
            alert.showAndWait();
            Conexion connRespaldo = new Conexion();
            if(!connRespaldo.QueryExecute(query)){
                FileWriter fichero = null;
                PrintWriter pw = null;
                try
                {
                    SimpleDateFormat formatoErr = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaHoy = new Date();
                    String fecha2 = formatoErr.format(fechaHoy);
                    fichero = new FileWriter(Variables_Globales.RutaImagenes+"DeteccionErrores"+fecha2+".txt", true);
                    pw = new PrintWriter(fichero);
                    pw.println("ActualizaExistenciaSalida - Entro a Salida con error"); 
                } catch (Exception e) {
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(e, msjHeader, msjText);
                } finally {
                   try {
                   // Nuevamente aprovechamos el finally para 
                   // asegurarnos que se cierra el fichero.
                   if (null != fichero)
                      fichero.close();
                   } catch (Exception e2) {
                      String msjHeader = "¡Error de SQL!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(e2, msjHeader, msjText);;
                   }
                }
            }
        }
        return result;
    }

    private boolean RegistraMovimiento(String cod, String can, boolean cancel) throws SQLException {
        boolean result = false;
        String folioInventario;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date fechaHoy = new Date();
        String fecha = formato.format(fechaHoy);

        String query;

        folioInventario = String.valueOf(getFolioInventario());

        query = "Insert into tblinventario (invfolio,invmovimiento,";
        query += "invfecha,invcantidad,productocodigo,ventafolio,invdescripcion)";
        query += "Values (" + folioInventario + ",";

        if (!cancel) {
            query += "'Salida',";
        } else {
            query += "'Entrada',";
        }

        query += "'" + fecha + "',";
        query += can + ",";
        query += "'" + cod + "',";
        query += spnFolio.getValue().toString() + ",";
        if (!cancel) {
            query += "'Salida por venta')";
        } else {
            query += "'Entrada por cancelacion')";
        }
        ConnectionOpen();
        if (conn.QueryUpdate(query)) {
            System.out.println("Entro a Registra Movimiento");
            if (ActualizaFolioInventario()) {
                result = true;
            }
        }else{
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en Venta");
            alert.initOwner(btnIniciar.getScene().getWindow());
            alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error RegistraMovimiento");
            alert.showAndWait();
            Conexion connRespaldo = new Conexion();
            if(!connRespaldo.QueryExecute(query)){
                FileWriter fichero = null;
                PrintWriter pw = null;
                try
                {
                    SimpleDateFormat formatoErr = new SimpleDateFormat("yyyy-MM-dd");
                    String fecha2 = formatoErr.format(fechaHoy);
                    fichero = new FileWriter(Variables_Globales.RutaImagenes+"DeteccionErrores"+fecha2+".txt", true);
                    pw = new PrintWriter(fichero);
                    pw.println("RegistraMovimiento - Entro con error"); 
                } catch (Exception e) {
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(e, msjHeader, msjText);
                } finally {
                   try {
                   // Nuevamente aprovechamos el finally para 
                   // asegurarnos que se cierra el fichero.
                   if (null != fichero)
                      fichero.close();
                   } catch (Exception e2) {
                      String msjHeader = "¡Error de SQL!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(e2, msjHeader, msjText);
                   }
                }
            }          
        }
        return result;
    }

    private boolean ActualizaFolioInventario() {
        boolean result = false;
        String sqlStmt;
  
        sqlStmt = "Update tblfolios set folioinventario = folioinventario + '1'";
        ConnectionOpen();
        if (conn.QueryUpdate(sqlStmt)) {
            System.out.println("Entro a ActualizaFolioInventario");
            result = true;
        }else{
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en Venta");
            alert.initOwner(btnIniciar.getScene().getWindow());
            alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error ActualizaFolioInventario");
            alert.showAndWait();
        }
        return result;
    }

    private boolean ActualizaFolioVenta() throws SQLException, SQLException {
        boolean result = false;
        String sqlStmt;

        if(Variables_Globales.local.equals("L127")){
            sqlStmt = "Update tblfolios set folioventa = folioventa + 1";
            ConnectionOpen();
            if (conn.QueryUpdate(sqlStmt)) {
                System.out.println("Entro a FolioVenta");
                result = true;
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error en Venta");
                alert.initOwner(btnIniciar.getScene().getWindow());
                alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error FolioVenta");
                alert.showAndWait();
            }
        }
        
        if(Variables_Globales.local.equals("L58")){
            sqlStmt = "Update tblfolios set folioventa58 = folioventa58 + 1";
            ConnectionOpen();
            if (conn.QueryUpdate(sqlStmt)) {
                System.out.println("Entro a FolioVenta");
                result = true;
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error en Venta");
                alert.initOwner(btnIniciar.getScene().getWindow());
                alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error FolioVenta");
                alert.showAndWait();
            }
        }
        
        if(Variables_Globales.local.equals("L64")){
            sqlStmt = "Update tblfolios set folioventa64 = folioventa64 + 1";
            ConnectionOpen();
            if (conn.QueryUpdate(sqlStmt)) {
                System.out.println("Entro a FolioVenta");
                result = true;
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error en Venta");
                alert.initOwner(btnIniciar.getScene().getWindow());
                alert.setContentText("Error al realizar venta. Revisar en Consultas si fue almacenada correctamente. Error FolioVenta");
                alert.showAndWait();
            }
        }
        try {    
            conn.setResult.close();
        } catch (SQLException ex) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
        return result;
    }
    
    

    private void mostrarBienvenida(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xxcell/view/Principal.fxml"));
        Region contentRootRegion = (Region) loader.load();
        
        double origW;
        double origH;
        
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
        origH = primaryScreenBounds.getHeight();
        origW = primaryScreenBounds.getWidth();
        
        //If the Region containing the GUI does not already have a preferred width and height, set it.
        //But, if it does, we can use that setting as the "standard" resolution.
        if ( contentRootRegion.getPrefWidth() == Region.USE_COMPUTED_SIZE )
            contentRootRegion.setPrefWidth( origW );
        else
            origW = contentRootRegion.getPrefWidth();

        if ( contentRootRegion.getPrefHeight() == Region.USE_COMPUTED_SIZE )
            contentRootRegion.setPrefHeight( origH );
        else
            origH = contentRootRegion.getPrefHeight();
         
        Group group = new Group( contentRootRegion );
        Stage principalStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(group,origH,origW);
        ((Node) e.getSource()).scaleXProperty().bind(scene.widthProperty().divide(origW));
        ((Node) e.getSource()).scaleXProperty().bind(scene.heightProperty().divide(origH));
        principalStage.setScene(scene);
        principalStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        principalStage.setFullScreen(true);
        principalStage.show();
    }
    
    public void imprimetTicket() {
        
        System.out.println("Imprime ticket");
        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        Date fechaHoy = new Date();
        String fecha = formato.format(fechaHoy);
        PrinterService printerService = new PrinterService();

        if(Variables_Globales.impresora.equals("EPSON TM-T20II Receipt")){
            //print some stuff EPSON TM-T20II Receipt   EPSON-TM-T20II
            printerService.printString(Variables_Globales.impresora, "          XXCELL - Reparacion y Accesorios \n");
            printerService.printString(Variables_Globales.impresora, "       10 poniente y 5 de Mayo Col. Centro \n");
            printerService.printString(Variables_Globales.impresora, "               "+fecha+" \n");
            if(txtCodDistribuidor.getText().length() != 0){
                printerService.printString(Variables_Globales.impresora, "    Distribuidor:   "+txtCodDistribuidor.getText()+" \n");
            }
            printerService.printString(Variables_Globales.impresora, "  ---------------------------------------------\n");
            printerService.printString(Variables_Globales.impresora, " Cantidad           Producto            Precio\n");
            printerService.printString(Variables_Globales.impresora, "  ---------------------------------------------\n");
            for (int fila = 0; fila < productosVenta.size(); fila++) {
                printerService.printString(Variables_Globales.impresora, "\n    "+ productosVenta.get(fila).getCantidad()+ "       "
                                                                          + productosVenta.get(fila).getNombre());
                printerService.printString(Variables_Globales.impresora, "\n Codigo: " + productosVenta.get(fila).getCodigo() + 
                                                                                "                  $"+productosVenta.get(fila).getImporte()+"\n");
                if(Double.valueOf(productosVenta.get(fila).getDescuento())>0){
                        printerService.printString(Variables_Globales.impresora, "Descuento: " + productosVenta.get(fila).getDescuento() + "\n");
                }
            }
            printerService.printString(Variables_Globales.impresora, "    ------------------------------------------\n");
            printerService.printString(Variables_Globales.impresora, "  Total:                               $"+lblTotal.getText()+"\n");
            printerService.printString(Variables_Globales.impresora, "  Recibimos:                           $"+Variables_Globales.recibida+"\n");
            printerService.printString(Variables_Globales.impresora, "  Cambio:                              $"+Variables_Globales.cambio+"\n");
            printerService.printString(Variables_Globales.impresora, "    ------------------------------------------\n");
            printerService.printString(Variables_Globales.impresora, "             Gracias por su preferencia \n ");
            printerService.printString(Variables_Globales.impresora, "                      LOF"+ spnFolio.getValue().toString() +"PME"+usuario+"CO"+Variables_Globales.local+" \n\n\n\n");
        }
        if(Variables_Globales.impresora.equals("EC-PM-5890X")){
            //print some stuff EC - LINE
            printerService.printString(Variables_Globales.impresora, "XXCELL - Reparacion y Accesorios\n");
            printerService.printString(Variables_Globales.impresora, "10 pte y 5 de Mayo Col. Centro\n");
            printerService.printString(Variables_Globales.impresora, "     "+fecha+" \n");
            if(txtCodDistribuidor.getText().length() != 0){
                printerService.printString(Variables_Globales.impresora, "Distribuidor: "+txtCodDistribuidor.getText()+" \n");
            }
            printerService.printString(Variables_Globales.impresora, "------------------------------\n");
            printerService.printString(Variables_Globales.impresora, "Cantidad Producto Precio\n");
            printerService.printString(Variables_Globales.impresora, "------------------------------\n");
            for (int fila = 0; fila < productosVenta.size(); fila++) {
                printerService.printString(Variables_Globales.impresora, "\n"+ productosVenta.get(fila).getCantidad()+ " "
                                                                          + productosVenta.get(fila).getNombre());
                printerService.printString(Variables_Globales.impresora, "\n Codigo: " + productosVenta.get(fila).getCodigo() + 
                                                                                "   $"+productosVenta.get(fila).getImporte()+"\n");
                if(Double.valueOf(productosVenta.get(fila).getDescuento())>0){
                    printerService.printString(Variables_Globales.impresora, "Descuento: " + productosVenta.get(fila).getDescuento() + "\n");
                }
            }
            printerService.printString(Variables_Globales.impresora, "------------------------------\n");
            printerService.printString(Variables_Globales.impresora, "  Total:               $"+lblTotal.getText()+"\n");
            printerService.printString(Variables_Globales.impresora, "  Recibimos:           $"+Variables_Globales.recibida+"\n");
            printerService.printString(Variables_Globales.impresora, "  Cambio:              $"+Variables_Globales.cambio+"\n");
            printerService.printString(Variables_Globales.impresora, "------------------------------\n");
            printerService.printString(Variables_Globales.impresora, "  Gracias por su preferencia \n ");
            printerService.printString(Variables_Globales.impresora, "     LOF"+ spnFolio.getValue().toString() +"PME"+usuario+"CO"+Variables_Globales.local+" \n\n\n\n");

        }      
        // cut that paper!
        byte[] cutP = new byte[] { 0x1d, 'V', 1 };
        byte[] open = {27, 112, 48, 55, 121};
        
        printerService.printBytes(Variables_Globales.impresora, open);
        printerService.printBytes(Variables_Globales.impresora, cutP);
	
    }
    
    public void ventaArchivo(){
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaHoy = new Date();
        String fecha = formato.format(fechaHoy);
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter(Variables_Globales.RutaImagenes + "VentasRespaldo" + Variables_Globales.local + fecha + ".txt",true);
            pw = new PrintWriter(fichero);

            for (int fila = 0; fila < productosVenta.size(); fila++) {
                 pw.println(productosVenta.get(fila).getCantidad()+ " " + productosVenta.get(fila).getNombre());
                 pw.println("Codigo: " + productosVenta.get(fila).getCodigo() + "   $"+productosVenta.get(fila).getImporte()+"");
            }
            pw.println("Fol:"+ spnFolio.getValue().toString() +"Emp:"+usuario+"Loc:"+Variables_Globales.local+" \n");
            pw.println("---------------------------");
        } catch (Exception e) {
            String msjHeader = "¡Error de SQL!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(e, msjHeader, msjText);
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              String msjHeader = "¡Error de SQL!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(e2, msjHeader, msjText);
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
