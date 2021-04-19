package game;

import java.awt.Image;
import java.io.IOException;
import javax.swing.JPanel;
import model.Details;
import model.ModelTile;
import model.building.Building;
import model.building.Plant;
import model.building.Restaurant;
import model.building.Ride;
import model.building.Road;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import res.ResourceLoader;

public class GameEngineTest {
    static GameEngine engine;
    
    @BeforeClass
    public static void engineConstruct() throws IOException{
        engine = new GameEngine(new JPanel(),new Road(new Details("road.png",1,1),10,"road"));
        engine.building = new Ride(new Details("target.png",2,2),15,"target_shooting");
    }
    
    /**
     * Doesnt test for out of bounds and overlapping placement
     */
    @Test
    public void testNearRoad(){
        assertFalse(engine.nearRoad(10,20));
        assertTrue(engine.nearRoad(21,10));
    }
    
    @Test
    public void testVisualizePlacing() throws IOException{
        Image image = ResourceLoader.loadImage("res/target.png");
        //Image entrance = ResourceLoader.loadImage("res/entrance.png");
        engine.visualizePlacing(21, 11, image);
        
        assertEquals(image,engine.tiles[21][11].getImage());
        //cant test this without third party tester
        //assertEquals(entrance,engine.tiles[22][12].getImage());
    }
    /*
    @Test
    public void testFindBuilding(){
        Building b = engine.building;
        ModelTile[][] m = engine.modelTiles;
        int[] i = engine.findBuilding(m, b, 2, 2);
    }*/
    
    @Test
    public void testGetParkValue(){
        engine.buildings.add(new Road(new Details("road.png",1,1),10,"road"));
        engine.buildings.add(new Ride(new Details("roller.png",2,2),300,"roller_coaster"));
        engine.buildings.add(new Ride(new Details("dojo.png",1,2),200,"dojo"));
        engine.buildings.add(new Road(new Details("road.png",1,1),10,"road"));
        engine.buildings.add(new Road(new Details("road.png",1,1),10,"road"));
        engine.buildings.add(new Ride(new Details("caro.png",2,1),200,"carousel"));
        engine.buildings.add(new Ride(new Details("target.png",2,2),150,"target_shooting"));
        engine.buildings.add(new Restaurant(new Details("buffet.png",1,1),100,"buffet"));
        engine.buildings.add(new Plant(new Details("bush.png",1,1),15,"bush",5));
        engine.buildings.add(new Plant(new Details("bush.png",1,1),15,"bush",5));
        
        assertEquals(1020,engine.getParkValue());
        
        engine.buildings.set(4,null);
        engine.buildings.set(5,null);
        
        assertEquals(1000,engine.getParkValue());
    }
}
