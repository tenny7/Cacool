/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacool;


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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;






/**
 *
 * @author Tennyson Onovwiona
 * @email tennyson.onovwiona@gmail.com
 */


public class FXMLDocumentController implements Initializable {

    public MenuItem closeButton;
    public Label neededFiveStars;
    public Button rollBack;
    @FXML private Button refresh;
    @FXML private TableView<Ratings> tableView;
    @FXML private TableColumn<Ratings, Double> firstColumn;
    @FXML private TableColumn<Ratings, Double> secondColumn;
    @FXML private TableColumn<Ratings, Integer> thirdColumn;
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

    @FXML private double[] targetKpiArray       =   new double[2];
    @FXML private int[] savedFiveStarsArray     =   new int[2];
    @FXML private int[] TotalRatingArray        =   new int[2];
    @FXML private int[] needStarsArray          =   new int[1];
    @FXML private int sum                       =   0;
    @FXML private double[] totalKpiTarget       =   new double[1];
    @FXML private int[] oldSum                  =   new int[2];
    @FXML private int[] oldTotalRating          =   new int[2];
    @FXML private static DecimalFormat df       =   new DecimalFormat("0.00");
    @FXML private int fiveStarCounter;
    @FXML private int five_star     = 0;
    @FXML private int four_star     = 0;
    @FXML private int three_star    = 0;
    @FXML private int two_star      = 0;
    @FXML private int one_star      = 0;

    public boolean isRollbackClicked = false;
    Locale locale = new Locale("en", "US");
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
    String date = dateFormat.format(new Date());
    String time = timeFormat.format(new Date());


  public void handleCalculateRating(ActionEvent event) {
        //try and catch to test the code for empty string or null value
        try{
             if(        fiveStar.getText().matches("\\d+")  ||
                        fourStar.getText().matches("\\d+") ||
                        threeStar.getText().matches("\\d+") ||
                        twoStar.getText().matches("\\d+") ||
                        oneStar.getText().matches("\\d+")
                ){
                 if(fiveStar.getText().isEmpty()){
                     five_star  =   0;
                 }else{
                     five_star  =   Integer.parseInt(fiveStar.getText());
                 }
                 if(fourStar.getText().isEmpty()){
                     four_star  =   0;
                 }else{
                     four_star  =   Integer.parseInt(fourStar.getText());
                 }
                 if(threeStar.getText().isEmpty()){
                     three_star =   0;
                 }else{
                     three_star =   Integer.parseInt(threeStar.getText());
                 }
                 if(twoStar.getText().isEmpty()){
                     two_star   =   0;
                 }else{
                     two_star   =   Integer.parseInt(twoStar.getText());
                 }
                 if(oneStar.getText().isEmpty()){
                     one_star   =   0;
                 }else{
                     one_star   =   Integer.parseInt(oneStar.getText());
                 }
                 // get Target Kpi from database and saved five star count
                    try {
                 // SQLiteJDBC db = new SQLiteJDBC();
                        ResultSet res = SQLiteJDBC.selectRecords();
                        while (res.next()) {
                            double Target_kpi       = res.getDouble("Target_kpi");
                            int Weighted_sum        = res.getInt("Weighted_sum");
                            int savedFiveStars      = res.getInt("Five_stars_count");
                            int Total_rating        = res.getInt("Total_rating");

                            //assign database stored values to the linked list variables
                            oldTotalRating[0]       = Total_rating;
                            oldSum[0]               = Weighted_sum;
                            targetKpiArray[0]       = Target_kpi; //save to array kpi at index 0
                            savedFiveStarsArray[0]  = savedFiveStars; //saved fivestar count
                        }
                    } catch (Exception e) {
                        status.setText(e.getMessage());
                    }

                    try {
                        // int total_no_of_rating = Integer.parseInt(noOfRating.getText());
                        sum = ((five_star * 5) + (four_star * 4) + (three_star * 3) + (two_star * 2) + (one_star * 1));
                        int total_no_of_rating = five_star + four_star + three_star + two_star + one_star;

                        //add old sum to newSum weighted average
                        int newSum = sum + oldSum[0]; //add both together

                        //add old total rating to new total rating and find average
                        int old_total_rating = oldTotalRating[0];
                        double target_kpi_value = targetKpiArray[0];


                        total_no_of_rating = total_no_of_rating + old_total_rating;

                        double rating = ( (double) newSum / (double)total_no_of_rating);
                        double currentKpi = (double)Math.round(rating * 100)/100;
                        fiveStarCounter = savedFiveStarsArray[0] + five_star;

                        SQLiteJDBC.updateRating( currentKpi, total_no_of_rating, newSum, fiveStarCounter, target_kpi_value,date + "  " + time);
                        ResultSet res2 = SQLiteJDBC.selectRecords();

                        while (res2.next()) {
                            int savedFiveStars          = res2.getInt("Five_stars_count");
                            int Total_rating            = res2.getInt("Total_rating");
                            savedFiveStarsArray[0]      = savedFiveStars;
                            TotalRatingArray[0]         = Total_rating;
                        }
                        int stars = calculateNeededFiveStars(TotalRatingArray[0],target_kpi_value,currentKpi);

                        SQLiteJDBC.updateFiveStarNeeded(stars, target_kpi_value, currentKpi);
                        ResultSet res8 = SQLiteJDBC.selectNeededStars();
                        while (res8.next()) {
                            int Needed_five_stars  = res8.getInt("Needed_five_stars");
                            needStarsArray[0] = Needed_five_stars;
                        }
                        if (rating < 4.0) {
                            displayInfo.setText("Current kpi: " + rating);
                            status.setText("Do not lose hope. We can still achieve our monthly goal");
                            neededFiveStars.setText("Required: "+ needStarsArray[0]);
                        }
                        if (rating >= 4.0 && rating < 4.5) {
                            displayInfo.setText("Current kpi: " + rating);
                            status.setText("There has been a significant improvement. :)!");
                            neededFiveStars.setText("Balance 5-Star Count: "+ needStarsArray[0]);
                        }
                        if (rating >= 4.52 && rating < 4.56) {
                            displayInfo.setText("Current kpi: " + rating);
                            status.setText("Keep up the good work ");
                            neededFiveStars.setText("Balance 5-Star Count: "+needStarsArray[0]);
                        }
                        if (rating >= 4.56 && rating < 4.6) {
                            displayInfo.setText("Current kpi: " + rating);
                            status.setText("Invincible!  :) ");
                            neededFiveStars.setText("Balance 5-Star Count: "+needStarsArray[0]);
                        }
                        if (rating >= 4.60 && rating < 5.0) {
                            displayInfo.setText("Current kpi: " + rating);
                            status.setText("Microsoft needs you!");
                            neededFiveStars.setText("Required stars: "+ needStarsArray[0]);
                        }
                                fiveStar.setText("");
                                fourStar.setText("");
                                threeStar.setText("");
                                twoStar.setText("");
                                oneStar.setText("");
                        refreshTable();
                    } catch (NumberFormatException | ClassNotFoundException | NoSuchElementException | SQLException e) {
                        status.setText("Error: " + e.getMessage() + e.getCause() );
                }
            }
            else{
                showDialog("Not an integer. Please enter an integer value");
            }
        }catch(NumberFormatException e){
            status.setText("The error is "+e.getMessage() + ", Cause: "+ e.getCause());
        }
    }

