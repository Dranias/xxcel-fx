package xxcell.Conexion;
    
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion {
        /*public static Connection conector() throws ClassNotFoundException, SQLException
        {
            try
            {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/xxcell","root","");
		return conn;
            } 
            catch(Exception e)
            {
                return null;
            }
        }*/
    public Connection conexion;
    public Connection jasperconexion;
    static String query;
    public Statement sqlStmt;
    public ResultSet setResult;
    public PreparedStatement stmt;

    //ConexiÃ³n para Jasper Reports
    public Connection JasperConexion() throws SQLException {
            //CONEXIÃ“N LOCALHOLST
        //this.jasperconexion = DriverManager.getConnection("jdbc:mysql://localhost/xxcell","root","");
        //ConexiÃ³nn Nube
        this.jasperconexion = DriverManager.getConnection("jdbc:mysql://xxcel.c5usl4xthvwb.us-east-2.rds.amazonaws.com:3306/xxcell","admin","xxcel2019");
        //CONEXIÃ“N LOCAL 58
	//this.jasperconexion = DriverManager.getConnection("jdbc:mysql://192.168.1.65:3306/xxcell","Local58","xxcell");
        //CONEXIÃ“N LOCAL 127
        //this.jasperconexion = DriverManager.getConnection("jdbc:mysql://192.168.1.68:3306/xxcell","Local127","xxcell");
        //CONEXIÃ“N LOCAL 64
        //this.jasperconexion = DriverManager.getConnection("jdbc:mysql://192.168.1.68:3306/xxcell","Local64","xxcell");
        return jasperconexion;
    }
    

    public void preparedStatement(String sql) throws SQLException {
        stmt = conexion.prepareStatement(sql);
    }
    
    public boolean ConnectionOpen()
    {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            //conexion = DriverManager.getConnection("jdbc:mysql://localhost/xxcell","root","");
            conexion = DriverManager.getConnection("jdbc:mysql://xxcel.c5usl4xthvwb.us-east-2.rds.amazonaws.com:3306/xxcell","admin","xxcel2019");
            //conexion = DriverManager.getConnection("jdbc:mysql://192.168.1.65:3306/xxcell","Local58","xxcell");
            //conexion = DriverManager.getConnection("jdbc:mysql://192.168.1.68:3306/xxcell","Local127","xxcell");
            //conexion = DriverManager.getConnection("jdbc:mysql://192.168.1.68:3306/xxcell","Local64","xxcell");
            return true;
        } catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
            return false;
	}
    }

    public void ConnectionClose()
    {
	try{
            conexion.close();
	} catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
	
    public boolean QueryExecute(String sqlQuery)
    {
        try{
	sqlStmt = conexion.createStatement();
	} catch(Exception e){
            return false;
	}
	try{
            setResult = sqlStmt.executeQuery(sqlQuery);
            return true;
	} catch(SQLException e){
            return false;
	}/*finally{
            if(sqlStmt != null ){
                try {
                    sqlStmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }	*/	
    }
    
	
    public boolean QueryUpdate(String sqlQuery)
    {
        try{
            //stmt = conexion.prepareStatement(sqlQuery);
            sqlStmt = conexion.createStatement();
	} catch(Exception e){
            return false;
	}
	try{
            sqlStmt.executeUpdate(sqlQuery);
            return true;
	} catch(Exception e){
            JOptionPane.showMessageDialog(null, "Query Update: "+e.getMessage().toString());
            return false;
	} finally{
            if(sqlStmt != null ){
                try {
                    sqlStmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
	
    public void AutoCommit(boolean commit){
		try{
			conexion.setAutoCommit(commit);
		} catch(SQLException e){}
	}
	
    public void RollBack(){
		try{
			conexion.rollback();
		} catch(SQLException e){}
	}
	
    public void Commit(){
		try{
			conexion.commit();
		} catch(SQLException e){}
	}
}
