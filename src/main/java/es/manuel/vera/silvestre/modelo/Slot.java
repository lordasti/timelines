package es.manuel.vera.silvestre.modelo;

import lombok.Data;

@Data
public class Slot implements Comparable<Slot> {
    private final String name;
    private final Stats stat;
    private Crew crew;

    @Override
    public int compareTo(Slot other) {
        return Integer.compare(crew.getTotal(), other.getCrew().getTotal());
    }
}
