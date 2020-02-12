package xxcell.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import xxcell.Conexion.Conexion;
import xxcell.model.Imagenes;

public class GaleriaAgregarProductoController implements Initializable {

    Stage stage;
    Stage primaryStage;

    @FXML
    private ImageView ImageCentral;

    @FXML
    private TableView<Imagenes> tblImages;

    @FXML
    private TableColumn<Imagenes, String> colNombre;

    @FXML
    private Button btnGuardarImagen;

    @FXML
    private Button btnAceptar;

    @FXML
    private JFXButton btnAbrirImagen;

    @FXML
    private JFXTextField txtNameofImage;

    File file;
    FileInputStream fin;

    ObservableList<Imagenes> imagenes = FXCollections.observableArrayList();

    //Conexión a MYSQL
    Conexion conn = new Conexion();

    boolean BanderaImagen = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String nombre;

        tblImages.setPlaceholder(new Label("---XXCELL---"));
        colNombre.setCellValueFactory(cellData -> cellData.getValue().getNombre());

        try {
            tblImages.setItems(ObtenerImagenes());
            //cargarGaleria();
        } catch (SQLException ex) {
            Logger.getLogger(GaleryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<Imagenes> ObtenerImagenes() throws SQLException {
        String query = "Select * from galeria";
        String nombre;

        if (conn.QueryExecute(query)) {
            while (conn.setResult.next()) {
                nombre = conn.setResult.getString("NombreImagen");
                imagenes.add(new Imagenes(nombre));
            }
        }
        return imagenes;
    }

    @FXML
    void TblMoussePressed(MouseEvent event) throws SQLException, IOException {
        Imagenes images;
        String query;
        String nombre;
        //Variables para las Imagenes
        Blob blob;
        byte[] data;
        BufferedImage img;
        WritableImage image;

        if (event.isPrimaryButtonDown()) {
            btnGuardarImagen.setDisable(true);
            btnAceptar.setDisable(false);
            System.out.println("Holis apretaste el click");
            if (tblImages.getSelectionModel().getSelectedItem() != null) {
                //ImageCentral = null;
                images = tblImages.getSelectionModel().getSelectedItem();
                nombre = images.getName();
                query = "Select galeria.Imagen FROM galeria "
                        + "WHERE NombreImagen = '" + nombre + "'";
                System.out.println(query);
                if (conn.QueryExecute(query)) {
                    System.out.println("Holis se hizo el query " + query);
                    while (conn.setResult.next()) {
                        blob = conn.setResult.getBlob("Imagen");
                        if (blob != null) {
                            data = blob.getBytes(1, (int) blob.length());
                            img = ImageIO.read(new ByteArrayInputStream(data));
                            image = SwingFXUtils.toFXImage(img, null);
                            ImageCentral.setImage(image);
                        }
                    }
                }
                conn.setResult.close();
            }
        }
    }

    @FXML
    void abrirImagenAction(ActionEvent event) {

        tblImages.getSelectionModel().clearSelection();
        btnAceptar.setDisable(true);
        String query;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter JPG = new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.JPG");
        fileChooser.getExtensionFilters().addAll(JPG);

        WritableImage image = null;
        ImageCentral.setImage(image);

        //Show open file dialog
        file = fileChooser.showOpenDialog(btnAbrirImagen.getScene().getWindow());
        if (file == null) {
            String mensaje = "No ha seleccionado ninguna Imagen/Foto \n";
            Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);
            incompleteAlert.setTitle("Imagen de Producto");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnAbrirImagen.getScene().getWindow());
            incompleteAlert.showAndWait();
        } else {
            try {
                fin = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                /*  Creación de Log en caso de fallo  */
                String msjHeader = "¡Error con Archivo! ¡Archivo no Encontrado!";
                String msjText = "Copiar y mandarlo por correo ó mensaje";
                //log.SendLogReport(ex, msjHeader, msjText);
            }
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(file);
            } catch (IOException ex) {
                Logger.getLogger(AltaEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                /*  Creación de Log en caso de fallo  */
                String msjHeader = "¡IO Exception / Buffered Image!";
                String msjText = "Copiar y mandarlo por correo ó mensaje";
//                    log.SendLogReport(ex, msjHeader, msjText);
            }
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageCentral.autosize();
            ImageCentral.setImage(image);
            BanderaImagen = true;
            btnGuardarImagen.setDisable(false);
            txtNameofImage.requestFocus();
        }
    }

