/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fivestar;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;






/**
 *
 * @author Tennyson
 */


public class FXMLDocumentController implements Initializable {

    public MenuItem closeButton;
    public Label neededFiveStars;
    @FXML private Button refresh;
    @FXML private TableView<Ratings> tableView;
    @FXML private TableColumn<Ratings, Double> firstColumn;
    @FXML private TableColumn<Ratings, Double> secondColumn;
    @FXML private TableColumn<Ratings, Double> thirdColumn;
    @FXML private TableColumn<Ratings, Integer> fourthColumn;
    @FXML private TableColumn<Ratings, Integer> fifthColumn;
    @FXML private TableColumn<Ratings, String> sixthColumn;




    @FXML private Button fetchData;
    @FXML private TextField fiveStar;
    @FXML private TextField fourStar;
    @FXML private TextField threeStar;
    @FXML private TextField twoStar;
    @FXML private TextField oneStar;

    @FXML private TextField noOfRating;
    @FXML private TextField monthlyTarget;
    @FXML private Label displayTarget;
    @FXML private Label status;
    @FXML private Button calculateRating;
    @FXML private Button setTarget;
    @FXML private Label displayInfo;




    @FXML private double[] targetKpiArray       =new double[2];
    @FXML private int[] savedFiveStarsArray     =new int[2];
    @FXML private Integer sum = 0;
    @FXML private double[] totalKpiTarget       = new double[1];
    @FXML private LinkedList<Integer> oldSum    = new LinkedList<Integer>();
    @FXML private LinkedList<Double> oldTotalRating = new LinkedList<Double>();
    @FXML private static DecimalFormat df           = new DecimalFormat("0.00");
    @FXML private int fiveStarCounter;
    @FXML private int five_star     = 0;
    @FXML private int four_star     = 0;
    @FXML private int three_star    = 0;
    @FXML private int two_star      = 0;
    @FXML private int one_star      = 0;
    
    
    
