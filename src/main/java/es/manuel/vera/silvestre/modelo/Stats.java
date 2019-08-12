package es.manuel.vera.silvestre.modelo;

import lombok.Getter;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Getter
public enum Stats{
    COMMAND(0, "CMD", "command"),
    DIPLOMACY(1, "DIP", "diplomacy"),
    SECURITY(2, "SEC", "security"),
    ENGINEERING(3, "ENG", "engineering"),
    SCIENCE(4, "SCI", "science"),
    MEDICINE(5, "MED", "medicine");

    private final int index;
    private final String name;
    private final String longName;

    Stats(int index, String name, String longName){
        this.index = index;
        this.name = name;
        this.longName = longName;
    }

    public static Stats getStatFromSkillName(String skillName){
        int index = skillName.indexOf("_skill");
        String stat = (index > -1) ? skillName.substring(0, index) : skillName;

        return Stream.of(values()).filter(s -> s.longName.equals(stat)).findAny()
            .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String toString(){
        return name;
    }
}
