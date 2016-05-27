package database;

class Registr {
    private String name;
    private int value;

    Registr() {
        name = "";
        value = 0;
    }

    public Registr(int value) {
        this.value = value;
        name = "";
    }

    Registr(String name, int value) {
        this.name = name;
        this.value = value;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }
}
