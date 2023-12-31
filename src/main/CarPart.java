package main;

public class CarPart {
    private int id;
    private String name;
    private double weight;
    private boolean isDefective;
    
    /**
     * This class represents a car part with an id, name, 
     * weight, and a boolean value indicating if it is defective.
     */
    public CarPart(int id, String name, double weight, boolean isDefective) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.isDefective = isDefective;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name= name;
    }
    public double getWeight() {
        return this.weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isDefective() {
        return this.isDefective;
    }
    public void setDefective(boolean isDefective) {
        this.isDefective = isDefective;
    }
    /**
     * Returns the parts name as its string representation
     * @return (String) The part name
     */
    public String toString() {
        return this.getName();
    }
}