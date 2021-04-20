package model.worker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
import model.Details;
import model.building.Building;

public abstract class Worker extends JPanel {

    private Details details;
    private BufferedImage image;
    private int workingSpeed;

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

    public Worker(Details details, BufferedImage image) {
        this.details = details;
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 10, 10, this);
    }

    public Details getDetails() {
        return details;
    }

    public void checkSalary() {

    }

    abstract void findPath();

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

        // Print distance
        //System.out.println("Shortest path length is: " + dist[dest]);
        // Print path
        //System.out.println("Path is ::");
        //for (int i = path.size() - 1; i >= 0; i--) {
        //    System.out.print(path.get(i) + " ");
        //}
        //path.clear();
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
}
