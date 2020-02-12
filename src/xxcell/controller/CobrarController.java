/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class CobrarController implements Initializable {
    
    @FXML
    private StackPane root;
    
    @FXML
    private Label lblCantidadLetra;

    @FXML
    private Label lblCobrarView;

    @FXML
    private JFXTextField txtCantidad;

    @FXML
    private Label lblCambioView;

    @FXML
    private JFXButton btnCancelar;

    @FXML
    private JFXButton btnAceptar;
    
    Funciones fun = new Funciones();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCobrarView.setText(Variables_Globales.totalVenta);
        btnAceptar.setDefaultButton(true);
        btnCancelar.setCancelButton(true);
        txtCantidad.requestFocus();
        // TODO
        
        txtCantidad.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            CalculaCambio();
        });
    }    
    
    @FXML
    void ActionAceptar(ActionEvent event) {
        if(CobroCorrecto()){
            Variables_Globales.ventaRealizada = true;
            Stage stage;
            stage = (Stage) btnAceptar.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void ActionCancelar(ActionEvent event) {
        Variables_Globales.ventaRealizada = false;
        Stage stage;
        stage = (Stage) btnAceptar.getScene().getWindow();
        stage.close();
    }
        
    private void CalculaCambio(){
		double pagar;
		double recibida;
		double cambio;
		DecimalFormat formato = new DecimalFormat("####0.00");
		
		if(!lblCobrarView.getText().isEmpty() && !txtCantidad.getText().isEmpty()) {
			pagar = Double.valueOf(lblCobrarView.getText());
			
			recibida = Double.valueOf(txtCantidad.getText());
			Variables_Globales.recibida = recibida;
			if(recibida>=pagar){
				cambio = recibida-pagar;
                                Variables_Globales.cambio = cambio;
				lblCambioView.setText(formato.format(cambio));
                                cambioletras();
				//lblViewcambio.setText(String.format("%5.2f", cambio));
			}
                        else{
				lblCambioView.setText("0.00");
                                Variables_Globales.cambio = 0.00;
                        }
		} else if(txtCantidad.getText().length()==0)
                    lblCantidadLetra.setText(null);
	}
	
	private boolean CobroCorrecto(){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cobrar");
            alert.setContentText("La cantidad ingresada debe ser mayor o igual al total");
            alert.initOwner(btnAceptar.getScene().getWindow());
            boolean result=true;
            if(txtCantidad.getText().length() > 0){
                if(Double.valueOf(txtCantidad.getText()) < Double.valueOf(lblCobrarView.getText())){
            //if(lblCambioView.getText().equals("0.00")){
                    System.out.println("Error en cantidad recibida");
                    alert.showAndWait();        
                    result = false;
                }
            } else {
                alert.showAndWait();        
                result = false;
            }
                    
            return result;
	}
        
        public void cambioletras(){
            String sEntero;
            String decimal;
            int longitud;
            int iEntero;

            longitud = lblCambioView.getText().length();

            sEntero = lblCambioView.getText().substring(0, longitud-3);
            iEntero = Integer.valueOf(sEntero);

            decimal = lblCambioView.getText().substring(longitud-lblCambioView.getText().length())+"/100 M.N";
            lblCantidadLetra.setText(fun.getStringofNumber(iEntero)+" PESOS "+decimal);
        }
}
