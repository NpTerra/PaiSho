package dev.rnandor.paisho.ui.panels.tiles;

import dev.rnandor.paisho.TileRegistry;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import dev.rnandor.paisho.ui.ResourceHelper;
import dev.rnandor.paisho.ui.panels.ImagePanel;

import java.io.IOException;

public abstract class AbstractTilePanel<T extends GinsengTile> extends ImagePanel {
     AbstractTilePanel(Class<T> clazz, boolean host, TileRegistry<GinsengTile> registry) throws IOException {
        super(ResourceHelper.getResource((host ? "/tiles/host/" : "/tiles/guest/") + registry.getCode(clazz) + ".png"));

        setSize(34, 34);
    }
}
