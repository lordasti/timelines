package es.manuel.vera.silvestre.modelo;

import lombok.Data;

import java.util.List;

@Data
public class Gauntlet{
    private final Stats primary;
    private final List<String> traits;
    private final List<Slot> slots;

    public int getScore(){
        return slots.stream().map(Slot::getCrew).mapToInt(Crew::getGauntletScore).sum();
    }

    @Override
    public String toString(){
        return slots.toString();
    }
}
