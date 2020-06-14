/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fivestar;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

public class Fivestar extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        primaryStage.setTitle("Performance Calculator");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);

        primaryStage.show();



    }


    public static void main(String[] args) {
//        SQLiteJDBC sqlite_data = new SQLiteJDBC();
        ResultSet rs ;
        
        try{
           rs = SQLiteJDBC.displayRating(); 
           while(rs.next()){
               System.out.println(rs.getString("Target_kpi") + " " + rs.getString("Current_kpi") );
//               System.out.println(LocalDate.now() + " " + LocalTime.now());
           }
        }catch(ClassNotFoundException e){
             e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        launch(args);
    }
}
