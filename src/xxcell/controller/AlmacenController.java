package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.GeneraCodigos;
import xxcell.model.generaCodigoBarras;

public class AlmacenController implements Initializable {
    
    @FXML
    private JFXButton AgregProd;
    
    @FXML
    private JFXButton btnRespaldar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        GeneraCodigos generador = new GeneraCodigos();
        try {
            generador.GeneraCodigoNumerico();
        } catch (SQLException ex) {
            Logger.getLogger(AlmacenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void AddAction(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/AgregarProducto.fxml"));
        Stage principalStage = new Stage();
        scene = new Scene(principal);
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.setResizable(false);
        principalStage.setTitle("XXCELL - Agregar producto");
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void ActionRespaldo(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/ModificarPrecios.fxml"));
        Stage principalStage = new Stage();
        scene = new Scene(principal);
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.setResizable(false);
        principalStage.setTitle("XXCELL - Agregar producto");
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void ActionEntradas(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/EntradasInventario.fxml"));
        Stage principalStage = new Stage();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double origH = primaryScreenBounds.getHeight();
        scene = new Scene(principal);
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.setResizable(false);
        principalStage.setTitle("XXCELL - Entrada de inventario");
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
        
    private void cargarScene(String path, String title) throws IOException{
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource(path));
        Stage principalStage = new Stage();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double origH = primaryScreenBounds.getHeight();
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.setResizable(false);
        principalStage.setTitle("XXCELL - "+title+"");
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void ActionGaleria(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Galery.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.WINDOW_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void ActionGenerarCodigos(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Galery.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.WINDOW_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void ActionAgregadoRapido(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/AgregadoRapido.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.WINDOW_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(AgregProd.getScene().getWindow());
        principalStage.showAndWait(); 
    }
    
    @FXML
    void CrearCodigosPng(ActionEvent event) throws IOException, SQLException {
        Conexion conn = new Conexion();
        conn.ConnectionOpen();
        generaCodigoBarras codigoBarras = new generaCodigoBarras();
        String query = "SELECT ID, Marca, Modelo, Tipo, Identificador FROM productos";
        String producto, codigo;
        if(conn.QueryExecute(query))
        {
            while (conn.setResult.next())
            {
                codigo = conn.setResult.getString("ID");
                producto = conn.setResult.getString("Marca") + "_";
                producto += conn.setResult.getString("Modelo") + "_";
                producto += conn.setResult.getString("Tipo") + "_";
                producto += conn.setResult.getString("Identificador") + ".png";
                codigoBarras.createBarCode128(producto, codigo);
            }    
        }
        conn.setResult.close();
    }
}
