package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import org.controlsfx.control.textfield.TextFields;
import xxcell.Conexion.Conexion;
import static xxcell.controller.LoginController.scene;
import xxcell.model.GeneraCodigos;
import xxcell.model.LogReport;
import xxcell.model.generaCodigoBarras;

public class AgregarProductoController implements Initializable {

    
    
    //Para Masonry
    Stage principalStage = new Stage();
    
    private Alert alert; 
    
    File file;
    FileInputStream fin;
    boolean BanderaImagen = false;
    
    //Conexión a MYSQL
    Conexion conn = new Conexion(); 
    
    //Funciones
    Funciones func = new Funciones();
    GeneraCodigos generaCodigo = new GeneraCodigos();
    generaCodigoBarras codigoBarras = new generaCodigoBarras();
    
    //BOTONES 
    @FXML
    private JFXButton Agregar;        
    @FXML
    private JFXButton Cancelar;
    @FXML
    private JFXButton btnGenerarCodigo;
    @FXML
    private JFXButton btnResetImagen;
    @FXML
    private JFXButton btnGalery;
    @FXML
    private JFXButton btnReiniciar;
    
    //Image View
    @FXML
    private ImageView ImgViewProducto;

    //TEXTFIELDS
    @FXML
    private JFXTextField txtICodigo;
    @FXML
    private JFXTextField txtMarca;
    @FXML
    private JFXTextField txtModelo;    
    @FXML
    private JFXTextField txtDisponible;
    @FXML
    private JFXTextField txtPrecioDistribuidor;
    @FXML
    private JFXTextField txtPrecioPublico;
    @FXML
    private JFXTextField txtTipo;
    @FXML
    private JFXTextField txtCosto;
    @FXML
    private JFXTextField txtMayoreo;
    @FXML
    private JFXTextField txtIdentificador;
    @FXML
    private TextField TFL127;   
    @FXML
    private TextField TFL64;
    @FXML
    private TextField TFL58;

    //Area para la Descripcion
    @FXML
    private JFXTextArea DescTxt;
    //LABEL DE ERROR PARA LA VALIDACIÓN DE LOS TEXTFIELDS
    @FXML
    private Label lblErrCodigo;
    @FXML
    private Label lblEModelo;
    @FXML
    private Label lblEMarca;
    @FXML
    private Label lblESum;
    @FXML
    private Label lblEnumero;
    @FXML
    private Label lblVistaActual;
    @FXML
    private Label lblVistaEntradas;
    @FXML
    private Label lblVistaSalidas;
    @FXML
    private Label lblCantidadID;
    @FXML
    private Label lblErrTipo;
    @FXML
    private Label lblErrPrecioP;
    @FXML
    private Label lblErrCosto;
    @FXML
    private Label lblErrPrecioDist;
    @FXML
    private Label lblErrIdentidicador;
    
    String CodigoID;
    
    LogReport log;
    
    //Lista para el autocompletado de JFXTextField buscar
    Set<String> hs_Marca = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_Tipo = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_Modelo = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS
    Set<String> hs_ID = new HashSet<>(); // HashSet Para quitar elementos repetidos de POSSIBLEWORDS

