package xxcell.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.control.Alert;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import xxcell.controller.Variables_Globales;

public class generaCodigoBarras {
    LogReport log;
    public generaCodigoBarras(){
        
    }
    
    
    public void CodigoBarras39(String producto, String Codigo) throws FileNotFoundException, IOException{
        Code39Bean bean = new Code39Bean();
        final int dpi = 150;
 
        //Configure the barcode generator
        bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar, width exactly one pixel
        bean.setWideFactor(3);
        bean.doQuietZone(false);
 
        //Open output file
        //Local 58
        //File outputFile = new File("C:\\Users\\XXCELL_L127\\Pictures\\Codigo de Barras\\"+producto);
        
        //Local64
        //File outputFile = new File("C:\\Users\\Hp\\Pictures\\Codigo de barras\\"+producto);
        
        //Local 127
        //File outputFile = new File("C:\\Users\\User\\Pictures\\Codigo de Barras\\"+producto);
        
        //Local
        File outputFile = new File(Variables_Globales.RutaImagenes+""+producto);
        
        OutputStream out = new FileOutputStream(outputFile);
 
        try {
            //Set up the canvas provider for monochrome PNG output
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
 
            //Generate the barcode
            bean.generateBarcode(canvas, Codigo);
 
            //Signal end of generation
            canvas.finish();
        } finally {
            out.close();
        }
    }
    
    
    public void createBarCode128(String fileName, String Codigo) {
    try {
      Code128Bean bean = new Code128Bean();
      final int dpi = 160;

      //Configure the barcode generator
      bean.setModuleWidth(UnitConv.in2mm(2.8f / dpi));

      bean.doQuietZone(false);

      //Open output file
      File outputFile = new File(Variables_Globales.RutaImagenes + fileName);

      FileOutputStream out = new FileOutputStream(outputFile);
    
      BitmapCanvasProvider canvas = new BitmapCanvasProvider(
          out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

      //Generate the barcode
      bean.generateBarcode(canvas, Codigo);
   
      //Signal end of generation
      canvas.finish();
    
      System.out.println("Bar Code is generated successfully…");
    }
    catch (IOException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error Al generar Codigo!");
        alert.setContentText("Verificar: "+ fileName);
        alert.showAndWait();
        ex.printStackTrace();
    }
  }
}
