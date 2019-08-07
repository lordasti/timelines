package es.manuel.vera.silvestre.modelo;

import lombok.Data;

import java.util.List;

@Data
public class Gauntlet{
    private final Stats primary;
    private final List<String> traits;
    private final List<Slot> slots;

    public int getScore(){
        return slots.stream().map(Slot::getCrew).mapToInt(crew -> crew.getGauntletScore(traits)).sum();
    }

    @Override
    public String toString(){
        getSlots().sort(Slot::compareTo);
        return getSlots().toString();
    }
}
