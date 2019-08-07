package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.Skill;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoyageUtilTest{

    private static final Logger LOGGER = Logger.getLogger(VoyageUtilTest.class);

    @Test
    public void shouldDoDeterministicSimulation(){
        int[] numSims = {1, 5, 10, 25, 50, 75, 100, 250, 500};
        for(int i = numSims.length - 1; i > -1; i--){
            testNumSimDeterministic(numSims[i]);
        }
    }

    private void testNumSimDeterministic(int numSims){
        Skill cmd = mock(Skill.class);
        Skill dip = mock(Skill.class);
        Skill eng = mock(Skill.class);
        Skill sec = mock(Skill.class);
        Skill sci = mock(Skill.class);
        Skill med = mock(Skill.class);

        when(cmd.getAvgTotal()).thenReturn(12500);

        when(dip.getAvgTotal()).thenReturn(12500);

        when(eng.getAvgTotal()).thenReturn(2500);

        when(sec.getAvgTotal()).thenReturn(2500);

        when(sci.getAvgTotal()).thenReturn(2500);

        when(med.getAvgTotal()).thenReturn(2500);

        StopWatch watch = StopWatch.createStarted();
        int result = VoyageUtil.doDeterministicSimulation(Arrays.asList(cmd, dip, eng, sec, sci, med), 2500);
        watch.stop();
        LOGGER.debug("Estimate:" + LocalTime.ofSecondOfDay(result));

        LOGGER.info("numSims[" + numSims + "] = " + result + ". Took: " +
            watch.getTime(TimeUnit.MILLISECONDS) + " ms.");
    }
}