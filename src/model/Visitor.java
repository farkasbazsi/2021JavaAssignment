package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * spawnTile-ról indulunk.
 * boldogság maximumon, éhség 0.
 * JPanel.add(visitor)
 * - mozgás másodpercenként
 * - cél kiválasztása (building listából random 1 játék aminek a statusza active)
 * - másodpercenként menjen a cél felé
 * @author Hexa
 */

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
            //mozogj
        }
    }
    
}
