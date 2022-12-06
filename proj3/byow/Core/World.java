package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.*;


import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

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
    private static ArrayList<Integer> wallList = new ArrayList<>();
    private static TETile[][] world;
    private static int avatarX;
    private static int avatarY;
    private static int gateX;
    private static int gateY;
    private static TETile avatar = Tileset.REESE;
    private static TETile gate = Tileset.LOCKED_DOOR;

    private static String movement = "";
    private static final ArrayList<String> newMovement = new ArrayList<>();

    public static TERenderer ter = new TERenderer();

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

    public static int xy1D(int x, int y) {
        return (y * maxWidth) + x;
    }

    public static void addRoom(Position p, int width, int height) {

        // base cases
        if (width <= 1 || height <= 1) {
            return;
        }
        if (p.x + width + 2 > maxWidth || p.y + height + 2 > maxHeight) {
            return;
        }
        for (int i = 0; i < width + 3; i++) {
            for (int j = 0; j < height + 3; j++) {
                if (world[p.x + i][p.y + j] != Tileset.NOTHING) {
                    return;
                }
            }
        }

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
                wallList.add(xy1D(t.x, t.y));
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
        for (String s : urmom) {
            int xval = Integer.parseInt(s.split("\\D")[1]);
            int yval = Integer.parseInt(s.split("\\D")[3]);
            world[xval][yval] = Tileset.FLOOR;
            floorList.add(xy1D(xval, yval));
            if (wallList.contains(xy1D(xval, yval))) {
                wallList.remove((Object) xy1D(xval, yval));
            }
            wqu.union(WIDTH * HEIGHT, xy1D(xval, yval));
            if (world[xval - 1][yval] == Tileset.NOTHING) {
                world[xval - 1][yval] = Tileset.WALL;
                wallList.add(xy1D(xval - 1, yval));
                if (floorList.contains(xy1D(xval - 1, yval))) {
                    floorList.remove((Object) xy1D(xval - 1, yval));
                }
            }
            if (world[xval + 1][yval] == Tileset.NOTHING) {
                world[xval + 1][yval] = Tileset.WALL;
                wallList.add(xy1D(xval + 1, yval));
                if (floorList.contains(xy1D(xval + 1, yval))) {
                    floorList.remove((Object) xy1D(xval + 1, yval));
                }
            }
            if ((world[xval][yval - 1] == Tileset.NOTHING)) {
                world[xval][yval - 1] = Tileset.WALL;
                wallList.add(xy1D(xval, yval - 1));
                if (floorList.contains(xy1D(xval, yval - 1))) {
                    floorList.remove((Object) xy1D(xval, yval - 1));
                }
            }
            if ((world[xval][yval + 1] == Tileset.NOTHING)) {
                world[xval][yval + 1] = Tileset.WALL;
                wallList.add(xy1D(xval, yval + 1));
                if (floorList.contains(xy1D(xval, yval + 1))) {
                    floorList.remove((Object) xy1D(xval, yval + 1));
                }
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
        gateX = RANDOM.nextInt(2, WIDTH);
        gateY = RANDOM.nextInt(2, HEIGHT);
        while (true) {
            if (world[gateX][gateY] == Tileset.WALL) {
                if ((world[gateX + 1][gateY] == Tileset.FLOOR || world[gateX - 1][gateY] == Tileset.FLOOR || world[gateX][gateY - 1] == Tileset.FLOOR || world[gateX][gateY + 1] == Tileset.FLOOR)) {
                    world[gateX][gateY] = gate;
                    break;
                }
            }
            gateX = RANDOM.nextInt(2, WIDTH);
            gateY = RANDOM.nextInt(2, HEIGHT);
        }
        world[gateX][gateY] = gate;
        world[avatarX][avatarY] = avatar;
    }

    public static void mainMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        bigFont();
        drawScreen(WIDTH / 2, 17, "CS61B: THE GAME");
        smallFont();
        drawScreen((WIDTH / 2), 13, "New Game (N) \t Load Game (L) \t Replay Game (R) \t Change Avatar (A) \t Quit (Q)");
    }

    public static void newGame() {
        StdDraw.clear(Color.BLACK);
        bigFont();
        drawScreen(WIDTH / 2, HEIGHT / 2 + 10, "ENTER SEED:");
        smallFont();
        drawScreen(WIDTH / 2, HEIGHT / 2 - 10, "Save (S)\t\tBack (B)");
        SEED = seedInput();
        StdDraw.pause(2000);
        loadSeed(SEED);
        System.out.println("SEED: " + SEED);
        allowMovement();
    }

    public static TETile[][] newGameString(Long seed) {
        SEED = seed;
        return loadSeed(seed);
    }

    private static void loadGame() {
        In in = new In("./byow/Saves/saved-world");
        if (in.isEmpty()) {
            return;
        } else {
            Long seed = Long.parseLong(in.readLine());
            String list = in.readLine();
            String[] newList = list.split("");
            List<String> movementList = new ArrayList<>();
            for (String obj : newList) {
                if (Objects.equals(obj, "[") || Objects.equals(obj, "]") || Objects.equals(obj, ",")) {
                    continue;
                }
                movementList.add(obj);
            }
            loadSeed(seed);
            doMovements(movementList);
            allowMovement();
        }
    }

    public static void doMovements(List<String> movementList) {
        while (!movementList.isEmpty()) {
            String some = movementList.get(0).toLowerCase();
            switch (some) {
                case "w" -> moveUp();
                case "a" -> moveLeft();
                case "d" -> moveRight();
                case "s" -> moveDown();
            }
            movementList.remove(some);
        }
    }

    public static void chooseAvatarScreen() {
        StdDraw.clear(Color.BLACK);
        bigFont();
        drawScreen(WIDTH / 2, HEIGHT * 2 / 3, "CHOOSE AVATAR!");
        smallFont();
        drawScreen(WIDTH / 2, 17, "ʊ [DEFAULT] (1)");
        drawScreen(WIDTH / 2, (int) 15.5, "⁓ Worm (2)");
        drawScreen(WIDTH / 2, 13, "☃ Snowman (3)");
        drawScreen(WIDTH / 2, (int) 11.5, "⏾ Mr. Moon (4)");
        drawScreen(WIDTH / 2, 9, "☺ Smiley (5)");
        chooseAvatar();
    }

    public static void chooseAvatar() {
        int input = 0;
        while (input < 1) {
            if (StdDraw.hasNextKeyTyped()) {
                char something = StdDraw.nextKeyTyped();
                if (something == '1') {
                    input += 1;
                }
                if (something == '2') {
                    input += 2;
                    avatar = Tileset.WORM;
                } else if (something == '3') {
                    input += 3;
                    avatar = Tileset.SNOWMAN;
                } else if (something == '4') {
                    input += 4;
                    avatar = Tileset.MRMOON;
                } else if (something == '5') {
                    input += 5;
                    avatar = Tileset.SMILEY;
                }
            }
        }
        StdDraw.clear(Color.BLACK);
        bigFont();
        drawScreen(WIDTH / 2, 17, "AVATAR CHOSEN:");
        biggerFont();
        if (input == 1) {
            drawScreen(WIDTH / 2, 14, "ʊ");
        } else if (input == 2) {
            drawScreen(WIDTH / 2, 14, "⁓");
            smallFont();
            drawScreen(WIDTH / 2, (int) 12.5, "Worm");
        } else if (input == 3) {
            drawScreen(WIDTH / 2, 14, "☃");
            smallFont();
            drawScreen(WIDTH / 2, (int) 12.5, "Snowman");
        } else if (input == 4) {
            drawScreen(WIDTH / 2, 14, "⏾");
            smallFont();
            drawScreen(WIDTH / 2, (int) 12.5, "Mr. Moon");
        } else if (input == 5) {
            drawScreen(WIDTH / 2, 14, "☺");
            smallFont();
            drawScreen(WIDTH / 2, (int) 12.5, "Smiley");
        }
        StdDraw.pause(2000);
        mainMenuHandler();
    }

    public static void quitGame() {
        getOut(SEED);
        System.exit(0);
    }

    public static void mainMenuHandler() {
        mainMenu();
        String input = "";
        while (input.length() < 1) {
            if (StdDraw.hasNextKeyTyped()) {
                char something = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (something == 'n') {
                    input += something;
                    newGame();
                } else if (something == 'l') {
                    input += something;
                    loadGame();
                } else if (something == 'a') {
                    input += something;
                    chooseAvatarScreen();
                } else if (something == 'q') {
                    input += something;
                    quitGame();
                } else if (something == 'r') {
                    input += something;
                    replayGame();
                }
            }
        }
    }

    public static void replayGame() {
        In in = new In("./byow/Saves/saved-world");
        if (in.isEmpty()) {
            return;
        } else {
            Long seed = Long.parseLong(in.readLine());
            String list = in.readLine();
            String[] newList = list.split("");
            List<String> movementList = new ArrayList<>();
            for (String obj : newList) {
                if (Objects.equals(obj, "[") || Objects.equals(obj, "]") || Objects.equals(obj, ",")) {
                    continue;
                }
                movementList.add(obj);
            }
            loadSeed(seed);
            doSlowMovements(movementList);
            allowMovement();
        }
    }

    public static void doSlowMovements(List<String> movementList) {
        while (!movementList.isEmpty()) {
            StdDraw.pause(100);
            String some = movementList.get(0).toLowerCase();
            switch (some) {
                case "w" -> moveUp();
                case "a" -> moveLeft();
                case "d" -> moveRight();
                case "s" -> moveDown();
            }
            movementList.remove(some);
        }
    }

    public static void drawScreen(int width, int height, String s) {
        StdDraw.text(width, height, s);
        StdDraw.show();
    }

    public static Long seedInput() {
        String input = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char something = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (something == 's' && input.length() > 1) {
                    break;
                } else if (something == 'b') {
                    mainMenuHandler();
                } else if (Character.isAlphabetic(something) || Character.isSpaceChar(something) || !Character.isDigit(something)) {
                    continue;
                } else {
                    StdDraw.clear(Color.BLACK);
                    bigFont();
                    drawScreen(WIDTH / 2, HEIGHT / 2 + 10, "ENTER SEED:");
                    smallFont();
                    drawScreen(WIDTH / 2, HEIGHT / 2 - 10, "Save (S)\t\tBack (B)");
                    input += something;
                    drawScreen(WIDTH / 2, HEIGHT / 2, input);
                }
            }
        }
        StdDraw.clear(Color.BLACK);
        bigFont();
        drawScreen(WIDTH / 2, HEIGHT / 2 + 10, "SEED ENTERED:");
        smallFont();
        drawScreen(WIDTH / 2, HEIGHT / 2, input);
        Long seed = Long.parseLong(input);
        return seed;
    }

    private static void bigFont() {
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
    }

    private static void smallFont() {
        Font fontSmall = new Font("Monaco", Font.BOLD, 17);
        StdDraw.setFont(fontSmall);
    }

    private static void biggerFont() {
        Font bigger = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(bigger);
    }

    public static TETile[][] loadSeed(Long seed) {
        String[] ags = new String[10];
        ags[0] = String.valueOf(seed);
        return main(ags);
    }

    public static void allowMovement() {
        boolean val = true;
        while (val) {
            mouse();
            if (StdDraw.hasNextKeyTyped()) {
                char some = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (some == 'w') {
                    moveUp();
                    movement = "w";
                } else if (some == 'a') {
                    moveLeft();
                    movement = "a";
                } else if (some == 'd') {
                    moveRight();
                    movement = "d";
                } else if (some == 's') {
                    moveDown();
                    movement = "s";
                    // SAVE AND QUIT
                } else if (some == ':') {
                    while (!StdDraw.hasNextKeyTyped()) {
                        continue;
                    }
                    if (Character.toLowerCase(StdDraw.nextKeyTyped()) == 'q') {
                        System.out.println(newMovement);
                        val = false;
                        quitGame();
                    }
                }
                newMovement.add(movement);
            }
        }
    }

    public static void moveLeft() {
        if (world[avatarX - 1][avatarY] == Tileset.FLOOR && world[avatarX][avatarY] == avatar) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX--;
            world[avatarX][avatarY] = avatar;
            ter.renderFrame(world);
        }
    }

    public static void moveRight() {
        if (world[avatarX + 1][avatarY] == Tileset.FLOOR && world[avatarX][avatarY] == avatar) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX++;
            world[avatarX][avatarY] = avatar;
            ter.renderFrame(world);
        }
    }

    public static void moveUp() {
        if (world[avatarX][avatarY + 1] == Tileset.FLOOR && world[avatarX][avatarY] == avatar) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarY++;
            world[avatarX][avatarY] = avatar;
            ter.renderFrame(world);
        }
    }

    public static void moveDown() {
        if (world[avatarX][avatarY - 1] == Tileset.FLOOR && world[avatarX][avatarY] == avatar) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarY--;
            world[avatarX][avatarY] = avatar;
            ter.renderFrame(world);
        }
    }

    public static void mouse() {
        java.util.Date date = new java.util.Date();
        StdDraw.clear(Color.BLACK);
        ter.renderFrame(world);
        Font fontSmall = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(fontSmall);
        StdDraw.setPenColor(Color.white);
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        StdDraw.textLeft(WIDTH / 2, maxHeight, String.valueOf(date));
        StdDraw.show();
        if (mouseX <= 0 || mouseX >= maxWidth || mouseY <= 0 || mouseY >= maxHeight) {
            return;
        } else if (world[mouseX][mouseY] == Tileset.NOTHING) {
            StdDraw.textRight(maxWidth, maxHeight, "THE VOID.™");
            StdDraw.show();
        } else if (world[mouseX][mouseY] == Tileset.WALL) {
            StdDraw.textRight(maxWidth, maxHeight, "amogus wall");
            StdDraw.show();
        } else if (world[mouseX][mouseY] == Tileset.FLOOR) {
            StdDraw.textRight(maxWidth, maxHeight, Tileset.FLOOR.description());
            StdDraw.show();
        } else if (world[mouseX][mouseY] == avatar) {
            StdDraw.textRight(maxWidth, maxHeight, avatar.description());
            StdDraw.show();
        } else if (world[mouseX][mouseY] == gate) {
            StdDraw.textRight(maxWidth, maxHeight, gate.description());
            StdDraw.show();
        }
    }

    private static void getOut(Long seed) {
        String expString = World.newMovement.toString();
        Out exporter = new Out("./byow/Saves/saved-world");
        exporter.println(seed);
        exporter.println(expString);
    }

    public static TETile[][] main(String[] args) {
        ter.initialize(WIDTH, HEIGHT);
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