    public int calculateNeededFiveStars(int TotalRating,double targetKpi,double currentKpi){
        double needed_stars = TotalRating * (currentKpi - targetKpi)/(targetKpi - 5);
        double stars = Math.round(needed_stars);
        int needStars = (int)stars;
        if(needStars<0)
            return 0;
        return needStars;
    }

    public void handleSetTarget(ActionEvent event) throws SQLException, ClassNotFoundException {

      // Step 2: Opening database connection
        try {
            double monthly_target = Double.parseDouble(monthlyTarget.getText());

            SQLiteJDBC.insertRecords(monthly_target,date + "  " + time);

            // LocalDate.now() + " " + LocalTime.now()
            showDialog("Data inserted successfully");
            //display in app updateRating
      
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
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Total_rating"));
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
                int Total_rating        =   res3.getInt("Total_rating");
                int Weighted_sum        =   res3.getInt("Weighted_sum");
                int Five_stars_count    =   res3.getInt("Five_stars_count");
                String Entry_date       =   res3.getString("Entry_date");
                int Needed_five_stars   =   res3.getInt("Needed_five_stars");

                //Printing Results LocalDate.now()+ " "+ LocalTime.now()
                ratings.add(new Ratings(Target_kpi,Current_kpi,Total_rating,Five_stars_count,Entry_date));
                oldSum[0] = Weighted_sum;
                displayTarget.setText("Monthly Target: "+Target_kpi );
                displayInfo.setText(String.valueOf("Current kpi: "+Current_kpi));
//                int stars = calculateNeededFiveStars(TotalRatingArray[0],Target_kpi,Current_kpi);
//                needStarsArray[0] = stars;
//                SQLiteJDBC.updateFiveStarNeeded(stars,Target_kpi,Current_kpi);

                neededFiveStars.setText("Balance 5-Star Count: "+ Needed_five_stars);
                oldTotalRating[0] = Total_rating;
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
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Total_rating"));
        sixthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, String>("Entry_date"));
        tableView.setItems(getRatings());
    }

    public void handleRefresh(ActionEvent actionEvent) {
        firstColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Target_kpi"));
        secondColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Double>("Current_kpi"));
        thirdColumn.setCellValueFactory(new PropertyValueFactory<Ratings, Integer>("Total_rating"));
        sixthColumn.setCellValueFactory(new PropertyValueFactory<Ratings, String>("Entry_date"));
        tableView.setItems(getRatings());
    }

    public void closeButtonAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void handleRollBack(ActionEvent actionEvent) throws ClassNotFoundException, SQLException{


                try {

                    SQLiteJDBC.connection.rollback();
                    SQLiteJDBC.connection.commit();



                    refreshTable();

                } catch (Exception e) {
                    status.setText(e.getMessage());



                }









    }
}

