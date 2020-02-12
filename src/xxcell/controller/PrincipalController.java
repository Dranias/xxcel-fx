package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import static javafx.scene.input.KeyCode.F1;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import xxcell.Conexion.Conexion;
import xxcell.XCELL;
import xxcell.model.EnvioCorreo;
import xxcell.model.LogReport;

public class PrincipalController implements Initializable {
    
    Alert alert = new Alert(AlertType.ERROR);
    
    EnvioCorreo sendMail = new EnvioCorreo();

    int cantidad, cantidadproductos = 0;
    float total, totalvendido = 0;
    
    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private StackPane root;
    
    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton btnAlmacen;

    @FXML
    private JFXButton btnRecursosHumanos;
    
    @FXML
    private Label lblBottom;
    
    LogReport log;
    Window window; 
    
    private Node Venta = null;
    private Node Consultas = null;
    private AnchorPane Almacen = null;
    private AnchorPane RecursosHumanos = null;
    private AnchorPane Estadisticas = null;
    private SplitPane MostrarVentaDia = null;
    private Node ModificarAlmacen = null;
    private AnchorPane Movimientos = null;
    private Node ModificarEmpleado = null;
    private Node EstadisticasProducto = null;
    private Node MostrarNomina = null;
    private Node Login = null;
    private Node MostrarDistribuidores = null;
    
    Node principal = null;
    
    private Scene pscene;
    private Stage primaryStage;
    private static boolean flagVentas = false;
    private static boolean flagConsultas = false;
    private StackPane mainLayout;
    
    Conexion conn = new Conexion();
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        lblBottom.setText("Local: " + Variables_Globales.localPublico + "  Impresora: " + Variables_Globales.impresora +"           XXCEL - Punto de Venta - ");
        
