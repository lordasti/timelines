package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.manuel.vera.silvestre.modelo.AvatarCrew;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrewAvatarsResponseTest{

    private CrewAvatarsResponse response;

    @Before
    public void setUp() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = CrewAvatarsResponseTest.class.getResourceAsStream("/get_avatars_test.json");
        response = mapper.readValue(is, CrewAvatarsResponse.class);
    }

    @Test
    public void getAvatars(){
        List<AvatarCrew> avatars = response.getAvatars();
        MatcherAssert.assertThat(avatars, Is.is(Matchers.notNullValue()));

        AvatarCrew crew = avatars.get(0);
        MatcherAssert.assertThat(crew.getId(), Is.is(9998L));
        MatcherAssert.assertThat(crew.getSymbol(), Is.is("federation_president_crew"));
        MatcherAssert.assertThat(crew.getName(), Is.is("UFP President Ra-ghoratreii"));
        MatcherAssert.assertThat(crew.getShortName(), Is.is("Ra-ghoratreii"));
        MatcherAssert.assertThat(crew.getFlavor(), Is.is(""));
        MatcherAssert.assertThat(crew.getLevel(), Is.is(0));
        MatcherAssert.assertThat(crew.getStars(), Is.is(0));
        MatcherAssert.assertThat(crew.getMaxStars(), Is.is(5));
        MatcherAssert.assertThat(crew.isActive(), Is.is(false));
        MatcherAssert.assertThat(crew.getTraits(), Is.is(Arrays.asList("efrosian", "cultural figure", "federation",
            "president", "diplomat")));
        MatcherAssert.assertThat(crew.getHiddenTraits(), Is.is(Arrays.asList("nonhuman", "male", "organic", "tos",
            "federation president", "crew max rarity 5")));
        MatcherAssert.assertThat(crew.getMissingEquipment(), Is.is(Collections.emptyList()));
        MatcherAssert.assertThat(crew.getSkills(), Is.is(Collections.emptyList()));
    }
}