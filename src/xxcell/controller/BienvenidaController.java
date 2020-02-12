/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class BienvenidaController implements Initializable {
    @FXML
    private ImageView imgLogo;
    
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private HBox hbImage;

    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            
        });
        //Image image = new Image(BienvenidaController.class.getResourceAsStream("/xxcell/Images/XXCELL_Logo.png"));
        //imgLogo.setImage(image);
        
//        imgLogo.fitWidthProperty().bind(hbImage.widthProperty());
//        imgLogo.fitHeightProperty().bind(hbImage.heightProperty());
    }    
    
}
