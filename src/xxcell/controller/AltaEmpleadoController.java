/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import xxcell.Conexion.Conexion;

public class AltaEmpleadoController implements Initializable {
    
    Conexion conn = new Conexion();
    PreparedStatement ps = null;
    
    File file;
    File fileIne;
    File fileInePost;
    FileInputStream fin;
    FileInputStream fisIne;
    FileInputStream fisInePost;
    
    @FXML
    private ImageView FotoView;

    @FXML
    private JFXButton Foto;
    
    @FXML
    private JFXButton btnIne;

    @FXML
    private JFXButton Cancelar;

    @FXML
    private JFXTextField Contra;

    @FXML
    private JFXTextField usuario;

    @FXML
    private JFXButton Agregar;

    @FXML
    private JFXTextField txtNombre;

    @FXML
    private JFXTextField txtApellido;

    @FXML
    private JFXTextField txtDireccion;

    @FXML
    private JFXTextField txtTelefono;

    @FXML
    private JFXDatePicker datepickContratacion;
    
    @FXML
    private JFXTextField txtNumEmpleado;

    @FXML
    private JFXTextArea txtareaReferencia;

    @FXML
    private ImageView IneView;

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
    private JFXComboBox<String> Nivel; 
    
    @FXML
    private Label lblNivelError;
    
    @FXML
    private Label lblImageFotoError;

    @FXML
    private Label lblImageIneError;
    
    @FXML
    private Label lblErrorContratacion;

    @FXML
    private Label lblErrorNumEmpleado;
    
    @FXML
    private ImageView InePostView;

    @FXML
    private JFXButton btnInePost;
        
    private Boolean flag = true;
    private Boolean flagIne = true;
    
    LocalDate ld;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        iniciarComboBox();
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
        
        txtTelefono.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        txtNumEmpleado.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
      
        //Regresa a Recursos Humanos
        Cancelar.setOnAction((ActionEvent e) -> {
            Stage stage;
            stage = (Stage) Cancelar.getScene().getWindow();
            stage.close();
        }); 
        
