package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"archetypeId"})
public class Item{
    private final int type;
    private final String symbol;
    private final String name;
    private final String flavor;
    private final long archetypeId;
    private final int stars;
}
