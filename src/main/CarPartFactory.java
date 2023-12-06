package main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import interfaces.*;
import data_structures.*;

public class CarPartFactory {
    private List<PartMachine> machines;
    private Stack<CarPart> productionBin;
    private Map<Integer, CarPart> partCatalog;
    private Map<Integer, List<CarPart>> inventory;
    private List<Order> orders;
    private Map<Integer, Integer> defectives;
    private String orderPath;
    private String partsPath;

        
    /**
     * CarPartFactory class represents a factory that produces car parts using machines and parts from a given directory.
     * It also manages orders and inventory of produced parts.
     * 
     * @param orderPath the path to the directory containing order files
     * @param partsPath the path to the directory containing machine and part files
     * @throws IOException if there is an error reading the files
     */
    public CarPartFactory(String orderPath, String partsPath) throws IOException {
        this.orderPath = orderPath;
        this.partsPath = partsPath;
        orders = new DoublyLinkedList<Order>();
        machines = new DoublyLinkedList<PartMachine>();
        productionBin = new LinkedStack<CarPart>();
        partCatalog = new HashTableSC<Integer, CarPart>(15, new BasicHashFunction());
        inventory = new HashTableSC<Integer, List<CarPart>>(15, new BasicHashFunction());
        defectives = new HashTableSC<Integer, Integer>(15, new BasicHashFunction());
        
        setupOrders(orderPath);
        setupMachines(partsPath);
        setupInventory();
        
    }
    
