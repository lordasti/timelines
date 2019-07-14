package es.manuel.vera.silvestre.util.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class GoogleAuthorizeUtil{

    public static Credential authorizeWithServiceCredential() throws IOException{

        InputStream is = GoogleAuthorizeUtil.class
            .getResourceAsStream("/sheets-service-account.json");
        return GoogleCredential.fromStream(is)
                               .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }
}
