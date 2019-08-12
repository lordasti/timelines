package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveCrew extends Crew{

    public ActiveCrew(@JsonProperty("id") long id, @JsonProperty("archetype_id") long archetypeId,
        @JsonProperty("symbol") String symbol, @JsonProperty("name") String name,
        @JsonProperty("short_name") String shortName, @JsonProperty("flavor") String flavor,
        @JsonProperty("level") int level, @JsonProperty("rarity") int stars,
        @JsonProperty("max_rarity") int maxStars, @JsonProperty("active_status") int activeStatus,
        @JsonProperty("traits") List<String> traits, @JsonProperty("traits_hidden") List<String> hiddenTraits,
        @JsonProperty("equipment_slots") List<Map<String,Long>> equipmentSlots,
        @JsonProperty("equipment") List<List<Long>> equipment,
        @JsonProperty("skills") Map<String,Map<String,Integer>> skillMap, @JsonProperty("ship_battle") PvP pvp){
        super(id, archetypeId, symbol, name, shortName, flavor, level, stars, maxStars, activeStatus != 0,
            traits, hiddenTraits, getMissingEquipment(equipmentSlots, equipment),
            Stream.of(Stats.values()).map(stat -> getSkill(stat, skillMap)).collect(Collectors.toList()), pvp, false);
    }

    private static List<Long> getMissingEquipment(
        List<Map<String,Long>> equipmentSlots,
        List<List<Long>> equipment){
        if(equipment.size() == 4){
            return new ArrayList<>();
        }
        return equipmentSlots.stream().map(s -> s.get("archetype"))
            .filter(l -> equipment.stream().map(e -> e.get(1)).noneMatch(e -> e.equals(l)))
            .collect(Collectors.toList());
    }

    private static Skill getSkill(Stats stat, Map<String,Map<String,Integer>> skills){
        String key = stat.getLongName() + "_skill";

        if(!skills.containsKey(key)){
            return new Skill(stat, 0, 0, 0);
        }

        Map<String,Integer> map = skills.get(key);
        return new Skill(stat, map.get("core"), map.get("range_min"), map.get("range_max"));
    }
}
