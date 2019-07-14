package es.manuel.vera.silvestre.modelo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.time.LocalTime;

public class VoyageTest{

    @Test
    public void shouldCalculateDuration(){
        Voyage voyage = new Voyage(new BonusStats(Stats.COMMAND, Stats.DIPLOMACY), 2500);
        voyage.getVoyageSlots().get(0).setCrew(new Crew("cmd1", 1, 5, 5000, 5000, 0, 0, 0, 0, 0, null));
        voyage.getVoyageSlots().get(1).setCrew(new Crew("cmd2", 2, 5, 5000, 5000, 0, 0, 0, 0, 0, null));
        voyage.getVoyageSlots().get(2).setCrew(new Crew("dip1", 3, 5, 5000, 0, 5000, 0, 0, 0, 0, null));
        voyage.getVoyageSlots().get(3).setCrew(new Crew("dip2", 4, 5, 5000, 0, 5000, 0, 0, 0, 0, null));
        voyage.getVoyageSlots().get(4).setCrew(new Crew("eng1", 5, 5, 1000, 0, 0, 1000, 0, 0, 0, null));
        voyage.getVoyageSlots().get(5).setCrew(new Crew("eng2", 6, 5, 1000, 0, 0, 1000, 0, 0, 0, null));
        voyage.getVoyageSlots().get(6).setCrew(new Crew("sec1", 7, 5, 1000, 0, 0, 0, 1000, 0, 0, null));
        voyage.getVoyageSlots().get(7).setCrew(new Crew("sec2", 8, 5, 1000, 0, 0, 0, 1000, 0, 0, null));
        voyage.getVoyageSlots().get(8).setCrew(new Crew("sci1", 9, 5, 1000, 0, 0, 0, 0, 1000, 0, null));
        voyage.getVoyageSlots().get(9).setCrew(new Crew("sci2", 10, 5, 1000, 0, 0, 0, 0, 1000, 0, null));
        voyage.getVoyageSlots().get(10).setCrew(new Crew("med1", 11, 5, 1000, 0, 0, 0, 0, 0, 1000, null));
        voyage.getVoyageSlots().get(11).setCrew(new Crew("med2", 12, 5, 1000, 0, 0, 0, 0, 0, 1000, null));

        Double result = voyage.calculateDuration();
        LocalTime time = LocalTime.ofSecondOfDay(result.longValue());

        MatcherAssert.assertThat(time, Is.is(LocalTime.of(7, 6)));
    }
}