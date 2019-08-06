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

    @Test
    public void shouldDoSimulation(){
        int[] numSims = {1, 5, 10, 25, 50, 75, 100, 250, 500};
        for(int i = numSims.length - 1; i > -1; i--){
            testNumSim(numSims[i]);
        }
    }

    private void testNumSim(int numSims){
        Skill cmd = mock(Skill.class);
        Skill dip = mock(Skill.class);
        Skill eng = mock(Skill.class);
        Skill sec = mock(Skill.class);
        Skill sci = mock(Skill.class);
        Skill med = mock(Skill.class);

        when(cmd.getBase()).thenReturn(12500);
        when(cmd.getMin()).thenReturn(2500);
        when(cmd.getMax()).thenReturn(7500);

        when(dip.getBase()).thenReturn(12500);
        when(dip.getMin()).thenReturn(2500);
        when(dip.getMax()).thenReturn(7500);

        when(eng.getBase()).thenReturn(2500);
        when(eng.getMin()).thenReturn(250);
        when(eng.getMax()).thenReturn(750);

        when(sec.getBase()).thenReturn(2500);
        when(sec.getMin()).thenReturn(250);
        when(sec.getMax()).thenReturn(750);

        when(sci.getBase()).thenReturn(2500);
        when(sci.getMin()).thenReturn(250);
        when(sci.getMax()).thenReturn(750);

        when(med.getBase()).thenReturn(2500);
        when(med.getMin()).thenReturn(250);
        when(med.getMax()).thenReturn(750);

        StopWatch watch = StopWatch.createStarted();
        Double result = VoyageUtil.doNumSim(Arrays.asList(cmd, dip, eng, sec, sci, med), numSims, 2500);
        watch.stop();

        LOGGER.info("numSims[" + numSims + "] = " + result + ". Took: " +
            watch.getTime(TimeUnit.MILLISECONDS) + " ms.");
    }
}