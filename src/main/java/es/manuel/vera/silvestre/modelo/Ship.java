package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"archetypeId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ship{
    private final long archetypeId;
    private final String symbol;
    private final String name;
    private final int stars;
    private final String flavor;
    private final int maxLevel;
    private final int shields;
    private final int hull;
    private final int attack;
    private final int evasion;
    private final int accuracy;
    private final int criticalChance;
    private final int criticalBonus;
    private final float attacksPerSecond;
    private final int shieldRegen;
    private final List<String> traits;
    private final List<String> hiddenTraits;
    private final int antimatter;
    private final int level;

    public Ship(@JsonProperty("archetype_id") long archetypeId, @JsonProperty("symbol") String symbol,
        @JsonProperty("name") String name, @JsonProperty("rarity") int stars, @JsonProperty("flavor") String flavor,
        @JsonProperty("max_level") int maxLevel, @JsonProperty("shields") int shields, @JsonProperty("hull") int hull,
        @JsonProperty("attack") int attack, @JsonProperty("evasion") int evasion,
        @JsonProperty("accuracy") int accuracy, @JsonProperty("crit_chance") int criticalChance,
        @JsonProperty("crit_bonus") int criticalBonus, @JsonProperty("attacks_per_second") float attacksPerSecond,
        @JsonProperty("shield_regen") int shieldRegen, @JsonProperty("traits") List<String> traits,
        @JsonProperty("traits_hidden") List<String> hiddenTraits, @JsonProperty("antimatter") int antimatter,
        @JsonProperty("level") int level){
        this.archetypeId = archetypeId;
        this.symbol = symbol;
        this.name = name;
        this.stars = stars;
        this.flavor = flavor;
        this.maxLevel = maxLevel + 1;
        this.shields = shields;
        this.hull = hull;
        this.attack = attack;
        this.evasion = evasion;
        this.accuracy = accuracy;
        this.criticalChance = criticalChance;
        this.criticalBonus = criticalBonus;
        this.attacksPerSecond = attacksPerSecond;
        this.shieldRegen = shieldRegen;
        this.traits = traits;
        this.hiddenTraits = hiddenTraits;
        this.antimatter = antimatter;
        this.level = level + 1;

        getTraits().replaceAll(t -> t.replace('_', ' '));
        getHiddenTraits().replaceAll(t -> t.replace('_', ' '));
    }
}
