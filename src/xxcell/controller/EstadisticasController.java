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

/**
 * FXML Controller class
 *
 * @author Adrián Pérez
 */
public class EstadisticasController implements Initializable {

    @FXML
    private JFXButton btnEstadisticaProducto;
    @FXML
    private JFXButton btnEstadisticaCosto;
    @FXML
    private JFXButton btnEstadisticaUtilidad;
    @FXML
    private JFXButton btnRastreo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(!Variables_Globales.Rol.equals("0")){
            btnEstadisticaCosto.setDisable(true);
            btnEstadisticaCosto.setVisible(false);
            btnEstadisticaUtilidad.setDisable(true);
            btnEstadisticaUtilidad.setVisible(false);
        }
    }    
    
}
