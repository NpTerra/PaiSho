package dev.rnandor.paisho;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;

public final class TileRegistry {

    private HashMap<String, Class<? extends Tile>> tiles = new HashMap<>();

    public TileRegistry() throws EntryClashException {
        this("dev.rnandor.paisho");
    }

    public TileRegistry(String packageName) throws EntryClashException {
        var ref = new Reflections(packageName);
        for(var clazz : ref.getSubTypesOf(Tile.class)) {
            if(clazz.isAnonymousClass() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                continue;

            if(!clazz.isAnnotationPresent(TileEntry.class))
                continue;

            // YEAH
            String id = clazz.getAnnotation(TileEntry.class).value();
            if(tiles.containsKey(id))
                throw new EntryClashException(
                        "Tile ID clash: " + id +
                        " is already registered for " + tiles.get(id).getName() +
                        ", cannot register " + clazz.getName()
                );

            tiles.put(id, clazz);
        }
    }

    public Optional<Class<? extends Tile>> getTile(String name) {
        return Optional.ofNullable(tiles.get(name));
    }

    public static class EntryClashException extends Exception {
        public EntryClashException(String message) {
            super(message);
        }
    }

}
