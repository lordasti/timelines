package es.manuel.vera.silvestre.modelo;

import lombok.Data;

@Data
public class Slot implements Comparable<Slot> {
    private final String name;
    private final Stats stat;
    private Crew crew;

    @Override
    public int compareTo(Slot other) {
        return Double.compare(crew.getVoyageTime(), other.getCrew().getVoyageTime());
    }

    public boolean isEmpty() {
        return crew == null;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
