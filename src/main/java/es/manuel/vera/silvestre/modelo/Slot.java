package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Slot implements Comparable<Slot>{
    private final int index;
    private final String name;
    private final Stats stat;
    private final String trait;
    private final Crew crew;

    public Slot(int index, String name, Stats stat, String trait){
        this.index = index;
        this.name = name;
        this.stat = stat;
        this.trait = trait.replace('_', ' ');
        crew = null;
    }

    @Override
    public int compareTo(Slot o){
        return Integer.compare(index, o.index);
    }

    @Override
    public String toString(){
        return "Slot(index=" + index + ", stat=" + stat + ", crew=" + crew + ")";
    }
}
