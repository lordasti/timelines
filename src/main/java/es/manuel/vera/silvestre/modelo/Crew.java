package es.manuel.vera.silvestre.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Crew{
    private final String name;
    private final boolean active;
    private final int stars;
    private final List<Skill> skills;

    public Crew(List<Object> raw){
        name = raw.size() > 0 ? (String) raw.get(0) : "";
        active = raw.size() > 49 ? BooleanUtils.toBoolean((String) raw.get(49)) : false;
        stars = raw.size() > 50 ? NumberUtils.isCreatable((String) raw.get(50)) ?
            NumberUtils.createInteger((String) raw.get(50)) : 0 : 0;
        Skill command = new Skill(Stats.COMMAND, raw, 60);
        Skill diplomacy = new Skill(Stats.DIPLOMACY, raw, 67);
        Skill engineering = new Skill(Stats.ENGINEERING, raw, 74);
        Skill security = new Skill(Stats.SECURITY, raw, 81);
        Skill science = new Skill(Stats.SCIENCE, raw, 88);
        Skill medicine = new Skill(Stats.MEDICINE, raw, 95);
        skills = Arrays.asList(command, diplomacy, engineering, security, science, medicine);
    }

    public boolean hasStars(){
        return stars > 0;
    }

    public Skill getSkill(Stats stat){
        return getSkillStream(stat).findAny().orElse(null);
    }

    private Stream<Skill> getSkillStream(Stats stat){
        return skills.stream()
            .filter(skill -> stat == skill.getStat());
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof Crew){
            return name.equals(((Crew) other).name);
        }

        return false;
    }

    @Override
    public String toString(){
        return name;
    }
}
