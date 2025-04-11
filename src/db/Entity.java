package db;

public abstract class Entity {
    public int id;

    public void setId(int id) { //I added this method myself
        this.id = id;
    }

    public int getId() { //I added this method myself
        return id;
    }

    public abstract Entity copy();

    public abstract int getEntityCode();
}