        //Acciones para el boton Agregar
        Agregar.setOnAction((ActionEvent e) -> {
            String mensaje = "";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
//************************Validacion de datos************************
            //Validacion de usuario
            if(usuario.getText().length()>10){
                lblUsuarioError.setText("No debe superar 10 caracteres");
                flag = false;
            } else if(usuario.getText().isEmpty() || usuario.getText().length()<5){
                flag = false;
                lblUsuarioError.setText("Debe contener al menos 5 caracteres");
            } else
                lblUsuarioError.setText("");
            //Validacion de contraseña
            if(Contra.getText().length()>5){
                flag = false;
                lblContraseñaError.setText("No debe superar 5 caracteres");
            } else if(Contra.getText().isEmpty() || Contra.getText().length()<3){
                flag = false;
                lblContraseñaError.setText("Debe contener al menos 3 caracteres");
            } else
                lblContraseñaError.setText("");
            //Validacion de Nivel
            if(Nivel.getValue() == null){
                flag = false;
                lblNivelError.setText("Seleccione un nivel");
            } else
                lblNivelError.setText("");
            //Validacion de Foto
            if(FotoView.getImage() == null){
                lblImageFotoError.setText("Seleccione Fotografia");
                flag = false;
            } else
                lblImageFotoError.setText("");
            //Validacion Nombre
            if(txtNombre.getText().isEmpty()){
                lblNombreError.setText("No debe estar vacio");
                flag = false;
            } else if(txtNombre.getText().length() > 50){
                lblNombreError.setText("No debe superar 50 caracteres");
                flag = false;
            } else
                lblNombreError.setText("");
            //Validacion apellido
            if(txtApellido.getText().isEmpty()){
                lblApellidoError.setText("No debe estar vacio");
                flag = false;
            } else if(txtApellido.getText().length() > 50){
                lblApellidoError.setText("No debe superar 50 caracteres");
            } else
                lblApellidoError.setText("");
            //Validacion Direccion
            if(txtDireccion.getText().isEmpty()){
                lblDireccionError.setText("No debe estar vacio");
                flag = false;
            } else if(txtDireccion.getText().length() > 300){
                lblDireccionError.setText("No debe superar 300 caracteres");
            } else
                lblDireccionError.setText("");
            //Validacion Telefono
            if(txtTelefono.getText().isEmpty()){
                lblTelefonoError.setText("No debe estar vacio");
                flag = false;
            } else if(txtTelefono.getText().length() > 300){
                lblTelefonoError.setText("No debe superar 300 caracteres");
            } else
                lblTelefonoError.setText("");
            //Validacion Contratacion
            if(datepickContratacion.getValue() == null){
                lblErrorContratacion.setText("No debe estar vacio");
                flag = false;
            } else
                lblErrorContratacion.setText("");
            //Validacion numero empleado
            if(txtNumEmpleado.getText().isEmpty()){
                lblErrorNumEmpleado.setText("No debe estar vacio");
                flag = false;
            } else if(txtNumEmpleado.getText().length() > 2){
                lblErrorNumEmpleado.setText("No debe superar 2 digitos");
            } else
                lblErrorNumEmpleado.setText("");
            
            if(IneView.getImage()== null) { //Si no se selecciono imagen para Ine flagIne se vuelve falso
                flagIne = false;        
            }
            
            try {
                if(flag){       
                    AgregarSQL();
                } else {
                    incompleteAlert.setTitle("Error al agregar empleado");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.initOwner(Agregar.getScene().getWindow());
                    incompleteAlert.setContentText("Hubo un error al agregar empleado");
                    incompleteAlert.showAndWait();
                    flag = true;
                }
                    
            } catch (SQLException ex) {
                Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Stage stage;
            //stage = (Stage) Agregar.getScene().getWindow();
            //stage.close();
        }); 
        //Agregar una imagen
        Foto.setOnAction(btnLoadEventListener); 
        btnIne.setOnAction(btnIneLoadEventListener);
        btnInePost.setOnAction(btnInePostLoadEventListener);
    }    
    
    public void AgregarSQL() throws SQLException {
        ld = datepickContratacion.getValue();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Alert succesAlert = new Alert(Alert.AlertType.INFORMATION);
        boolean bandera = true;
        String query;
        int nivel = 1;
        
        query = "SELECT * FROM empleado WHERE usuario='"+usuario.getText()+"'";
        conn.QueryExecute(query);
        try {
            if(conn.setResult.first()) {
                bandera = false;
            }
        } catch (SQLException e) {
            }
        
        query = "SELECT * FROM empleado WHERE NumEmpleado='"+Integer.parseInt(txtNumEmpleado.getText())+"'";
        conn.QueryExecute(query);
        try {
            if(conn.setResult.first()) {
                bandera = false;
            }
        } catch (SQLException e) {
            }
        //Si no hay cohincidencias en la base de datos puede continuar el query
        if(bandera){
            if(Nivel.getValue().equals("0. Administrador"))
                nivel = 0;
            if(Nivel.getValue().equals("1. Tiempo Completo"))
                nivel = 1;
            if(Nivel.getValue().equals("2. Fin de Semana"))
                nivel = 2;
            if(flagIne){ //Se comprueba el valor de flagIne para obtener el query correspondiente
                query = "Insert into empleado(usuario,contra,nivel,Foto,Nombre,Apellido,Direccion,";
                query += "Telefono,Referencia,Ine,Contratacion,InePosterior,NumEmpleado) ";
                query += "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                conn.preparedStatement(query);
                conn.stmt.setString(1, usuario.getText());
                conn.stmt.setString(2, Contra.getText());
                conn.stmt.setInt(3, nivel);
                conn.stmt.setBinaryStream(4, fin,(int)file.length());
                conn.stmt.setString(5, txtNombre.getText());
                conn.stmt.setString(6, txtApellido.getText());
                conn.stmt.setString(7, txtDireccion.getText());
                conn.stmt.setString(8, txtTelefono.getText());
                conn.stmt.setString(9, txtareaReferencia.getText());
                conn.stmt.setBinaryStream(10, fisIne,(int)fileIne.length());
                conn.stmt.setDate(11, Date.valueOf(ld));
                conn.stmt.setBinaryStream(12, fisInePost,(int)fileInePost.length());
                conn.stmt.setInt(13, Integer.parseInt(txtNumEmpleado.getText()));
            } else {
                query = "Insert into empleado(usuario,contra,nivel,Foto,Nombre,Apellido,Direccion,Telefono,Referencia,Contratacion,NumEmpleado) values (?,?,?,?,?,?,?,?,?,?,?)";
                conn.preparedStatement(query);
                conn.stmt.setString(1, usuario.getText());
                conn.stmt.setString(2, Contra.getText());
                conn.stmt.setInt(3, nivel);
                conn.stmt.setBinaryStream(4, fin,(int)file.length());
                conn.stmt.setString(5, txtNombre.getText());
                conn.stmt.setString(6, txtApellido.getText());
                conn.stmt.setString(7, txtDireccion.getText());
                conn.stmt.setString(8, txtTelefono.getText());
                conn.stmt.setString(9, txtareaReferencia.getText());
                conn.stmt.setDate(10, Date.valueOf(ld));
                conn.stmt.setInt(11, Integer.parseInt(txtNumEmpleado.getText()));
            }
            conn.stmt.executeUpdate();
            conn.Commit();
            succesAlert.setTitle("Alta satisfactoria");
            succesAlert.setHeaderText(null);
            succesAlert.initOwner(Agregar.getScene().getWindow());
            succesAlert.setContentText("Empleado registrado correctamente");
            succesAlert.showAndWait();
            ReinicioTxtFields();
        }
        else{
            alert.setTitle("Error");
            alert.setHeaderText("Error en la alta de Empleado");
            alert.initOwner(Agregar.getScene().getWindow());
            alert.setContentText("El 'Número de Empleado' o el 'Nombre de Usuario' ya han sido registrados.");
            alert.showAndWait();
        }
    }
    
