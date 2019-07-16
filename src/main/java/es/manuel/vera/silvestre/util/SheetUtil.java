package es.manuel.vera.silvestre.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

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
}
