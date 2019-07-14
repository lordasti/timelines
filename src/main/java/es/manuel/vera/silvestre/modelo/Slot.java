package es.manuel.vera.silvestre.modelo;

import lombok.Data;

@Data
public class Slot{
    private final String name;
    private final Stats stat;
    private Crew crew;

    public boolean isNotEmpty(){
        return !isEmpty();
    }

    public boolean isEmpty(){
        return crew == null;
    }
}
