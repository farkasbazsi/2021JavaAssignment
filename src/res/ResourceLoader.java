/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package res;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class ResourceLoader {

    /**
     * Helps with the loading of resources
     *
     * @param resName
     * @return
     */
    public static InputStream loadResource(String resName) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resName);
    }

    /**
     * Helps with the loading of images
     *
     * @param resName
     * @return
     * @throws IOException
     */
    public static Image loadImage(String resName) throws IOException {
        URL url = ResourceLoader.class.getClassLoader().getResource(resName);
        return ImageIO.read(url);
    }
}
