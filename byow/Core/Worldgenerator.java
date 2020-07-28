package Core;
import TileEngine.TERenderer;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.util.*;

//import Dungeon;
public class Worldgenerator {

    //TERenderer ter = new TERenderer();
    private static int WIDTH;
    private static int HEIGHT;
    private static long SEED;
    //private static final long SEED = 12;
    private static Random RANDOM;
    public static int x_c; // x-coordinate of the character
    public static int y_c; // y-coordinate of the character


    public static TETile [][] setup(int width, int height, long seed) {

        // initalizes the world


        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        RANDOM = new Random(SEED);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        ArrayList<Dungeon> dungeons = new ArrayList<>();
        // first, generate dungeons
        generate_dungeons(world, dungeons);


        // Finding the nearest neighbor in the set of unconnected vertices (centers of the dungeons)
        Set<Integer> connected = new HashSet<>();
        int d;

        for (int i = 0; i < dungeons.size(); i++)
        {
            int min = Integer.MAX_VALUE;
            int dest = 0;
            connected.add(i);
            for (int j = 0; j < dungeons.size(); j++)
            {
                d = dist(dungeons.get(i).md_x, dungeons.get(j).md_x, dungeons.get(i).md_y, dungeons.get(j).md_y);
                if (d < min && (!connected.contains(j)))
                {
                    min = d;
                    dest = j;
                }

            }

            dungeons.get(i).setDist(dest);


        }



        // Connects the dungeons to each other; creates a cycle
        generate_hallways(world, dungeons);



        int character_location = RandomUtils.uniform(RANDOM, dungeons.size());


        x_c = dungeons.get(character_location).md_x;
        y_c = dungeons.get(character_location).md_y;

        // place the character at the center of some room
        world[x_c][y_c] = Tileset.AVATAR;


        // places a door to unlock at some valid location within the world
        place_door(world);







        return world;
    }




