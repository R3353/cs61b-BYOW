package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

/**
 * Draws a world that is mostly empty except for a small region.
 */
public class World {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    private static final int maxWidth = WIDTH - 1;
    private static final int maxHeight = HEIGHT - 1;

    private static long SEED;
    private static Random RANDOM;

    private static int roomCount = 0;
    private static HashMap<Integer, ArrayList<Integer>> roomDict = new HashMap<>();
    private static ArrayList<Boolean> visited = new ArrayList<>(roomCount);
    private static WeightedQuickUnionUF wqu = new WeightedQuickUnionUF((WIDTH * HEIGHT) + 1);
    private static ArrayList<Integer> floorList = new ArrayList<>();
    private static TETile[][] world;
    private static int avatarX;
    private static int avatarY;

    private static String loadedSeed = "";
    private static String movement = "";
    private static String newMovement = "";

    private static boolean gameStarted = false;



    /* key: room number (acc to roomcount)
     *  value: List(p.x, p.y, width, height)
     */

    public static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static int xyTo1D(Position p) {
        return (p.y * maxWidth) + p.x;
    }

    public static int xy1D(int x, int y) {
        return (y * maxWidth) + x;
    }

    public static void addRoom(Position p, int width, int height) {

        // base cases
        if (width <= 1 || height <= 1) {  //if width or height is less than or equal to 1
            return;
        }
        if (p.x + width + 2 > maxWidth || p.y + height + 2 > maxHeight) { // if the dimension of the room (including
            return;                                                       // walls) is out of bounds of the window's
        }                                                                 // dimensions
        for (int i = 0; i < width + 3; i++) {   //check if there is space for this room to be made (if where the room would be all = Tileset.NOTHING
            for (int j = 0; j < height + 3; j++) {
                if (world[p.x + i][p.y + j] != Tileset.NOTHING) {
                    return;
                }
            }
        }

        //keeping track of rooms
        ArrayList<Integer> roomVal = new ArrayList<>();
        roomVal.add(p.x);
        roomVal.add(p.y);
        roomVal.add(width);
        roomVal.add(height);

        roomDict.put(roomCount, roomVal);


        for (int i = 0; i < width + 2; i++) {
            for (int j = 0; j < height + 2; j++) {
                Position t = p.shift(i, j);
                world[t.x][t.y] = Tileset.WALL;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Position t = p.shift(1, 1);
                world[t.x + i][t.y + j] = Tileset.FLOOR;
                wqu.union((WIDTH * HEIGHT), xy1D(t.x + i, t.y + j));
                floorList.add(xy1D(t.x + i, t.y + i));
            }
        }
        roomCount++;
    }

    private static int roomX(int room) {
        return roomDict.get(room).get(0);
    }

    private static int roomY(int room) {
        return roomDict.get(room).get(1);
    }

    private static int roomWidth(int room) {
        return roomDict.get(room).get(2);
    }

    private static int roomHeight(int room) {
        return roomDict.get(room).get(3);
    }


    public static int closestRoom(int room) {
        //find the closest width and height to p.x and p.y. using distance formula, determine the closest room. to
        // connect to a room, it must be unvisited
        Position roomPos = new Position(roomX(room), roomY(room));
        ArrayList<Integer> pList = new ArrayList<>();
        ArrayList<Integer> qList = new ArrayList<>();
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Integer> sortedDistances;
        int minDistance = 0;

        for (int i = 0; i < roomCount; i++) {
            pList.add(roomX(i));
            qList.add(roomY(i));
        }

        for (int i = 0; i < roomCount; i++) {
            distances.add(distance(roomPos, new Position(pList.get(i), qList.get(i))));
        }

        sortedDistances = new ArrayList<>(distances);
        sort(sortedDistances, reverseOrder());

        for (int bruh : sortedDistances) {
            if (bruh == 0) {
                continue;
            } else {
                minDistance = bruh;
            }
        }
        return distances.indexOf(minDistance);
    }

    private static int distance(Position p1, Position p2) {
        return (int) sqrt(pow((p2.x + p1.x), 2) + pow((p2.y + p1.y), 2));
    }

    private static void makeHallway(int room1, int RFTClosestRoom) {
        List<String> urmom = trial.Thing.findPath(world, randomFloorTile(room1).x, randomFloorTile(room1).y, randomFloorTile(RFTClosestRoom));
        int xval;
        int yval;
        for (String s : urmom) {
            xval = Integer.parseInt(s.split("\\D")[1]);
            yval = Integer.parseInt(s.split("\\D")[3]);
            world[xval][yval] = Tileset.FLOOR;
            floorList.add(xy1D(xval, yval));
            wqu.union(WIDTH * HEIGHT, xy1D(xval, yval));
            if (world[xval - 1][yval] == Tileset.NOTHING) {
                world[xval - 1][yval] = Tileset.WALL;
            }
            if (world[xval + 1][yval] == Tileset.NOTHING) {
                world[xval + 1][yval] = Tileset.WALL;
            }
            if ((world[xval][yval - 1] == Tileset.NOTHING)) {
                world[xval][yval - 1] = Tileset.WALL;
            }
            if ((world[xval][yval + 1] == Tileset.NOTHING)) {
                world[xval][yval + 1] = Tileset.WALL;
            }
        }
    }

