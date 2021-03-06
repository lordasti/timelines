package es.manuel.vera.silvestre.util.google;

import com.google.api.client.auth.oauth2.Credential;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;

public class GoogleAuthorizeUtilTest{

    @Test
    public void shouldAuthorize() throws IOException{
        Credential credential = GoogleAuthorizeUtil.authorizeWithServiceCredential();
        MatcherAssert.assertThat(credential, Is.is((CoreMatchers.notNullValue())));
    }
}
