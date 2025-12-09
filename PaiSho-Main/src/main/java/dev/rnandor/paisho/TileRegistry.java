package dev.rnandor.paisho;

import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class TileRegistry<T extends Tile> implements Serializable {

    private HashMap<String, Class<? extends T>> tiles = new HashMap<>();
    private HashMap<Class<? extends T>, String> codes = new HashMap<>();

    /**
     * Constructs a TileRegistry by scanning the default package for tile classes.
     *
     * @param tClass The base class of the tiles.
     * @throws EntryClashException If there is a clash in tile entry IDs.
     */
    public TileRegistry(Class<T> tClass) throws EntryClashException {
        this(TileRegistry.class.getPackageName(), tClass);
    }

    /**
     * Constructs a TileRegistry by scanning the specified package for tile classes.
     *
     * @param packageName The package to scan for tile classes.
     * @param tClass      The base class of the tiles.
     * @throws EntryClashException If there is a clash in tile entry IDs.
     */
    public TileRegistry(String packageName, Class<T> tClass) throws EntryClashException {
        var ref = new Reflections(packageName);
        for(var clazz : ref.getSubTypesOf(tClass)) {
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
            codes.put(clazz, id);
        }
    }

    /**
     * Gets the code associated with the given tile class.
     *
     * @param clazz The tile class.
     * @return An Optional containing the code if found, otherwise an empty Optional.
     */
    public Optional<String> getCode(Class<? extends T> clazz) {
        return Optional.ofNullable(codes.get(clazz));
    }

    /**
     * Gets the tile class associated with the given name.
     *
     * @param name The name of the tile.
     * @return An Optional containing the tile class if found, otherwise an empty Optional.
     */
    public Optional<Class<? extends T>> getTile(String name) {
        return Optional.ofNullable(tiles.get(name));
    }

    /**
     * Gets a list of all registered tile classes.
     *
     * @return A list of all registered tile classes.
     */
    public List<Class<? extends T>> getTiles() {
        return new ArrayList<>(tiles.values());
    }

    /**
     * Exception thrown when there is a clash in tile entry IDs.
     */
    public static class EntryClashException extends Exception {
        public EntryClashException(String message) {
            super(message);
        }
    }

}
