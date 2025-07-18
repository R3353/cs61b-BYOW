package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.ArrayList;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */

    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        World.mainMenuHandler();
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
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        InputSource inputSource = new StringInputDevice(input);
        StringBuilder inp = new StringBuilder();
        TETile[][] returned = new TETile[0][];
        ArrayList<String> weLikeToMoveItMoveIT = new ArrayList<>();
        while (inputSource.possibleNextInput()) {
            char c = Character.toLowerCase(inputSource.getNextKey());
            if (c == 'n') {
                while (inputSource.possibleNextInput()) {
                    char next = inputSource.getNextKey();
                    if (Character.isDigit(next)) {
                        inp.append(next);
                    } else if (Character.toLowerCase(next) == 's') {
                        break;
                    }
                }
            } else if (Character.isAlphabetic(c)) {
                weLikeToMoveItMoveIT.add(String.valueOf(c));
            } else if (c == ':') {
                if (inputSource.possibleNextInput() && Character.toLowerCase(inputSource.getNextKey()) == 'q') {
                    break;
                }
            }
        }
        returned = World.newGameString(Long.parseLong(String.valueOf(inp)));
        World.doMovements(weLikeToMoveItMoveIT);
        return returned;
    }
}
