package game;

import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import model.Details;
import model.ModelTile;
import model.Tile;
import model.building.Building;
import model.building.Plant;
import model.building.Restaurant;
import model.building.Ride;
import model.building.Road;
import model.worker.Worker;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import res.ResourceLoader;

public class GameEngineTest {

    static GameEngine engine;

    @BeforeClass
    public static void engineConstruct() throws IOException {
        Building building = new Road(new Details("road.png", 1, 1), 10, "road");
        engine = new GameEngine(new JPanel(), building);
        engine.building = new Ride(new Details("target.png", 2, 2), 15, "target_shooting");
    }


    @Test
    public void testNearRoad() {
        assertFalse(engine.nearRoad(10, 20));
        assertTrue(engine.nearRoad(21, 10));
    }

    @Test
    public void testVisualizePlacing() throws IOException {
        Image image = ResourceLoader.loadImage("res/target.png");
        //Image entrance = ResourceLoader.loadImage("res/entrance.png");
        engine.visualizePlacing(21, 11, image);

        assertEquals(image, engine.tiles[21][11].getImage());
        //assertEquals(entrance,engine.tiles[22][12].getImage());
    }

    @Test
    public void testGetParkValue() {
        engine.buildings.add(new Road(new Details("road.png", 1, 1), 10, "road"));
        engine.buildings.add(new Ride(new Details("roller.png", 2, 2), 300, "roller_coaster"));
        engine.buildings.add(new Ride(new Details("dojo.png", 1, 2), 200, "dojo"));
        engine.buildings.add(new Road(new Details("road.png", 1, 1), 10, "road"));
        engine.buildings.add(new Road(new Details("road.png", 1, 1), 10, "road"));
        engine.buildings.add(new Ride(new Details("caro.png", 2, 1), 200, "carousel"));
        engine.buildings.add(new Ride(new Details("target.png", 2, 2), 150, "target_shooting"));
        engine.buildings.add(new Restaurant(new Details("buffet.png", 1, 1), 100, "buffet"));
        engine.buildings.add(new Plant(new Details("bush.png", 1, 1), 15, "bush", 5));
        engine.buildings.add(new Plant(new Details("bush.png", 1, 1), 15, "bush", 5));

        assertEquals(1020, engine.getParkValue());

        engine.buildings.set(4, null);
        engine.buildings.set(5, null);

        assertEquals(1000, engine.getParkValue());
    }
    
    @Test
    public void testAddCleaner() throws IOException{
        engine.newCleaner();
        assertEquals(engine.getWorkers().size(),1);
    }
    
    @Test
    public void testFireCleaner1() throws IOException{
        engine.newCleaner();
        engine.fireCleaner();
        assertEquals(engine.getWorkers().size(),0);
    }
    
    @Test
    public void testFireCleaner2() throws IOException{
        engine.fireCleaner();
        assertEquals(engine.getWorkers().size(),0);
    }
   
    @Test
    public void testInitBuilding() throws IOException{
        ArrayList buildings = new ArrayList<>();
        ModelTile[][] modelTiles = new ModelTile[25][25];
        Tile tiles[][] = new Tile[25][25];
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (i == 22 && j == 12) {
                    buildings.add(new Building(new Details("test.png",0,0), 10, "test") {});
                    modelTiles[i][j] = new ModelTile("road");
                    modelTiles[i][j].setIndex(0);
                    ArrayList<Integer> newNode = new ArrayList<>();
                    newNode.add(i);
                    newNode.add(j);

                } else {
                    modelTiles[i][j] = new ModelTile("grass");
                    tiles[i][j] = new Tile(ResourceLoader.loadImage("res/Grass.png"));
                }
            }
        }
        assertEquals(buildings.size(),1);
    }
    
}
