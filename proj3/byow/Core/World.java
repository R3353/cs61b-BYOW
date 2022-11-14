package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.Random;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class World {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    private static final int maxWidth = WIDTH - 1;
    private static final int maxHeight = HEIGHT - 1;

    private static long SEED;
    private static Random RANDOM;

    private WeightedQuickUnionUF wallsWQU;
    private WeightedQuickUnionUF floorsWQU;

    public World(int seed) {
        SEED = seed;
        RANDOM = new Random(SEED);

        wallsWQU = new WeightedQuickUnionUF(WIDTH * HEIGHT);
        floorsWQU = new WeightedQuickUnionUF(WIDTH * HEIGHT);
    }

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
        if (width <= 0 || height <= 0) {
            return;
        }

        //base case where room isnt out of bounds

        for (int i = 0; i < width; i++) {
            world[p.x + i][p.y] = Tileset.WALL;
        }


    }

    public static void drawWorld(TETile[][] tiles) {
        Position p = new Position(2, 5);
        addRoom(tiles, p, 3, 4);
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

        drawWorld(world);

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

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.NOTHING;
            default: return Tileset.NOTHING;
        }
    }

}