package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.manuel.vera.silvestre.modelo.Character;
import es.manuel.vera.silvestre.modelo.*;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerResponseTest{

    private PlayerResponse response;

    @Before
    public void setUp() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = PlayerResponseTest.class.getResourceAsStream("/player_test.json");
        response = mapper.readValue(is, PlayerResponse.class);
    }

    @Test
    public void getPlayer(){
        Player player = response.getPlayer();

        MatcherAssert.assertThat(player, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(player.getDbid(), Is.is(900237943844864L));
        MatcherAssert.assertThat(player.getMoney(), Is.is(11376331L));
        MatcherAssert.assertThat(player.getHonor(), Is.is(46488));
        MatcherAssert.assertThat(player.getShuttleRentalTokens(), Is.is(29));
        MatcherAssert.assertThat(player.getVipPoints(), Is.is(6000));
        MatcherAssert.assertThat(player.getVipLevel(), Is.is(8));
        MatcherAssert.assertThat(player.getReplicatorUsesToday(), Is.is(0));
        MatcherAssert.assertThat(player.getReplicatorLimit(), Is.is(6));

        Character character = player.getCharacter();
        MatcherAssert.assertThat(character, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(character.getDisplayName(), Is.is("Lord Asti"));
        MatcherAssert.assertThat(character.getLevel(), Is.is(74));
        MatcherAssert.assertThat(character.getRemainingCadetTickets(), Is.is(0));
        MatcherAssert.assertThat(character.getRemainingPvPTickets(), Is.is(0));

        List<ActiveCrew> activeCrew = character.getActiveCrew();
        MatcherAssert.assertThat(activeCrew, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(activeCrew, Matchers.hasSize(206));

        ActiveCrew crew = activeCrew.get(16);
        MatcherAssert.assertThat(crew.getId(), Is.is(292170991L));
        MatcherAssert.assertThat(crew.getSymbol(), Is.is("chekov_cowboy_crew"));
        MatcherAssert.assertThat(crew.getName(), Is.is("Claiborne Chekov"));
        MatcherAssert.assertThat(crew.getShortName(), Is.is("Chekov"));
        MatcherAssert.assertThat(crew.getFlavor(), Is.is(""));
        MatcherAssert.assertThat(crew.getLevel(), Is.is(50));
        MatcherAssert.assertThat(crew.getStars(), Is.is(2));
        MatcherAssert.assertThat(crew.getMaxStars(), Is.is(4));
        MatcherAssert.assertThat(crew.isActive(), Is.is(false));
        MatcherAssert.assertThat(crew.getTraits(), Is.is(Arrays.asList("federation", "starfleet", "human", "costumed",
            "duelist")));
        MatcherAssert.assertThat(crew.getHiddenTraits(), Is.is(Arrays.asList("male", "organic", "tos", "ensign",
            "bridge crew", "chekov", "crew max rarity 4")));
        MatcherAssert.assertThat(crew.getMissingEquipment(), Is.is(Collections.singletonList(818L)));
        MatcherAssert.assertThat(crew.getSkills(), Is.is(Arrays.asList(
            new Skill(Stats.COMMAND, 109, 18, 40),
            new Skill(Stats.DIPLOMACY, 0, 0, 0),
            new Skill(Stats.SECURITY, 189, 49, 131),
            new Skill(Stats.ENGINEERING, 0, 0, 0),
            new Skill(Stats.SCIENCE, 150, 12, 29),
            new Skill(Stats.MEDICINE, 0, 0, 0))));

        PvP pvp = crew.getPvp();
        MatcherAssert.assertThat(pvp, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(pvp.getAccuracy(), Is.is(105));
        MatcherAssert.assertThat(pvp.getEvasion(), Is.is(25));
        MatcherAssert.assertThat(pvp.getCriticalChance(), Is.is(25));
        MatcherAssert.assertThat(pvp.getCriticalBonus(), Is.is(55));

        List<InventoryItem> inventory = character.getInventory();
        MatcherAssert.assertThat(inventory, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inventory, Matchers.hasSize(882));

        InventoryItem item = inventory.get(0);
        MatcherAssert.assertThat(item, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(item.getType(), Is.is(3));
        MatcherAssert.assertThat(item.getSymbol(), Is.is("casing_compon"));
        MatcherAssert.assertThat(item.getName(), Is.is("Casing"));
        MatcherAssert.assertThat(item.getFlavor(), Is.is(""));
        MatcherAssert.assertThat(item.getArchetypeId(), Is.is(158L));
        MatcherAssert.assertThat(item.getQuantity(), Is.is(3));
        MatcherAssert.assertThat(item.getStars(), Is.is(0));

        List<Ship> ships = character.getShips();
        MatcherAssert.assertThat(ships, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(ships, Matchers.hasSize(54));

        Ship ship = ships.get(0);
        MatcherAssert.assertThat(ship, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(ship.getArchetypeId(), Is.is(2817L));
        MatcherAssert.assertThat(ship.getSymbol(), Is.is("enterprise_d_ship"));
        MatcherAssert.assertThat(ship.getName(), Is.is("U.S.S. Enterprise NCC-1701-D"));
        MatcherAssert.assertThat(ship.getStars(), Is.is(4));
        MatcherAssert.assertThat(ship.getFlavor(), Is.is(
            "A Galaxy-class vessel famously commanded by Captain Picard. Allows for saucer separation in emergency " +
                "situations."));
        MatcherAssert.assertThat(ship.getMaxLevel(), Is.is(9));
        MatcherAssert.assertThat(ship.getShields(), Is.is(160000));
        MatcherAssert.assertThat(ship.getHull(), Is.is(328750));
        MatcherAssert.assertThat(ship.getAttack(), Is.is(8150));
        MatcherAssert.assertThat(ship.getEvasion(), Is.is(7580));
        MatcherAssert.assertThat(ship.getAccuracy(), Is.is(9300));
        MatcherAssert.assertThat(ship.getCriticalChance(), Is.is(500));
        MatcherAssert.assertThat(ship.getCriticalBonus(), Is.is(5000));
        MatcherAssert.assertThat(ship.getAttacksPerSecond(), Is.is(0.88f));
        MatcherAssert.assertThat(ship.getShieldRegen(), Is.is(2650));
        MatcherAssert.assertThat(ship.getTraits(), Is.is(Arrays.asList("federation", "explorer", "war_veteran")));
        MatcherAssert.assertThat(ship.getHiddenTraits(), Is.is(Collections.emptyList()));
        MatcherAssert.assertThat(ship.getAntimatter(), Is.is(2250));
        MatcherAssert.assertThat(ship.getLevel(), Is.is(9));

        Event event = character.getEvent();
        MatcherAssert.assertThat(event, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(event.getName(), Is.is("Storm Warning"));
        MatcherAssert.assertThat(event.getFeaturedCrewSymbols(), Is.is(Arrays.asList("q2_crew", "riker_q_crew",
            "dsc_airiam_ltcmdr_crew")));

        List<Long> frozenCrewIds = character.getFrozenCrewIds();
        MatcherAssert.assertThat(frozenCrewIds, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(frozenCrewIds, Matchers.hasSize(286));
    }

    @Test
    public void getRecipes(){
        List<ArchetypeItem> recipes = response.getRecipes();

        MatcherAssert.assertThat(recipes, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(recipes, Matchers.hasSize(1442));

        ArchetypeItem itemWithRecipe = recipes.get(0);
        MatcherAssert.assertThat(itemWithRecipe.getArchetypeId(), Is.is(6624L));
        MatcherAssert.assertThat(itemWithRecipe.getSymbol(), Is.is("sarek_young_robes_quality4_equip"));
        MatcherAssert.assertThat(itemWithRecipe.getType(), Is.is(2));
        MatcherAssert.assertThat(itemWithRecipe.getName(), Is.is("Sarek's Robes"));
        MatcherAssert.assertThat(itemWithRecipe.getFlavor(), Is.is(""));
        MatcherAssert.assertThat(itemWithRecipe.getStars(), Is.is(4));
        MatcherAssert.assertThat(itemWithRecipe.getSources(), Is.is(Collections.emptyList()));

        Recipe recipe = itemWithRecipe.getRecipe();
        MatcherAssert.assertThat(recipe, Is.is(Matchers.notNullValue()));

        List<Demand> demands = recipe.getDemands();
        MatcherAssert.assertThat(demands, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(demands, Matchers.hasSize(3));

        Demand demand = demands.get(0);
        MatcherAssert.assertThat(demand.getArchetypeId(), Is.is(6623L));
        MatcherAssert.assertThat(demand.getCount(), Is.is(2));

        ArchetypeItem itemWithSorces = recipes.get(2);
        MatcherAssert.assertThat(itemWithSorces.getArchetypeId(), Is.is(270L));
        MatcherAssert.assertThat(itemWithSorces.getSymbol(), Is.is("clothing_pattern_quality4_compon"));
        MatcherAssert.assertThat(itemWithSorces.getType(), Is.is(3));
        MatcherAssert.assertThat(itemWithSorces.getName(), Is.is("Clothing Pattern"));
        MatcherAssert.assertThat(itemWithSorces.getFlavor(), Is.is(""));
        MatcherAssert.assertThat(itemWithSorces.getStars(), Is.is(4));
        MatcherAssert.assertThat(itemWithSorces.getRecipe(), Is.is(CoreMatchers.nullValue()));

        List<Source> sources = itemWithSorces.getSources();
        MatcherAssert.assertThat(sources, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(sources, Matchers.hasSize(25));

        Source source = sources.get(0);
        MatcherAssert.assertThat(source.getType(), Is.is(0));
        MatcherAssert.assertThat(source.getId(), Is.is(179L));
        MatcherAssert.assertThat(source.getName(), Is.is("Deadly Disruptions"));
        MatcherAssert.assertThat(source.getEnergyQuotient(), Is.is(0.0043731778425656));
        MatcherAssert.assertThat(source.getChanceGrade(), Is.is(1));
        MatcherAssert.assertThat(source.getMission(), Is.is(15));
        MatcherAssert.assertThat(source.getDispute(), Is.is(2));
        MatcherAssert.assertThat(source.getMastery(), Is.is(1));
    }
}