package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import xxcell.Conexion.Conexion;
import xxcell.model.Productos;
import org.controlsfx.control.textfield.TextFields;
import static xxcell.controller.LoginController.scene;
import xxcell.model.LogReport;

public class ModificarAlmacenController implements Initializable {
    Conexion conn = new Conexion();   
    
    LogReport log;
    Window window;

    Productos aux;

    String sqlStmt = "SELECT * FROM productos";
    String StmtSq;
    String query;
    
    //Combobox
    @FXML
    private JFXComboBox<String> cmbTipo;
    @FXML
    private JFXComboBox<String> cmbModelo;
    @FXML
    private JFXComboBox<String> cmbMarca;
    @FXML
    private JFXComboBox<String> cmbIdentificador;

    @FXML
    private JFXTextField buscar;

    //botones
    @FXML
    private JFXButton filtrar;
    @FXML
    private JFXButton reset;
    @FXML
    private JFXButton Modificar;
    @FXML
    private JFXButton Eliminar;
    @FXML
    private JFXButton btnAddFoto;
    @FXML
    private JFXButton btnEliminarFoto;
    @FXML
    private JFXButton btnResetFoto;
    
    //Image View para la Foto de Producto
    @FXML
    private ImageView imageViewFoto;
    //Variables para la Imagen/Foto
    File file = null;
    FileInputStream fin = null;
    boolean BanderaImagen = false;
    boolean BanHasImage = false;
    boolean BanDeleteImage = false;
    
    //Variables para la Tabla creada
    @FXML
    private TableView<Productos> Tabla;
    @FXML
    private TableColumn<Productos, String> IDCol; //COLUMNA ID
    @FXML
    private TableColumn<Productos, String> ModCol; //COLUMNA MODELO
    @FXML
    private TableColumn<Productos, String> MarcaCol; //COLUMNA MARCA
    @FXML
    private TableColumn<Productos, Number> PrPubCol; //COLUMNA PRECIO PUBLICO ***
    @FXML
    private TableColumn<Productos, Number> DispCol; //COLUMNA DISPONIBILIDAD ****
    @FXML
    private TableColumn<Productos, String> TipoCol; //COLUMNA TIPO
    @FXML
    private TableColumn<Productos, String> NomCol; //COLUMNA NOMBRE
    @FXML
    private TableColumn<Productos, Number> PrDisCol; //COLUMNA PRECIO DISTRIBUIDOR***
    @FXML
    private TableColumn<Productos, Number> L58Col; //COLUMNA DISPONIBILIDAD EN LOCAL 58 ****
    @FXML
    private TableColumn<Productos, Number> L64Col; //COLUMNA DISPONIBILIDAD EN LOCAL 64****
    @FXML
    private TableColumn<Productos, Number> L127Col; //COLUMNA DISPONIBILIDAD EN LOCAL 127****
    
    
    //Lista para Llenar la tabla de productos;
    ObservableList<Productos> productos = FXCollections.observableArrayList();
    
    //Datos para Editar los algún campo
     //TEXTFIELDS
    @FXML
    private JFXTextField IDTxt;
    @FXML
    private JFXTextField MarcaTxt;
    @FXML
    private JFXTextField ModeloTxt;    
    @FXML
    private JFXTextField PreDtxt;
    @FXML
    private JFXTextField PrecPtxt;
    @FXML
    private JFXTextField Tipotxt;
    @FXML
    private JFXTextField Nomtxt;
    @FXML
    private TextField TFL127;
    @FXML
    private TextField TFL64;
    @FXML
    private TextField TFL58;
    //FIN TEXTFIELD
    //Area para la Descripcion
    @FXML
    private JFXTextArea DescTxt;
    //LABEL DE ERROR PARA LA VALIDACIÓN DE LOS TEXTFIELDS
    @FXML
    private Label lblEID;
    @FXML
    private Label lblEModelo;
    @FXML
    private Label lblEMarca;
    @FXML
    private Label lblESum;
    @FXML
    private Label lblEnumero;
    @FXML
    private Label lbl127Dispon;
    @FXML
    private Label lbl64Dispon;
    @FXML
    private Label lbl58Dispon;
    @FXML
    private Label lblDispon;
    @FXML
    private Label lblErrorImagen;
    
    
    //Lista para el autocompletado de JFXTextField buscar
    Set<String> hs_Buscar = new HashSet<>();
    Set<String> hs_Marca = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_Tipo = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_Modelo = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_Identificador = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
     
