package pt.tecnico.hds.mad.client;

public class GridUser {

    private String id;
    private Position pos;

    public GridUser(String id, int x, int y) {
        this.id = id;
        this.pos = new Position(x, y);
    }

    public String getId() {
        return id;
    }

    public Position getPos() {
        return pos;
    }
}
