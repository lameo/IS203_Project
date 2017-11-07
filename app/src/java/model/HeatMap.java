package model;

public class HeatMap {
    private String semanticPlace;
    private int qtyPax;
    private int heatLevel;

    public HeatMap(String semanticPlace, int qtyPax, int heatLevel) {
        this.semanticPlace = semanticPlace;
        this.qtyPax = qtyPax;
        this.heatLevel = heatLevel;
    }

    public String getPlace() {
        return semanticPlace;
    }

    public void setPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
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
        return "heatmap{" + "place=" + semanticPlace + ", qtyPax=" + qtyPax + ", heatLevel=" + heatLevel + '}';
    }
    
    
}
