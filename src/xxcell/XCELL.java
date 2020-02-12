package xxcell;

import com.jfoenix.controls.JFXDecorator;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class XCELL extends Application {
    private Stage primaryStage;
    private StackPane mainLayout;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.primaryStage.setTitle("XXCELL");
        showLoginView();        
    }
    
    private void showLoginView() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(XCELL.class.getResource("/xxcell/view/Login.fxml"));
        Font.loadFont(getClass().getResourceAsStream("/xxcell/resources/Robotica.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/xxcell/resources/conthrax-sb.ttf"), 10);
        mainLayout = loader.load();
        JFXDecorator decorator = new JFXDecorator(primaryStage, mainLayout);
        Scene scene = new Scene(decorator);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/xxcell/Images/XXCELL450.png"));
        primaryStage.show();
    }
    
    public static void main(String[] args) throws Exception {  
        launch(args);
    }
    
}
