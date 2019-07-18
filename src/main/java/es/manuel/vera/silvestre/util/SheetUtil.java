package es.manuel.vera.silvestre.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SheetUtil{
    public static int readInt(List<Object> raw, int pos){
        if(raw.size() <= pos){
            return 0;
        }

        String value = StringUtils.remove((String) raw.get(pos), ',');
        return NumberUtils.isCreatable(value) ? NumberUtils.createInteger(value) : 0;
    }

    public static boolean readBoolean(List<Object> raw, int pos){
        return raw.size() > pos && BooleanUtils.toBoolean((String) raw.get(pos));
    }

    public static List<String> readList(List<Object> raw, int pos){
        if(raw.size() <= pos){
            return new ArrayList<>();
        }

        return Arrays.asList(((String) raw.get(pos)).split(",")).stream().map(String::trim).map(String::toLowerCase)
            .collect(Collectors.toList());
    }
}
