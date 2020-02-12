package xxcell.controller;

import xxcell.model.Distribuidores;
import xxcell.model.Empleados;
import xxcell.model.Productos;

public class Variables_Globales {
    static String Rol;      //Rol del usuario
    static String totalVenta;
    static boolean ventaRealizada;
    static Productos BusquedaVenta = new Productos();
    static Productos producto;
    static Empleados empleado;
    static Distribuidores BusquedaDistribuidor = new Distribuidores();
    
    //static String local = "L58";
    //static String local = "L64";
    static String local = "L127";
    
    static String Columna;
    //public static String localPublico = "L58";
    //public static String localPublico = "L64";
    public static String localPublico = "L127";
    
    public static String nameImage;
    static double recibida;
    static double cambio;
    //public static String impresora = "EC-PM-5890X";
    public static String impresora = "EPSON TM-T20II Receipt";
    
    //ImpresiÃ³n de Codigo de Barras
    //public static String RutaImagenes = "C:\\Users\\LENOVO\\Pictures\\Codigo de Barras\\";  
    public static String RutaImagenes = "C:\\Users\\Hp\\Pictures\\Codigo de barras\\";
    //public static String RutaImagenes = "C:\\Users\\XXCELL_L127\\Pictures\\Codigo de Barras\\";
    //public static String RutaImagenes = "C:\\Users\\CPU\\Pictures\\CODIGOS DE BARRA\\";   // Casa Servidor
    
    //public static String RutaImagenes = "F:\\Imagenes\\Codigo\\";
    
    public static String CodigoProducto = "";
}