    public void iniciarComboBox()
    {
        //inicia el combobox Nivel
        ObservableList<String> options = FXCollections.observableArrayList("0. Administrador","1. Tiempo Completo", "2. Fin de Semana");
        Nivel.getItems().addAll(options);
    }
    
    //Manejador de eventos para seleccion de imagen Foto
    EventHandler<ActionEvent> btnLoadEventListener
    = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
             
            //Set extension filter 0442228297092
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            FileChooser.ExtensionFilter JPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.JPEG");
            fileChooser.getExtensionFilters().addAll(JPEG, extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            file = fileChooser.showOpenDialog(Cancelar.getScene().getWindow());
            if(file == null){
                
            }
            else {
                try {
                    fin = new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch( Error e){
                    
                }          
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(file);
                } catch (IOException ex) {
                    Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                FotoView.autosize();
                FotoView.setImage(image);
            }
        }
    };
    //Manejador de eventos para seleccion de imagen Ine
    EventHandler<ActionEvent> btnIneLoadEventListener
    = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
             
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            FileChooser.ExtensionFilter JPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.JPEG");
            fileChooser.getExtensionFilters().addAll(JPEG, extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            fileIne = fileChooser.showOpenDialog(Cancelar.getScene().getWindow());
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
            FileChooser.ExtensionFilter JPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.JPEG");
            fileChooser.getExtensionFilters().addAll(JPEG, extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            fileInePost = fileChooser.showOpenDialog(Cancelar.getScene().getWindow());
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
            }
        }
    };
    
    //Limpia todos los campos del formulario
    public void ReinicioTxtFields(){
        usuario.setText("");
        Contra.setText("");
        Nivel.getSelectionModel().clearSelection();
        Nivel.getItems().clear();
        iniciarComboBox();
        txtNombre.setText("");
        txtApellido.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        datepickContratacion.setValue(null);
        txtNumEmpleado.setText("");
        txtareaReferencia.setText("");
        FotoView.setImage(null);
        InePostView.setImage(null);
        IneView.setImage(null);
    }
}
