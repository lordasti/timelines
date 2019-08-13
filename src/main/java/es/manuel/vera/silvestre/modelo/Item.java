package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"archetypeId"})
public class Item{
    protected final int type;
    protected final String symbol;
    protected final String name;
    protected final String flavor;
    protected final long archetypeId;
    protected final int stars;

    @Override
    public String toString(){
        return name + " {" + stars + "}";
    }
}
