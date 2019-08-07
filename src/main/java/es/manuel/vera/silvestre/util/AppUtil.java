package es.manuel.vera.silvestre.util;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.Crew;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppUtil{

    private static final String SPREADSHEET_ID = "1xTaefRpNV_gPHMympcVU-c2uAe8hVIYOjbt3MXBusjc";

    public static List<Crew> getActiveCrew() throws IOException, GeneralSecurityException{
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter
            (Crew::isActive).collect(Collectors.toList());
    }

    private static List<List<Object>> getRoster() throws IOException, GeneralSecurityException{
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Stats'").execute();

        List<List<Object>> rawCrew = response.getValues();

        //remove headers
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);

        return rawCrew;
    }

    public static List<Crew> getMyCrew() throws IOException, GeneralSecurityException{
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter(Crew::hasStars)
            .collect(Collectors.toList());
    }

    public static List<Crew> getTimelinesCrew() throws IOException, GeneralSecurityException{
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id)))
            .collect(Collectors.toList());
    }

    public static List<String> getTraits() throws IOException, GeneralSecurityException{
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Data'").execute();

        List<List<Object>> rawData = response.getValues();

        //remove header
        rawData.remove(0);

        return rawData.stream().map(data -> (String) data.get(7)).filter(StringUtils::isNotEmpty)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }
}
