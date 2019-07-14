package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Skill{
    private final Stats stat;
    private final int base;
    private final int min;
    private final int max;
    private final int avg;
    private final int avgTotal;

    public Skill(Stats stat, List<Object> raw, int initialPos){
        this.stat = stat;
        base = readFromRaw(raw, initialPos);
        min = readFromRaw(raw, initialPos + 1);
        max = readFromRaw(raw, initialPos + 2);
        avg = readFromRaw(raw, initialPos + 3);
        avgTotal = readFromRaw(raw, initialPos + 4);
    }

    private int readFromRaw(List<Object> raw, int pos){
        if(raw.size() <= pos){
            return 0;
        }

        String value = StringUtils.remove((String) raw.get(pos), ',');
        return NumberUtils.isCreatable(value) ? NumberUtils.createInteger(value) : 0;
    }

    public Skill sum(Skill other){
        return new Skill(stat, base + other.base, min + other.min, max + other.max,
            avg + other.avg, avgTotal + other.avgTotal);
    }
}
