package xxcell.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javax.imageio.ImageIO;
import xxcell.Conexion.Conexion;

public class PopUpEmpleadoController implements Initializable {

    Conexion conn = new Conexion();
    
    @FXML
    private ImageView imageView;
    
    @FXML
    private AnchorPane pane;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Variables para las Imagenes
        Blob blob;
        double height, width;
        byte[] data;
        BufferedImage img;
        WritableImage image;
        //*************************
        String query = "SELECT "+Variables_Globales.Columna+" FROM empleado WHERE NumEmpleado = '"+Variables_Globales.empleado.getNumEmp()+"'";
        conn.QueryExecute(query);
        try {
            if(conn.setResult.first()) {
                blob = conn.setResult.getBlob(Variables_Globales.Columna);
                if(blob != null){
                    data = blob.getBytes(1, (int)blob.length());
                    try{
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        image = SwingFXUtils.toFXImage(img, null);
                        pane.setPrefHeight(50);
                        pane.setPrefWidth(50);
                        imageView.setImage(image);
                    }catch(IOException ex){
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PopUpImagenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