        Platform.runLater(()-> {
            window = borderPane.getScene().getWindow();             
            log = new LogReport(window);
            root.requestFocus();
            //Lambda 
            borderPane.getScene().getWindow().setOnCloseRequest(event -> {
                event.consume();
                try {
                    cerrar();
                } catch (SQLException ex) {
                    /*  Creación de Log en caso de fallo  */
                    String msjHeader = "¡Error de SQL!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                } catch (JRException ex) {
                    String msjHeader = "¡Error de Reporte!";
                    String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                    log.SendLogReport(ex, msjHeader, msjText);
                }
            }); 
        });
        
        
        VBox sideMenu = null;
        try {
            sideMenu = FXMLLoader.load(getClass().getResource("/xxcell/view/SideMenu.fxml"));
            mostrarBienvenida();
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            /*  Creación de Log en caso de fallo  */
            String msjHeader = "¡IO EXCEPTION! / Mostrar Bienvenida";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
        
        
        drawer.setOnDrawerOpening((e) -> {
            titleBurger.getAnimation().setRate(1);
            titleBurger.getAnimation().play();
        });
        
        drawer.setOnDrawerClosing((e) -> {
            titleBurger.getAnimation().setRate(-1);
            titleBurger.getAnimation().play();
            });
        
        titleBurger.setOnMouseClicked((e)->{
            if (drawer.isHidden() || drawer.isHidding()) drawer.open();
            else drawer.close();
        });
        
        drawer.setDefaultDrawerSize(150);
        drawer.setSidePane(sideMenu);
        
        for(Node node : sideMenu.getChildren()){
            if(node.getAccessibleText()!= null){
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)-> {
                    switch(node.getAccessibleText()){
                        case "Venta" : {
                            if(!flagVentas)
                            try {
                                mostrarVenta();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION / Mostrar Venta!";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(ex, msjHeader, msjText);
                                
                            }
                        }
                        break;
                        
                        case "Consultas"  : {
                            if(Venta == null)
                            try {
                                mostrarConsultas();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Mostrar Consulta";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "Almacen"  : {
                            if(Venta == null)
                            try {
                                mostrarAlmacen();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Mostrar Almacen";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "RecursosHumanos"  : {
                            if(Venta == null)
                            try {
                                mostrarRecursosHumanos();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Mostrar Recursos Humanos";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "VentasDia" : {
                            if(Venta == null)
                            try {
                                mostrarVentasDia();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Mostrar Ventas del Dia";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "Movimientos" : {
                            if(Venta == null)
                            try {
                                mostrarMovimientos();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Movimientos";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "Estadisticas" : {
                            if(Venta == null)
                            try {
                                mostrarEstadisticas();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Estadisticas";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "CerrarSesion":{
                            try {
                                mostrarLogin();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Login";
                                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            } catch (SQLException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JRException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            
                        }
                    }
                });
            }
        }
                               
        
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()){
                    case F1 : {
                        if(!flagVentas)
                        try {
                            mostrarVenta();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Mostrar Venta";
                            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case F2 : {
                        if(Venta == null)
                        try {
                            mostrarConsultas();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Consultas";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case F3: {
                        if(Venta == null)
                        try {
                            mostrarVentasDia();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Ventas Día";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case F5: {
                        if(Venta == null)
                        try {
                            mostrarMovimientos();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Movimientos";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                            
                        }
                    }
                    break;
                    
                    case F6: {
                        if(Variables_Globales.Rol.equals("0") && Venta == null)
                        try {
                            mostrarAlmacen();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Almacen";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case F7: {
                        if(Variables_Globales.Rol.equals("0") && Venta == null)
                        try {
                            mostrarRecursosHumanos();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Recursos Humanos";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case F8: {
                        if(Variables_Globales.Rol.equals("0") && Venta == null)
                        try {
                            mostrarEstadisticas();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Estadisticas";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                    
                    case SPACE : {
                        if (drawer.isHidden() || drawer.isHidding()) drawer.open();
                        else drawer.close();
                    }
                    break;
                    
                    case ESCAPE : {
                        try {
                            if(Venta != null){
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Salir de Punto de Venta");
                                alert.setContentText("¿Desea realmente salir de Punto de Venta?");
                                alert.initOwner(borderPane.getScene().getWindow());

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK){
                                    Venta = null;
                                    mostrarBienvenida();
                                }         
                            }
                            else mostrarBienvenida();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Pantalla Principal";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                    }
                    break;
                }
            }
        });   
    }
      
    private void mostrarVenta() throws IOException {   
        flagVentas = true;
        Venta = FXMLLoader.load(getClass().getResource("/xxcell/view/Venta.fxml"));
        drawer.setContent(Venta);
        if(flagConsultas)
            flagConsultas = false;
    }
    
    private void mostrarConsultas() throws IOException {   
        flagConsultas = true;
        Consultas = FXMLLoader.load(getClass().getResource("/xxcell/view/Consultas.fxml"));
        drawer.setContent(Consultas);
        if(flagVentas)
            flagVentas = false;
    }
    
    private void mostrarBienvenida() throws IOException {
        root.requestFocus();
        flagVentas = false;
        flagConsultas = false;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Bienvenida.fxml"));
        drawer.setContent(principal);
    }
    
    private void mostrarMovimientos() throws IOException {   
        Movimientos = FXMLLoader.load(getClass().getResource("/xxcell/view/Movimientos.fxml"));        
        drawer.setContent(Movimientos);
    }
    
    private void mostrarAlmacen() throws IOException {   
        Almacen = FXMLLoader.load(getClass().getResource("/xxcell/view/Almacen.fxml"));        
        drawer.setContent(Almacen);
        for(Node node : Almacen.getChildren()){
            if(node.getAccessibleText()!= null){
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)-> {
                    switch(node.getAccessibleText()){
                        case "modificar" : {
                        try {
                            mostrarModificarAlmacen();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Modificar Almacen";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                        }
                        break;
                    }
                });
            }
        }
    }
    
    
    private void mostrarRecursosHumanos() throws IOException {   
        RecursosHumanos = FXMLLoader.load(getClass().getResource("/xxcell/view/RecursosHumanos.fxml"));
        drawer.setContent(RecursosHumanos);
        for(Node node : RecursosHumanos.getChildren()){
            if(node.getAccessibleText()!= null){
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)-> {
                    switch(node.getAccessibleText()){
                        case "ModificarEmpleado" : {
                        try {
                            mostrarModificarEmpleado();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Modificar Empleado";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                        }
                        break;
                        case "Nomina" : {
                        try {
                            mostrarNomina();
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                            /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Nomina";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                        }
                        break;
                        case "Distribuidores" : {
                            try {
                                mostrarDistribuidores();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                            String msjHeader = "¡IO EXCEPTION! / Distribuidores";
                            String msjText = "Copiar y mandarlo por correo ó mensaje";
                             
                            log.SendLogReport(ex, msjHeader, msjText);
                            }  
                        }
                    }
                });
            }
        }
    }
    
    private void mostrarEstadisticas() throws IOException {   
        Estadisticas = FXMLLoader.load(getClass().getResource("/xxcell/view/Estadisticas.fxml"));
        drawer.setContent(Estadisticas);
        for(Node node : Estadisticas.getChildren()){
            if(node.getAccessibleText()!= null){
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)-> {
                    switch(node.getAccessibleText()){
                        case "deProducto" : {
                            try {
                                mostrarEstadisticasProducto();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Estadisticas de Producto";
                                String msjText = "Copiar y mandarlo por correo ó mensaje";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "deCostos" : {
                            try {
                                mostrarEstadisticasCostos();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Estadisticas de Costo";
                                String msjText = "Copiar y mandarlo por correo ó mensaje";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "deUtilidad" : {
                            try {
                                mostrarEstadisticasdeUtilidad();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Estadisticas de Utilidad";
                                String msjText = "Copiar y mandarlo por correo ó mensaje";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                        
                        case "deRastreo" : {
                            try {
                                mostrarEstadisticasdeRastreo();
                            } catch (IOException ex) {
                                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                                /*  Creación de Log en caso de fallo  */
                                String msjHeader = "¡IO EXCEPTION! / Estadisticas de Utilidad";
                                String msjText = "Copiar y mandarlo por correo ó mensaje";
                                 
                                log.SendLogReport(ex, msjHeader, msjText);
                            }
                        }
                        break;
                    }
                });
            }
        }
    }
    
    private void mostrarModificarAlmacen() throws IOException {   
        ModificarAlmacen = FXMLLoader.load(getClass().getResource("/xxcell/view/ModificarAlmacen.fxml"));
        drawer.setContent(ModificarAlmacen);
    }
    
    private void mostrarModificarEmpleado() throws IOException{
        ModificarEmpleado = FXMLLoader.load(getClass().getResource("/xxcell/view/ModificarEmpleado.fxml"));
        drawer.setContent(ModificarEmpleado);
    }
    private void mostrarEstadisticasProducto() throws IOException{
        EstadisticasProducto = FXMLLoader.load(getClass().getResource("/xxcell/view/EstadisticasProductos.fxml"));
        drawer.setContent(EstadisticasProducto);
    }   
    private void mostrarEstadisticasCostos() throws IOException{
        EstadisticasProducto = FXMLLoader.load(getClass().getResource("/xxcell/view/EstadisticasCostos.fxml"));
        drawer.setContent(EstadisticasProducto);
    } 
    private void mostrarEstadisticasdeUtilidad() throws IOException{
        EstadisticasProducto = FXMLLoader.load(getClass().getResource("/xxcell/view/EstadisticasUtilidad.fxml"));
        drawer.setContent(EstadisticasProducto);
    }
    private void mostrarEstadisticasdeRastreo() throws IOException{
        EstadisticasProducto = FXMLLoader.load(getClass().getResource("/xxcell/view/Rastreo.fxml"));
        drawer.setContent(EstadisticasProducto);
    }
    
    private void mostrarVentasDia() throws IOException{
        MostrarVentaDia = FXMLLoader.load(getClass().getResource("/xxcell/view/VentasDia.fxml"));
        drawer.setContent(MostrarVentaDia);
    }
    private void mostrarSalidas() throws IOException{
        Almacen = FXMLLoader.load(getClass().getResource("/xxcell/view/Prestamos.fxml"));
        drawer.setContent(Almacen);
    }
 
    private void mostrarLogin() throws IOException, SQLException, JRException{
        Variables_Globales.Rol = null;
        Stage stage;
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        
        
        alert.setTitle("Salir del sistema");
        alert.setContentText("¿Desea realmente salir del sistema?");
        alert.initOwner(borderPane.getScene().getWindow());
        
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            obtenerParametros();
            if(cantidadproductos>0){
                crearReporte();
                sendMail.EnviarCorreo();
                //sendMail.EnviarCorreoPDF();
            }
            else
                sendMail.EnviarCorreo();
            stage = (Stage) drawer.getScene().getWindow();
            stage.close();
            
            conn.ConnectionClose();
            if(conn.conexion == null) {
                System.out.println("Simon");        
            }else{
                System.out.println("Nelson");        
            }
        }         
        
        this.primaryStage = new Stage();
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(XCELL.class.getResource("/xxcell/view/Login.fxml"));
        mainLayout = loader.load();
        JFXDecorator decorator = new JFXDecorator(primaryStage, mainLayout);
        Scene scene = new Scene(decorator);
        primaryStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void mostrarNomina() throws IOException{
        MostrarNomina = FXMLLoader.load(getClass().getResource("/xxcell/view/NominaEmpleados.fxml"));
        drawer.setContent(MostrarNomina);
    }
    
    private void mostrarDistribuidores() throws IOException{
        MostrarDistribuidores = FXMLLoader.load(getClass().getResource("/xxcell/view/Distribuidores.fxml"));
        drawer.setContent(MostrarDistribuidores);
    }

    private void cerrar() throws SQLException, JRException {
        long TInicio, TFin, tiempo; //Variables para determinar el tiempo de ejecución
        Alert alert = new Alert(AlertType.CONFIRMATION);
        
        
        alert.setTitle("Salir del sistema");
        alert.setContentText("¿Desea realmente salir del sistema?");
        alert.initOwner(borderPane.getScene().getWindow());
        
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            TInicio = System.currentTimeMillis(); /*  Creación de Log en caso de fallo  */
            obtenerParametros();
            if(cantidadproductos>0){
                crearReporte();
                sendMail.EnviarCorreo();
            }
            else{
                sendMail.EnviarCorreo();
            }
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            conn.ConnectionClose();
            if(conn.conexion == null) {
                System.out.println("Simon");        
            }else{
                System.out.println("Nelson");        
            }
            borderPane.getScene().getWindow().hide();
        }         
    }
    
    public void obtenerParametros() throws SQLException{
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        String STSQL;
        Conexion conn = new Conexion();
        
        STSQL = "SELECT tblventadetalle.productoPrecio, tblventadetalle.ventaCantidad ";
        STSQL += "FROM tblventas ";
        STSQL +=    "INNER JOIN tblventadetalle ";
        STSQL +=        "ON tblventas.ventaFolio = tblventadetalle.ventaFolio ";
        STSQL +=    "INNER JOIN productos ";
        STSQL +=        "ON tblventadetalle.productoCodigo = productos.ID ";
        STSQL += "AND tblventas.NumLocal = '"+Variables_Globales.local+"' ";
        STSQL += "WHERE tblventas.ventaFecha > '"+today+"' ";
        STSQL += "AND tblventas.ventaFecha < '"+tomorrow+"'";
        
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
        
        if(conn.QueryExecute(STSQL))
        {
            while (conn.setResult.next())
            {
                cantidad = conn.setResult.getInt("ventaCantidad");
                total = (conn.setResult.getFloat("productoPrecio"))*cantidad;
                cantidadproductos = cantidadproductos + cantidad;
                totalvendido = totalvendido + total;
            }
        }
    }
    
    private void crearReporte() throws SQLException, JRException{
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yy-MM-dd");
        Map parametro = new HashMap();
        Conexion conn = new Conexion();
        String local = Variables_Globales.localPublico;
        
        //Parametros para llenar Jasper
        parametro.put("Local", Variables_Globales.local);
        parametro.put("Today", String.valueOf(today));
        parametro.put("Tomorrow", String.valueOf(tomorrow)); 
        parametro.put("NumVentas", String.valueOf(cantidadproductos));
        parametro.put("CantidadVentas", String.valueOf(totalvendido));

        JasperReport myreport = (JasperReport) JRLoader.loadObjectFromFile("src/xxcell/Reportes/ReporteDia.jasper");
        JasperPrint myPrint = JasperFillManager.fillReport(myreport, parametro, conn.JasperConexion());
        
        //Local
        //JasperExportManager.exportReportToPdfFile(myPrint, "src/xxcell/Reportes/VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
        
        //Local 58
        //JasperExportManager.exportReportToPdfFile(myPrint, "C:\\Users\\XXCELL_L127\\Google Drive\\VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
        
        //Local 64
        //JasperExportManager.exportReportToPdfFile(myPrint, "C:\\Users\\LENOVO\\Google Drive\\VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
        
        //Local 127
        JasperExportManager.exportReportToPdfFile(myPrint, "C:\\Users\\Hp\\Google Drive\\VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
        
        //JasperExportManager.exportReportToPdfFile(myPrint, "F:\\Punto de Venta\\VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
        //ADMINISTRADOR
        //JasperExportManager.exportReportToPdfFile(myPrint, "C:\\Users\\CPU\\Pictures\\CODIGOS DE BARRA\\VentaDia_"+local+"_"+ formato.format(fechaHoy) +".pdf");
    }
}
