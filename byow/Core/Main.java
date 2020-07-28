package Core;

import java.io.IOException;
import TileEngine.TETile;
import TileEngine.TERenderer;
/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */

public class Main {
    public static void main(String[] args) throws IOException {
        //boolean b = '2' == 50;
        //System.out.println(b);
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Engine engine = new Engine();
            TETile [][] world = engine.interactWithInputString(args[0]);

            System.out.println(TETile.toString(world));
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
