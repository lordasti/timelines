package es.manuel.vera.silvestre.util;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.Crew;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppUtil{

    private static final Logger LOGGER = Logger.getLogger(AppUtil.class);
    private static final String SPREADSHEET_ID = "1xTaefRpNV_gPHMympcVU-c2uAe8hVIYOjbt3MXBusjc";

    public static List<Crew> getActiveCrew(){
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter
            (Crew::isActive).collect(Collectors.toList());
    }

    private static List<List<Object>> getRoster(){
        List<List<Object>> rawCrew = new ArrayList<>();
        try{
            Sheets sheetsService = SheetsServiceUtil.getSheetsService();

            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Stats'").execute();

            rawCrew = response.getValues();

            //remove headers
            rawCrew.remove(0);
            rawCrew.remove(0);
            rawCrew.remove(0);
            rawCrew.remove(0);
        }catch(Exception e){
            LOGGER.error("Exception in getRoster", e);
        }

        return rawCrew;
    }

    public static List<Crew> getMyCrew(){
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter(Crew::hasStars)
            .collect(Collectors.toList());
    }

    public static List<Crew> getCandidates(){
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id)))
            .filter(crew -> !crew.hasStars())
            .collect(Collectors.toList());
    }

    public static List<Crew> getTimelinesCrew(){
        List<List<Object>> rawCrew = getRoster();
        return IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id)))
            .collect(Collectors.toList());
    }

    public static List<String> getTraits(){
        List<List<Object>> rawData = new ArrayList<>();

        try{
            Sheets sheetsService = SheetsServiceUtil.getSheetsService();

            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Data'").execute();

            rawData = response.getValues();

            //remove header
            rawData.remove(0);
        }catch(Exception e){
            LOGGER.error("Exception in getTraits", e);
        }

        return rawData.stream().map(data -> (String) data.get(7)).filter(StringUtils::isNotEmpty)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }

    public static void serializeToFile(Object o, String fileName){
        try(ObjectOutputStream oos =
            new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))){
            oos.writeObject(o);
        }catch(Exception e){
            LOGGER.error("Exception in serializeToFile", e);
        }
    }

    public static <T> T deserializeFromFile(String fileName){
        try(ObjectInputStream ois =
            new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))){
            return (T) ois.readObject();
        }catch(Exception e){
            LOGGER.error("Exception in deserializeFromFile", e);
        }

        return null;
    }
}