    @FXML
    void actionBtnGuardarImagen(ActionEvent event) throws SQLException {
        String nombre;
        String query;
        Alert incompleteAlert = new Alert(Alert.AlertType.INFORMATION);

        if (!txtNameofImage.getText().isEmpty() && BanderaImagen) {
            nombre = txtNameofImage.getText();
            query = "INSERT INTO galeria (NombreImagen, Imagen) "
                    + "values (?,?)";
            conn.preparedStatement(query);
            conn.stmt.setString(1, nombre);
            conn.stmt.setBinaryStream(2, fin, (int) file.length());
            int succes = conn.stmt.executeUpdate();
            if (succes == 1) {
                String mensaje = "Imagen Añadida \n";
                incompleteAlert.setTitle("Galeria de imagenes");
                incompleteAlert.setHeaderText(null);
                incompleteAlert.setContentText(mensaje);
                incompleteAlert.initOwner(btnAbrirImagen.getScene().getWindow());
                incompleteAlert.showAndWait();
                limpiarGaleria();
            }
            conn.setResult.close();
        } else {
            String mensaje = "Cargue una imagen y escriba un nombre para ella.\n";
            incompleteAlert.setTitle("Galeria de imagenes");
            incompleteAlert.setHeaderText(null);
            incompleteAlert.setContentText(mensaje);
            incompleteAlert.initOwner(btnAbrirImagen.getScene().getWindow());
            incompleteAlert.showAndWait();
            txtNameofImage.requestFocus();
        }
    }

    private void limpiarGaleria() throws SQLException {
        tblImages.refresh();
        imagenes.removeAll(imagenes);
        tblImages.setItems(ObtenerImagenes());
        WritableImage image = null;
        ImageCentral.setImage(image);
        txtNameofImage.setText("");
        btnGuardarImagen.setDisable(true);
    }

    @FXML
    void actionBtnAceptar(ActionEvent event) {
        Imagenes images;
        String nombre;
        if (tblImages.getSelectionModel().getSelectedItem() != null) {
            //ImageCentral = null;
            images = tblImages.getSelectionModel().getSelectedItem();
            Variables_Globales.nameImage = images.getName();
            Stage stage;
            stage = (Stage) tblImages.getScene().getWindow();
            stage.close();
        }

    }

    private void cargarGaleria() throws SQLException, IOException {
        //Variables para las Imagenes
        Blob blob;
        byte[] data;
        BufferedImage img;
        WritableImage image;
        String Nombre;
        //*************************
        String query = "Select * from galeria";

        if (conn.QueryExecute(query)) {
            while (conn.setResult.next()) {
                blob = conn.setResult.getBlob("Imagen");
                Nombre = conn.setResult.getString("NombreImagen");
                if (blob != null) {
                    data = blob.getBytes(1, (int) blob.length());
                    try {
                        img = ImageIO.read(new ByteArrayInputStream(data));
                        image = SwingFXUtils.toFXImage(img, null);
                        ImageView imageView = new ImageView(image);
                        imageView.setId(Nombre);
                        //imageView.setImage(image);

                        if (image.getHeight() > image.getWidth()) {
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(200);
                        }
                        if (image.getHeight() < image.getWidth()) {
                            imageView.setFitWidth(200);
                            imageView.setFitHeight(100);
                        }
                        if (image.getHeight() == image.getWidth()) {
                            imageView.setFitWidth(200);
                            imageView.setFitHeight(200);
                        }
                        imageView.setOnMouseClicked((MouseEvent mouseEvent) -> {
                            Image imageAux;
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (mouseEvent.getClickCount() == 2) {
                                    imageAux = imageView.getImage();
                                    imageView.setEffect(new DropShadow(30, Color.BLUE));
                                    System.out.println("Hice Click" + imageView.getId());
                                    ImageCentral.setImage(imageAux);
                                    //preview.setImage(imageAux);
                                    //BanderaImagen = true;
                                }
                            }
                        });
                        String Name = Nombre;
                        //TilePane.getChildren().add(imageView);
                    } catch (IOException ex) {
                        Logger.getLogger(ModificarEmpleadoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
