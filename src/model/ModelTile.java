package model;

public class ModelTile {

    private String type;
    private int index;

    public ModelTile(String s) {
        type = s;
        index = -1;
    }

    public ModelTile(String s, int i) {
        type = s;
        index = i;
    }

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
