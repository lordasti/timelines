package es.manuel.vera.silvestre.modelo;

public enum Stats{
    COMMAND(0, "CMD"),
    DIPLOMACY(1, "DIP"),
    SECURITY(2, "SEC"),
    ENGINEERING(3, "ENG"),
    SCIENCE(4, "SCI"),
    MEDICINE(5, "MED");

    private final int index;
    private final String name;

    Stats(int index, String name){
        this.index = index;
        this.name = name;
    }

    public int getIndex(){
        return index;
    }

    @Override
    public String toString(){
        return name;
    }
}
