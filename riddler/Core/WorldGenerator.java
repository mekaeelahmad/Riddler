package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.TERenderer;
import java.util.*;
import java.util.List;


/**Class that is used specifically to generate a World.
 * @author Vinay Agrawal, Mekaeel Ahmad*/
public class WorldGenerator {
    /**Floor from Engine.*/
    private static final TETile FLOOR = Engine.FLOOR;
    /**Wall form Engine.*/
    private static final TETile WALL = Engine.WALL;
    /**Nothing Tile from Engine.*/
    private static final TETile NOTHING = Engine.NOTHING;
    /**Unsolved Riddle from Engine.*/
    public static final TETile UNSOLVED_RIDDLE = Engine.UNSOLVED_RIDDLE;
    /**Locked Door from Engine.*/
    public static final TETile LOCKED_DOOR = Engine.LOCKED_DOOR;
    /**Width of the World from engine.*/
    private final int _WIDTH;
    /**Height of the World from engine.*/
    private final int _HEIGHT;
    /**Psuedorandom generator.*/
    private Random rand;
    /**World 2D array from the engine.*/
    private TETile[][] _world;
    /**Stack that is used for the previous spaces.*/
    private Stack<Space> previous;
    /**ArrayList that contains all the Points for every floor.*/
    private ArrayList<Point> floors;
    /**Array that contains all the Point for every question tile.*/
    private Point[] qTiles;
    /**Current Point. */
    private Point current;

    /**WorldGenerator constructor.
     * @param randGen Psuedorandom generator
     * @param width width of the game
     * @param height height of the game*/
    public WorldGenerator(Random randGen, int width, int height) {
        _WIDTH = width;
        _HEIGHT = height;
        rand = randGen;
        _world = new TETile[_WIDTH][_HEIGHT];
        for (int x = 0; x < _WIDTH; x++) {
            for (int y = 0; y < _HEIGHT; y++) {
                _world[x][y] = NOTHING;
            }
        }
        current = new Point(_WIDTH / 4, _HEIGHT / 2, "R");
        previous = new Stack<>();
        floors = new ArrayList<>();
    }

    /**Main class that is used for testing purposes.
     * @param args used for testing purposes to generate a test world*/
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        Random rand = new Random(232382783);
        WorldGenerator world = generateWorld(rand, 80, 30);
        ter.initialize(80, 30);
        ter.renderFrame(world.getWorld());
        return;
    }

    /**This method adds question tiles to the current world.
     * @return returns the new array where riddles tiles have been added*/
    public Point[] addNQuestionTiles(int n) {
        qTiles = new Point[n];
        int[] randN = RandomUtils.permutation(rand, floors.size(), n);
        for (int i = 0; i < n; i++) {
            Point p = floors.get(randN[i]);
            qTiles[i] = p;
            add(UNSOLVED_RIDDLE, p);
        }
        return qTiles;
    }

    /**Returns the current world.*/
    public TETile[][] getWorld() {
        return _world;
    }

    /**Returns the psuedorandom generator.*/
    public Random getRand() {
        return rand;
    }

    /**Returns an instance of this class with a fully developed world.*/
    public static WorldGenerator generateWorld(Random randGen, int width, int height) {
        WorldGenerator randWorld = new WorldGenerator(randGen, width, height);
        int numSpaces = RandomUtils.uniform(randWorld.getRand(), 20, 30);
        while (numSpaces > 0)  {
            int randSpace = RandomUtils.uniform(randWorld.getRand(), 4);
            switch (randSpace) {
                case 0:
                case 1:
                    randWorld.addRoom();
                    break;
                case 2:
                    randWorld.addV();
                    break;
                case 3:
                    randWorld.addH();
                    break;
                default:
                    break;

            }
            numSpaces--;
        }
        randWorld.add(NOTHING, new Point(width / 4, height / 2));
        randWorld.add(LOCKED_DOOR, new Point(width / 4 + 1, height / 2));
        return randWorld;
    }

    /**Adds this tile to the 2D array.
     * @param tile tile type to add
     * @param p Point where you want to add a tile*/
    public void add(TETile tile, Point p) {
        _world[p.getX()][p.getY()] = tile;
    }

    /**Adds a room to the world 2D array.*/
    public void addRoom() {
        while (current == null) {
            if (previous.size() < 2) {
                return;
            } else {
                previous.pop();
                current = previous.peek().generateExit(rand);
            }
        }
        int width = RandomUtils.uniform(rand, 4, _WIDTH / 4);
        int height = RandomUtils.uniform(rand, 4, _HEIGHT / 4);
        Space room = new Space(current, "R", width, height);
        if (checkFits(room)) {
            floors.addAll(room.getFloors());
            draw(room.getFloors(), FLOOR);
            draw(room.getWalls(), WALL);
            add(FLOOR, current);
            previous.push(room);
            current = room.generateExit(rand);
        } else {
            current = previous.peek().generateExit(rand);
            addRoom();
        }
        return;
    }

    /**AddH adds a horizontal hallway to the 2D array.*/
    public void addH() {
        while (current == null) {
            if (previous.size() < 2) {
                return;
            } else {
                previous.pop();
                current = previous.peek().generateExit(rand);
            }
        }
        int width = RandomUtils.uniform(rand, 4, _WIDTH / 4);
        int height = RandomUtils.uniform(rand, 3, 5);
        Space hall = new Space(current, "R", width, height);
        if (checkFits(hall)) {
            floors.addAll(hall.getFloors());
            draw(hall.getFloors(), FLOOR);
            draw(hall.getWalls(), WALL);
            add(FLOOR, current);
            previous.push(hall);
            current = hall.generateExit(rand);
        } else {
            current = previous.peek().generateExit(rand);
            addH();
        }
        return;
    }

    /**AddV adds a vertical hall.*/
    public void addV() {
        while (current == null) {
            if (previous.size() < 2) {
                return;
            } else {
                previous.pop();
                current = previous.peek().generateExit(rand);
            }
        }
        int width = RandomUtils.uniform(rand, 3, 5);
        int height = RandomUtils.uniform(rand, 4, _HEIGHT / 4);
        Space hall = new Space(current, "R", width, height);
        if (checkFits(hall)) {
            floors.addAll(hall.getFloors());
            draw(hall.getFloors(), FLOOR);
            draw(hall.getWalls(), WALL);
            add(FLOOR, current);
            previous.push(hall);
            current = hall.generateExit(rand);
        } else {
            current = previous.peek().generateExit(rand);
            addV();
        }
        return;
    }

    /**Checks to see if a new space fits with the current Space.
     * @param obj space object to be added
     * @return whether or not this space can fit*/
    public boolean checkFits(Space obj) {
        List<Point> walls = obj.getWalls();
        for (Point p: walls) {
            int x = p.getX();
            int y = p.getY();
            if (x < 0 || x >= _WIDTH) {
                return false;
            }
            if (y < 0 || y >= _HEIGHT) {
                return false;
            }
            if (!getTile(p).description().equals("nothing")) {
                return false;
            }
        }
        return true;
    }

    /**Returns the tile according to the given coordinates in Point.
     * @param p coordinate that you're looking for.*/
    public TETile getTile(Point p) {
        return _world[p.getX()][p.getY()];
    }

    /**Takes in a list of items and adds them to the current 2D array.
     * @param items list of Points that need to be added
     * @param type type of tile that you want to add to the 2D array*/
    public void draw(List<Point> items, TETile type) {
        for (Point p: items) {
            add(type, p);
        }
    }

}