    boolean ban;
    
     //Función para llenar el TableView con los datos que el usuario indique
    public ObservableList<Productos> ObtenerProd(String STSQL) throws SQLException{
        String Mod, Marc, DI, Nom, Tip, Dep, NombreImagen;   
        int Disp, L58,L64,L127;
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
                L58 = conn.setResult.getInt("L58");
                L64 = conn.setResult.getInt("L64");
                L127 = conn.setResult.getInt("L127");
                NombreImagen = conn.setResult.getString("NombreImagen");
                productos.add(new Productos(DI,Marc,Mod,Nom,PPub,PDist,Tip,Dep,Disp, L58,L64,L127, NombreImagen));
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
                    hs_Modelo.add(conn.setResult.getString("Modelo"));
                    hs_Buscar.add(conn.setResult.getString("Modelo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        query = "SELECT DISTINCT ID FROM productos ORDER BY ID";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_Buscar.add(conn.setResult.getString("ID"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        query = "SELECT DISTINCT Tipo FROM productos ORDER BY Tipo";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_Tipo.add(conn.setResult.getString("Tipo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        query = "SELECT DISTINCT Marca FROM productos";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_Modelo.add(conn.setResult.getString("Marca"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        query = "SELECT DISTINCT Identificador FROM productos";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_Modelo.add(conn.setResult.getString("Identificador"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }    
    
     //Inicia los elementos de los ComboBox para las Marcas y los tipos de Objetos
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
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        //Inicia el Combobox Tipo
        query = "SELECT DISTINCT Tipo FROM productos ORDER BY Tipo";
        if(conn.QueryExecute(query))
        {
            try {
                while(conn.setResult.next()){
                    cmbTipo.getItems().add(conn.setResult.getString("Tipo"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
    }
    
    public void IniciarcmbOcultos(String Tipo){
        switch (Tipo){
            case "Identificador":
                cmbIdentificador.getSelectionModel().clearSelection();
                cmbIdentificador.getItems().clear();
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
            break;
            case "Modelo":
                cmbModelo.getSelectionModel().clearSelection();
                cmbModelo.getItems().clear();
                query = "SELECT DISTINCT Modelo FROM productos WHERE Marca = '"+cmbMarca.getValue()+"' ORDER BY Modelo";
                if(conn.QueryExecute(query))
                {
                    try {
                        while(conn.setResult.next()){
                           cmbModelo.getItems().add(conn.setResult.getString("Modelo"));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                        String msjHeader = "¡ERROR!";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(ex, msjHeader, msjText);
                    }
                }
            break;
        }
            
    }
    
    //Funcion para eliminar un producto
     void EliminarProd(){
        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
        incompleteAlert.setTitle("Eliminar Producto");
        incompleteAlert.setHeaderText(null);
        aux = Tabla.getSelectionModel().getSelectedItem();
        StmtSq = "DELETE FROM productos WHERE ID='"+aux.getID()+"'";                  
        if(conn.QueryUpdate(StmtSq))
        {
            String mensaje = "Producto Eliminado \n";
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
        else
        {
            String mensaje = "Producto No se ha podido eliminar \n";
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
   }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        Platform.runLater(() -> {
            window = Modificar.getScene().getWindow();             
            log = new LogReport(window);
        });
        
        deshabilitar();
        Autocompletar();
        
        TextFields.bindAutoCompletion(buscar, hs_Buscar);
        TextFields.bindAutoCompletion(ModeloTxt, hs_Modelo);
        TextFields.bindAutoCompletion(MarcaTxt, hs_Marca);
        TextFields.bindAutoCompletion(Tipotxt, hs_Tipo);
        
        btnAddFoto.setOnAction(btnLoadEventListener);
        //Botón Reset imagen
        btnEliminarFoto.setOnAction((ActionEvent e) -> {       
            EliminarFoto();
        }); 
        btnResetFoto.setOnAction((ActionEvent e) -> {       
            ResetImagen();
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
        
        cmbIdentificador.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("Key realeased");
                String s = Funciones.jumpTo(event.getText(), cmbIdentificador.getValue(), cmbIdentificador.getItems());
                System.out.println(event.getText());
                if (s != null) {
                    cmbIdentificador.setValue(s);
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
        
        Tabla.setOnMousePressed(new EventHandler<MouseEvent>() {
             @Override 
             public void handle(MouseEvent event) {
                if(!Tabla.getSelectionModel().isEmpty()){
                    aux = Tabla.getSelectionModel().getSelectedItem();
                    llenado(aux);
                }
            }
        });
    //FIN DE TEXFIELDS******
 //**********************************************BOTON EDITAR*******************************
        Modificar.setOnAction((ActionEvent e) -> {
            if(Tabla.getSelectionModel().isEmpty()){
                    String mensaje = "No se ha seleccionado ningun producto. \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Eliminar Producto");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Modificar.getScene().getWindow());
                    incompleteAlert.showAndWait();
            }
            else
            {
                int  y, z, w;
                int conta = 0;    
                ban=true;
//*******************Validación de ID *************************
                if(IDTxt.getText().length() == 0 || IDTxt.getText().length() > 13) {
                    lblEID.setText("ID debe ser mayor a 0 y Menor a 14 caracteres");
                    IDTxt.requestFocus();
                    ban = false;
                }
                else{
                    lblEID.setText("");
                    if(IDTxt.getText().equals(aux.getID())){
                        conta++;
                        lblEID.setText("Sin Modificaciones");
                    }
                }
//*******************Validación de Modelo***************************
                if(ModeloTxt.getText().length() == 0 || ModeloTxt.getText().length() > 15) {
                    lblEModelo.setText("Modelo debe ser mayor a 0 y Menor a 15 caracteres");
                    ModeloTxt.requestFocus();
                    ban = false;
                }
                else{
                    lblEModelo.setText("");
                    if(ModeloTxt.getText().equals(aux.getModelo())){
                        conta++;
                        lblEModelo.setText("Sin Modificaciones");
                    }
                }
//*******************Validación para Marca************************************
                if(MarcaTxt.getText().length() == 0 || MarcaTxt.getText().length() > 15) {
                    lblEMarca.setText("Marca debe ser mayor a 0 y Menor a 15 caracteres");
                    MarcaTxt.requestFocus();
                    ban = false;
                }
                else{
                    lblEMarca.setText("");
                    if(MarcaTxt.getText().equals(aux.getMarca()))
                    {
                        lblEMarca.setText("Sin Modificaciones");
                        conta++;
                    }
                }
//*******************Validación para el local 64**************************************
                if(esnumero(TFL64.getText()))
                {
                    lblESum.setText("");
                    if(TFL64.getText().length() == 0) {
                        TFL64.setText("0");
                        w=0;
                    }
                    else
                        w = Integer.parseInt(TFL64.getText());
                    
                    if(w == 0)
                        conta++;
                }
                else
                {
                    ban=false;
                    lblEnumero.setText("Los datos deben ser números (Error en local 64)");
                    w=0;
                }
 //*******************Validación para el local 127**********************************
                if(esnumero(TFL127.getText()))
                {
                    if(TFL127.getText().length() == 0) {
                        TFL127.setText("0");
                        y=0;
                    }
                    else
                        y = Integer.parseInt(TFL127.getText());
                    
                    if(y==0)
                        conta++;
                }
                else
                {
                    ban=false;
                    lblEnumero.setText("Los datos deben ser números (Error en local 127)");
                    y=0;
                }
//*******************Validación para el local 58************************************
                if(esnumero(TFL58.getText()))
                {
                    if(TFL58.getText().length() == 0) {
                        TFL58.setText("0");
                        z=0;
                    }
                    else
                        z = Integer.parseInt(TFL58.getText());
                    if(z==0)
                        conta++;
                }
                else
                {
                    ban=false;
                    lblEnumero.setText("Los datos deben ser números (Error en local 58)");
                    z=0;
                }
//***************************************** Validación para la imagen **************************************************
                if(BanderaImagen){
                    conta--;
                }
                //Acceso a Query
                if(ban)
                {
                    if(conta==7){
                        String mensaje = "¡Debe hacer al menos una modificacion! \n";
                        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                        incompleteAlert.setTitle("Gestión de Productos");
                        incompleteAlert.setHeaderText(null);
                        incompleteAlert.setContentText(mensaje);
                        incompleteAlert.initOwner(Modificar.getScene().getWindow());
                        incompleteAlert.showAndWait();
                    }else{
                        try {
                            AgregarSQL(aux);
                        } catch (SQLException ex) {
                            Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                            String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                            log.SendLogReport(ex, msjHeader, msjText);
                        }
                        ReinicioTxtFields();
                        Tabla.refresh();
                        conta = 0;
                    }
                }
            }//FIN ELSE
        });//*********************************************** FIN BOTON MODIFICAR **********************************
        iniciarComboBox();
        IDCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        MarcaCol.setCellValueFactory(cellData -> cellData.getValue().marcaProperty());
        ModCol.setCellValueFactory(cellData -> cellData.getValue().modeloProperty());
        TipoCol.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        NomCol.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        PrPubCol.setCellValueFactory(cellData -> cellData.getValue().preciopubProperty());
        PrDisCol.setCellValueFactory(cellData -> cellData.getValue().preciodistProperty());
        DispCol.setCellValueFactory(cellData -> cellData.getValue().disponProperty());
        DispCol.setCellFactory( column -> {
            return new TableCell<Productos, Number>(){
                @Override
                protected void updateItem(Number num, boolean empty){
                    super.updateItem(num, empty);
                    
                    if(num == null){
                        setText("");
                        setStyle("");
                    }else{
                        setText(String.valueOf(num));
                        if(num.intValue() <= 0){
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: #FF9980");
                        }
                        if(num.intValue() < 6 && num.intValue() > 0){
                            setTextFill(Color.CHOCOLATE);
                            setStyle("-fx-background-color: #FFFF9F");
                        }
                    }
                }
            };
        });
        L58Col.setCellValueFactory(cellData -> cellData.getValue().l58Property());
        L58Col.setCellFactory( column -> {
            return new TableCell<Productos, Number>(){
                @Override
                protected void updateItem(Number num, boolean empty){
                    super.updateItem(num, empty);
                    
                    if(num == null){
                        setText("");
                        setStyle("");
                    }else{
                        setText(String.valueOf(num));
                        if(num.intValue() <= 0){
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: #FF9980");
                        }
                        if(num.intValue() < 2 && num.intValue() > 0){
                            setTextFill(Color.CHOCOLATE);
                            setStyle("-fx-background-color: #FFFF9F");
                        }
                    }
                }
            };
        });
        L64Col.setCellValueFactory(cellData -> cellData.getValue().l64Property());
        L64Col.setCellFactory( column -> {
            return new TableCell<Productos, Number>(){
                @Override
                protected void updateItem(Number num, boolean empty){
                    super.updateItem(num, empty);
                    
                    if(num == null){
                        setText("");
                        setStyle("");
                    }else{
                        setText(String.valueOf(num));
                        if(num.intValue() <= 0){
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: #FF9980");
                        }
                        if(num.intValue() < 2 && num.intValue() > 0){
                            setTextFill(Color.CHOCOLATE);
                            setStyle("-fx-background-color: #FFFF9F");
                        }
                    }
                }
            };
        });
        L127Col.setCellValueFactory(cellData -> cellData.getValue().l127Property());
        L127Col.setCellFactory( column -> {
            return new TableCell<Productos, Number>(){
                @Override
                protected void updateItem(Number num, boolean empty){
                    super.updateItem(num, empty);
                    
                    if(num == null){
                        setText("");
                        setStyle("");
                    }else{
                        setText(String.valueOf(num));
                        if(num.intValue() <= 0){
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: #FF9980");
                        }
                        if(num.intValue() < 2 && num.intValue() > 0){
                            setTextFill(Color.CHOCOLATE);
                            setStyle("-fx-background-color: #FFFF9F");
                        }
                    }
                }
            };
        });
        
        //Función para el comboBox Marca; Esté llenara el TableView dependiendo la Opción que el usuario Ingrese
        cmbMarca.setOnAction(e -> {
            IniciarcmbOcultos("Modelo");
            buscar.clear();
            StmtSq = "Select * FROM productos "; 
            StmtSq += "WHERE Marca='"+cmbMarca.getValue()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(StmtSq));
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        });//FIN DE LAMBDA MARCA       
        
        //Función para el combobox Tipo
        cmbTipo.setOnAction(e -> {
            IniciarcmbOcultos("Identificador");
            buscar.clear();
            StmtSq = "Select * FROM productos "; 
            StmtSq += "WHERE Tipo='"+cmbTipo.getValue()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(StmtSq));
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            } 
        });//FIN DE LAMBDA   
        //Función para el autocompletado del Textfield Buscar
        buscar.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            StmtSq = "Select * FROM productos ";
            StmtSq += "WHERE Modelo='"+buscar.getText()+"' OR  ID='"+buscar.getText()+"'";
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(StmtSq));
            } catch (SQLException ex) {
                Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        });   
        
        filtrar.setOnAction((ActionEvent e) -> { 
            filtrar();
        });
        
        reset.setOnAction((ActionEvent e) -> { 
            Tabla.refresh();
            productos.removeAll(productos);
            ReinicioTxtFields();
            iniciarComboBox();
        });//FIN RESET
        
        Eliminar.setOnAction((ActionEvent e) -> { 
            String resetStmt;
            if(Tabla.getSelectionModel().isEmpty()){
                    String mensaje = "No se ha seleccionado ningun producto. \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Eliminar Producto");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Eliminar.getScene().getWindow());
                    incompleteAlert.showAndWait();
            }else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Eliminar producto");
                        //alert.setHeaderText("¿Desea realmente salir del sistema?");
                alert.setContentText("¿Desea realmente eliminar el producto seleccionado?");
                alert.initOwner(buscar.getScene().getWindow());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    EliminarProd();
                }         
                resetStmt = "SELECT * FROM productos";
                Tabla.refresh();
                productos.removeAll(productos);
                ReinicioTxtFields();
            }
        });//FIN ELIMINAR
    }//Fin de INIT  
    
    //FUNCIÓN PARA VALIDAR SI LOS DATOS INGRESADOS EN "MERCANCIA ENTRANTE, Y LOS 3 LOCALES SON NUMEROS
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
    
    
    //Funcion para agregar a la base de datos el producto que desee
    public void AgregarSQL(Productos Prod) throws SQLException {
        double PrecioPublico = Double.parseDouble(PrecPtxt.getText());
        double PrecioDistribuidor = Double.parseDouble(PreDtxt.getText());
        int l58 = Integer.parseInt(TFL58.getText());
        int l64 = Integer.parseInt(TFL64.getText());
        int l127 = Integer.parseInt(TFL127.getText());

        //Sino Existe, agregará uno Nuevo
        if(BanderaImagen){
            query =  "Update productos set ID = ?, Modelo = ?, Identificador = ?, PrecPub = ?, "
            + "Marca = ?, Tipo = ?, PrecDist = ?, ";
            query += "Descrip = ?, L58 = ?, L64 = ?, L127 = ?, NombreImagen = ? "
            + "WHERE ID = '"+Prod.getID()+"'";
            if(BanDeleteImage){
                conn.preparedStatement(query);
                conn.stmt.setString(1, IDTxt.getText());
                conn.stmt.setString(2, ModeloTxt.getText());
                conn.stmt.setString(3, Nomtxt.getText());
                conn.stmt.setDouble(4, PrecioPublico);
                conn.stmt.setString(5, MarcaTxt.getText());
                conn.stmt.setString(6, Tipotxt.getText());
                conn.stmt.setDouble(7, PrecioDistribuidor);
                conn.stmt.setString(8, DescTxt.getText());
                conn.stmt.setInt(9, l58);
                conn.stmt.setInt(10, l64);
                conn.stmt.setInt(11, l127);   
                conn.stmt.setString(12, null);
            }else{
                conn.preparedStatement(query);
                conn.stmt.setString(1, IDTxt.getText());
                conn.stmt.setString(2, ModeloTxt.getText());
                conn.stmt.setString(3, Nomtxt.getText());
                conn.stmt.setDouble(4, PrecioPublico);
                conn.stmt.setString(5, MarcaTxt.getText());
                conn.stmt.setString(6, Tipotxt.getText());
                conn.stmt.setDouble(7, PrecioDistribuidor);
                conn.stmt.setString(8, DescTxt.getText());
                conn.stmt.setInt(9, l58);
                conn.stmt.setInt(10, l64);
                conn.stmt.setInt(11, l127);
                conn.stmt.setString(12, Variables_Globales.nameImage);    
            }
        }else{
            query =  "Update productos set ID = ?, Modelo = ?, Identificador = ?, PrecPub = ?, "
                    + "Marca = ?, Tipo = ?, PrecDist = ?, ";
            query += "Descrip = ?, L58 = ?, L64 = ?, L127 = ? "
                    + "WHERE ID = '"+Prod.getID()+"'";
            conn.preparedStatement(query);
            conn.stmt.setString(1, IDTxt.getText());
            conn.stmt.setString(2, ModeloTxt.getText());
            conn.stmt.setString(3, Nomtxt.getText());
            conn.stmt.setDouble(4, PrecioPublico);
            conn.stmt.setString(5, MarcaTxt.getText());
            conn.stmt.setString(6, Tipotxt.getText());
            conn.stmt.setDouble(7, PrecioDistribuidor);
            conn.stmt.setString(8, DescTxt.getText());
            conn.stmt.setInt(9, l58);
            conn.stmt.setInt(10, l64);
            conn.stmt.setInt(11, l127);
        }
            int succes = conn.stmt.executeUpdate();
        if(succes == 1){
            String mensaje = "Producto Modificado \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Gestión de Productos");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
            ReinicioTxtFields();
            ResetImagen();
        }else{
            String mensaje = "Producto NO PUDO SER añadido \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Gestión de Productos");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
    }
    
    
    //FUNCION QUE AUTOCOMPLETA LOS TEXFIELDS CUANDO SE DA DOBLE CLICK A UNA FILA
    public void llenado(Productos Prod)
    {
        //Variables para las Imagenes
        Conexion conTemporal = new Conexion(); 
        Blob blob = null;
        byte[] data = null;
        BufferedImage img = null;
        WritableImage image = null;
        String ImageQuery;
        
        if(Prod.getNombreImagen() != null){
            ImageQuery = "Select Imagen from galeria where NombreImagen = '"+Prod.getNombreImagen()+"'";
            conn.QueryExecute(ImageQuery);
            try {
                if(conn.setResult.first()){
                    blob = conn.setResult.getBlob("Imagen");
                    BanHasImage = true;
                    data = blob.getBytes(1, (int)blob.length());
                    try{
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        image = SwingFXUtils.toFXImage(img, null);
                        imageViewFoto.setImage(image);
                    }catch(IOException ex){
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                        String msjHeader = "¡ERROR IO! <Modificar Almacen>";
                        String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                        log.SendLogReport(ex, msjHeader, msjText);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PopUpImagenController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡ERROR de SQL! <Modificar Almacen>";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }else{
            ResetImagen();
        }
        
        habilitar();
        String cadena;
        IDTxt.setText(Prod.getID());
        MarcaTxt.setText(Prod.getMarca());
        ModeloTxt.setText(Prod.getModelo());
        Nomtxt.setText(Prod.getNombre());
        Tipotxt.setText(Prod.getTipo());
        DescTxt.setText(Prod.getDescrip());
        cadena = String.valueOf(Prod.getPrecioPub());
        PrecPtxt.setText(cadena);
        cadena = String.valueOf(Prod.getPrecioDist());
        PreDtxt.setText(cadena);
        cadena = String.valueOf(Prod.getDispon());
        lblDispon.setText(" "+cadena);
        cadena = String.valueOf(Prod.getL127());
        lbl127Dispon.setText(" "+cadena);
        TFL127.setText(cadena);
        cadena = String.valueOf(Prod.getL58());
        lbl58Dispon.setText(" "+cadena);
        TFL58.setText(cadena);
        cadena = String.valueOf(Prod.getL64());
        lbl64Dispon.setText(" "+cadena);
        TFL64.setText(cadena);
    }
    
    public void ReinicioTxtFields(){
        cmbIdentificador.getSelectionModel().clearSelection();
        cmbIdentificador.getItems().clear();
        cmbModelo.getSelectionModel().clearSelection();
        cmbModelo.getItems().clear();
        cmbMarca.getSelectionModel().clearSelection();
        cmbMarca.getItems().clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbTipo.getItems().clear();
        iniciarComboBox();
        
        IDTxt.setText("");
        MarcaTxt.setText("");
        ModeloTxt.setText("");
        Nomtxt.setText("");
        Tipotxt.setText("");
        DescTxt.setText("");
        PrecPtxt.setText("");
        PreDtxt.setText("");
        TFL127.setText("");
        TFL58.setText("");
        TFL64.setText("");
        lblEID.setText("");
        lblEModelo.setText("");
        lblEMarca.setText("");
        lblESum.setText("");
        lblEnumero.setText("");
        lbl127Dispon.setText("");
        lbl64Dispon.setText("");
        lbl58Dispon.setText("");
        lblDispon.setText("");
        lblErrorImagen.setText("");
        BanderaImagen = false;
        BanHasImage = false;
        BanDeleteImage = false;
        deshabilitar();
        ResetImagen();
        buscar.clear();
    }
    
    public void deshabilitar(){
        IDTxt.setDisable(true);
        MarcaTxt.setDisable(true);
        ModeloTxt.setDisable(true);
        Nomtxt.setDisable(true);
        Tipotxt.setDisable(true);
        DescTxt.setDisable(true);
        TFL127.setDisable(true);
        TFL58.setDisable(true);
        TFL64.setDisable(true);
        PrecPtxt.setDisable(true);
        PreDtxt.setDisable(true);
    }
    
    public void habilitar(){
        IDTxt.setDisable(false);
        MarcaTxt.setDisable(false);
        ModeloTxt.setDisable(false);
        Nomtxt.setDisable(false);
        Tipotxt.setDisable(false);
        DescTxt.setDisable(false);
        TFL127.setDisable(false);
        TFL58.setDisable(false);
        TFL64.setDisable(false);
        PrecPtxt.setDisable(false);
        PreDtxt.setDisable(false);
    }
    
    //Manejador de eventos para seleccion de imagen Foto
    EventHandler<ActionEvent> btnLoadEventListener
    = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            try {
                MostrarGaleria();
            } catch (SQLException ex) {
                Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    public void MostrarGaleria() throws SQLException, IOException{
        String nombre, query;
        //Variables para las Imagenes
        Blob blob;
        byte[] data;
        BufferedImage img;
        WritableImage image;
        
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/GaleriaAgregarProducto.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.WINDOW_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(btnAddFoto.getScene().getWindow());
        principalStage.showAndWait(); 
        if(Variables_Globales.nameImage != null){
            nombre = Variables_Globales.nameImage;
            query = "Select galeria.Imagen FROM galeria "
                        + "WHERE NombreImagen = '"+nombre+"'";
            if(conn.QueryExecute(query)){
                while(conn.setResult.next()) {
                    blob = conn.setResult.getBlob("Imagen");
                    if(blob != null){
                        data = blob.getBytes(1, (int)blob.length());
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        image = SwingFXUtils.toFXImage(img, null);  
                        imageViewFoto.setImage(image);
                        BanderaImagen = true;
                    }
                }
            }    
        }
    }
    
    public void ResetImagen() {
        if(BanHasImage == false){
            BanderaImagen = false;
            File fileReset = new File("src/xxcell/Images/producto-sin-foto.jpg");
            Image imageReset = new Image(fileReset.toURI().toString());  
            imageViewFoto.setImage(imageReset);
            lblErrorImagen.setText("");
        }
    }
    
    public void EliminarFoto(){
        if(BanHasImage){
            BanderaImagen = true;
            BanDeleteImage = true;
            File fileReset = new File("src/xxcell/Images/producto-sin-foto.jpg");
            Image imageReset = new Image(fileReset.toURI().toString());  
            imageViewFoto.setImage(imageReset); 
            lblErrorImagen.setText("");
        }else{
            lblErrorImagen.setText("No hay imagen Registrada");
        }
    }
    
    public void filtrar(){
        String queryFiltrar;  
        int CmbNoNulos = 0;
        if(cmbMarca.getValue() != null)
            CmbNoNulos++;
        if(cmbTipo.getValue() != null)
            CmbNoNulos++;
        if(cmbIdentificador.getValue() != null)
            CmbNoNulos++;
        if(cmbModelo.getValue() != null)
            CmbNoNulos++;
        System.out.println(CmbNoNulos);
        if(cmbMarca.getValue() != null && cmbTipo.getValue() != null && cmbIdentificador.getValue() == null && cmbModelo.getValue() == null){
            queryFiltrar = "Select * FROM productos ";
            queryFiltrar += "WHERE Tipo='"+cmbTipo.getValue()+"' AND Marca = '"+cmbMarca.getValue()+"'";
            System.out.println(queryFiltrar);
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(queryFiltrar));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        
        if(cmbMarca.getValue() != null && cmbTipo.getValue() == null && cmbIdentificador.getValue() == null && cmbModelo.getValue() != null){
            queryFiltrar = "Select * FROM productos ";
            queryFiltrar += "WHERE Modelo='"+cmbModelo.getValue()+"' AND Marca = '"+cmbMarca.getValue()+"'";
            System.out.println(queryFiltrar);
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(queryFiltrar));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        if(cmbMarca.getValue() == null && cmbTipo.getValue() != null && cmbIdentificador.getValue() != null && cmbModelo.getValue() == null){
            queryFiltrar = "Select * FROM productos ";
            queryFiltrar += "WHERE Tipo='"+cmbTipo.getValue()+"' AND Identificador = '"+cmbIdentificador.getValue()+"'";
            System.out.println(queryFiltrar);
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(queryFiltrar));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        if(cmbMarca.getValue() != null && cmbTipo.getValue() != null && cmbIdentificador.getValue() != null && cmbModelo.getValue() != null){
            queryFiltrar = "Select * FROM productos ";
            queryFiltrar += "WHERE Tipo='"+cmbTipo.getValue()+"' AND Identificador = '"+cmbIdentificador.getValue()+"' ";
            queryFiltrar += "AND Modelo='"+cmbModelo.getValue()+"' AND Marca = '"+cmbMarca.getValue()+"'";
            System.out.println(queryFiltrar);
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(queryFiltrar));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
        }
        if(cmbMarca.getValue() != null && cmbTipo.getValue() != null && cmbIdentificador.getValue() != null && cmbModelo.getValue() == null){
            queryFiltrar = "Select * FROM productos ";
            queryFiltrar += "WHERE Tipo='"+cmbTipo.getValue()+"' AND Identificador = '"+cmbIdentificador.getValue()+"' ";
            queryFiltrar += " AND Marca = '"+cmbMarca.getValue()+"'";
            System.out.println(queryFiltrar);
            try {   
                Tabla.refresh();
                productos.removeAll(productos);
                Tabla.setItems(ObtenerProd(queryFiltrar));
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
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
