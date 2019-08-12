package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.manuel.vera.silvestre.modelo.Slot;
import es.manuel.vera.silvestre.modelo.Stats;
import es.manuel.vera.silvestre.modelo.Voyage;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class VoyageResponseTest{

    private VoyageResponse response;

    @Before
    public void setUp() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = PlayerResponseTest.class.getResourceAsStream("/voyage_test.json");
        response = mapper.readValue(is, VoyageResponse.class);
    }

    @Test
    public void getVoyage(){
        Voyage voyage = response.getVoyage();

        MatcherAssert.assertThat(voyage, Is.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(voyage.getShipTrait(), Is.is("maquis"));
        MatcherAssert.assertThat(voyage.getVoyageEstimate(), Is.is(0));
        MatcherAssert.assertThat(voyage.getBonusStats().getPrimary(), Is.is(Stats.COMMAND));
        MatcherAssert.assertThat(voyage.getBonusStats().getSecondary(), Is.is(Stats.SCIENCE));
        testSlot(voyage.getSlots().get(0), 0, "First Officer", Stats.COMMAND, "jury rigger");
        testSlot(voyage.getSlots().get(1), 1, "Helm Officer", Stats.COMMAND, "jury rigger");
        testSlot(voyage.getSlots().get(2), 2, "Communications Officer", Stats.DIPLOMACY, "inspiring");
        testSlot(voyage.getSlots().get(3), 3, "Diplomat", Stats.DIPLOMACY, "klingon");
        testSlot(voyage.getSlots().get(4), 4, "Chief Security Officer", Stats.SECURITY, "communicator");
        testSlot(voyage.getSlots().get(5), 5, "Tactical Officer", Stats.SECURITY, "villain");
        testSlot(voyage.getSlots().get(6), 6, "Chief Engineer", Stats.ENGINEERING, "romantic");
        testSlot(voyage.getSlots().get(7), 7, "Engineer", Stats.ENGINEERING, "brutal");
        testSlot(voyage.getSlots().get(8), 8, "Chief Science Officer", Stats.SCIENCE, "innovator");
        testSlot(voyage.getSlots().get(9), 9, "Deputy Science Officer", Stats.SCIENCE, "federation");
        testSlot(voyage.getSlots().get(10), 10, "Chief Medical Officer", Stats.MEDICINE, "hologram");
        testSlot(voyage.getSlots().get(11), 11, "Ship's Counselor", Stats.MEDICINE, "federation");
    }

    private void testSlot(Slot slot, int expectedIndex, String expectedName, Stats expectedStat, String expectedTrait){
        MatcherAssert.assertThat(slot.getIndex(), Is.is(expectedIndex));
        MatcherAssert.assertThat(slot.getName(), Is.is(expectedName));
        MatcherAssert.assertThat(slot.getStat(), Is.is(expectedStat));
        MatcherAssert.assertThat(slot.getTrait(), Is.is(expectedTrait));
        MatcherAssert.assertThat(slot.getCrew(), Is.is(CoreMatchers.nullValue()));
    }
}