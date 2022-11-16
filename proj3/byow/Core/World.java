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
            world[p.x+i][p.y] = Tileset.WALL;
            Position newPos = p.shift(0, height + 1);
            world[newPos.x+i][newPos.y] = Tileset.WALL;
            Position newPost = p.shift(0, i);
<<<<<<< HEAD
            world[newPost.x][newPost.y] = Tileset.FLOWER;
            Position right = p.shift(width+1, i);
            world[right.x][right.y] = Tileset.WALL;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++){
                Position t = p.shift(1, 1);
                world[t.x + i][t.y + j];
=======
            world[newPost.x][newPost.y] = Tileset.WALL;
            Position newRight = p.shift(width + 1, i);
            world[newRight.x][newRight.y] = Tileset.WALL;
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Position t = p.shift(1, 1);
                world[t.x + i][t.y + j] = Tileset.FLOOR;
>>>>>>> 4ad843197ffbae47866d0ac8a06134a1d4a3c85f
            }
        }

        //makes a room
//        for (int i = 0; i < width + 2; i++) {
//            for (int j = 0; j < height + 2; j++) {
//                if (i == 0 || i == width + 2) {
//                    world[p.x + i][p.y + j] = Tileset.WALL;
//                }
//                if (i > 0 && i < width + 2) {
//                    if (j == 0 && j == height + 2) {
//                        world[p.x + i][p.y + j] = Tileset.WALL;
//                    }
//                    if (j > 0 && j < height + 2) {
//                        world[p.x + i][p.y + j] = Tileset.FLOOR;
//                    }
//                }
//            }
//        }

    }

    public static void drawWorldTest(TETile[][] tiles) {
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