    public static void place_door(TETile [][] world)
    {


        LinkedHashMap<Integer, Integer> m = new LinkedHashMap<>();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++)
            {
                if (world[x][y] == Tileset.WALL && ((x - 1) >= 0 && (y - 1) >= 0 && (x + 1) < WIDTH && (y + 1) < HEIGHT))
                {
                    if ((world[x][y - 1] == Tileset.WALL && world[x + 1][y] == Tileset.WALL) ||
                (world[x][y - 1] == Tileset.WALL && world[x - 1][y] == Tileset.WALL) ||
                        (world[x + 1][y] == Tileset.WALL && world[x][y + 1] == Tileset.WALL) ||
                        (world[x - 1][y] == Tileset.WALL && world[x][y + 1] == Tileset.WALL))
                    {
                    continue;
                    }
                else
                    {
                        m.put(x, y);
                    }
            }
        }

        }

        int random_index = RandomUtils.uniform(RANDOM, m.size());
        int i = 0;
        for (Map.Entry<Integer, Integer> kv : m.entrySet()) {
            if (i == random_index)
            {
                world[kv.getKey()][kv.getValue()] = Tileset.LOCKED_DOOR;

            }
            i++;
        }
    }









    public static void generate_dungeons(TETile[][] world, ArrayList<Dungeon> dungeon_set)
    {
        // n is the largest possible number of dungeons
        int n = RANDOM.nextInt(Integer.MAX_VALUE) & Integer.MAX_VALUE;
        int world_area = WIDTH * HEIGHT; // total area
        int used_area = 0; // area that is filled with tiles other than Tileset.NOTHING

        int x, y; // (x, y) coordinates of the bottom left tile of the dungeon

        // The function continues until n is 0 and % of the world is filled
        while (n > 0 && used_area < world_area * 0.50)
        {
            try
            {
                // creates the starting (x, y) coordinates for the dungeon);
                x = RandomUtils.uniform(RANDOM, WIDTH - 2);
                y = RandomUtils.uniform(RANDOM, HEIGHT - 2);
                Dungeon new_dungeon = intialize_dungeon(x, y, world);
                // draws the dungeon in our environment
                drawDungeon(world, new_dungeon);


                dungeon_set.add(new_dungeon);
                used_area += new_dungeon.width * new_dungeon.height;
                n--;


            }

            catch (DungeonInitializationException ex)
            {


                n--;
                //continue; // given a trial of the room creation, discard it.
            }

        }


    }

    public static Dungeon intialize_dungeon(int x, int y, TETile[][] world) throws DungeonInitializationException
    {
        // coordinates for the height and width of each of the dungeons
        int horizontal = RandomUtils.uniform(RANDOM, x+2, WIDTH); // introduce a lower bound
        int upper = RandomUtils.uniform(RANDOM, y+2, HEIGHT);






        // check the distances

        int relativeWidth = dist(x, horizontal, y, y);
        int relativeHeight = dist(x, x, y, upper);


        if (relativeWidth > WIDTH / 4 || relativeHeight > HEIGHT / 4)
        {
            throw new DungeonInitializationException();
        }



        // Check the tiles for exceptions

        for (int i = x; i <= horizontal; i++)
        {
            for (int j = y; j <= upper; j++)
            {

                if (world[i][j] != Tileset.NOTHING)
                {
                    throw new DungeonInitializationException();
                }
            }
        }


        return new Dungeon(x, horizontal, y, upper, relativeWidth, relativeHeight);
    }

    // variation on distance formula for this program
    public static int dist(int x1, int x2, int y1, int y2)
    {
        double k = Math.abs(x2 - x1);
        double j = Math.abs(y2 - y1);
        double dist = Math.sqrt(Math.pow(k, 2) + Math.pow(j, 2));
        int fin = (int)dist + 1;


        return fin;
    }


    public static void drawDungeon (TETile[][] world, Dungeon dun)
    {
        // Sets tiles.NOTHING to useful tiles

        // relative coordinates
        int relx_start = dun.x1;
        int relx_end = dun.x2;
        int rely_start = dun.y1;
        int rely_end = dun.y2;



        for (int i = relx_start; i <= relx_end; i++)
        {
            for (int j = rely_start; j <= rely_end; j++)
            {
                // If the coordinates are located in the first or the last row, set the tile as a wall;


                if ((i == relx_start) || (i == relx_end))
                {
                    world[i][j] = Tileset.WALL;

                }

                // If it's the first or the last row, set the tile as a wall;


                else if (j == rely_start || j == rely_end)
                {
                    world[i][j] = Tileset.WALL;
                }

                else // Otherwise, put the floor tile in;
                {
                    world[i][j] = Tileset.FLOOR;
                }


            }

        }

    }


    public static void generate_hallways(TETile [][] world, ArrayList<Dungeon> dun_list)
    {
        // Put indices into the arraylist of linked lists (to create components)

        for (int i = 0; i < dun_list.size(); i++) {





            int choice = RandomUtils.uniform(RANDOM, 0, 2);


            int destination_index = dun_list.get(i).dist_to_parent;

            {
                if (choice == 0) {
                    move_updown(world, dun_list.get(i), dun_list.get(destination_index), dun_list.get(i).md_y);
                    move_leftright(world, dun_list.get(i), dun_list.get(destination_index), dun_list.get(destination_index).md_x);

                }

                else {
                    move_leftright(world, dun_list.get(i), dun_list.get(destination_index), dun_list.get(i).md_x);
                    move_updown(world, dun_list.get(i), dun_list.get(destination_index), dun_list.get(destination_index).md_y); }


            }
            }


    }

    public static void move_updown(TETile [][] world, Dungeon d1, Dungeon d2, int col)
    {

        int p = d1.md_x;

        // build turns

        fill_corners(world, p, col);

        // move up or down
        while (p != d2.md_x)
        {


                world[p][col] = Tileset.FLOOR;

                if (world[p][col + 1] == Tileset.NOTHING)
                    world[p][col + 1] = Tileset.WALL;

                if (world[p][col - 1] == Tileset.NOTHING)
                    world[p][col - 1] = Tileset.WALL;

                if (d1.md_x < d2.md_x) {
                    p++;

                }

                else {
                    p--;
                }

        }

        fill_corners(world, p, col);

    }


    public static void move_leftright(TETile [][] world, Dungeon d1, Dungeon d2, int row)
    {
        int j = d1.md_y;

        fill_corners(world, row, j);

        while (j != d2.md_y)
        {
            world[row][j] = Tileset.FLOOR;
            if (world[row + 1][j] == Tileset.NOTHING)
                world[row + 1][j] = Tileset.WALL;

            if (world[row - 1][j] == Tileset.NOTHING)
                world[row - 1][j] = Tileset.WALL;

            if (d1.md_y < d2.md_y) {
                j++;

            }

            else {
                j--;
            }
        }

        fill_corners(world, row, j);

    }

    public static void fill_corners(TETile [][] world, int row, int col)
    {
        for (int k = row - 1; k <= row + 1; k++)
        {
            for (int i = col - 1; i <= col + 1; i++)
            {
                if (world[k][i] == Tileset.NOTHING)
                    world[k][i] = Tileset.WALL;
            }
        }

    }


}

class DungeonInitializationException extends Exception {


    /**
     *
     */
    //private static final long serialVersionUID = 1L;

    public DungeonInitializationException() {

    }
}

