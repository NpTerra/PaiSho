package dev.rnandor.paisho.ui;

import dev.rnandor.paisho.Main;

import java.awt.image.BufferedImage;
import java.net.URL;

public final class ResourceHelper {

    private ResourceHelper() {}

    public static URL getResource(String path) {
        return Main.class.getResource(path);
    }
}