    public List<PartMachine> getMachines() {
       return this.machines;
    }
    public void setMachines(List<PartMachine> machines) {
        this.machines = machines;
    }
    public Stack<CarPart> getProductionBin() {
        return this.productionBin;
    }
    public void setProductionBin(Stack<CarPart> production) {
       this.productionBin = production;
    }
    public Map<Integer, CarPart> getPartCatalog() {
        return this.partCatalog;
    }
    public void setPartCatalog(Map<Integer, CarPart> partCatalog) {
        this.partCatalog = partCatalog;
    }
    public Map<Integer, List<CarPart>> getInventory() {
       return this.inventory;   
    }
    public void setInventory(Map<Integer, List<CarPart>> inventory) {
        this.inventory = inventory;
    }
    public List<Order> getOrders() {
        return this.orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public Map<Integer, Integer> getDefectives() {
        return this.defectives;
    }
    public void setDefectives(Map<Integer, Integer> defectives) {
        this.defectives = defectives;
    }

    /**
     * Reads orders from orders.csv and sets them up for processing.
     * @param path the path of the file containing the orders
     * @throws IOException if there is an error reading the file
     */
    public void setupOrders(String path) throws IOException {
		try(BufferedReader orderFile = new BufferedReader(new FileReader(orderPath))){
			String curOrder;
			orderFile.readLine();

			while((curOrder = orderFile.readLine()) != null) {
                Map<Integer, Integer> orderMap = new HashTableSC<Integer,Integer>(15, new BasicHashFunction());
				String[] orderDetails = curOrder.split(",");
                String[] partsOrdered = orderDetails[2].split("-");
                for(String part : partsOrdered){
                    //Removing everything but the numbers from the ordered parts
                    String partNums = part.replace("(","").replace(")", "");
                    
                    String[] KandV = partNums.split(" ");
                    
                    orderMap.put(Integer.parseInt(KandV[0]), Integer.parseInt(KandV[1]));

                }
				orders.add(new Order(Integer.parseInt(orderDetails[0]), orderDetails[1],orderMap, false));
			}
		}
		
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }


    /**
     * Reads a file containing car part details and sets up the machines to produce those parts.
     * 
     * Also adds the parts to the part catalog.
     * @param path the path of the file containing the car part details
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void setupMachines(String path) throws IOException {
       	try(BufferedReader partFile = new BufferedReader(new FileReader(partsPath))){
			String curPart;
			partFile.readLine();
			
			while((curPart = partFile.readLine()) != null) {
                String[] partDetails = curPart.split(",");
                Integer id = Integer.parseInt(partDetails[0]);
                String name = partDetails[1];
                Double weight = Double.parseDouble(partDetails[2]);
                CarPart temp = new CarPart(id, name, weight, false);
                Double weightError = Double.parseDouble(partDetails[3]);
                Integer period = Integer.parseInt(partDetails[4]);
                Integer chanceOfDefective = Integer.parseInt(partDetails[5]);

                machines.add(new PartMachine(id, temp, period, weightError, chanceOfDefective));

                partCatalog.put(id, temp);
		}
    }
		
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }


    /**
     * Sets up the inventory for the car parts factory by creating a doubly linked list for each part ID.
     * The doubly linked list is initially empty.
     */
    public void setupInventory() {
        for(int i = 0; i < machines.size(); i++) {
            inventory.put(machines.get(i).getPart().getId(), new DoublyLinkedList<CarPart>());
            defectives.put(machines.get(i).getPart().getId(), 0);
        }
    }


    /**
     * Removes a car part from the production bin and stores it in the inventory.
     * If the car part is defective, it is stored in the defectives map instead.
     */
    public void storeInInventory() {
        while(!productionBin.isEmpty()){
       CarPart temp = productionBin.pop();

    	   if(!temp.isDefective()) {
    		   inventory.get(temp.getId()).add(temp);
    	   }else{
    		   if (defectives.containsKey(temp.getId())) {
    			    defectives.put(temp.getId(), defectives.get(temp.getId()) + 1);
    			} else {
    			    defectives.put(temp.getId(), 1);
    			}
       	}
    }
}

    /**
     * Runs the car part factory for a specified number of days and minutes per day.
     * Produces car parts using the machines and stores them in the production bin.
     * After each day, stores the produced car parts in the inventory and processes orders.
     * 
     * @param days the number of days to run the factory
     * @param minutes the number of minutes per day to run the factory
     */
    public void runFactory(int days, int minutes) {
        for(int i = 0; i < days; i++){
            for(PartMachine machine : machines ){
                for(int j = 0; j < minutes; j++) {
                        CarPart temp = machine.produceCarPart();
                        if(temp != null){
                            productionBin.push(temp);
                        }

                    }
                	for(int p = 0; p < 10; p++) {
                		CarPart temp = machine.getConveyorBelt().dequeue();
                		if(temp != null) {
                			productionBin.push(temp);
                		}
                		machine.getConveyorBelt().enqueue(null);
                	}
            	}
            
            this.storeInInventory();        
            }
        processOrders();
    }

   
    /**
     * Processes the orders by checking if they can be fulfilled 
     * with the current inventory and fulfilling them if possible.
     */
    public void processOrders() {
        for(Order order : this.orders){
            if(!order.isFulfilled()){
                boolean canFulfill = true;
                for(Integer part : order.getRequestedParts().getKeys()){
                    if(this.getInventory().get(part).size() < order.getRequestedParts().get(part)){
                        canFulfill = false;
                        break;
                    }
                }
                if(canFulfill){
                    for(Integer part : order.getRequestedParts().getKeys()){
                        for(int i = 0; i < order.getRequestedParts().get(part); i++){
                            this.getInventory().get(part).remove(0);
                        }
                    }
                    order.setFulfilled(true);
                }
            }
        }
    }
    /**
     * Generates a report indicating how many parts were produced per machine,
     * how many of those were defective and are still in inventory. Additionally, 
     * it also shows how many orders were successfully fulfilled. 
     */
    public void generateReport() {
        String report = "\t\t\tREPORT\n\n";
        report += "Parts Produced per Machine\n";
        for (PartMachine machine : this.getMachines()) {
            report += machine + "\t(" + 
            this.getDefectives().get(machine.getPart().getId()) +" defective)\t(" + 
            this.getInventory().get(machine.getPart().getId()).size() + " in inventory)\n";
        }
       
        report += "\nORDERS\n\n";
        for (Order transaction : this.getOrders()) {
            report += transaction + "\n";
        }
        System.out.println(report);
    }

   

}
