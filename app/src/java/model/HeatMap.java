package model;

/**
 * Represents heatmap object containing variable semanticPlace, qtyPax and heatLevel
 */
public class HeatMap {
    private String semanticPlace;
    private int qtyPax;
    private int heatLevel;

    /**
     * Constructor object for Heatmap
     * 
     * @param semanticPlace String name of the location
     * @param qtyPax int Total number of student present
     * @param heatLevel int heatlevel of the location
     */
    public HeatMap(String semanticPlace, int qtyPax, int heatLevel) {
        this.semanticPlace = semanticPlace;
        this.qtyPax = qtyPax;
        this.heatLevel = heatLevel;
    }

    /**
     * Getter method for semantic place
     * @return String semanticPlace
     */
    public String getPlace() {
        return semanticPlace;
    }

    /**
     * Setter method for semantic place
     * @param semanticPlace String semantic place to be updated to
     */
    public void setPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    /**
     * Getter method for number of people present at the location
     * @return int number of people present at the location
     */
    public int getQtyPax() {
        return qtyPax;
    }

    /**
     * Setter method for number of people present at the location
     * @param qtyPax int number of people to be updated to
     */
    public void setQtyPax(int qtyPax) {
        this.qtyPax = qtyPax;
    }

    /**
     * Getter method for the heatlevel of the location
     * @return int heatlevel of the location
     */
    public int getHeatLevel() {
        return heatLevel;
    }

    /**
     * Setter method for heatlevel of the location
     * @param heatLevel int heatlevel to be updated to
     */
    public void setHeatLevel(int heatLevel) {
        this.heatLevel = heatLevel;
    }

    /**
     * Returns heatmap object in a string format
     * @return String containing attribute of the heatmap object
     */
    @Override
    public String toString() {
        return "heatmap{" + "place=" + semanticPlace + ", qtyPax=" + qtyPax + ", heatLevel=" + heatLevel + '}';
    }
    
    
}
