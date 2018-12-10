/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BDD;

/**
 *
 * @author Eric
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
public class ConexionBDD {
    /*
        CONFIGURAR LA CONEXION A LA BDD..
        USAR POSTGRESS..
        INSTALAR PG EN LA PC DE LA ESCUELA
    
    */
    
    public String puerto="3306";
    public String nomservidor="localhost";
    public String db="app_huella";
    public String user="root";
    public String pass="";
        Connection conn=null;
    public Connection conectar(){
        try{
            String ruta="jdbc:mysql://";
            String servidor= nomservidor+":"+puerto+"/";
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(ruta+servidor+db+"?userTimezone=true&serverTimezone=UTC",user,pass);
            if(conn !=null){
                System.out.println("conexion de base de datos.");
            }else if(conn==null){
                throw new SQLException();
                
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null,"Se produjo un error: "+e.getMessage());
        }
        catch(NullPointerException e){
            JOptionPane.showMessageDialog(null,"Se produjo un error: "+e.getMessage());
        }finally{
            return conn;
        } 
    }
    public void desconectar(){
        conn = null;
        System.out.println("Desconexion a base de datos listo..");
         
    }
    
}
