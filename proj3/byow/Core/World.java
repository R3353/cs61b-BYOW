package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.Random;

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

    private WeightedQuickUnionUF wallsWQU;
    private WeightedQuickUnionUF floorsWQU;

//    public World(int seed) {
//        SEED = seed;
//        RANDOM = new Random(SEED);
//
//        wallsWQU = new WeightedQuickUnionUF(WIDTH * HEIGHT);
//        floorsWQU = new WeightedQuickUnionUF(WIDTH * HEIGHT);
//    }

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

        private int xyTo1D(Position p) {
            return p.x * WIDTH + p.y;
        }
    }

    public static void addRoom(TETile[][] world, Position p, int width, int height) {
        // p = top left wall tile, width and height = dimension of room.
        // would have to add 2 to the wall iteration to account for left and right wall

        // base cases
        if (width <= 0 || height <= 0) {  //if width or height is less than or equal to 0
            return;
        }
//        if (p.x + width + 2 > maxWidth || p.y + height + 2 > maxHeight) { // if the dimension of the room (including
//            return;                                                       // walls) is out of bounds of the window's
//        }                                                                 // dimensions
//        for (int i = 0; i < width + 2; i++) {   //check if there is space for this room to be made (if where the room would be all = Tileset.NOTHING
//            for (int j = 0; j < height + 2; j++) {
//                if (world[p.x + i][p.y + j] != Tileset.NOTHING) {
//                    break;
//                }
//                break;
//            }
//            return;
//        }

        for (int i = 0; i < width + 2; i++) {

        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Position t = p.shift(1, 1);
                world[t.x + i][t.y + j] = Tileset.FLOOR;
            }
        }

        int sortingHat = RANDOM.nextInt(4);
        if (sortingHat == 0) { // bottom
            world[p.x + RANDOM.nextInt(width + 1)][p.y] = Tileset.FLOWER;}
        else if (sortingHat == 1) { // top
            world[p.x + RANDOM.nextInt(width + 1)][p.y + height + 1] = Tileset.GRASS;}
        else if (sortingHat == 2) { // left
            world[p.x][p.y + RANDOM.nextInt(height + 1)] = Tileset.WATER;}
        else if (sortingHat == 3) { // right
            world[p.x + width + 1][p.y + RANDOM.nextInt(height + 2)] = Tileset.AVATAR;}

    }

    public static void drawWorldTest(TETile[][] tiles) {
        Position p = new Position(2, 5);
        addRoom(tiles, p, 3, 4);
        Position pp = new Position(9, 9);
        addRoom(tiles, pp, 5, 8);
        Position ppp = new Position(15, 20);
        addRoom(tiles, ppp, 5, 5);
        Position pppp = new Position(20, 5);
        addRoom(tiles, pppp, 2, 2);
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

        // fills in a block 14 tiles wide by 4 tiles tall
//        for (int x = 20; x < 35; x += 1) {
//            for (int y = 5; y < 10; y += 1) {
//                world[x][y] = Tileset.WALL;
//            }
//        }

        // draws the world to the screen
        ter.renderFrame(world);

//        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
//        fillWithRandomTiles(randomTiles);
//
//        ter.renderFrame(randomTiles);
    }


    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
//    public static void fillWithRandomTiles(TETile[][] tiles) {
//        int height = tiles[0].length;
//        int width = tiles.length;
//        for (int x = 0; x < width; x += 1) {
//            for (int y = 0; y < height; y += 1) {
//                tiles[x][y] = randomTile();
//            }
//        }
//    }

    /**
     * Picks a RANDOM tile with a 33% change of being
     * a wall, 33% chance of being a flower, and 33%
     * chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0:
                return Tileset.WALL;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.NOTHING;
            default:
                return Tileset.NOTHING;
        }
    }

}