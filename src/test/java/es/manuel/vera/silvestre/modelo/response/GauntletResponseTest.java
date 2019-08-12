package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.manuel.vera.silvestre.modelo.Gauntlet;
import es.manuel.vera.silvestre.modelo.Stats;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;

public class GauntletResponseTest{

    private GauntletResponse response;

    @Before
    public void setUp() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = GauntletResponseTest.class.getResourceAsStream("/gauntlet_test.json");
        response = mapper.readValue(is, GauntletResponse.class);
    }

    @Test
    public void getGauntlet(){
        Gauntlet gauntlet = response.getGauntlet();
        MatcherAssert.assertThat(gauntlet, Is.is(Matchers.notNullValue()));

        MatcherAssert.assertThat(gauntlet.getPrimary(), Is.is(Stats.DIPLOMACY));
        MatcherAssert.assertThat(gauntlet.getTraits(), Is.is(Arrays.asList("crafty", "scoundrel", "costumed")));
    }
}