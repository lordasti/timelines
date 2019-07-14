package es.manuel.vera.silvestre.modelo;

import es.manuel.vera.silvestre.util.SheetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Skill{
    private final Stats stat;
    private final int base;
    private final int min;
    private final int max;
    private final int avg;
    private final int avgTotal;

    public Skill(Stats stat, List<Object> raw, int initialPos){
        this.stat = stat;
        base = SheetUtil.readInt(raw, initialPos);
        min = SheetUtil.readInt(raw, initialPos + 1);
        max = SheetUtil.readInt(raw, initialPos + 2);
        avg = SheetUtil.readInt(raw, initialPos + 3);
        avgTotal = SheetUtil.readInt(raw, initialPos + 4);
    }

    public Skill sum(Skill other){
        return new Skill(stat, base + other.base, min + other.min, max + other.max,
            avg + other.avg, avgTotal + other.avgTotal);
    }
}
