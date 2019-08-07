package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.BonusStats;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

//import static org.mockito.Mockito.*;

public class CombinationUtilTest{

    private static final Logger LOGGER = Logger.getLogger(CombinationUtilTest.class);

    @Test
    public void shouldGetCombinations(){
        StopWatch watch = StopWatch.createStarted();
        List<BonusStats> combinations = CombinationUtil.getCombinations();
        watch.stop();

        assertThat(combinations, hasSize(30));

        LOGGER.info(combinations);
        LOGGER
            .info("[" + combinations.size() + "] permutations. Took: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms.");
    }
}