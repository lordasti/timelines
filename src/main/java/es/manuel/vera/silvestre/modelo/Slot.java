package es.manuel.vera.silvestre.modelo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Slot implements Comparable<Slot>{
    private final int index;
    private final Stats stat;
    private final Crew crew;

    @Override
    public int compareTo(Slot o){
        return Integer.compare(index, o.index);
    }
}
