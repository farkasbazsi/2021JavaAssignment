/**
 * 2020/2021/2
 * Szoftvertechnológia
 *
 * FFN project
 * Nagy Gergő, Falusi Gergő Gábor, Farkas Balázs
 *
 * 2021.05.12.
 */
package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.accessibility.AccessibleContext;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import model.building.Building;

/**
 * spawnTile-ról indulunk. boldogság maximumon, éhség 0. JPanel.add(visitor) -
 * mozgás másodpercenként - cél kiválasztása (building listából random 1 játék
 * aminek a statusza active) - másodpercenként menjen a cél felé
 *
 * @author Hexa
 */
public class Visitor extends JPanel {

    private Details details;
    private int happiness;
    private int hunger;
    private Timer timer;
    private Timer subTimer;
    private int time = 0;
    private BufferedImage image;
    private final int height = 23;
    private final int width = 25;
    private int money;

    public LinkedList<Integer> path = new LinkedList<Integer>();
    public boolean arrived = true;
    public int posVis = 0;
    public int pathIndex = 0;
    public Building randBuilding;
    public int[] prevBuild = new int[2];

    public int i = 22;
    public int j = 12;
    public int source;
    public int dest;

    public int tilesUntillTrash;
    public boolean leaving;

    public Visitor(Details details) {
        this.details = details;
        timer = new Timer(1000, new visitorTimer());
        timer.start();
        subTimer = new Timer(1000, new subTimer());
        subTimer.start();
        happiness = (int) ((Math.random() * (100 - 60)) + 60);
        money = (int) ((Math.random() * (1000 - 600)) + 600);
        hunger = (int) ((Math.random() * (30 - 0)) + 0);

        tilesUntillTrash = 0;
        leaving = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 10, 10, this);
    }

    public int getHappiness() {
        return happiness;
    }

    public void useRide(int price) {
        if (money - price >= 0) {
            money -= price;
        } else {
            money = 0;
        }
    }

    /**
     * Changes the visitors happines by amount It can't go below 0 and above 100
     *
     * @param amount
     */
    public void changeHappiness(int amount) {
        if (happiness + amount >= 100) {
            happiness = 100;
        } else if (happiness + amount <= 0) {
            happiness = 0;
        } else {
            happiness += amount;
        }
    }

    public int getHunger() {
        return hunger;
    }

    /**
     * If visitor eats something hunger changes to zero.
     */
    public void eatSomething() {
        hunger = 0;
    }

    private class subTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {

        }
    }

    private class visitorTimer implements ActionListener {

        /**
         * Every second visitors hunger increases by 1;
         *
         * @param arg0
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (hunger < 100) {
                hunger++;
            }
            if (hunger > 70) {
                changeHappiness(-5);
            } else if (hunger > 60) {
                changeHappiness(-3);
            } else if (hunger > 50) {
                changeHappiness(-1);
            }
            setBackground(happiness < 40 ? Color.RED : happiness > 70 ? Color.GREEN : Color.YELLOW);
        }
    }

    // function to print the shortest distance and path
    // between source vertex and destination vertex
    public void printShortestDistance(
            ArrayList<ArrayList<Integer>> adj,
            int s, int dest, int v) {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int pred[] = new int[v];
        int dist[] = new int[v];

        if (BFS(adj, s, dest, v, pred, dist) == false) {
            //System.out.println("Given source and destination"
            //        + "are not connected");
            //edge = true;
            return;
        }

        int crawl = dest;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }
    }

    // a modified version of BFS that stores predecessor
    // of each vertex in array pred
    // and its distance from source in array dist
    private static boolean BFS(ArrayList<ArrayList<Integer>> adj, int src,
            int dest, int v, int pred[], int dist[]) {
        // a queue to maintain queue of vertices whose
        // adjacency list is to be scanned as per normal
        // BFS algorithm using LinkedList of Integer type
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // boolean array visited[] which stores the
        // information whether ith vertex is reached
        // at least once in the Breadth first search
        boolean visited[] = new boolean[v];

        // initially all vertices are unvisited
        // so v[i] for all i is false
        // and as no path is yet constructed
        // dist[i] for all i set to infinity
        for (int i = 0; i < v; i++) {
            visited[i] = false;
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }

        // now source is first to be visited and
        // distance from source to itself should be 0
        visited[src] = true;
        dist[src] = 0;
        queue.add(src);

        // bfs Algorithm
        while (!queue.isEmpty()) {
            int u = queue.remove();
            for (int i = 0; i < adj.get(u).size(); i++) {
                if (visited[adj.get(u).get(i)] == false) {
                    visited[adj.get(u).get(i)] = true;
                    dist[adj.get(u).get(i)] = dist[u] + 1;
                    pred[adj.get(u).get(i)] = u;
                    queue.add(adj.get(u).get(i));

                    // stopping condition (when we find
                    // our destination)
                    if (adj.get(u).get(i) == dest) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getSubTimer() {
        return subTimer;
    }

    public void setSubTimer(Timer subTimer) {
        this.subTimer = subTimer;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public LinkedList<Integer> getPath() {
        return path;
    }

    public void setPath(LinkedList<Integer> path) {
        this.path = path;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public int getPosVis() {
        return posVis;
    }

    public void setPosVis(int posVis) {
        this.posVis = posVis;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(int pathIndex) {
        this.pathIndex = pathIndex;
    }

    public Building getRandBuilding() {
        return randBuilding;
    }

    public void setRandBuilding(Building randBuilding) {
        this.randBuilding = randBuilding;
    }

    public int[] getPrevBuild() {
        return prevBuild;
    }

    public void setPrevBuild(int[] prevBuild) {
        this.prevBuild = prevBuild;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getTilesUntillTrash() {
        return tilesUntillTrash;
    }

    public void setTilesUntillTrash(int tilesUntillTrash) {
        this.tilesUntillTrash = tilesUntillTrash;
    }

    public boolean isLeaving() {
        return leaving;
    }

    public void setLeaving(boolean leaving) {
        this.leaving = leaving;
    }

    public ComponentUI getUi() {
        return ui;
    }

    public void setUi(ComponentUI ui) {
        this.ui = ui;
    }

    public EventListenerList getListenerList() {
        return listenerList;
    }

    public void setListenerList(EventListenerList listenerList) {
        this.listenerList = listenerList;
    }

    public AccessibleContext getAccessibleContext() {
        return accessibleContext;
    }

    public void setAccessibleContext(AccessibleContext accessibleContext) {
        this.accessibleContext = accessibleContext;
    }

}
