/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

public class SideMenuController implements Initializable {
    @FXML
    private JFXButton btnVenta;

    @FXML
    private JFXButton btnConsultas;
    
    @FXML
    private JFXButton btnAlmacen;

    @FXML
    private JFXButton btnRecursosHumanos;
    
    @FXML
    private JFXButton btnCerrarSesion;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        habilitaOpciones();
    }    
    
    
    public void habilitaOpciones(){
        System.out.println(Variables_Globales.Rol);
        if(!Variables_Globales.Rol.equals("0")) {
            btnAlmacen.setDisable(true);
            btnAlmacen.setVisible(false);
            btnRecursosHumanos.setDisable(true);
            btnRecursosHumanos.setVisible(false);
        }
    }
    
}
