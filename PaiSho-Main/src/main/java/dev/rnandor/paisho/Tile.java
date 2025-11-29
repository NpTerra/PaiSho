package dev.rnandor.paisho;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class Tile {

    @Getter
    private final String name;
    @Getter
    private final boolean host;

    private int locX;
    private int locY;

    public boolean isGuest() {
        return !host;
    }

    public abstract List<Position> getTiles(Table table);
}
