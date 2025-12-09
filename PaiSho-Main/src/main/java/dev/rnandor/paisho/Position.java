package dev.rnandor.paisho;

import lombok.*;

import java.io.Serializable;
import java.util.Comparator;

@Data
@AllArgsConstructor
public final class Position implements Comparable<Position>, Serializable {
    private int x;
    private int y;

    @Override
    public int compareTo(Position o) {
        return Comparator.comparing(Position::getX).thenComparing(Position::getY).compare(this, o);
    }
}
