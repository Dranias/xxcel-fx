/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.Empleados;


public class ModificarEmpleadoController implements Initializable {
    
    Conexion conn = new Conexion();
    String qry = "SELECT * FROM empleado";
    
    //Variables para las Imagenes
    Blob blob;
    byte[] data;
    BufferedImage img;
    WritableImage image;
    //*************************
    
    Empleados aux = new Empleados();
    
    //Visor de Imagenes
    @FXML
    private ImageView Foto;
    @FXML
    private ImageView IneView;
    @FXML
    private ImageView InePostView;
    
    boolean flagIne;
    boolean INEMod = false;
    boolean INEPostMod = false;
    
    File file;
    File fileIne;
    File fileInePost;
    FileInputStream fin;
    FileInputStream fisIne;
    FileInputStream fisInePost;
    
    //Radio Button Para ver la contraeña;
    @FXML
    private JFXRadioButton MostrarContra;
    String Contraseña;
    
    @FXML
    private JFXPasswordField TxtContra;
    
    boolean ban = true;

    //Combobox para el nivel
    @FXML
    private JFXComboBox<String> Nivel; 
    
    //Textfields
    @FXML
    private JFXTextField VerContra;
    @FXML
    private JFXTextField IDtxt;
    @FXML
    private JFXTextField TxtNombre;
    @FXML
    private JFXTextField TxtApellido;
    @FXML
    private JFXTextField TxtDireccion;
    @FXML
    private JFXTextField TxtTelefono;
    @FXML
    private JFXTextField NumEmpleado;
    
    //Text Area para las Refencias
    @FXML
    private JFXTextArea Referencia;
    
    //Botones
    @FXML
    private JFXButton Modificar;
    @FXML
    private JFXButton btnIne;
    @FXML
    private JFXButton btnInePost;
    @FXML
    private JFXButton Eliminar;
    @FXML
    private JFXButton btnVerInePost;
    @FXML
    private JFXButton btnverIne;
    
    
    //Elementos usados para el TableView
    @FXML
    private TableView<Empleados> Tabla;
    @FXML
    private TableColumn<Empleados, String> nombre;   
    @FXML
    private TableColumn<Empleados, String> apellido;   
    @FXML
    private TableColumn<Empleados, String> usuario;
    @FXML
    private TableColumn<Empleados, Number> EmpleadoNum;
    
    //Elementos para la validación
    boolean flag;
    int conta;
    @FXML
    private Label lblNombreError;
    @FXML
    private Label lblApellidoError;
    @FXML
    private Label lblDireccionError;
    @FXML
    private Label lblTelefonoError;   
    @FXML
    private Label lblUsuarioError;
    @FXML
    private Label lblContraseñaError;
    @FXML
    private Label lblNivelError;   
    @FXML
    private Label lblImageIneError;    
    @FXML
    private Label lblImageInePostError;
    @FXML
    private Label lblErrorNumEmpleado;
    
    //Lista para Llenar la tabla de productos;
    ObservableList<Empleados> empleados = FXCollections.observableArrayList();
    
