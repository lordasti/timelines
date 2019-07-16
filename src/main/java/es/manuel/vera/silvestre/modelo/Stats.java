package es.manuel.vera.silvestre.modelo;

public enum Stats{
    COMMAND(0, "CMD"),
    DIPLOMACY(1, "DIP"),
    ENGINEERING(2, "ENG"),
    SECURITY(3, "SEC"),
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

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
