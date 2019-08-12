package es.manuel.vera.silvestre.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetCrew extends Crew{

    public SheetCrew(List<Object> raw){
        super(0, 0, null, raw.size() > 0 ? (String) raw.get(0) : "", null, null, 0, 0,
            0, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            Arrays.asList(new Skill(Stats.COMMAND, raw, 60), new Skill(Stats.DIPLOMACY, raw, 67),
                new Skill(Stats.SECURITY, raw, 81), new Skill(Stats.ENGINEERING, raw, 74),
                new Skill(Stats.SCIENCE, raw, 88), new Skill(Stats.MEDICINE, raw, 95)), null, false);
    }
}
