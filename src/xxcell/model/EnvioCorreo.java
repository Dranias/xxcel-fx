package xxcell.model;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.application.Platform;
import javafx.stage.Window;
import javax.mail.*;
import javax.mail.internet.*;
import xxcell.controller.Variables_Globales;

public class EnvioCorreo {
    
    LogReport log;
    Window window; 
    
    Date fechaHoy = new Date();
    SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat formatohora = new SimpleDateFormat("hh:mm aa");
    
    String host ="smtp.gmail.com" ;
    String user = "mensajeriaxxcell@gmail.com";
    String pass = "xxcellmensajeria";
    String from = "mensajeriaxxcell@gmail.com";
    String destinatario = "noaydeh@hotmail.com";
    
    long TInicio, TFin, tiempo; //Variables para determinar el tiempo de ejecución
    
    public EnvioCorreo(){
    }
    
    public void EnviarCorreo() {
        try{         
            //  Propiedades de la conexión
            Properties props = new Properties();
            // Nombre del host de correo, es smtp.gmail.com
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            // TLS si está disponible
            props.setProperty("mail.smtp.starttls.enable", "true");
            // Puerto de gmail para envio de correos
            props.setProperty("mail.smtp.port","587");
            // Nombre del usuario
            props.setProperty("mail.smtp.user", user);
            // Si requiere o no usuario y password para conectarse.
            props.setProperty("mail.smtp.auth", "true");
            
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            
            MimeMessage message = new MimeMessage(session);
            
            // Quien envia el correo
            message.setFrom(new InternetAddress(from));
            // A quien va dirigido
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("doktortrash@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("heynalle@gmail.com"));
            message.setSubject("Cierre de Local " + Variables_Globales.localPublico);
            message.setText("Se cerro a las : " + formato.format(fechaHoy) +" " +formatohora.format(fechaHoy));
            
            Transport t = session.getTransport("smtp");
            t.connect(user,pass);
            t.sendMessage(message,message.getAllRecipients());
            t.close();           
        }catch(MessagingException ex){
            String msjHeader = "¡ERROR!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
    }
    
    public void EnviarCorreoEstadisticas(String Mensaje) {
        try{         
            //  Propiedades de la conexión
            Properties props = new Properties();
            // Nombre del host de correo, es smtp.gmail.com
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            // TLS si está disponible
            props.setProperty("mail.smtp.starttls.enable", "true");
            // Puerto de gmail para envio de correos
            props.setProperty("mail.smtp.port","587");
            // Nombre del usuario
            props.setProperty("mail.smtp.user", user);
            // Si requiere o no usuario y password para conectarse.
            props.setProperty("mail.smtp.auth", "true");
            
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            
            MimeMessage message = new MimeMessage(session);
            
            // Quien envia el correo
            message.setFrom(new InternetAddress(from));
            // A quien va dirigido
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("doktortrash@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("heynalle@gmail.com"));
            message.setSubject("Estadisticas de Productos");
            message.setText(Mensaje);
            
            Transport t = session.getTransport("smtp");
            t.connect(user,pass);
            t.sendMessage(message,message.getAllRecipients());
            t.close();           
        }catch(MessagingException ex){
            String msjHeader = "¡ERROR!";
            String msjText = "Copiar y mandarlo por correo a noaydeh@hotmail.com";
            log.SendLogReport(ex, msjHeader, msjText);
        }
    }
}
