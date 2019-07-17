package es.manuel.vera.silvestre.modelo;

import lombok.Data;

@Data
public class BonusStats{
    private final Stats primary;
    private final Stats secondary;

    @Override
    public String toString(){
        return "[" + primary + ":" + secondary + "]";
    }
}
