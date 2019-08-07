package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.Stats;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.*;

public class PermutationUtilTest{

    private static final Logger LOGGER = Logger.getLogger(PermutationUtilTest.class);

    @Test
    public void shouldDoPermutation(){
        List<Stats> stats = Arrays.asList(Stats.values());

        StopWatch watch = StopWatch.createStarted();
        List<List<Stats>> permutations = PermutationUtil.of(stats);
        watch.stop();

        assertThat(permutations, hasSize(720));

        LOGGER
            .info("[" + permutations.size() + "] permutations. Took: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms.");
    }
}