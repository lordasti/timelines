package es.manuel.vera.silvestre.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.modelo.response.CrewAvatarsResponse;
import es.manuel.vera.silvestre.modelo.response.GauntletResponse;
import es.manuel.vera.silvestre.modelo.response.PlayerResponse;
import es.manuel.vera.silvestre.modelo.response.VoyageResponse;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppUtil{
    private static final Logger LOGGER = Logger.getLogger(AppUtil.class);
    private static final String SPREADSHEET_ID = "1xTaefRpNV_gPHMympcVU-c2uAe8hVIYOjbt3MXBusjc";
    private static Player player = null;
    private static Voyage voyage = null;
    private static Gauntlet gauntlet = null;
    private static List<AvatarCrew> avatars = null;
    private static List<List<Object>> sheetRoster = null;
    private static List<SheetCrew> sheetCrew = null;
    private static List<Crew> frozenCrew = null;
    private static List<Crew> myCrew = null;
    private static List<Crew> timelinesCrew = null;
    private static List<InventoryItem> inventory = null;
    private static Map<Long,InventoryItem> inventoryMap = null;
    private static List<Long> missingEquipmentIds = null;
    private static List<ArchetypeItem> recipes = null;

    public static List<Crew> getTimelinesCrew(){
        if(timelinesCrew != null){
            return timelinesCrew;
        }

        List<SheetCrew> sheetCrew = getSheetCrew();
        List<AvatarCrew> avatarCrew = getAvatars();
        List<Crew> myCrew = getMyCrew();

        timelinesCrew = avatarCrew.stream().map(avatar -> {
            Crew mine = myCrew.stream()
                .filter(my -> my.getArchetypeId() == avatar.getId())
                .findAny()
                .orElse(null);
            if(mine != null){
                return mine;
            }

            return avatar.toBuilder()
                .skills(
                    sheetCrew.stream()
                        .filter(sheet -> avatar.getName().equals(sheet.getName()))
                        .map(Crew::getSkills)
                        .findAny()
                        .orElseGet(() -> {
                            LOGGER.warn("Avatar not found on Sheet: " + avatar);
                            return Arrays.asList(new Skill(Stats.COMMAND, 0, 0, 0),
                                new Skill(Stats.DIPLOMACY, 0, 0, 0),
                                new Skill(Stats.SECURITY, 0, 0, 0),
                                new Skill(Stats.ENGINEERING, 0, 0, 0),
                                new Skill(Stats.SCIENCE, 0, 0, 0),
                                new Skill(Stats.MEDICINE, 0, 0, 0));
                        })
                )
                .build();
        }).collect(Collectors.toList());

        return timelinesCrew;
    }

    public static List<Crew> getMyCrew(){
        if(myCrew != null){
            return myCrew;
        }

        myCrew =
            Stream.concat(getActiveCrew().stream(), getFrozenCrew().stream()).sorted().collect(Collectors.toList());
        return myCrew;
    }

    private static List<Crew> getFrozenCrew(){
        if(frozenCrew != null){
            return frozenCrew;
        }

        getAvatars();
        getSheetCrew();

        frozenCrew = getPlayer().getCharacter().getFrozenCrewIds().stream()
            .map(AppUtil::findAvatarCrewById)
            .map(AppUtil::completeWithSheetSkills)
            .collect(Collectors.toList());
        return frozenCrew;
    }

    private static Crew findAvatarCrewById(Long id){
        return avatars.stream().filter(avatar -> id.equals(avatar.getId())).findAny().orElseThrow(
            NoSuchElementException::new);
    }

    private static Crew completeWithSheetSkills(Crew crew){
        return crew.toBuilder().skills(
            sheetCrew.stream().filter(sheet -> crew.getName().equals(sheet.getName()))
                .map(Crew::getSkills).findAny()
                .orElseThrow(() -> new NoSuchElementException("Crew: " + crew.getName())))
            .build();
    }

    public static List<? extends Crew> getActiveCrew(){
        return getPlayer().getCharacter().getActiveCrew();
    }

    private static Player getPlayer(){
        if(player != null){
            return player;
        }

        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = AppUtil.class.getResourceAsStream("/player.json");
            PlayerResponse response = mapper.readValue(is, PlayerResponse.class);
            player = response.getPlayer();
        }catch(IOException e){
            LOGGER.error("Error in getPlayer", e);
        }

        return player;
    }

    private static List<AvatarCrew> getAvatars(){
        if(avatars != null){
            return avatars;
        }

        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = AppUtil.class.getResourceAsStream("/get_avatars.json");
            CrewAvatarsResponse response = mapper.readValue(is, CrewAvatarsResponse.class);
            avatars = response.getAvatars();
        }catch(IOException e){
            LOGGER.error("Error in getPlayer", e);
        }

        return avatars;
    }

    private static List<SheetCrew> getSheetCrew(){
        if(sheetCrew != null){
            return sheetCrew;
        }

        List<List<Object>> rawSheetCrew = getSheetRoster();
        sheetCrew = rawSheetCrew.stream().map(SheetCrew::new).collect(Collectors.toList());
        return sheetCrew;
    }

    private static List<List<Object>> getSheetRoster(){
        if(sheetRoster != null){
            return sheetRoster;
        }

        try{
            Sheets sheetsService = SheetsServiceUtil.getSheetsService();

            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Stats'").execute();

            sheetRoster = response.getValues();

            //remove headers
            sheetRoster.remove(0);
            sheetRoster.remove(0);
            sheetRoster.remove(0);
            sheetRoster.remove(0);
        }catch(Exception e){
            LOGGER.error("Exception in getRoster", e);
        }

        return sheetRoster;
    }

    public static List<Ship> getShips(){
        return getPlayer().getCharacter().getShips();
    }

    public static Voyage getVoyage(){
        if(voyage != null){
            return voyage;
        }

        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = AppUtil.class.getResourceAsStream("/voyage.json");
            VoyageResponse response = mapper.readValue(is, VoyageResponse.class);
            voyage = response.getVoyage();
        }catch(IOException e){
            LOGGER.error("Error in getVoyage", e);
        }

        return voyage;
    }

    public static Gauntlet getGauntlet(){
        if(gauntlet != null){
            return gauntlet;
        }

        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = AppUtil.class.getResourceAsStream("/gauntlet.json");
            GauntletResponse response = mapper.readValue(is, GauntletResponse.class);
            gauntlet = response.getGauntlet();
        }catch(IOException e){
            LOGGER.error("Error in getGauntlet", e);
        }

        return gauntlet;
    }

    public static Map<Long,InventoryItem> getInventoryMap(){
        if(inventoryMap != null){
            return inventoryMap;
        }

        inventoryMap = getInventory().stream().collect(Collectors.toMap(item -> item.getArchetypeId(), item -> item));
        return inventoryMap;
    }

    public static List<InventoryItem> getInventory(){
        if(inventory != null){
            return inventory;
        }

        inventory = getPlayer().getCharacter().getInventory();
        return inventory;
    }

    public static List<Long> getMissingEquipmentIds(){
        if(missingEquipmentIds != null){
            return missingEquipmentIds;
        }

        missingEquipmentIds = getActiveCrew().stream()
            .flatMap(crew -> crew.getMissingEquipment().stream())
            .collect(Collectors.toList());
        return missingEquipmentIds;
    }

    public static List<ArchetypeItem> getRecipes(){
        if(recipes != null){
            return recipes;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = AppUtil.class.getResourceAsStream("/player.json");
            PlayerResponse response = mapper.readValue(is, PlayerResponse.class);
            recipes = response.getRecipes();
        }catch(IOException e){
            LOGGER.error("Error in getRecipes", e);
        }
        return recipes;
    }
}
