package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import xxcell.Conexion.Conexion;
import xxcell.model.PrinterService;
import xxcell.model.Transaccion;

/**
 * FXML Controller class
 *
 * @author Dranias
 */
public class CancelarPrestamosController implements Initializable {

    @FXML
    private TableView<Transaccion> tblPrestamos;
    
    @FXML
    private TableColumn<Transaccion, String> colCodigo;
    @FXML
    private TableColumn<Transaccion, String> colProcedencia;
    @FXML
    private TableColumn<Transaccion, String> colDestino;
    @FXML
    private TableColumn<Transaccion, String> colFecha;
    @FXML
    private TableColumn<Transaccion, String> colNotas;

    @FXML
    private JFXComboBox<String> cmbNotas;

    @FXML
    private JFXTextField txtCodigo;
    
    @FXML
    private Label lblErrCodigo;
    
    @FXML
    private JFXButton btnImprimir;
    

    Conexion conn = new Conexion();
    
    Transaccion aux = new Transaccion();
    
    public ObservableList<Transaccion> ObenerPrestamo() throws SQLException{
        String query = "SELECT * FROM prestamos JOIN productos ON prestamos.Codigo = productos.ID ";
        String codigo, procedencia, destino, notas, producto;
        int cantidad, folio;
        Timestamp fecha;
        
        query += "WHERE Procedencia = '"+Variables_Globales.local+"' AND Destino = 'Otros' ORDER BY prestamos.FechaHora";
        System.out.println(query);
        ObservableList<Transaccion> prestamos = FXCollections.observableArrayList();
        if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                codigo = conn.setResult.getString("Codigo");
                cantidad = conn.setResult.getInt("Cantidad");
                procedencia = conn.setResult.getString("Procedencia");
                destino = conn.setResult.getString("Destino");
                fecha = conn.setResult.getTimestamp("FechaHora");
                notas = conn.setResult.getString("Notas");
                folio = conn.setResult.getInt("Folio");
                producto = conn.setResult.getString("Marca") + " " + conn.setResult.getString("Modelo")
                        +" " + conn.setResult.getString("Tipo")+ " " + conn.setResult.getString("Identificador");
                Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                String s;
                s = formatter.format(fecha);
                prestamos.add(new Transaccion(codigo, cantidad, procedencia, destino, s, notas, folio, producto));
            }
        }
        return prestamos;
    }
    
    public void iniciaCombobox() throws SQLException{
        String notas;
         String query = "SELECT DISTINCT Notas FROM prestamos WHERE Procedencia = '"+Variables_Globales.local+"' AND Destino = 'Otros' ORDER by Notas";
         if(conn.QueryExecute(query)){
            while(conn.setResult.next()){
                notas = conn.setResult.getString("Notas");
                cmbNotas.getItems().add(notas);
            }
         }
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ConnectionOpen();
        
        try {
            iniciaCombobox();
        } catch (SQLException ex) {
            Logger.getLogger(CancelarPrestamosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblPrestamos.refresh();
        try {
            tblPrestamos.setItems(ObenerPrestamo());
        } catch (SQLException ex) {
            Logger.getLogger(PrestamosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Inicio de las columnas
        colCodigo.setCellValueFactory(cellData -> cellData.getValue().CodigoProperty());
        colProcedencia.setCellValueFactory(cellData -> cellData.getValue().ProcedenciaProperty());
        colDestino.setCellValueFactory(cellData -> cellData.getValue().DestinoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().FechaProperty());
        colNotas.setCellValueFactory(cellData -> cellData.getValue().NotasProperty());
        
        //Función: ToolTip para mostrar la descripción de producto
        tblPrestamos.setRowFactory(tv -> new TableRow<Transaccion>() {
            private Tooltip tooltip = new Tooltip();
            @Override
            public void updateItem(Transaccion Prod, boolean empty) {
                super.updateItem(Prod, empty);
                if (Prod == null) {
                    setTooltip(null);
                } else {
                    if(Prod.getProducto().length() > 0){
                        tooltip.setText(Prod.getProducto());
                        setTooltip(tooltip);
                    }
                }
            }
        });
        
        btnImprimir.setOnAction((ActionEvent e) -> {
            if(cmbNotas.getValue().length() != 0)
                try {
                    imprimetTicket();
            } catch (SQLException ex) {
                Logger.getLogger(CancelarPrestamosController.class.getName()).log(Level.SEVERE, null, ex);
            }
            else
                lblErrCodigo.setText("Seleccione Referencia");
            
        });//FIN btnPrecio
    }    
    
    @FXML
    void KeyPressedTxtCodigo(KeyEvent event) throws IOException, SQLException {
        Conexion conn = new Conexion();
        String query;
        boolean acceso = true;
        if(event.getCode() == KeyCode.ENTER){
            if(cmbNotas.getValue() == null){
                System.out.println("CMB null");
                acceso = false;
                lblErrCodigo.setText("Seleccione Referencia");
            }
            query = "SELECT * from productos WHERE ID = '"+txtCodigo.getText()+"'";
            System.out.println(query);
            conn.QueryExecute(query);
            if(!conn.setResult.first()){
                lblErrCodigo.setText("No existe el producto");
                System.out.println("No existe");
                acceso = false;
            }else{
                if(conn.setResult.getInt("CantidadActual") == 0){
                    acceso = false;
                    lblErrCodigo.setText("No hay productos en el inventario");
                }
                if(conn.setResult.getInt(Variables_Globales.local) == 0){
                    acceso = false;
                    lblErrCodigo.setText("No hay productos en el Local");
                }
            }
            if(acceso){
                lblErrCodigo.setText("");
                eliminar();
                tblPrestamos.refresh();
                try {
                    tblPrestamos.setItems(ObenerPrestamo());
                    iniciaCombobox();
                    txtCodigo.clear();
                    txtCodigo.requestFocus();
                } catch (SQLException ex) {
                    Logger.getLogger(PrestamosController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void eliminar(){
        System.out.println("Entre a eliminar");
        String query = "DELETE FROM prestamos where codigo = '"+txtCodigo.getText()+"' AND Notas = '"+ cmbNotas.getValue() +"'";
        System.out.println(query);
        conn.QueryUpdate(query);
    }
    
    
    public void imprimetTicket() throws SQLException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        Date fechaHoy = new Date();
        String fecha = formato.format(fechaHoy);
        PrinterService printerService = new PrinterService();
        
        String query = "Select * from prestamos "
                    + "INNER JOIN productos "
                    + "ON productos.ID = prestamos.Codigo "
                    + "WHERE Notas = '"+ cmbNotas.getValue() +"'";
        
        System.out.println(query);
        
        if(conn.QueryExecute(query)){
            if(Variables_Globales.impresora.equals("EPSON TM-T20II Receipt")){
                //print some stuff EPSON TM-T20II Receipt   EPSON-TM-T20II
                printerService.printString(Variables_Globales.impresora, "          XXCELL - Reparacion y Accesorios \n");
                printerService.printString(Variables_Globales.impresora, "               "+fecha+" \n");
                printerService.printString(Variables_Globales.impresora, "                Prestamo   \n");
                printerService.printString(Variables_Globales.impresora, "       Referencia: "+cmbNotas.getValue()+"\n");
                printerService.printString(Variables_Globales.impresora, "  ---------------------------------------------\n");
                printerService.printString(Variables_Globales.impresora, " Codigo    Producto           \n");
                printerService.printString(Variables_Globales.impresora, "  ---------------------------------------------\n");
            }
            if(Variables_Globales.impresora.equals("EC-PM-5890X")){
                //print some stuff EC - LINE
                printerService.printString(Variables_Globales.impresora, "XXCELL - Reparacion y Accesorios\n");
                printerService.printString(Variables_Globales.impresora, "10 pte y 5 de Mayo Col. Centro\n");
                printerService.printString(Variables_Globales.impresora, "     "+fecha+" \n");
                printerService.printString(Variables_Globales.impresora, "------------------------------\n");
                printerService.printString(Variables_Globales.impresora, " Codigo     Producto \n");
                printerService.printString(Variables_Globales.impresora, "------------------------------\n");
            }      
            if(Variables_Globales.impresora.equals("EPSON TM-T20II Receipt")){
                //print some stuff EPSON TM-T20II Receipt   EPSON-TM-T20II
                while(conn.setResult.next()){
                    //print some stuff EPSON TM-T20II Receipt   EPSON-TM-T20II
                    System.out.println(conn.setResult.getString("Codigo") + " ");
                    System.out.print(conn.setResult.getString("Marca") + " ");
                    System.out.print(conn.setResult.getString("Modelo") + " ");
                    System.out.print(conn.setResult.getString("Tipo") + " ");
                    System.out.println(conn.setResult.getString("Identificador"));
                    printerService.printString(Variables_Globales.impresora, "" + conn.setResult.getString("Codigo"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Marca"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Modelo"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Tipo") + "\n");
                    printerService.printString(Variables_Globales.impresora, "  ---------------------------------------------\n");
            }
            }
            if(Variables_Globales.impresora.equals("EC-PM-5890X")){
                while(conn.setResult.next()){
                    //print some stuff EPSON TM-T20II Receipt   EPSON-TM-T20II
                    System.out.println(conn.setResult.getString("Codigo") + " ");
                    System.out.print(conn.setResult.getString("Marca") + " ");
                    System.out.print(conn.setResult.getString("Modelo") + " ");
                    System.out.print(conn.setResult.getString("Tipo") + " ");
                    System.out.println(conn.setResult.getString("Identificador"));
                    printerService.printString(Variables_Globales.impresora, "" + conn.setResult.getString("Codigo"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Marca"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Modelo"));
                    printerService.printString(Variables_Globales.impresora, " " + conn.setResult.getString("Tipo") + "\n");
                    printerService.printString(Variables_Globales.impresora, "---------------\n");
               
                }
            }
            printerService.printString(Variables_Globales.impresora, "\n\n\n\n\n");
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };
            printerService.printBytes(Variables_Globales.impresora, cutP);
        }
    }
    
    public void ConnectionOpen(){
        if(!conn.ConnectionOpen()) {
            conn.ConnectionClose();
            conn.ConnectionOpen();
        }
    }
}