    boolean ban; //Bandera para validar los datos, algún campo falla éste se pone en falso
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conn.ConnectionOpen();
        try {
            Autocompletar();
        } catch (SQLException ex) {
            Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            txtICodigo.requestFocus();
            log  = new LogReport(btnGenerarCodigo.getScene().getWindow());
        });
        
        TextFormatter<String> txtFormatterICodigo = new TextFormatter<>(getFilter());
        TextFormatter<String> txtFormatterMarca = new TextFormatter<>(getFilter());
        TextFormatter<String> txtFormatterModelo = new TextFormatter<>(getFilter());
        TextFormatter<String> txtFormatterIdentificador = new TextFormatter<>(getFilter());
        TextFormatter<String> txtFormatterTipo = new TextFormatter<>(getFilter());
        
        txtMarca.setTextFormatter(txtFormatterMarca);
        TextFields.bindAutoCompletion(txtMarca, hs_Marca);
        
        txtICodigo.setTextFormatter(txtFormatterICodigo);
        TextFields.bindAutoCompletion(txtICodigo, hs_ID);
        
        txtIdentificador.setTextFormatter(txtFormatterIdentificador);
        
        txtModelo.setTextFormatter(txtFormatterModelo);
        TextFields.bindAutoCompletion(txtModelo, hs_Modelo);
        
        txtTipo.setTextFormatter(txtFormatterTipo);
        TextFields.bindAutoCompletion(txtTipo, hs_Tipo);
        
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
        
        txtDisponible.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            txtEjemploJtextFieldChanged();
        });
        //TextFormater para solo poder teclear numeros
        TFL127.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        TFL64.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        TFL58.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        txtDisponible.setTextFormatter(new TextFormatter<String>(formatter,"",filter));
        
        //Botón Reset imagen
        Cancelar.setOnAction((ActionEvent e) -> {       
            Stage stage;
            stage = (Stage) btnGalery.getScene().getWindow();
            stage.close();
        }); 
        
        //Botón Agregar
        Agregar.setOnAction((ActionEvent e) -> {
            int x, y, z, w;
            int suma;    
            ban=true;
//*******************Validación de Codigo *************************
            if(txtICodigo.getText().length() == 0 || txtICodigo.getText().length() > 13) {
                lblErrCodigo.setText("ID debe ser mayor a 0 y Menor a 14 caracteres");
                txtICodigo.requestFocus();
                ban = false;
            }
            else
                lblErrCodigo.setText("");
//*******************Validación de Modelo***************************
            if(txtModelo.getText().length() == 0 || txtModelo.getText().length() > 15) {
                lblEModelo.setText("Modelo debe ser mayor a 0 y Menor a 15 caracteres");
                txtModelo.requestFocus();
                ban = false;
            }
            else
                lblEModelo.setText("");
//*******************Validación para Marca************************************
            if(txtMarca.getText().length() == 0 || txtMarca.getText().length() > 15) {
                lblEMarca.setText("Marca debe ser mayor a 0 y Menor a 15 caracteres");
                txtMarca.requestFocus();
                ban = false;
            }
            else
                lblEMarca.setText("");
//*******************Validación para IDENTIFICADOR/NOMBRE************************************
            if(txtIdentificador.getText().length() == 0 || txtIdentificador.getText().length() > 30) {
                lblErrIdentidicador.setText("Identificador debe ser mayor a 0 y Menor a 15 caracteres");
                txtIdentificador.requestFocus();
                ban = false;
            }
            else
                lblErrIdentidicador.setText("");
//*******************Validación para Tipo************************************
            if(txtTipo.getText().length() == 0 || txtTipo.getText().length() > 30) {
                lblErrTipo.setText("Tipo debe ser mayor a 0 y Menor a 15 caracteres");
                txtTipo.requestFocus();
                ban = false;
            }
            else
                lblErrTipo.setText("");
//*******************Validación para el Precio a Público**************************************
            if(!esnumero(txtPrecioPublico.getText()))
            {
                ban=false;
                lblErrPrecioP.setText("Debe ingresar un número.");
            }
            else
                lblErrPrecioP.setText("");
//*******************Validación para el Costo**************************************
            if(!esnumero(txtCosto.getText()))
            {
                ban=false;
                lblErrCosto.setText("Debe ingresar un número.");
            }
            else
                lblErrCosto.setText("");
//*******************Validación para el Precio a Distribuidores**************************************
            if(!esnumero(txtPrecioDistribuidor.getText()))
            {
                ban=false;
                lblErrPrecioDist.setText("Debe ingresar un número.");
            }
            else{
                lblErrPrecioDist.setText("");
                if(txtPrecioDistribuidor.getText().length() == 0){
                    if(esnumero(txtPrecioPublico.getText()))
                        txtPrecioDistribuidor.setText(txtPrecioPublico.getText());
                }
            }
//*******************Validación para el local 64**************************************
            if(esnumero(TFL64.getText()))
            {
                lblESum.setText("");
                if(TFL64.getText().length() == 0) {
                    TFL64.setText("0");
                    w=0;
                }
                else
                    w = Integer.parseInt(TFL64.getText());
            }
            else
            {
                ban=false;
                lblEnumero.setText("Los datos deben ser números (Error en local 64)");
                w=0;
            }
 //*******************Validación para el local 127**********************************
            if(esnumero(TFL127.getText()))
            {
                if(TFL127.getText().length() == 0) {
                    TFL127.setText("0");
                    y=0;
                }
                else
                    y = Integer.parseInt(TFL127.getText());
            }
            else
            {
                ban=false;
                lblEnumero.setText("Los datos deben ser números (Error en local 127)");
                y=0;
            }
//*******************Validación para el local 58************************************
            if(esnumero(TFL58.getText()))
            {
                if(TFL58.getText().length() == 0) {
                    TFL58.setText("0");
                    z=0;
                }
                else
                    z = Integer.parseInt(TFL58.getText());
            }
            else
            {
                ban=false;
                lblEnumero.setText("Los datos deben ser números (Error en local 58)");
                z=0;
            }
//*******************Validación para el Textfield de Disponibilidad******************************
            if(esnumero(txtDisponible.getText()))
            {
                if(txtDisponible.getText().length() == 0) {
                    txtDisponible.setText("0");
                    x=0;
                }
                else
                    x = Integer.parseInt(txtDisponible.getText());
            }
            else
            {
                ban=false;
                lblEnumero.setText("Los datos deben ser números (Error en Mercancia entrante)");
                x=0;
            }
//*******************Validación para La sumas de los locales con respecto al total de productos*********************
            suma = y+z+w;
            if(suma>x)
            {
                lblEnumero.setText("");
                lblESum.setText("La suma de los locales no debe ser mayor a la mercancia entrante");
                ban=false;
            }
            else
                lblESum.setText("");
            if(ban)
            {
                try {
                    AgregarSQL();
                } catch (IOException ex) {
                    Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }           
        });
        
        Cancelar.setOnAction((ActionEvent e) -> {       
            Stage stage;
            stage = (Stage) Cancelar.getScene().getWindow();
            stage.close();
        });     
        
        btnResetImagen.setOnAction((ActionEvent e) -> {       
            ResetImagen();
        });
        
        btnReiniciar.setOnAction((ActionEvent e) -> {       
            inicializa();
            ResetImagen();
        });

        btnGalery.setOnAction((ActionEvent e) -> {       
            try {
                MostrarGaleria();
            } catch (SQLException ex) {
                Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AgregarProductoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnGenerarCodigo.setOnAction((ActionEvent e) -> {       
            if(txtMarca.getText().length() == 0 || txtTipo.getText().length() == 0 || txtModelo.getText().length() == 0){
                lblErrCodigo.setText("Marca, Modelo y Tipo no deben estár vacios");
            }
            if(txtMarca.getText().length() > 1 && txtTipo.getText().length() > 2 && txtModelo.getText().length() > 0){
                txtICodigo.setText(generaCodigo.GeneraCodigos(txtMarca.getText(), txtModelo.getText(), txtTipo.getText()));
                lblErrCodigo.setText("");
            }
            lblCantidadID.setText("( " + String.valueOf(txtICodigo.getText().length())+ " )");
        });
        
        txtICodigo.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            CantidadTextoID();
        }); 
    } //FIN INIT
    
    //Autocompletados en los textfields
    public void Autocompletar() throws SQLException
    {
        String query;
        query = "SELECT DISTINCT ID, Marca, Modelo, Tipo FROM productos";
        if(conn.QueryExecute(query)){
            try {
                while(conn.setResult.next()){
                    hs_Modelo.add(conn.setResult.getString("Modelo"));
                    hs_ID.add(conn.setResult.getString("ID"));
                    hs_Tipo.add(conn.setResult.getString("Tipo"));
                    hs_Marca.add(conn.setResult.getString("Marca"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                String msjHeader = "¡Error!";
                String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
                log.SendLogReport(ex, msjHeader, msjText);
            }
            conn.setResult.close();
        }
    }
    
    //Funcion para agregar a la base de datos el producto que desee
    public void AgregarSQL() throws IOException {
        int ProductoDisponible = Integer.parseInt(txtDisponible.getText());
        double PrecioPublico = Double.parseDouble(txtPrecioPublico.getText());
        double PrecioDistribuidor = Double.parseDouble(txtPrecioDistribuidor.getText());
        double costo = Double.valueOf(txtCosto.getText());
        int l58 = Integer.parseInt(TFL58.getText());
        int l64 = Integer.parseInt(TFL64.getText());
        int l127 = Integer.parseInt(TFL127.getText());
        int entradas = Integer.parseInt(lblVistaEntradas.getText());
        int salidas = Integer.parseInt(lblVistaSalidas.getText());
        String ID, CodigoAlfa;
        GeneraCodigos generador = new GeneraCodigos();
        ID = "";
        try {
            CodigoAlfa = txtICodigo.getText();
            ID = generador.GeneraCodigoNumericoAgregar(txtICodigo.getText());
            ID = generador.ValidacionCadenaAgregar(CodigoAlfa, ID);
        } catch (SQLException ex) {
            String msjHeader = "¡Error con la Creacion de ID!";
            String msjText = "Copiar y mandarlo por correo ó mensaje - URGENTE";
            Logger.getLogger(AlmacenController.class.getName()).log(Level.SEVERE, null, ex);
            log.SendLogReport(ex, msjHeader, msjText);
        }
        String query = "SELECT * FROM productos WHERE ID='"+ID+"'";
        conn.QueryExecute(query);
            try {
                //SI EL ID YA EXISTE EN LA BASE DE DATOS cancela el alta.
                if(conn.setResult.first()) {
                    String mensaje = "¡El ID de Producto ya existe! \n";
                    Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
                    incompleteAlert.setTitle("Gestión de Productos");
                    incompleteAlert.setHeaderText(null);
                    incompleteAlert.setContentText(mensaje);
                    incompleteAlert.initOwner(Agregar.getScene().getWindow());
                    incompleteAlert.showAndWait();                   
                } 
                //Sino Existe, agregará uno Nuevo
                else {
                    if(BanderaImagen){
                        query =  "INSERT INTO productos (ID, Modelo, Identificador, PrecPub, Costo, Marca, Tipo, PrecDist, ";
                        query += "Descrip, CantidadActual, Entradas, Salidas, L58, L64, L127, NombreImagen) ";
                        query += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        conn.preparedStatement(query);
                        conn.stmt.setString(1, ID);
                        conn.stmt.setString(2, txtModelo.getText());
                        conn.stmt.setString(3, txtIdentificador.getText());
                        conn.stmt.setDouble(4, PrecioPublico);
                        conn.stmt.setDouble(5, costo);
                        conn.stmt.setString(6, txtMarca.getText());
                        conn.stmt.setString(7, txtTipo.getText());
                        conn.stmt.setDouble(8, PrecioDistribuidor);
                        conn.stmt.setString(9, DescTxt.getText());
                        conn.stmt.setInt(10, ProductoDisponible);
                        conn.stmt.setInt(11, entradas);
                        conn.stmt.setInt(12, salidas);
                        conn.stmt.setInt(13, l58);
                        conn.stmt.setInt(14, l64);
                        conn.stmt.setInt(15, l127);
                        conn.stmt.setString(16, Variables_Globales.nameImage);
                    }else{
                        query =  "INSERT INTO productos (ID, Modelo, Identificador, PrecPub, Costo, Marca, Tipo, PrecDist, ";
                        query += "Descrip, CantidadActual, Entradas, Salidas, L58, L64, L127, Dispon) ";
                        query += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        conn.preparedStatement(query);
                        conn.stmt.setString(1, ID);
                        conn.stmt.setString(2, txtModelo.getText());
                        conn.stmt.setString(3, txtIdentificador.getText());
                        conn.stmt.setDouble(4, PrecioPublico);
                        conn.stmt.setDouble(5, costo);
                        conn.stmt.setString(6, txtMarca.getText());
                        conn.stmt.setString(7, txtTipo.getText());
                        conn.stmt.setDouble(8, PrecioDistribuidor);
                        conn.stmt.setString(9, DescTxt.getText());
                        conn.stmt.setInt(10, ProductoDisponible);
                        conn.stmt.setInt(11, entradas);
                        conn.stmt.setInt(12, salidas);
                        conn.stmt.setInt(13, l58);
                        conn.stmt.setInt(14, l64);
                        conn.stmt.setInt(15, l127);
                        conn.stmt.setInt(16, 0);
                    }
                    int succes = conn.stmt.executeUpdate();
                    if(succes == 1){
                        String mensaje = "Producto Añadido \n";
                        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
                        incompleteAlert.setTitle("Gestión de Productos");
                        incompleteAlert.setHeaderText(null);
                        incompleteAlert.setContentText(mensaje);
                        incompleteAlert.initOwner(Agregar.getScene().getWindow());
                        incompleteAlert.showAndWait();
                        //inicializa();
                        ResetImagen();
                        tblInventario(ID);
                    }else{
                        String mensaje = "Producto NO PUDO SER añadido \n";
                        Alert incompleteAlert = new Alert(Alert.AlertType.ERROR);
                        incompleteAlert.setTitle("Gestión de Productos");
                        incompleteAlert.setHeaderText(null);
                        incompleteAlert.setContentText(mensaje);
                        incompleteAlert.initOwner(Agregar.getScene().getWindow());
                        incompleteAlert.showAndWait();
                    }
                    conn.setResult.close();
                }
            } catch (SQLException ex) {
                /*  Creación de Log en caso de fallo  */
                String msjHeader = "¡Error con la Base de Datos!";
                String msjText = "Copiar y mandarlo por correo ó mensaje";
                log.SendLogReport(ex, msjHeader, msjText);
                System.out.println(ex);
            }     
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
    //FIN DE FUNCION ESNUMERO
    
    private void txtEjemploJtextFieldChanged(){
	int suma;
	int inicial;
	int entradas;
	int salidas;
		
	if(!func.IsInteger(txtDisponible.getText()))
            this.lblVistaActual.setText("Error");
	else{
            inicial = Integer.valueOf(txtDisponible.getText());
            entradas = Integer.valueOf(lblVistaEntradas.getText());
            salidas = Integer.valueOf(lblVistaSalidas.getText());
            suma = inicial+entradas-salidas;
            this.lblVistaActual.setText(String.valueOf(suma));
	}
    }
    
    public void CantidadTextoID(){
        if(txtICodigo.getText().length() != 0){
            lblCantidadID.setText("( " + String.valueOf(txtICodigo.getText().length())+ " )");
        }
        else{
            lblCantidadID.setText("");
        }
        if(txtICodigo.getText().length() > 13){
            lblErrCodigo.setText("ID debe ser mayor a 0 y Menor a 14 caracteres");
        }
        else{
            lblErrCodigo.setText("");
        }
    }
    
    private void inicializa(){
        
        txtCosto.setText("");
        txtDisponible.setText("");
        txtICodigo.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtIdentificador.setText("");
        txtPrecioDistribuidor.setText("");
        txtPrecioPublico.setText("");
        txtTipo.setText("");
        TFL127.setText("");
        TFL58.setText("");
        TFL64.setText("");
        DescTxt.setText("");
        lblCantidadID.setText("");
        txtICodigo.requestFocus();
    }
    
    private UnaryOperator<Change> getFilter() {
        return change -> {
            String text = change.getText();
            change.setText(text.toUpperCase());
            return change;
        };
    }    
    
    public void ResetImagen() {
        BanderaImagen = false;
        File file = new File("src/xxcell/Images/producto-sin-foto.jpg");
        Image image = new Image(file.toURI().toString());  
        ImgViewProducto.setImage(image);
    }
    
    public void MostrarGaleria() throws SQLException, IOException{
        String nombre, query;
        //Variables para las Imagenes
        Blob blob;
        byte[] data;
        BufferedImage img;
        WritableImage image;
        
        Parent principal;
        principal = FXMLLoader.load(getClass().getResource("/xxcell/view/GaleriaAgregarProducto.fxml"));
        Stage principalStage = new Stage();
        principalStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        scene = new Scene(principal);
        principalStage.setScene(scene);
        principalStage.initModality(Modality.WINDOW_MODAL);
        principalStage.setResizable(false);
        principalStage.initOwner(btnGalery.getScene().getWindow());
        principalStage.showAndWait(); 
        if(Variables_Globales.nameImage != null){
            nombre = Variables_Globales.nameImage;
            query = "Select galeria.Imagen FROM galeria "
                        + "WHERE NombreImagen = '"+nombre+"'";
            if(conn.QueryExecute(query)){
                while(conn.setResult.next()) {
                    blob = conn.setResult.getBlob("Imagen");
                    if(blob != null){
                        data = blob.getBytes(1, (int)blob.length());
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        image = SwingFXUtils.toFXImage(img, null);  
                        ImgViewProducto.setImage(image);
                        BanderaImagen = true;
                    }
                }
                conn.setResult.close();
            }    
        }
    }
    
    public void tblInventario(String ID) throws SQLException{
        Date fechaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        String sqlStmt;

        sqlStmt = "Insert into tblinventario (invfolio,invmovimiento,invfecha,invcantidad,productocodigo,ventafolio,invdescripcion) ";
        sqlStmt += " Values ("+getFolio()+",";
        sqlStmt += "'Entrada',";
        sqlStmt += "'"+formato.format(fechaHoy)+"',";
        sqlStmt += txtDisponible.getText()+",";
        sqlStmt += "'"+ID+"',0,";
        sqlStmt += "'Entrada por Creacion de Producto')";
        
        Conexion connInv = new Conexion();
        if(connInv.QueryUpdate(sqlStmt)){
            FolioIncrementa();
        }
        
    }
    
    private boolean FolioIncrementa() {
        boolean result = false;
        String sqlStmt;

        sqlStmt = "Update tblfolios set folioinventario = folioinventario + '1'";
        if(conn.QueryUpdate(sqlStmt))
                result = true;

        return result;
    }
    
    private int getFolio() throws SQLException{
        int result = 0;
        String query;
        
        query = "Select folioinventario from tblfolios";
        conn.QueryExecute(query);

        try{
            if(conn.setResult.next()){
                result = conn.setResult.getInt("folioinventario");
            }
        } catch(SQLException e){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(btnGalery.getScene().getWindow());
            alert.setTitle("Punto de Venta - Error al obtener folio");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        conn.setResult.close();
        return result;
    }
}