package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Character{
    private final String displayName;
    private final int level;
    private final int remainingCadetTickets;
    private final int remainingPvPTickets;
    private final List<ActiveCrew> activeCrew;
    private final List<InventoryItem> inventory;
    private final List<Ship> ships;
    private final Event event;
    private final List<Long> frozenCrewIds;

    //TODO
    //private final List<Episode> episodes; //accepted_missions + dispute_histories
    //private final List<Shuttle> shuttleMissions;
    //private final List<Faction> factions;
    //private final List<TimelinesCollection> collections;

    public Character(@JsonProperty("display_name") String displayName, @JsonProperty("level") int level,
        @JsonProperty("cadet_tickets") Map<String,Integer> cadetTickets,
        @JsonProperty("pvp_tickets") Map<String,Integer> pvpTickets, @JsonProperty("crew") List<ActiveCrew> activeCrew,
        @JsonProperty("items") List<InventoryItem> inventory, @JsonProperty("ships") List<Ship> ships,
        @JsonProperty("events") List<Event> events,
        @JsonProperty("stored_immortals") List<Map<String,Long>> frozenCrew){
        this.displayName = displayName;
        this.level = level;
        remainingCadetTickets = cadetTickets.get("current");
        remainingPvPTickets = pvpTickets.get("current");
        this.activeCrew = activeCrew;
        this.inventory = inventory;
        this.ships = ships;
        event = Optional.ofNullable(events).filter(e -> !e.isEmpty()).map(e -> e.get(0)).orElse(null);
        frozenCrewIds = frozenCrew.stream().map(f -> f.get("id")).collect(Collectors.toList());
    }
}
