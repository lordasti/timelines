package es.manuel.vera.silvestre.modelo;

import es.manuel.vera.silvestre.util.SheetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Crew{
    private final int id;
    private final String name;
    private final boolean active;
    private final int stars;
    private final List<Skill> skills;
    private final List<String> traits;

    public Crew(int id, List<Object> raw){
        this.id = id;
        Skill command = new Skill(Stats.COMMAND, raw, 60);
        Skill diplomacy = new Skill(Stats.DIPLOMACY, raw, 67);
        Skill engineering = new Skill(Stats.ENGINEERING, raw, 74);
        Skill security = new Skill(Stats.SECURITY, raw, 81);
        Skill science = new Skill(Stats.SCIENCE, raw, 88);
        Skill medicine = new Skill(Stats.MEDICINE, raw, 95);

        name = raw.size() > 0 ? (String) raw.get(0) : "";
        active = SheetUtil.readBoolean(raw, 49);
        stars = SheetUtil.readInt(raw, 50);
        skills = Arrays.asList(command, diplomacy, engineering, security, science, medicine);
        traits = SheetUtil.readList(raw, 46);
    }

    public boolean hasStars(){
        return stars > 0;
    }

    public int getGauntletScore(BonusStats bonusStats, List<String> gauntletTraits){
        double factor = 0.05D + gauntletTraits.stream().mapToDouble(trait -> traits.contains(trait) ? 0.2D : 0D).sum();
        return (int) ((getSkill(bonusStats.getPrimary()).getAvg() +
            getSkill(bonusStats.getSecondary()).getAvg()) * factor);
    }

    public Skill getSkill(Stats stat){
        return skills.get(stat.getIndex());
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof Crew && id == ((Crew) other).id;
    }

    @Override
    public String toString(){
        return name;
    }
}