    //Función que obtendrá los objetos empleados para ingresarlos al tableview
    public ObservableList<Empleados> ObtenerEmpleados() throws SQLException{
        String usuar, contra, nom, apel, direc, tel, referencia;
        int lvl, numEmp;
        Date fecha; 
                
        if(conn.QueryExecute(qry))
        {
            while(conn.setResult.next())
            {
                usuar = conn.setResult.getString("usuario");
                contra = conn.setResult.getString("contra");
                lvl = conn.setResult.getInt("nivel");
                nom = conn.setResult.getString("Nombre");
                apel = conn.setResult.getString("Apellido");
                direc = conn.setResult.getString("Direccion");
                tel = conn.setResult.getString("Telefono");
                numEmp = conn.setResult.getInt("NumEmpleado");
                fecha = conn.setResult.getDate("Contratacion");
                referencia = conn.setResult.getString("Referencia");
                empleados.add(new Empleados(usuar, contra, nom, apel, direc, tel, numEmp, lvl, fecha, referencia));
            }
        }
        return empleados;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Inicia los valores de Combobox
        iniciarComboBox();
        
        VerContra.setVisible(false);
        
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
        
        btnverIne.setOnAction((ActionEvent e) -> {       
            try {
                mostrarImagen("Ine");
            } catch (IOException ex) {
                Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnVerInePost.setOnAction((ActionEvent e) -> {       
            try {
                mostrarImagen("InePosterior");
            } catch (IOException ex) {
                Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        TxtTelefono.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        NumEmpleado.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        
        //Inicializar con los objetos creados, las columnas del Tableview Tabla
        nombre.setCellValueFactory(cellData -> cellData.getValue().NombreProperty());
        apellido.setCellValueFactory(cellData -> cellData.getValue().ApellidoProperty());
        usuario.setCellValueFactory(cellData -> cellData.getValue().UsuarioProperty());
        EmpleadoNum.setCellValueFactory(cellData -> cellData.getValue().NumEmpProperty());
        try {
            Tabla.setItems(ObtenerEmpleados());
        } catch (SQLException ex) {
            Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        MostrarContra.setOnAction((ActionEvent e) -> {
            if(ban == true)
            {
                TxtContra.setVisible(false);
                VerContra.setVisible(true);
                VerContra.setText(Contraseña);
                ban = false;
            }
            else
            {
                VerContra.setVisible(false);
                TxtContra.setVisible(true);
                TxtContra.setText(Contraseña);
                ban = true;
            }
        }); 
        
        //Seleccionar un objeto con doble click
        Tabla.setOnMousePressed(new EventHandler<MouseEvent>() {
             @Override 
             public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
                    if(Tabla.getSelectionModel().getSelectedItem() != null ){
                        aux = Tabla.getSelectionModel().getSelectedItem();
                        limpiar();
                        try {
                            llenado(aux);
                        } catch (SQLException ex) {
                            Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        Modificar.setOnAction((ActionEvent e) -> {
            conta = 0;
            boolean band = false;
            String mensaje = "";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            Alert succesAlert = new Alert(Alert.AlertType.INFORMATION);
            VerContra.setVisible(false);
            
            //************************Validacion de datos************************
            //Validacion de usuario
            if(IDtxt.getText().length()>10){
                flag = false;
                lblUsuarioError.setText("No debe superar 10 caracteres");
            } else if(IDtxt.getText().isEmpty() || IDtxt.getText().length()<5){
                flag = false;
                lblUsuarioError.setText("Debe contener al menos 5 caracteres");
            } else{
                lblUsuarioError.setText("");
            }
            //Validacion de contraseña
            System.out.println("Contra vieja = " + aux.getContra());
            System.out.println("Contra nueva = " + VerContra.getText());
            if(VerContra.getText().length()>5){
                flag = false;
                lblContraseñaError.setText("No debe superar 5 caracteres");
            } else if(VerContra.getText().isEmpty() || VerContra.getText().length()<3){
                flag = false;
                lblContraseñaError.setText("Debe contener al menos 3 caracteres");
            } else{
                lblContraseñaError.setText("");
            }
            //Validacion de Nivel
            if(Nivel.getValue() == null){
                flag = false;
                lblNivelError.setText("Seleccione un nivel");
            } else{
                int x=1;
                lblNivelError.setText("");
                if("0. Administrador".equals(Nivel.getValue()))
                    x=0;
                if("1. Tiempo Completo".equals(Nivel.getValue()))
                    x=1;
                if("2. Fin de Semana".equals(Nivel.getValue()))
                    x=2;
            }     
    
            //Validacion Nombre
            if(TxtNombre.getText().isEmpty()){
                lblNombreError.setText("No debe estar vacio");
                flag = false;
            } else if(TxtNombre.getText().length() > 50){
                lblNombreError.setText("No debe superar 50 caracteres");
                flag = false;
            } else{
                lblNombreError.setText("");
            }
            //Validacion apellido
            if(TxtApellido.getText().isEmpty()){
                lblApellidoError.setText("No debe estar vacio");
                flag = false;
            } else if(TxtApellido.getText().length() > 50){
                lblApellidoError.setText("No debe superar 50 caracteres");
            } else{
                lblApellidoError.setText("");
            }
            //Validacion Direccion
            if(TxtDireccion.getText().isEmpty()){
                lblDireccionError.setText("No debe estar vacio");
                flag = false;
            } else if(TxtDireccion.getText().length() > 300){
                lblDireccionError.setText("No debe superar 300 caracteres");
            } else{
                lblDireccionError.setText("");
            }
            //Validacion Telefono
            if(TxtTelefono.getText().isEmpty()){
                lblTelefonoError.setText("No debe estar vacio");
                flag = false;
            } else if(TxtTelefono.getText().length() > 300){
                lblTelefonoError.setText("No debe superar 300 caracteres");
                flag = false;
            } else{
                lblTelefonoError.setText("");
            }
            //Validacion numero empleado
            if(NumEmpleado.getText().isEmpty()){
                lblErrorNumEmpleado.setText("No debe estar vacio");
                flag = false;
            } else if(NumEmpleado.getText().length() > 2){
                lblErrorNumEmpleado.setText("No debe superar 2 digitos");
                flag = false;
            } else{
                lblErrorNumEmpleado.setText("");
            }
            
            if(IneView.getImage()== null) { //Si no se selecciono imagen para Ine flagIne se vuelve falso
                flagIne = false;        
            }
            
            try {
                if(flag){       
                    SQL();
                    succesAlert.setTitle("Modificacion satisfactoria");
                    succesAlert.setHeaderText(null);
                    succesAlert.initOwner(Modificar.getScene().getWindow());
                    succesAlert.setContentText("Empleado modificado correctamente");
                    succesAlert.showAndWait();
                    ReinicioTxtFields();
                } else {
                    flag = true;
                }        
            } catch (SQLException ex) {
                Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                incompleteAlert.setTitle("Error al modificar empleado");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.initOwner(Modificar.getScene().getWindow());
                incompleteAlert.setContentText("Hubo un error al modificar empleado");
                incompleteAlert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
        
        Eliminar.setOnAction((ActionEvent e) -> { 
            String resetStmt;
            if(Tabla.getSelectionModel().isEmpty()){
                    String mensaje = "No se ha seleccionado ningun empleado. \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Eliminar empleado");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Eliminar.getScene().getWindow());
                    incompleteAlert.showAndWait();
            }else{
                EliminarEmpleado();
                try {
                    resetStmt = "SELECT * FROM empleado";
                    Tabla.refresh();
                    empleados.removeAll(empleados);
                    Tabla.setItems(ObtenerEmpleados());
                    ReinicioTxtFields();
                } catch (SQLException ex) {
                    Logger.getLogger(ModificarAlmacenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//FIN ELIMINAR
        
        btnIne.setOnAction(btnIneLoadEventListener);
        btnInePost.setOnAction(btnInePostLoadEventListener);
    }    
    
    public void SQL() throws SQLException, IOException
    {
        String query;
        int x = 0;
        if(Nivel.getValue() == "0. Administrador")
            x=0;
        if(Nivel.getValue() == "1. Tiempo Completo")
            x=1;
        if(Nivel.getValue() == "2. Fin de Semana")
            x=2;
        if(INEMod == true || INEPostMod == true)
        {
            query = "UPDATE empleado SET usuario = ?, contra = ?, nivel = ?, Nombre = ?, Apellido = ?, Direccion = ?, Telefono = ?, Referencia = ?, Ine = ?, InePosterior = ?, NumEmpleado = ? WHERE usuario = '"+aux.getUsuario()+"'";
            conn.preparedStatement(query);
            conn.stmt.setString(1, IDtxt.getText());
            conn.stmt.setString(2, VerContra.getText());
            conn.stmt.setInt(3, x);
            conn.stmt.setString(4, TxtNombre.getText());
            conn.stmt.setString(5, TxtApellido.getText());
            conn.stmt.setString(6, TxtDireccion.getText());
            conn.stmt.setString(7, TxtTelefono.getText());
            conn.stmt.setString(8, Referencia.getText());
            conn.stmt.setBinaryStream(9, fisIne,(int)fileIne.length());
            conn.stmt.setBinaryStream(10, fisInePost,(int)fileInePost.length());
            conn.stmt.setInt(11, Integer.parseInt(NumEmpleado.getText()));
        }
        else
        {
            query = "UPDATE empleado SET usuario = ?, contra = ?, nivel = ?, Nombre = ?, Apellido = ?, Direccion = ?, Telefono = ?, Referencia = ?, NumEmpleado = ? WHERE usuario = '"+aux.getUsuario()+"'";
            conn.preparedStatement(query);
            conn.stmt.setString(1, IDtxt.getText());
            conn.stmt.setString(2, VerContra.getText());
            conn.stmt.setInt(3, x);
            conn.stmt.setString(4, TxtNombre.getText());
            conn.stmt.setString(5, TxtApellido.getText());
            conn.stmt.setString(6, TxtDireccion.getText());
            conn.stmt.setString(7, TxtTelefono.getText());
            conn.stmt.setString(8, Referencia.getText());
            conn.stmt.setInt(9, Integer.parseInt(NumEmpleado.getText()));
        }
        conn.stmt.executeUpdate();
        conn.Commit();
    }
    //Funcion Eliminar
    void EliminarEmpleado(){
        String StmtSq;
        aux = Tabla.getSelectionModel().getSelectedItem();
        StmtSq = "DELETE FROM empleado WHERE NumEmpleado='"+aux.getNumEmp()+"'";                  
        if(conn.QueryUpdate(StmtSq))
        {
            String mensaje = "Empleado Eliminado \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Eliminar Empleado");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
        else
        {
            String mensaje = "Empleado no se ha podido eliminar \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Eliminar Empleado");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(Eliminar.getScene().getWindow());
            incompleteAlert.showAndWait();
        }
   }
    
    //Fuincion para iniciar el Combobox nivel
    public void iniciarComboBox()
    {
        //inicia el combobox Nivel
        ObservableList<String> options = FXCollections.observableArrayList("0. Administrador","1. Tiempo Completo", "2. Fin de Semana");
        Nivel.getItems().addAll(options);
    }
    
    //Funciín para llenar los Textfields de modificación
    public void llenado (Empleados emp) throws SQLException
    {
        String cadena;
        int lvl;
        String ID = IDtxt.getText();
        String qry = "Select * From empleado where usuario='"+ID+"'";
        
        if(conn.QueryExecute(qry))
        {
            if(conn.setResult.first())
            {
                blob = null;
                data = null;
                img=null;
                image=null;
                blob = conn.setResult.getBlob("Foto");
                if(blob != null){
                    data = blob.getBytes(1, (int)blob.length());
                    try{
                        img = ImageIO.read(new ByteArrayInputStream(data));
                    }catch(IOException ex){
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    image = SwingFXUtils.toFXImage(img, null);
                    Foto.setImage(image);
                }
                //Funcion para Mostrar el Ine;
                blob = null;
                data = null;
                img=null;
                image=null;
                blob = conn.setResult.getBlob("Ine");
                if(blob != null){
                    data = blob.getBytes(1, (int)blob.length());
                    try{
                        img = ImageIO.read(new ByteArrayInputStream(data));
                    }catch(IOException ex){
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    image = SwingFXUtils.toFXImage(img, null);
                    IneView.setImage(image);
                 }
                else
                    ResetImagenIne();
                
                //Funcion para mostrar el Ine Posterior
                blob = null;
                data = null;
                img=null;
                image=null;
                blob = conn.setResult.getBlob("InePosterior");
                if(blob != null){
                    data = blob.getBytes(1, (int)blob.length());
                    try{
                        img = ImageIO.read(new ByteArrayInputStream(data));
                    }catch(IOException ex){
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    image = SwingFXUtils.toFXImage(img, null);
                    InePostView.setImage(image);
                }
                else
                    ResetImagenInePosterior();
                
            }
        }
        
        Contraseña = emp.getContra();
        VerContra.setVisible(false);
        VerContra.setText(Contraseña);
        TxtContra.setText(Contraseña);
        IDtxt.setText(emp.getUsuario());
        TxtNombre.setText(emp.getNombre());
        TxtApellido.setText(emp.getApellido());
        TxtDireccion.setText(emp.getDireccion());
        TxtTelefono.setText(emp.getTelefono());
        Referencia.setText(emp.getReferencia());
        cadena = String.valueOf(emp.getNumEmp());
        NumEmpleado.setText(cadena);
        lvl = emp.getNivel();
        if(lvl == 0)
            Nivel.setValue("0. Administrador");
        if(lvl == 1)
            Nivel.setValue("1. Tiempo Completo");
        if(lvl == 2)
            Nivel.setValue("2. Fin de Semana");
    }
    
    //Limpia todos los campos del formulario
    public void ReinicioTxtFields(){
        usuario.setText("");
        TxtContra.setText("");
        Nivel.getSelectionModel().clearSelection();
        Nivel.getItems().clear();
        iniciarComboBox();
        TxtNombre.setText("");
        TxtApellido.setText("");
        TxtDireccion.setText("");
        TxtTelefono.setText("");
        NumEmpleado.setText("");
        Referencia.setText("");
        Foto.setImage(null);
        IneView.setImage(null);
        InePostView.setImage(null);
    }

    EventHandler<ActionEvent> btnIneLoadEventListener
    = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
             
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            fileIne = fileChooser.showOpenDialog(btnIne.getScene().getWindow());
            if(fileIne == null){
            }
            else {
                try {
                    fisIne = new FileInputStream(fileIne);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch( Error e){
                }          
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(fileIne);
                } catch (IOException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                IneView.autosize();
                IneView.setImage(image);
                INEMod = true;
            }
        }
    };
    //Manejador de eventos para seleccion de imagen InePost
    EventHandler<ActionEvent> btnInePostLoadEventListener
    = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
             
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            fileInePost = fileChooser.showOpenDialog(btnInePost.getScene().getWindow());
            if(fileInePost == null){
            }
            else {
                try {
                    fisInePost = new FileInputStream(fileInePost);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch( Error e){
                }          
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(fileInePost);
                } catch (IOException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                InePostView.autosize();
                InePostView.setImage(image);
                INEPostMod = true;
            }
        }
    };
    
    public void limpiar(){
        lblNombreError.setText("");
        lblApellidoError.setText("");
        lblDireccionError.setText("");
        lblTelefonoError.setText("");   
        lblUsuarioError.setText("");
        lblContraseñaError.setText("");
        lblNivelError.setText("");   
        lblImageIneError.setText("");    
        lblErrorNumEmpleado.setText("");
        lblImageInePostError.setText("");
        Foto.setImage(null);
        IneView.setImage(null);
        InePostView.setImage(null);
    }
    
    public void ResetImagenIne() {
        boolean BanderaImagen = false;
        File file = new File("src/xxcell/Images/No_Credencial.png");
        Image image = new Image(file.toURI().toString());  
        IneView.setImage(image);
        lblImageIneError.setText("No hay Imagen Ine");
    }
    public void ResetImagenInePosterior() {
        boolean BanderaImagen = false;
        File file = new File("src/xxcell/Images/No_Credencial.png");
        Image image = new Image(file.toURI().toString());  
        InePostView.setImage(image);
        lblImageInePostError.setText("Sin Imagen Ine");
    }
    
    public void mostrarImagen(String columna) throws IOException{
        Variables_Globales.empleado = aux;
        Variables_Globales.Columna = columna;
        String query = "SELECT "+columna+" FROM empleado WHERE NumEmpleado = '"+aux.getNumEmp()+"'";
        conn.QueryExecute(query);
        try {
            if(conn.setResult.first()) {
                if(conn.setResult.getBlob(columna) != null){
                    try {
                        Parent principal;
                        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/PopUpEmpleado.fxml"));
                        Stage principalStage = new Stage();
                        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
                        scene = new Scene(principal);
                        principalStage.setScene(scene);
                        principalStage.initModality(Modality.APPLICATION_MODAL);
                        principalStage.setTitle("Imagen de Producto");
                        principalStage.initOwner(btnInePost.getScene().getWindow());
                        principalStage.showAndWait(); 
                    } catch (IOException ex) {
                        Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
