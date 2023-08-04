package de.TerrainGuarding;


/*Class for vertices. Contains a vertex ID, x and y value of the vertex and a field for vType, which is used to store
the information whether a vertex of a terrain is a left/right convex/reflex vertex
 */
public class Vertex {
    private int ID;
    private double x;
    private double y;
    private String vType;


    public Vertex(int ID, double x, double y, String vType) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.vType = vType;
    }

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getID() {
        return ID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getvType() {
        return vType;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vertex vertex = (Vertex) o;
        return this.ID == vertex.getID() && this.x == vertex.getX() && this.y == vertex.getY() && this.vType.equals(vertex.getvType());
    }

    @Override
    public String toString() {
        return "Vertex{" + "ID=" + ID + ", x=" + x + ", y=" + y + ", vType=" + vType + "}";
    }

    public String toStringBare() {
        return ID + " " + x + " " + y + " " + vType;
    }

}
