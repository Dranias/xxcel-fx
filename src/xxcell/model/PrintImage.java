/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.ImageIcon;
import xxcell.controller.Variables_Globales;

public class PrintImage {
    
  public PrintImage() throws Exception {
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    DocPrintJob job = service.createPrintJob();
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    SimpleDoc doc = new SimpleDoc(new MyPrintable(), flavor, null);
    job.print(doc, null);
  }
}
    
class MyPrintable implements Printable  {
    ImageIcon printImage = new javax.swing.ImageIcon(Variables_Globales.RutaImagenes+""+Variables_Globales.CodigoProducto);
    public int print(Graphics g, PageFormat pf, int pageIndex) {
      Graphics2D g2d = (Graphics2D) g;
      g.translate((int) (pf.getImageableX()), (int) (pf.getImageableY()));
      if (pageIndex == 0) {
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();
        double imageWidth = printImage.getIconWidth();
        double imageHeight = printImage.getIconHeight();
        double scaleX = pageWidth / imageWidth;
        double scaleY = pageHeight / imageHeight;
        double scaleFactor = Math.min(scaleX, scaleY);
        g2d.scale(scaleFactor, scaleFactor);
        g.drawImage(printImage.getImage(), 0, 0, null);
        return Printable.PAGE_EXISTS;
      }
      return Printable.NO_SUCH_PAGE;
    }
}