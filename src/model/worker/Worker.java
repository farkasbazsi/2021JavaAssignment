package model.worker;

import model.Details;

public abstract class Worker {
    private Details details;
    private int workingSpeed;
    
    public Worker(Details details){
        this.details = details;
    }

    public Details getDetails() {
        return details;
    }
    
    public void checkSalary() {
        
    }
    
    abstract void findPath();
    
    
}
