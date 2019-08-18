package es.manuel.vera.silvestre.modelo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"name"})
public class Crew implements Comparable<Crew>{
    private final long id;
    private final long archetypeId;
    private final String symbol;
    private final String name;
    private final String shortName;
    private final String flavor;
    private final int level;
    private final int stars;
    private final int maxStars;
    private final boolean active;
    private final List<String> traits;
    private final List<String> hiddenTraits;
    private final List<Long> missingEquipment;
    private final List<Skill> skills;
    private final PvP pvp;
    private final boolean frozen;

    public Crew(long id, long archetypeId, String symbol, String name, String shortName, String flavor, int level,
        int stars, int maxStars, boolean active, List<String> traits, List<String> hiddenTraits,
        List<Long> missingEquipment, List<Skill> skills, PvP pvp, boolean frozen){
        this.id = id;
        this.archetypeId = archetypeId;
        this.symbol = symbol;
        this.name = name;
        this.shortName = shortName;
        this.flavor = flavor;
        this.level = level;
        this.stars = stars;
        this.maxStars = maxStars;
        this.active = active;
        this.traits = traits;
        this.hiddenTraits = hiddenTraits;
        this.missingEquipment = missingEquipment;
        this.skills = skills;
        this.pvp = pvp;
        this.frozen = frozen;

        getTraits().replaceAll(t -> t.replace('_', ' '));
        getHiddenTraits().replaceAll(t -> t.replace('_', ' '));
    }

    public int getGauntletPairScore(BonusStats bonusStats, List<String> gauntletTraits){
        double factor =
            0.05D + gauntletTraits.stream().mapToDouble(trait -> traits.contains(trait) ? 0.2D : 0D).sum();
        return (int) ((getSkill(bonusStats.getPrimary()).getAvg() +
            getSkill(bonusStats.getSecondary()).getAvg()) * (1 + factor));
    }

    public Skill getSkill(Stats stat){
        return skills.get(stat.getIndex());
    }

    public int getGauntletScore(List<String> gauntletTraits){
        double factor =
            1 + (0.05D + gauntletTraits.stream().mapToDouble(trait -> traits.contains(trait) ? 0.2D : 0D).sum());
        return (int) skills.stream().mapToInt(Skill::getAvg).mapToDouble(avg -> avg * factor).sum();
    }

    @Override
    public int compareTo(Crew o){
        return name.compareTo(o.getName());
    }

    @Override
    public String toString(){
        return name + " " + Stream.of(Stats.values()).map(stat -> "" + getSkill(stat).getAvgTotal()).collect(
            Collectors.joining(" "));
    }
}
