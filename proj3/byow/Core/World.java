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

    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int maxWidth = WIDTH - 1;
    private static final int maxHeight = HEIGHT - 1;

    private static final long SEED = 12345;
    private static final Random RANDOM = new Random(SEED);

    private WeightedQuickUnionUF wallsWQU;
    private WeightedQuickUnionUF floorsWQU;


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
        if (width <= 1 || height <= 1) {  //if width or height is less than or equal to 0
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
        if (p.y == 0 || p.y == 1 || p.y == 2 || p.y + height == maxHeight || p.y + 1 + height== maxHeight || p.y + 2 + height== maxHeight || p.y + 3 + height == maxHeight) {
            return;
        }

        if (p.x == 0 || p.x == 1 || p.x == 2 || p.x + width == maxWidth || p.x + 1 + width == maxWidth || p.x + 2 + width == maxWidth || p.x + 3 + width == maxWidth) {
            return;
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
        if (width == 1) {
            world[p.x][p.y+ ((height)/2)]= Tileset.FLOOR;
            world[p.x-1][p.y+ ((height)/2)]= Tileset.AVATAR;
            return;
        } else if (height == 1) {
            world[p.x+((width)/2)][p.y] = Tileset.FLOOR;
            world[p.x+((width)/2)][p.y-1] = Tileset.AVATAR;
            return;
        }

        int sortingHat = RANDOM.nextInt(4); /** NEED TO MAKE SURE IT'S NOT CORNER. also make sure there is no opening where two rooms touch. */
        if (sortingHat == 0) { // bottom
            world[p.x+((width)/2)][p.y] = Tileset.FLOOR;
            world[p.x+((width)/2)][p.y - 1] = Tileset.AVATAR;
        }
        else if (sortingHat == 1) { // top
            world[p.x+((width)/2)][p.y + height + 1] = Tileset.FLOOR;
            world[p.x+((width)/2)][p.y + height + 2] = Tileset.AVATAR;
        }
        else if (sortingHat == 2) { // left
            world[p.x][p.y+ ((height)/2)] = Tileset.FLOOR;
            world[p.x - 1][p.y+ ((height)/2)] = Tileset.AVATAR;
        }
        else { // right
            world[p.x + width + 1][p.y + ((height)/2)] = Tileset.FLOOR;
            world[p.x + width + 2][p.y + ((height)/2)] = Tileset.AVATAR;
        }
    }

    public static void drawWorldTest(TETile[][] tiles) {
        for (int i = 0; i < RANDOM.nextInt(HEIGHT, WIDTH*HEIGHT); i++) {
            Position p = new Position(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
            addRoom(tiles, p, RANDOM.nextInt(WIDTH/10), RANDOM.nextInt(HEIGHT/5));
        }
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