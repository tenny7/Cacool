/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacool;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;

/**
 *
 * @author Tennyson
 */
public class Ratings implements Initializable {

    private SimpleDoubleProperty    Target_kpi, Current_kpi;
    private SimpleIntegerProperty   Five_stars_count,Total_rating;
    private SimpleStringProperty    Entry_date;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public Ratings(Double target_kpi, Double current_kpi, Integer total_rating, int Five_stars_count,String entry_date) {
        this.Target_kpi         = new SimpleDoubleProperty(target_kpi) ;
        this.Current_kpi        = new SimpleDoubleProperty(current_kpi) ;
        this.Total_rating       = new SimpleIntegerProperty(total_rating) ;
        this.Five_stars_count   = new SimpleIntegerProperty(Five_stars_count);
        this.Entry_date         = new SimpleStringProperty(entry_date);
    }

    public double getTarget_kpi() {
        return Target_kpi.get();
    }

    public void setTarget_kpi(double target_kpi) {
        this.Target_kpi.set(target_kpi);
    }

    public double getCurrent_kpi() {
        return Current_kpi.get();
    }

    public void setCurrent_kpi(double current_kpi) {
        this.Current_kpi.set(current_kpi);
    }

    public int getTotal_rating() {
        return Total_rating.get();
    }

    public void setTotal_rating(int total_rating) {
        this.Total_rating.set(total_rating);
    }

    public int getFive_stars_count() { return Five_stars_count.get(); }

    public void setFive_stars_count(int five_stars_count) { this.Five_stars_count.set(five_stars_count); }

    public String getEntry_date() {return Entry_date.get();}

    public void setEntry_date(String entry_date) { this.Entry_date.set(entry_date); }

}
