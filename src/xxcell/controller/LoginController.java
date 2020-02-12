package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;

public class LoginController implements Initializable {
    
    @FXML private JFXButton btnLogin;
    @FXML private JFXPasswordField password_box;
    @FXML private JFXTextField username_box;
    @FXML private Label lblMensaje;
    
    static Scene scene;
    
    Conexion conn = new Conexion();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            username_box.requestFocus();
        });
        btnLogin.setDefaultButton(true);    
        RequiredFieldValidator validator = new RequiredFieldValidator();                    
        validator.setMessage("Campo requerido");
        username_box.getValidators().add(validator);                    
        password_box.getValidators().add(validator);                    
        username_box.focusedProperty().addListener((o,oldVal,newVal)->{                    
        if(!newVal) username_box.validate();                    
        });
        password_box.focusedProperty().addListener((o,oldVal,newVal)->{                    
        if(!newVal) password_box.validate();                    
        });
        
    }
    
    @SuppressWarnings("empty-statement")
    private boolean isValidCredentials() throws SQLException
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        boolean let_in = false;
        String user = username_box.getText();
        String pass = password_box.getText();
        String sqlStmt;
        sqlStmt = "SELECT * FROM empleado";
	sqlStmt += " WHERE usuario='"+user+"'";
	sqlStmt += " AND contra='"+pass+"'";
        
        conn.ConnectionOpen();
               
        if(!conn.ConnectionOpen()) {
            alert.setTitle("Error");
            alert.setHeaderText("No hay conexion con la base de datos");
            alert.setContentText("Lo sentimos, no se ha podido establecer la conexion con la base de datos");
            alert.showAndWait();        
        }
        else {
            conn.QueryExecute(sqlStmt);
            try {
                if(conn.setResult.first()) {
                    let_in = true;
                    Variables_Globales.Rol = conn.setResult.getString("nivel");
                } else {
                    username_box.clear();
                    password_box.clear();
                    lblMensaje.setText("Usuario y/o Contraseña incorrectos"); 
                    username_box.requestFocus();
                }
            } catch (SQLException e) {
                
                //No pondré nada aquí, me piro.
            }
        }//fin else
        conn.sqlStmt.close();
        return let_in;
    }
    
    @FXML
    void actionLogin(ActionEvent e) throws IOException {
            String mensaje = "";
            if(username_box.getText().length() == 0) {
                mensaje = "Usuario";
                username_box.requestFocus();
            }

            if(password_box.getText().length() == 0) {
                if(mensaje.length()==0)
                    password_box.requestFocus();
                mensaje = mensaje+"\n"+"Password";
            }

            if(mensaje.length() > 0){
                    mensaje = "Faltan los siguientes datos: \n"+mensaje;
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Datos incompletos");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.showAndWait();
            } else try {
                
                if(isValidCredentials()){
                    construirScene(e);
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }    

    }
    
    private void construirScene(ActionEvent e) throws IOException {
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
        
        principalStage.setOnCloseRequest(x -> {
            conn.ConnectionClose();
            System.out.println("Aios");
        });
    }
    
    public static Scene getScene() {
        return scene;
    }
}
