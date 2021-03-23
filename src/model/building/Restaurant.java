package model.building;

import model.Details;

public class Restaurant extends Building{
    private int cost;
    private int maxWorkers;
    private int maxVisitors;
    
    public Restaurant(Details details, int bcost, String name){
        super(details, bcost, name);
    }
    
    public void orderByVisitor(){
        
    }
}
