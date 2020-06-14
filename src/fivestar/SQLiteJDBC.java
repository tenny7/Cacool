/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fivestar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Tennyson
 */
public class SQLiteJDBC {
    //JDBC driver name and Database URL
    public static final String JDBC_DRIVER  = "org.sqlite.JDBC";
    public static final String DB_URL       = "jdbc:sqlite:cacool.db";
    private static boolean hasData          = false;
    public static Connection connection     = null;
    public static Statement statement       = null;
    
    
    
    public static ResultSet displayRating() throws ClassNotFoundException, SQLException{
        if(connection == null){
            createConnection();
        }
        
        statement = connection.createStatement();
        String sql_select = "SELECT Target_kpi,Current_kpi,Total_rating,Weighted_sum,Five_stars_count FROM fivestar_tb";
        ResultSet result_set = statement.executeQuery(sql_select);
        
        return result_set; 
        
        
    }
    
    public static void createConnection() throws ClassNotFoundException, SQLException{
       
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL);
            initialise();
    }
    
    public static void initialise()throws SQLException{
        if (!hasData){
            hasData = true;
            String sqlite_master    =   "SELECT name FROM sqlite_master WHERE type='table' AND name='fivestar_tb'";
            String sql              =   "CREATE TABLE fivestar_tb" +
                                        "(ID integer PRIMARY KEY AUTOINCREMENT," +
                                        " Target_kpi               real, " + 
                                        " Current_kpi              real, " + 
                                        " Total_rating             real, " + 
                                        " Weighted_sum             integer, " + 
                                        " Entry_date               text, " +
                                        " Five_stars_count         real  )"; 
            
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(sqlite_master);
            if(!res.next()){
                System.out.println("Building the Fivestar tabel with prepopulated values");
                Statement statement_2 = connection.createStatement();
                statement_2.execute(sql);
                
                //insertign some sample data
                String sql_2 = "INSERT INTO fivestar_tb (Target_kpi,Current_kpi,Total_rating,Weighted_sum,Entry_date,Five_stars_count) VALUES(?,?,?,?,?,?)";
                PreparedStatement prep = connection.prepareStatement(sql_2);
//                prep.setInt(1, 1);
                prep.setDouble(1, 5.0);
                prep.setDouble(2, 5.0);
                prep.setDouble(3, 1.0);
                prep.setInt(4, 5);
                prep.setString(5, LocalDate.now() + " " + LocalTime.now());
                prep.setDouble(6, 1);
                
                prep.execute();
            }
            
        }
    }
    
    public static void updateRating(double CurrentKpi,double TotalRating, int NewSum, int FiveStarCounter,double TargetKpi, String EntryDate) throws ClassNotFoundException, SQLException{
        if(SQLiteJDBC.connection == null){
            createConnection();
        }
        String update_fivestar_tb = "UPDATE fivestar_tb SET Current_kpi=?, Total_rating =?,Weighted_sum =?,Five_stars_count=?,Entry_date=? WHERE Target_kpi=?";
        PreparedStatement prep_2 = connection.prepareStatement(update_fivestar_tb);
            prep_2.setDouble(1, CurrentKpi);
            prep_2.setDouble(2, TotalRating);
            prep_2.setDouble(3, NewSum);
            prep_2.setInt(4, FiveStarCounter);
            prep_2.setString(5, LocalDate.now() + " " + LocalTime.now());
            prep_2.setDouble(6, TargetKpi);
            prep_2.executeUpdate();
    }
    
    public static ResultSet selectRecords()throws ClassNotFoundException, SQLException{
        if(connection == null){
            createConnection();
        }
        
        statement = connection.createStatement();
        String sql_select = "SELECT Target_kpi,Current_kpi,Total_rating,Weighted_sum,Five_stars_count,Entry_date FROM fivestar_tb";

       
        ResultSet res = statement.executeQuery(sql_select);
        
        return res; 
    }
    
    public static void insertRecords(double Target_kpi,String Entry_date)throws ClassNotFoundException,SQLException{
//    public void insertRecords(double Target_kpi,double Current_kpi,double Total_rating,int Weighted_sum,int Five_stars_count)throws SQLException{
        if(connection == null){
            createConnection();
        }
        String sql_3 = "INSERT INTO fivestar_tb (Target_kpi, Entry_date) values(?,?)";
                PreparedStatement prep = connection.prepareStatement(sql_3);
                prep.setDouble(1, Target_kpi);
                prep.setString(2, LocalDate.now() + " " + LocalTime.now());
                prep.execute();
    }
    
}
