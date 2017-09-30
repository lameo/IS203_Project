/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author xuying
 */
public class HeatMap {
    private String place;
    private int qtyPax;
    private int heatLevel;

    public HeatMap(String place, int qtyPax, int heatLevel) {
        this.place = place;
        this.qtyPax = qtyPax;
        this.heatLevel = heatLevel;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getQtyPax() {
        return qtyPax;
    }

    public void setQtyPax(int qtyPax) {
        this.qtyPax = qtyPax;
    }

    public int getHeatLevel() {
        return heatLevel;
    }

    public void setHeatLevel(int heatLevel) {
        this.heatLevel = heatLevel;
    }

    @Override
    public String toString() {
        return "heatmap{" + "place=" + place + ", qtyPax=" + qtyPax + ", heatLevel=" + heatLevel + '}';
    }
    
    
}
