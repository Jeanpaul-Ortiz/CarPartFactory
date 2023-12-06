package main;

import interfaces.Queue;
import data_structures.ListQueue;
import java.util.Random;


public class PartMachine {
    private int id;
    private CarPart part;
    private int period;
    private double weightError;
    private int chanceOfDefective;
    private Queue<CarPart> conveyorBelt;
    private Queue<Integer> timer;
    private int partsProduced;
    
   
    /**
     * PartMachine class represents a machine that produces car parts.
     * It has an id, a CarPart object, a period, a weight error, 
     * and chance of defective parts
     **/
    public PartMachine(int id, CarPart p1, int period, double weightError, int chanceOfDefective) {
        this.id = id;
        this.part = p1;
        this.period = period;
        this.weightError = weightError;
        this.chanceOfDefective = chanceOfDefective;
        
        timer = new ListQueue<Integer>();
        
        
       /**
        * Timer Inizialization
        **/
        int time = this.period - 1;
        while (time >= 0) {
            this.timer.enqueue(time);
            time--;
        }

        /**
         * Conveyor Belt Inizialization
         **/
        conveyorBelt = new ListQueue<CarPart>();
        for(int i = 0; i < 10; i++) {
            this.conveyorBelt.enqueue(null);
        }

    }

    public int getId() {
       return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Queue<Integer> getTimer() {
        return this.timer;
    }
    public void setTimer(Queue<Integer> timer) {
        this.timer = timer;
    }
    public CarPart getPart() {
       return this.part;
    }
    public void setPart(CarPart part1) {
        this.part = part1;
    }
    public Queue<CarPart> getConveyorBelt() {
        return this.conveyorBelt;
    }
    public void setConveyorBelt(Queue<CarPart> conveyorBelt) {
    	this.conveyorBelt = conveyorBelt;
    }
    public int getTotalPartsProduced() {
        return this.partsProduced;
    }
    public void setTotalPartsProduced(int count) {
    	this.partsProduced = count;
    }
    public double getPartWeightError() {
        return this.weightError;
    }
    public void setPartWeightError(double partWeightError) {
        this.weightError = partWeightError;
    }
    public int getChanceOfDefective() {
        return this.chanceOfDefective;
    }
    public void setChanceOfDefective(int chanceOfDefective) {
        this.chanceOfDefective = chanceOfDefective;
    }
    public void resetConveyorBelt() {
        for (int i = 0; i < 10; i++) {
            this.getConveyorBelt().dequeue();
            this.getConveyorBelt().enqueue(null);
        }
    }
    public int tickTimer() {
       this.getTimer().enqueue(this.getTimer().front());
       return this.getTimer().dequeue();
    }

    
    /*
     * Produces a car part when the timer hits 0
     * that has a weight that is its the parts 
     * standard weight +- a number within range of the weight error.
     * Also, has a chance of being defective, according to the machine parameters.
     * 
     */
    public CarPart produceCarPart() {
        Random rand = new Random();
        double randWeight = this.getPart().getWeight() + (this.getPartWeightError() - (rand.nextDouble() * this.getPartWeightError() * 2));
        if(tickTimer() == 0){
            CarPart newPart = new CarPart(this.getPart().getId(), this.getPart().getName(), randWeight, this.getTotalPartsProduced()%this.chanceOfDefective == 0);
            this.conveyorBelt.enqueue(newPart);
            partsProduced++;
        }
        else{
            this.conveyorBelt.enqueue(null);
        }
       return conveyorBelt.dequeue();
    }

    /**
     * Returns string representation of a Part Machine in the following format:
     * Machine {id} Produced: {part name} {total parts produced}
     */
    @Override
    public String toString() {
        return "Machine " + this.getId() + " Produced: " + this.getPart().getName() + " " + this.getTotalPartsProduced();
    }
    /**
     * Prints the content of the conveyor belt. 
     * The machine is shown as |Machine {id}|.
     * If the is a part it is presented as |P| and an empty space as _.
     */
    public void printConveyorBelt() {
        // String we will print
        String str = "";
        // Iterate through the conveyor belt
        for(int i = 0; i < this.getConveyorBelt().size(); i++){
            // When the current position is empty
            if (this.getConveyorBelt().front() == null) {
                str = "_" + str;
            }
            // When there is a CarPart
            else {
                str = "|P|" + str;
            }
            // Rotate the values
            this.getConveyorBelt().enqueue(this.getConveyorBelt().dequeue());
        }
        System.out.println("|Machine " + this.getId() + "|" + str);
    }
}
