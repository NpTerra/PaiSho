package dev.rnandor.paisho.ui;

import dev.rnandor.paisho.Main;

import java.awt.image.BufferedImage;
import java.net.URL;

public final class ResourceHelper {

    private ResourceHelper() {}

    /**
     * Loads an image resource from the given path.
     * The path should be relative to the classpath.
     *
     * @param path the path to the image resource
     * @return the loaded BufferedImage
     * @throws IllegalArgumentException if the resource is not found or cannot be read
     */
    public static URL getResource(String path) {
        return Main.class.getResource(path);
    }
}
