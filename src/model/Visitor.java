package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Visitor {
    private Details details;
    private int happiness;
    private int hunger;
    private Timer timer;
    private int time = 0;
    
    public Visitor(){
        timer = new Timer(1000,new visitorTimer());
        timer.start();
    }
    
    public Visitor(Details details){
        this.details = details;
    }

    public int getHappiness() {
        return happiness;
    }
    
    private void findPath(){
        
    }
    
    private void chooseGame(){
        
    }
    
    private void detectElements() {
        
    }
    
    private void goForARide() {
        
    }
    
    private void eatSomething() {
        
    }

    private class visitorTimer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.out.println(time++);
        }
    }
    
}
