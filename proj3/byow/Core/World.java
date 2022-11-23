package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

    private static final long SEED = 12345;
    private static final Random RANDOM = new Random(SEED);

    private static int roomCount = 0;
    private static HashMap<Integer, ArrayList<Integer>> roomDict = new HashMap<>();
    private static ArrayList<Boolean> visited = new ArrayList<>(roomCount);

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
        return (p.x * WIDTH) + p.y;
    }

    public static void addRoom(TETile[][] world, Position p, int width, int height) {
        // p = top left wall tile, width and height = dimension of room.
        // would have to add 2 to the wall iteration to account for left and right wall

        // base cases
        if (width < 1 || height < 1) {  //if width or height is less than or equal to 1
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
            }
        }
        roomCount++;

//        int targetX;
//        int targetY;
//
//        int sortingHat = RANDOM.nextInt(4); /** NEED TO MAKE SURE IT'S NOT CORNER. also make sure there is no opening where two rooms touch. */
//        if (sortingHat == 0) { // bottom
//            targetX = p.x + ((width+1)/2);
//            targetY = p.y;
//            world[targetX][targetY] = Tileset.FLOOR;
//            world[targetX][targetY - 1] = Tileset.AVATAR;
//        }
//        else if (sortingHat == 1) { // top
//            targetX = p.x+((width+1)/2);
//            targetY = p.y + height + 1;
//            world[targetX][targetY] = Tileset.FLOOR;
//            world[targetX][targetY + 1] = Tileset.AVATAR;
//        }
//        else if (sortingHat == 2) { // left
//            targetX = p.x;
//            targetY = p.y + ((height+1)/2);
//            world[targetX][targetY] = Tileset.FLOOR;
//            world[targetX - 1][targetY] = Tileset.AVATAR;
//        }
//        else { // right
//            targetX = p.x + width + 1;
//            targetY = p.y + ((height+1)/2);
//            world[targetX][targetY] = Tileset.FLOOR;
//            world[targetX + 1][targetY] = Tileset.AVATAR;
//        }
//        roomList.add(new Position(targetX, targetY));
    }

    /*to make hallways:
    * 1) make sure room has not been visited yet
    * 2)
    * makehallways: iterates through all rooms
    * makehallway: makes one hallway
     */

    public static int closestRoom(int room) {
        //find the closest width and height to p.x and p.y. using distance formula, determine the closest room. to
        // connect to a room, it must be unvisited
        Position roomPos = new Position(roomX(room), roomY(room));
        ArrayList<Integer> pList = new ArrayList();
        ArrayList<Integer> qList = new ArrayList();
        ArrayList<Integer> distances = new ArrayList();
        ArrayList<Integer> sortedDistances = new ArrayList<>();
        int minDistance = 0;

        for (int i = 0; i < roomCount; i++) {
//            if (visited.get(i) || room == 0) {
//                pList.add(roomX(room));
//                qList.add(roomY(room));
//            }
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
            } else if (bruh != 0) {
                minDistance = bruh;
            }
        }

        return distances.indexOf(minDistance);
    }

    private static int distance(Position p1, Position p2) {
        return (int) sqrt(pow((p2.x + p1.x), 2) + pow((p2.y + p1.y), 2));
    }

    private static void makeHallway(TETile[][] tiles, int room1, int RFTClosestRoom) {
        //NEED TO MODIFY SO IT CHOOSES RANDOM TILE INSIDE OF ROOM
        // if all rooms are visited, then yyyyyyur done
        List<String> urmom = trial.Thing.findPath(tiles, RANDOM.nextInt(roomX(room1), roomWidth(room1)), RANDOM.nextInt(roomY(room1), roomHeight(room1)), randomFloorTile(RFTClosestRoom));
        /**^^^^apparently not working bc "bound must be greater than origin. !!!!!!!!! what the FRICK!! */
        int xval;
        int yval;
        for (int i = 0; i < urmom.size(); i++) {
            xval = Integer.parseInt(String.valueOf(urmom.get(i).charAt(1)));
            yval = Integer.parseInt(String.valueOf(urmom.get(i).charAt(3)));
            tiles[xval][yval] = Tileset.FLOWER;
        }
    }

    public static Position randomFloorTile(int room) {
        //EPIC BRUH MOMENT
        return new Position(RANDOM.nextInt(roomX(room), roomWidth(room)), RANDOM.nextInt(roomY(room), roomHeight(room)));
    }

    public static void drawWorldTest(TETile[][] tiles) {
        for (int i = 0; i < RANDOM.nextInt(WIDTH, WIDTH*HEIGHT); i++) {
            Position p = new Position(RANDOM.nextInt(3, WIDTH-3), RANDOM.nextInt(3, HEIGHT - 3));
            addRoom(tiles, p, RANDOM.nextInt(WIDTH/4), RANDOM.nextInt(HEIGHT/2));
            //pathExists(tiles);
        }
//        Position p = new Position(10, 10);
//        addRoom(tiles, p, 2, 2);
//        Position p2 = new Position( 17, 17);
//        addRoom(tiles, p2, 2, 2);
//        makeHallway(tiles, roomList.get(0));
//        Position p3 = new Position(30, 10);
//        addRoom(tiles, p3, 2, 2);
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        drawWorldTest(world);
        makeHallway(world, 1, closestRoom(1));
        ter.renderFrame(world);
    }
}