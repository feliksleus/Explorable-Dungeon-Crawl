package Core;
import java.lang.*;


public class Dungeon {
    int x1, y1;
    int x2, y2;
    int width, height;

    int md_x;
    int md_y;
    int dist_to_parent;
    Dungeon next;


    public Dungeon(int x1, int x2, int y1, int y2)
    {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;


    }

    public Dungeon(int x1, int x2, int y1, int y2, int width, int height)
    {
        this.x1 = x1;
        this.x2 = x2;

        this.y1 = y1;
        this.y2 = y2;

        this.width = width;
        this.height = height;
        this.md_x = (x1 + x2) / 2;
        this.md_y = (y1 + y2) / 2;


    }

    public void setDist(int d)
    {
        dist_to_parent = d;
    }



}
