package Core;

import InputDemo.StringInputDevice;
import TileEngine.TERenderer;
import TileEngine.TETile;
import java.awt.Font;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
//import InputDemo.InputSource;
import TileEngine.Tileset;
import java.io.File;
//import InputDemo.StringInputDevice;
//import InputDemo.KeyboardInputSource;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileNotFoundException;
//import java.util.Scanner;

public class Engine {

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static long SEED = 0;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TERenderer ter = new TERenderer();

        while (true) {
            StdDraw.clear(Color.BLACK);
            Font font = new Font("Calibri", Font.BOLD, 25);
            StdDraw.setFont(font);
            StdDraw.enableDoubleBuffering();
            first_window();


            boolean window_menu = true;
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            char b = 'N';
            StringBuilder sb = new StringBuilder();
            int dec_space = 10;

            // Shows the menu screen until user performs some action
            while (window_menu) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    c = Character.toUpperCase(c);
                    switch (c) {

                        case 'L':
                            world = loadfile(); //You will save later
                            window_menu = false;
                            b = 'L';
                            break;
                        case 'Q':

                            System.exit(0);
                            break;
                        case 'N':

                            window_menu = false;
                            break;
                        default:
                            continue;

                    }
                }

            }

            StdDraw.clear(Color.BLACK);
            StdDraw.show();

            boolean seed_window = true;

            // Prompts the user to enter the new seed
            if (b == 'N') {

                while (seed_window) {
                    if (StdDraw.hasNextKeyTyped()) {
                        char c = StdDraw.nextKeyTyped();
                        c = Character.toUpperCase(c);
                        System.out.println(c);
                        if (c == 'S')
                        {

                            seed_window = false;
                            break;
                        } else if ('0' <= c && c <= '9') {
                            SEED *= 10;
                            SEED += Integer.parseInt(String.valueOf(c));
                            dec_space *= 10;

                            if (SEED < 0 || SEED == Long.MAX_VALUE) {
                                SEED = Long.MAX_VALUE;

                                break;

                            }
                            sb.append(c);



                        }


                    }


                    seed_enter(sb.toString());

                }

                world = Worldgenerator.setup(WIDTH, HEIGHT, SEED);
            }
            System.out.println(TETile.toString(world));

            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);
            System.out.println("rendered");


            int x = Worldgenerator.x_c; // locations of the x coordinate of the character
            int y = Worldgenerator.y_c; // locations of the y coordinate of the character
            boolean locked = true;


            // Main loop to traverse the world, until the door is unlocked, or other action is performed.

            while (locked == true) {


                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    c = Character.toUpperCase(c);
                    System.out.println(c);
                    if (c == 'Q') // we do not consider Q without ":" before it
                    {
                        savefile(world);
                        System.exit(0);
                    }

                    switch (c) {

                        case 'D':
                            if (world[x + 1][y] == Tileset.FLOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x + 1][y] = Tileset.AVATAR;
                                x = x + 1;
                                Worldgenerator.x_c = x;


                            } else if (world[x + 1][y] == Tileset.LOCKED_DOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x + 1][y] = Tileset.UNLOCKED_DOOR;
                                System.out.println("You've unlocked the door");


                                locked = false;
                            }


                            ter.renderFrame(world);

                            break;

                        case 'S':
                            if (world[x][y - 1] == Tileset.FLOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x][y - 1] = Tileset.AVATAR;
                                y = y - 1;
                                Worldgenerator.y_c = y;


                            } else if (world[x][y - 1] == Tileset.LOCKED_DOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x][y - 1] = Tileset.UNLOCKED_DOOR;

                                System.out.println("You've unlocked the door");
                                locked = false;
                            }

                            ter.renderFrame(world);
                            break;
                        case 'A':
                            if (world[x - 1][y] == Tileset.FLOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x - 1][y] = Tileset.AVATAR;
                                x = x - 1;
                                Worldgenerator.x_c = x;


                            } else if (world[x - 1][y] == Tileset.LOCKED_DOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x - 1][y] = Tileset.UNLOCKED_DOOR;
                                System.out.println("You've unlocked the door");
                                locked = false;
                            }

                            ter.renderFrame(world);
                            break;
                        case 'W':
                            if (world[x][y + 1] == Tileset.FLOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x][y + 1] = Tileset.AVATAR;
                                y = y + 1;
                                Worldgenerator.y_c = y;

                            } else if (world[x][y + 1] == Tileset.LOCKED_DOOR) {
                                world[x][y] = Tileset.FLOOR;
                                world[x][y + 1] = Tileset.UNLOCKED_DOOR;
                                System.out.println("You've unlocked the door");
                                locked = false;
                            }
                            ter.renderFrame(world);

                            break;
                        default:
                            continue;


                    }

                }

            }


          StdDraw.setCanvasSize(WIDTH * 10, HEIGHT * 10);



        }


    }

    // Generates the menu screen
    public static void first_window()
    {


        String new_game = "New Game (N)";
        String load_game = "Load Game (L)";
        String quit_game = "Quit (Q)";

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.orange);
        StdDraw.text(0.5, 0.6, new_game);
        StdDraw.text(0.5, 0.5, load_game);
        StdDraw.text(0.5, 0.4, quit_game);
        //StdDraw.text(0.5, 0.2)
        StdDraw.show();
    }

    public static void seed_enter(String s)
    {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(0.5, 0.2, s);
        StdDraw.text(0.5, 0.6, "Enter some number and press S");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */



    public TETile[][] interactWithInputString(String input) // throws FileNotFoundException, IOException {
    {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.


        input = input.toUpperCase();

        int processed = 1;
        int dec_space = 10;
        //SEED = 0;
        int i = 0;
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        StringBuilder sb = new StringBuilder();
        // handling the cases of creating a new game and entering the seed


        if (input.charAt(i) == 'L')
        {
            finalWorldFrame = loadfile();
            processed++;
        }

        else if (input.charAt(i) != 'N')
        {
            System.out.println("Invalid input");
            System.exit(0);
        }

        else // Enter the seed until the character is S.
        {
            for (i = 1; i < input.length(); i++) {

                char c = input.charAt(i);
                if (i == input.length() - 1 && c != 'S') {
                    System.out.println("Invalid input");
                    System.exit(0);
                }
                if (c == 'S') // || ((c != 'N') && (inputSource.index == 0))
                {
                    processed++;
                    break;
                }
                if ('0' <= c && c <= '9') {
                    SEED *= 10;
                    SEED += Integer.parseInt(String.valueOf(c));
                    dec_space *= 10;

                    if (SEED < 0) {
                        SEED = Long.MAX_VALUE;
                        processed++;

                    }

                }

                if (input.charAt(i) == 'L') {
                    finalWorldFrame = loadfile();
                    processed++;
                    break;
                }

                processed++;
            }

            System.out.println("The seed is: " + SEED);
            finalWorldFrame = Worldgenerator.setup(WIDTH, HEIGHT, SEED);
        }







            // Add interactivity


            int x = Worldgenerator.x_c; // locations of the x coordinate of the character
            int y = Worldgenerator.y_c; // locations of the y coordinate of the character
            boolean locked = true;

            System.out.println("Originally, the character is at x = " + x + " and y = " + y);

            while (processed < input.length() && locked == true)
            {

                char c = input.charAt(processed);
                if (c == ':')
                {
                    if (processed + 1 < input.length() && input.charAt(processed + 1) == 'Q')
                    {
                        // :Q saves case here
                        //System.out.println("File saved");
                        savefile(finalWorldFrame);
                        //System.exit(0);
                        processed++;
                        System.out.println("File saved");
                        break;
                    }

                    else
                    {
                        System.out.println("Invalid input");
                        System.exit(0);
                    }
                }

                // Test cases: XXXX:
                //             XXX:Q
                //             XQ:X
                if (c == 'Q') // we do not consider Q without ":" before it
                {
                    System.out.println("Invalid input");
                    System.exit(0);
                }

                // handles interactivity
                switch (c)
                {

                    case 'D':
                        if (finalWorldFrame[x + 1][y] == Tileset.FLOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x + 1][y] = Tileset.AVATAR;
                            x = x + 1;
                            Worldgenerator.x_c = x;


                        }

                        else if (finalWorldFrame[x + 1][y] == Tileset.LOCKED_DOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x + 1][y] = Tileset.UNLOCKED_DOOR;
                            System.out.println("You've unlocked the door");
                            locked = false;
                        }
                        processed++;
                        break;

                    case 'S':
                        if (finalWorldFrame[x][y - 1] == Tileset.FLOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x][y - 1] = Tileset.AVATAR;
                            y = y - 1;
                            Worldgenerator.y_c = y;

                        }


                        else if (finalWorldFrame[x][y - 1] == Tileset.LOCKED_DOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x][y - 1] = Tileset.UNLOCKED_DOOR;
                            System.out.println("You've unlocked the door");
                            locked = false;
                        }
                        processed++;
                        break;
                    case 'A':
                        if (finalWorldFrame[x - 1][y] == Tileset.FLOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x - 1][y] = Tileset.AVATAR;
                            x = x - 1;
                            Worldgenerator.x_c = x;

                        }

                        else if (finalWorldFrame[x - 1][y] == Tileset.LOCKED_DOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x - 1][y] = Tileset.UNLOCKED_DOOR;
                            System.out.println("You've unlocked the door");
                            locked = false;
                        }
                        processed++;
                        break;
                    case 'W':
                        if (finalWorldFrame[x][y + 1] == Tileset.FLOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x][y + 1] = Tileset.AVATAR;
                            y = y + 1;
                            Worldgenerator.y_c = y;
                        }


                        else if (finalWorldFrame[x][y + 1] == Tileset.LOCKED_DOOR)
                        {
                            finalWorldFrame[x][y] = Tileset.FLOOR;
                            finalWorldFrame[x][y + 1] = Tileset.UNLOCKED_DOOR;
                            System.out.println("You've unlocked the door");
                            locked = false;
                        }
                        processed++;
                        break;

                    case 'L':
                        finalWorldFrame = loadfile();
                        break;
                    default:
                        processed++;
                        break;
                }

                System.out.println("After 1 move " + c + " , the character is at x = " + Worldgenerator.x_c + "and y = " + Worldgenerator.y_c);

            }












        return finalWorldFrame;
    }


    // Loading
    public static TETile [][] loadfile()
    {
        TETile[][] old_world = new TETile[WIDTH][HEIGHT];


        try
        {
            File f = new File("world.txt");
            Scanner reader = new Scanner(f);
            int curr_height = 0;
            int curr_width = 0;
            while (reader.hasNextLine() && curr_height < HEIGHT)
            {
                String line = reader.nextLine();
                System.out.println(line.length());

                TETile [] row = new TETile[WIDTH];



                for (int i = 0; i < line.length(); i++)
                {
                    char c = line.charAt(i);

                    switch (c) // select the character from the tileset
                    {
                        case '·':
                            row[i] = Tileset.FLOOR;
                            break;
                        case '#':
                            row[i] = Tileset.WALL;
                            break;
                        case ' ':
                            row[i] = Tileset.NOTHING;
                            break;
                        case '@':
                            row[i] = Tileset.AVATAR;
                            break;
                        case '█':
                            row[i] = Tileset.LOCKED_DOOR;
                            break;
                        case '▢':
                            row[i] = Tileset.UNLOCKED_DOOR;
                            break;
                        case 'n':
                            System.out.println("You forgot the new line symbol");
                            System.exit(0);


                    }





                }

                //old_world[HEIGHT - curr_height - 1] = row;
                for (int i = 0; i < WIDTH; i++)
                {
                    old_world[i][HEIGHT - curr_height - 1] = row[i];
                    if (old_world[i][HEIGHT - curr_height - 1] == Tileset.AVATAR)
                    {
                        Worldgenerator.x_c = i;
                        Worldgenerator.y_c = HEIGHT - curr_height - 1;
                    }
                }
                curr_height++;

            }




        }

        catch (FileNotFoundException e)
        {
            System.out.println("This file does not exist");

            System.exit(0);
        }



        return old_world;
    }

    // Saving
    public static void savefile(TETile [][] world) {


        File f = new File("world.txt");



            try {
                FileWriter fw = new FileWriter("world.txt", false);


                String text = TETile.toString(world);
                fw.write(text);
                fw.close();




            }
            catch (IOException e) {
                System.out.println("An error occurred when trying to open and update the file.");
                System.exit(0);
            }



    }



}






