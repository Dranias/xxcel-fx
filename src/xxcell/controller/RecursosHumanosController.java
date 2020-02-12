/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static xxcell.controller.LoginController.scene;

public class RecursosHumanosController implements Initializable {

    @FXML
    private JFXButton AgregarEmp;
    
    @FXML
    private JFXButton Sueldos;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    void AddEmpleadoAction(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Alta Empleado.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(AgregarEmp.getScene().getWindow());
        principalStage.showAndWait();
    }
    
    @FXML
    void VentanaSueldos(ActionEvent event) throws IOException {
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/Sueldos.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.APPLICATION_MODAL);
        principalStage.initOwner(Sueldos.getScene().getWindow());
        principalStage.showAndWait();
    }
    
}
