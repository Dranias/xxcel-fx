package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import xxcell.Conexion.Conexion;


public class SueldosController implements Initializable {

    boolean ban;
    Alert succesAlert = new Alert(Alert.AlertType.INFORMATION);
    Alert alert = new Alert(Alert.AlertType.ERROR);
    
    Conexion conn = new Conexion();
    String qry;
    //Botones
    @FXML
    private JFXButton Actualizar;
    @FXML
    private JFXButton Cancelar;
    
    //TextFields
    @FXML
    private JFXTextField SueldoBase;

    @FXML
    private JFXTextField VentasMeta;

    @FXML
    private JFXTextField ComisionBase;

    @FXML
    private JFXTextField ComisionMeta;

    @FXML
    private JFXTextField Faltas;

    @FXML
    private JFXTextField DiaExtra;
    
    //Combobox
    @FXML
    private JFXComboBox<String> Nivel;
    
    //Label Error
    @FXML
    private Label lblErrSueldo;

    @FXML
    private Label lblErrVentasMeta;

    @FXML
    private Label lblErrComisionBase;

    @FXML
    private Label lblErrComisionMeta;

    @FXML
    private Label lblErrDesntFaltas;

    @FXML
    private Label lblErrdiaExtra;
    
    int x;
    
    public void iniciarComboBox()
    {
        //inicia el combobox Nivel
        ObservableList<String> options = FXCollections.observableArrayList("1. Tiempo Completo","2. Fin de Semana");
        Nivel.getItems().addAll(options);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ActivarTextFields(true);
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
        
        VentasMeta.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        
        iniciarComboBox();
        Nivel.setOnAction(e -> {
            ActivarTextFields(false);
            if("1. Tiempo Completo".equals(Nivel.getValue()))
                x=1;
            else
                x=2;
            qry = "SELECT * FROM sueldos WHERE Nivel = '"+x+"'";
            conn.QueryExecute(qry);
            try{
                if(conn.setResult.first()){
                    ban = true;
                    SueldoBase.setText(String.valueOf(conn.setResult.getFloat("Base")));
                    VentasMeta.setText(String.valueOf(conn.setResult.getInt("NumVentas")));
                    ComisionBase.setText(String.valueOf(conn.setResult.getFloat("ComisionBase")));
                    ComisionMeta.setText(String.valueOf(conn.setResult.getFloat("ComisionMeta")));
                    Faltas.setText(String.valueOf(conn.setResult.getFloat("DescuentoFalta")));
                    DiaExtra.setText(String.valueOf(conn.setResult.getFloat("DiaExtra")));
               }
                else{
                    ban = false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(SueldosController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        Actualizar.setOnAction((ActionEvent e) -> {
            int succes = 0;
            boolean AccesoSQL = true;
//*********************************Validación Sueldo Base******************************************
            if(!esnumero(SueldoBase.getText())){
                AccesoSQL = false;
                lblErrSueldo.setText("Ingrese un número ");
            }
            else
                lblErrComisionBase.setText("");
//*********************************Validación Descuento por Faltas**************************************            
            if(!esnumero(Faltas.getText())){
                AccesoSQL = false;
                lblErrDesntFaltas.setText("Ingrese un número");
            }else
                lblErrVentasMeta.setText("");
 //*******************************Validación Comisión Base******************************************
            if(!esnumero(ComisionBase.getText())){
                AccesoSQL = false;
                lblErrComisionBase.setText("Ingrese un número");
            }else{
                lblErrComisionBase.setText("");
            }
 //*******************************Validación Comisión Meta ******************************************
            if(!esnumero(ComisionMeta.getText())){
                AccesoSQL = false;
                lblErrComisionMeta.setText("Ingrese un número");
            }else{
                lblErrComisionMeta.setText("");
            }
//********************************Validación Precio por Día Extra ************************************
            if(!esnumero(DiaExtra.getText())){
                AccesoSQL = false;
                lblErrdiaExtra.setText("Ingrese un número");
            }else{
                lblErrdiaExtra.setText("");
            }
            
            if(AccesoSQL){
                System.out.println("Entre");
                if(ban){
                    if("1. Tiempo Completo".equals(Nivel.getValue()))
                        x=1;
                    else
                        x=2;
                    float decimal;
                    int Ventas;


                    qry = "UPDATE sueldos SET Base=?, NumVentas=?, DescuentoFalta=?, DiaExtra=?, ComisionBase=?, ComisionMeta=? ";
                    qry += "WHERE Nivel = '"+x+"'";
                    try {
                        conn.preparedStatement(qry);
                        //Se inserta el sueldo Base
                        decimal = Float.parseFloat(SueldoBase.getText());                    
                        conn.stmt.setFloat(1, decimal);
                        //Inserta Numero de Ventas para llegar a la comisión
                        Ventas = Integer.parseInt(VentasMeta.getText());
                        conn.stmt.setInt(2, Ventas);
                        //Se inserta el descuento por falta
                        decimal = Float.parseFloat(Faltas.getText());
                        conn.stmt.setFloat(3, decimal);
                        //Se inserta el precio por día extra trabajado
                        decimal = Float.parseFloat(DiaExtra.getText());
                        conn.stmt.setFloat(4, decimal);
                        //Se inserta la comisión base, cuando NO se llega a la meta de ventas
                        decimal = Float.parseFloat(ComisionBase.getText());
                        conn.stmt.setFloat(5, decimal);
                        //Se inserta la comisión meta, cuando SI llegan a la meta de ventas
                        decimal = Float.parseFloat(ComisionMeta.getText());
                        conn.stmt.setFloat(6, decimal);

                        succes = conn.stmt.executeUpdate();
                        conn.Commit();
                    } catch (SQLException ex) {
                        Logger.getLogger(SueldosController.class.getName()).log(Level.SEVERE, null, ex);
                    }  
                }
                else{
                    if("1. Tiempo Completo".equals(Nivel.getValue()))
                        x=1;
                    else
                        x=2;
                    float decimal;
                    int Ventas;
                    qry = "Insert into sueldos (Nivel, Base, NumVentas, DescuentoFalta, DiaExtra, ComisionBase, ComisionMeta) ";
                    qry += "values (?, ?, ?, ?, ?, ?, ?)";
                    try {
                        conn.preparedStatement(qry);
                        //Se inserta a Nivel
                        conn.stmt.setInt(1, x);
                        //Se inserta el sueldo Base
                        decimal = Float.parseFloat(SueldoBase.getText());                    
                        conn.stmt.setFloat(2, decimal);
                        //Inserta Numero de Ventas para llegar a la comisión
                        Ventas = Integer.parseInt(VentasMeta.getText());
                        conn.stmt.setInt(3, Ventas);
                        //Se inserta el descuento por falta
                        decimal = Float.parseFloat(Faltas.getText());
                        conn.stmt.setFloat(4, decimal);
                        //Se inserta el precio por día extra trabajado
                        decimal = Float.parseFloat(DiaExtra.getText());
                        conn.stmt.setFloat(5, decimal);
                        //Se inserta la comisión base, cuando NO se llega a la meta de ventas
                        decimal = Float.parseFloat(ComisionBase.getText());
                        conn.stmt.setFloat(6, decimal);
                        //Se inserta la comisión meta, cuando SI llegan a la meta de ventas
                        decimal = Float.parseFloat(ComisionMeta.getText());
                        conn.stmt.setFloat(7, decimal);

                        succes = conn.stmt.executeUpdate();
                        conn.Commit();
                    } catch (SQLException ex) {
                        Logger.getLogger(SueldosController.class.getName()).log(Level.SEVERE, null, ex);
                    }  
                }
                if(succes == 1){
                    String mensaje = "Sueldo Modificado \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Sueldos");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Actualizar.getScene().getWindow());
                    incompleteAlert.showAndWait();
                }else{
                    String mensaje = "Sueldo NO ha sido Modificado \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    incompleteAlert.setTitle("Sueldos");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Actualizar.getScene().getWindow());
                    incompleteAlert.showAndWait();
                }
                Reinicio();
            }
        });
        
        Cancelar.setOnAction((ActionEvent e) -> {       
            Stage stage;
            stage = (Stage) Cancelar.getScene().getWindow();
            stage.close();
        });
    }    
    
    //FUNCIÓN PARA VALIDAR SI LOS DATOS INGRESADOS EN "MERCANCIA ENTRANTE, Y LOS 3 LOCALES SON NUMEROS
    boolean esnumero (String x)
    {  
        double number = 0;
        if(x.isEmpty())
        {
            return true;
        }
        else
        {
            try {
                number = Double.parseDouble(x);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }
    
    public void ActivarTextFields(boolean bandera){
        SueldoBase.setDisable(bandera);
        VentasMeta.setDisable(bandera);
        ComisionBase.setDisable(bandera);
        ComisionMeta.setDisable(bandera);
        Faltas.setDisable(bandera);
        DiaExtra.setDisable(bandera);
    }
    public void Reinicio(){
        SueldoBase.clear();
        VentasMeta.clear();
        ComisionBase.clear();
        ComisionMeta.clear();
        Faltas.clear();
        DiaExtra.clear();
        Nivel.getSelectionModel().clearSelection();
        Nivel.getItems().clear();
        iniciarComboBox();
        ActivarTextFields(true);
    }
}