    public static Position randomFloorTile(int room) {
        return new Position(RANDOM.nextInt(roomX(room), (roomX(room) + roomWidth(room))), RANDOM.nextInt(roomY(room), (roomY(room) + roomHeight(room))));
    }

    public static void drawWorld() {
        for (int i = 0; i < RANDOM.nextInt(WIDTH, WIDTH * HEIGHT); i++) {
            Position p = new Position(RANDOM.nextInt(2, WIDTH - 2), RANDOM.nextInt(2, HEIGHT - 2));
            addRoom(p, RANDOM.nextInt(WIDTH / 5), RANDOM.nextInt(HEIGHT / 3));
        }
        for (int i = 0; i < roomCount; i++) {
            makeHallway(i, closestRoom(i));
        }
        avatarX = RANDOM.nextInt(WIDTH);
        avatarY = RANDOM.nextInt(HEIGHT);
        while (!floorList.contains(xy1D(avatarX, avatarY))) {
            avatarX = RANDOM.nextInt(WIDTH);
            avatarY = RANDOM.nextInt(HEIGHT);
        }
        world[avatarX][avatarY] = Tileset.AVATAR;
    }

    /** --------------------------------------------------------------------------------------------------------*/

    private static void mainMenu() {
        drawScreen(WIDTH / 2, HEIGHT * 2 / 3, "CS61B: THE GAME");
        Font fontSmall = new Font("Monaco", Font.BOLD, 17);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 3, "New Game (N)\nLoad Game (L)\nQuit (Q)");
    }

    private static void newGame() {
        StdDraw.clear(Color.BLACK);
        drawScreen(WIDTH / 2, HEIGHT / 2 + 10, "ENTER SEED:");
        drawScreen(WIDTH / 2, HEIGHT / 2 - 10, "Save (S)\nQuit(Q)");
        seedInput();
        loadSeed();
    }

    private static void loadGame() {
        //just loads up the previous game, so store the seed and movements.
    }

    private static void quitGame() {
        if (gameStarted) {
            return;
        } else if (!gameStarted) {
            System.exit(0);
        }
    }

    private static void quitAndSaveGame() {
        if (!gameStarted) {
            return;
        }
        if (gameStarted) {
            movement += newMovement;
            loadSeed();
            newMovement = "";
            System.exit(0);
        }
    }

    public static void mainMenuHandler() {
        String input = "";
        mainMenu();
        if (StdDraw.nextKeyTyped() == 'n' || StdDraw.nextKeyTyped() == 'N') {
            newGame();
        } else if (StdDraw.nextKeyTyped() == 'l' || StdDraw.nextKeyTyped() == 'L') {
            loadGame();
        } else if (StdDraw.nextKeyTyped() == 'q' || StdDraw.nextKeyTyped() == 'Q') {
            quitGame();
        }
    }

    public static void drawScreen(int width, int height, String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(width, height, s);

        StdDraw.show();
    }

    public static void drawScreen(int width, int height, Long s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(width, height, s);

        StdDraw.show();
    }

    public static Long seedInput() {
        Long input = Long.valueOf("");
        drawScreen(WIDTH / 2, HEIGHT / 2, input);

        input += StdDraw.nextKeyTyped();
        drawScreen(WIDTH / 2, HEIGHT / 2, input);
        
        SEED = input;
        return SEED;
    }

    public static void loadSeed() {
        loadedSeed = SEED + "," + movement;
    }

    /** --------------------------------------------------------------------------------------------------------*/


    public static void moveLeft() {
        if (world[avatarX - 1][avatarY] == Tileset.FLOOR && world[avatarX][avatarY] == Tileset.AVATAR) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX--;
            world[avatarX][avatarY] = Tileset.AVATAR;
        }
        newMovement += "A";
    }

    public static void moveRight() {
        if (world[avatarX + 1][avatarY] == Tileset.FLOOR && world[avatarX][avatarY] == Tileset.AVATAR) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX++;
            world[avatarX][avatarY] = Tileset.AVATAR;
        }
        newMovement += "D";
    }

    public static void moveUp() {
        if (world[avatarX][avatarY + 1] == Tileset.FLOOR && world[avatarX][avatarY] == Tileset.AVATAR) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarY++;
            world[avatarX][avatarY] = Tileset.AVATAR;
        }
        newMovement += "W";
    }

    public static void moveDown() {
        if (world[avatarX][avatarY - 1] == Tileset.FLOOR && world[avatarX][avatarY] == Tileset.AVATAR) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarY--;
            world[avatarX][avatarY] = Tileset.AVATAR;
        }
        newMovement += "S";
    }

    /** --------------------------------------------------------------------------------------------------------*/


    public static TETile[][] main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }


        SEED = Long.parseLong(args[0]);
        RANDOM = new Random(SEED);

        drawWorld();
        ter.renderFrame(world);
        return world;
    }
}
//Changes made today: added so that seed is inputted through running the program, similar to lab12
// implemented interactWithInputString()