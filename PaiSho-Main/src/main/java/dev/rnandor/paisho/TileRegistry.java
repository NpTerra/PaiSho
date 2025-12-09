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

    public TileRegistry(Class<T> tClass) throws EntryClashException {
        this(TileRegistry.class.getPackageName(), tClass);
    }

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

    public Optional<String> getCode(Class<? extends T> clazz) {
        return Optional.ofNullable(codes.get(clazz));
    }

    public Optional<Class<? extends T>> getTile(String name) {
        return Optional.ofNullable(tiles.get(name));
    }

    public List<Class<? extends T>> getTiles() {
        return new ArrayList<>(tiles.values());
    }

    public static class EntryClashException extends Exception {
        public EntryClashException(String message) {
            super(message);
        }
    }

}