  public void handleCalculateRating(ActionEvent event) {
        //try and catch to test the code for empty string or null values

        if(fiveStar.getText().isEmpty() | fourStar.getText().isEmpty() | threeStar.getText().isEmpty() | twoStar.getText().isEmpty() | oneStar.getText().isEmpty()){
            showDialog("No field can be empty");
        }

        try{
            if (    !fiveStar.getText().matches("\\b([0-9]|1[0-2])\\b") |
                    !fourStar.getText().matches("\\b([0-9]|1[0-2])\\b") |
                    !threeStar.getText().matches("\\b([0-9]|1[0-2])\\b") |
                    !twoStar.getText().matches("\\b([0-9]|1[0-2])\\b") |
                    !oneStar.getText().matches("\\b([0-9]|1[0-2])\\b")
            ){
                showDialog("Not an integer. Please enter an integer value");
            }else {


                five_star = Integer.parseInt(fiveStar.getText());
                four_star = Integer.parseInt(fourStar.getText());
                three_star = Integer.parseInt(threeStar.getText());
                two_star = Integer.parseInt(twoStar.getText());
                one_star = Integer.parseInt(oneStar.getText());


                //        get Target Kpi from database and saved five star count
                try {

//                    SQLiteJDBC db = new SQLiteJDBC();
                    ResultSet res = SQLiteJDBC.selectRecords();
                    
                    while (res.next()) {
                        double Target_kpi       = res.getDouble("Target_kpi");
                        int Weighted_sum        = res.getInt("Weighted_sum");
                        int savedFiveStars      = res.getInt("Five_stars_count");
                        double Total_rating     = res.getDouble("Total_rating");

                        //assign database stored values to the linked list variables
                        oldTotalRating.addFirst(Total_rating);
                        oldSum.addFirst(Weighted_sum);
                        targetKpiArray[0]       = Target_kpi; //save to array kpi at index 0
                        savedFiveStarsArray[0]  = savedFiveStars; //saved fivestar count
                    }

                } catch (Exception e) {
                    status.setText(e.getMessage());
                }

                try {
                    double total_no_of_rating = Double.parseDouble(noOfRating.getText());

                    sum = ((five_star * 5) + (four_star * 4) + (three_star * 3) + (two_star * 2) + (one_star * 1));

                    //add old sum to newSum weighted average
                    int intSum = (int) sum; //cast sum from Integer to int
                    int old_sum = (int) oldSum.getFirst(); //cast oldSum from Integer to int
                    int newSum = intSum + old_sum; //add both together

                    //add old total rating to new total rating and find average

                    double old_total_rating = oldTotalRating.getFirst();
                    double target_kpi_value = targetKpiArray[0];


                    total_no_of_rating = total_no_of_rating + old_total_rating;
                    Double total_sum = (double) newSum;

                    double rating = (total_sum / total_no_of_rating);
                    double currentKpi = rating;

                    SQLiteJDBC.updateRating(currentKpi,total_no_of_rating,newSum,fiveStarCounter,target_kpi_value,LocalDate.now() + " " + LocalTime.now());
                    ResultSet res2 = SQLiteJDBC.selectRecords();
                    
//                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (res2.next()) {
                        int savedFiveStars = res2.getInt("Five_stars_count");
                        savedFiveStarsArray[0] = savedFiveStars;
                    }


                   double stars = calculateNeededFiveStars(savedFiveStarsArray[0],target_kpi_value,currentKpi);
                    System.out.println("needed five stars "+stars);
                    int need_csat = (int)stars;

                    if (rating < 4.0) {
                        displayInfo.setText("Current kpi: " + String.valueOf(df.format(rating)));
                        status.setText("Liquid Metals!...I know i'm just a software, but i love cakes, especially end of month cakes.Do you?");
                        neededFiveStars.setText("Needed 5 stars: "+String.valueOf(need_csat));
                    }
                    if (rating >= 4.0 && rating < 4.5) {
                        displayInfo.setText("Current kpi: " + String.valueOf(df.format(rating)));
                        status.setText("My Gabbage Collection Class gets more 5 stars than you do :) !");
                        neededFiveStars.setText("Needed 5 stars: "+String.valueOf(need_csat));
                    }
                    if (rating >= 4.5 && rating < 5.0) {
                        displayInfo.setText("Current kpi: " + String.valueOf(df.format(rating)));
                        status.setText("Invincibles!  :) ");
                        neededFiveStars.setText("Needed 5 stars: "+String.valueOf(need_csat));
                    }
                    if (rating == 5.0) {
                        displayInfo.setText("Current kpi: " + String.valueOf(df.format(rating)));
                        status.setText("Microsoft needs you!");
                        neededFiveStars.setText("Needed 5 stars: "+String.valueOf(need_csat));
                    }

                    refreshTable();
                } catch (NumberFormatException | ClassNotFoundException | NoSuchElementException | SQLException e) {
                    status.setText("Error: " + e.getMessage() + e.getCause() );
                }
            }
        }catch(NumberFormatException e){
            status.setText("The error is "+e.getMessage() + ", Cause: "+ e.getCause());
        }
    }

    public double calculateNeededFiveStars(int savedStars,double targetKpi,double currentKpi){
        double savedFiveStarConverted = (int)savedStars;
        double requiredStars = (targetKpi * savedFiveStarConverted)/currentKpi;
        int rate = (int)requiredStars;
        return rate;
    }



    public void handleSetTarget(ActionEvent event) throws SQLException, ClassNotFoundException {

         // Step 2: Opening database connection
        try {
            double monthly_target = Double.parseDouble(monthlyTarget.getText());
            System.out.println("target" + monthly_target);
            SQLiteJDBC.insertRecords(monthly_target,LocalDate.now() + " " + LocalTime.now());
            showDialog("Data inserted successfully");

            //display in app
            displayTarget.setText(String.valueOf(monthly_target));
            refreshTable();

        }catch (Exception e){
            showDialog(e.getMessage());

        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Target_kpi"));
        secondColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Current_kpi"));
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Total_rating"));
        fourthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Weighted_sum"));
        fifthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Five_stars_count"));
        sixthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, String>("Entry_date"));
        tableView.setItems(getRatings());
    }


    /*
    * returns Observable
    * gets ratings from database and populate the gui table
    *
    */
    private ObservableList<Ratings> getRatings() {

        ObservableList<Ratings> ratings = FXCollections.observableArrayList();
        try{

            ResultSet res3 = SQLiteJDBC.selectRecords();
            while(res3.next()){
                double Target_kpi       =   res3.getDouble("Target_kpi");
                double Current_kpi      =   res3.getDouble("Current_kpi");
                double Total_rating     =   res3.getDouble("Total_rating");
                int Weighted_sum        =   res3.getInt("Weighted_sum");
                int Five_stars_count    =   res3.getInt("Five_stars_count");
                String Entry_date       =   res3.getString("Entry_date");

                //Printing Results LocalDate.now()+ " "+ LocalTime.now()
                ratings.add(new Ratings(Target_kpi,Current_kpi,Total_rating,Weighted_sum,Five_stars_count,Entry_date));
                oldSum.addFirst(Weighted_sum);
                displayTarget.setText("Monthly Target: "+Target_kpi );
                displayInfo.setText(String.valueOf("Current kpi: "+df.format(Current_kpi)));
                double stars = calculateNeededFiveStars(Five_stars_count,Target_kpi,Current_kpi);
                int need_csat = (int)stars;
                neededFiveStars.setText("Needed 5 stars: "+String.valueOf(need_csat));
                
                oldTotalRating.addFirst(Total_rating);
                targetKpiArray[0] =Target_kpi;
            }

        }catch(Exception e){
            status.setText(e.getMessage());
        }
        return ratings;
    }

    /*
    *Alert box
    */
    private void showDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleFetchData(ActionEvent actionEvent) {
        try{

             SQLiteJDBC db = new SQLiteJDBC();
             ResultSet res4 = db.selectRecords();
            while(res4.next()){
                String Target_kpi=res4.getString("Target_kpi");
                //Printing Results
                double target_to_save = Double.parseDouble(Target_kpi);

                displayTarget.setText("Monthly Target: "+ df.format(target_to_save));

            }

        }catch(Exception e){
            status.setText(e.getMessage());
        }
    }

    public void refreshTable(){
        firstColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Target_kpi"));
        secondColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Current_kpi"));
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Total_rating"));
        fourthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Weighted_sum"));
        sixthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, String>("Entry_date"));
        tableView.setItems(getRatings());
    }

    public void handleRefresh(ActionEvent actionEvent) {
        firstColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Target_kpi"));
        secondColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Current_kpi"));
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Total_rating"));
        fourthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Weighted_sum"));
        sixthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, String>("Entry_date"));
        tableView.setItems(getRatings());
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        Platform.exit();
    }
}

