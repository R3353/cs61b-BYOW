package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

import static byow.Core.ShortestPath.pathExists;

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

    private static int room = 0;

    private static ArrayList<Position> roomList = new ArrayList<>();



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
        room++;

        int targetX;
        int targetY;

        int sortingHat = RANDOM.nextInt(4); /** NEED TO MAKE SURE IT'S NOT CORNER. also make sure there is no opening where two rooms touch. */
        if (sortingHat == 0) { // bottom
            targetX = p.x + ((width+1)/2);
            targetY = p.y;
            world[targetX][targetY] = Tileset.FLOOR;
            world[targetX][targetY - 1] = Tileset.AVATAR;
        }
        else if (sortingHat == 1) { // top
            targetX = p.x+((width+1)/2);
            targetY = p.y + height + 1;
            world[targetX][targetY] = Tileset.FLOOR;
            world[targetX][targetY + 1] = Tileset.AVATAR;
        }
        else if (sortingHat == 2) { // left
            targetX = p.x;
            targetY = p.y + ((height+1)/2);
            world[targetX][targetY] = Tileset.FLOOR;
            world[targetX - 1][targetY] = Tileset.AVATAR;
        }
        else { // right
            targetX = p.x + width + 1;
            targetY = p.y + ((height+1)/2);
            world[targetX][targetY] = Tileset.FLOOR;
            world[targetX + 1][targetY] = Tileset.AVATAR;
        }
        roomList.add(new Position(targetX, targetY));
    }

    private static void makeHallway(TETile[][] tiles, Position room1) {
        pathExists(tiles, new Node(room1.x, room1.y, xyTo1D(room1)));
    }


    public static void drawWorldTest(TETile[][] tiles) {
//        for (int i = 0; i < RANDOM.nextInt(WIDTH, WIDTH*HEIGHT); i++) {
//            Position p = new Position(RANDOM.nextInt(3, WIDTH-3), RANDOM.nextInt(3, HEIGHT - 3));
//            addRoom(tiles, p, RANDOM.nextInt(WIDTH/4), RANDOM.nextInt(HEIGHT/2));
//            //pathExists(tiles);
//        }

        Position p = new Position(10, 10);
        addRoom(tiles, p, 2, 2);
        Position p2 = new Position( 17, 17);
        addRoom(tiles, p2, 2, 2);
        makeHallway(tiles, roomList.get(0));
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
        ter.renderFrame(world);
    }
}