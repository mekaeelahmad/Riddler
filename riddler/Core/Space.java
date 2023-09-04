package byow.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**Class that basically creates a hallway or a room for the WorldGenerator Class.
 * @author Vinay Agrawal, Mekaeel Ahmad*/
public class Space {

    /**Entrance for this space.*/
    private Point entrance;
    /**Exit for this space.*/
    private int exit;
    /**Tells you the order in which to check the walls for exits.*/
    int[] exitOrder;
    /**The type of space that we're creating.*/
    private String type;
    /**The width of this space.*/
    private int width;
    /**The height of this space.*/
    private int height;
    /**ArrayList that contains all the Points for every wall.*/
    private ArrayList<Point> walls;
    /**ArrayList that contains all the Points for every floor.*/
    private ArrayList<Point> floors;

    /**Space constructor.
     * @param entrance Point for the entrance of this Space
     * @param type type of space we're creating
     * @param width width of this space
     * @param height height of this space*/
    public Space(Point entrance, String type, int width, int height) {
        this.entrance = entrance;
        this.type = type;
        this.width = width;
        this.height = height;
        exit = 0;
        exitOrder = null;
        walls = new ArrayList<>();
        floors = new ArrayList<>();
        build();
    }
    /**Builds a room or hall.*/
    public void build() {
        switch (type) {
            case "R":
                buildRoomW(); //build room walls
                buildRoomF(); //build room floors
                break;
            case "H":
                buildHHall();
                break;
            case "V":
                buildVHall();
                break;
            default:
                break;
        }
    }
    /**Builds the floors by adding them to the arrayList containing floors*/
    public void buildRoomF() {
        int aWidth = width - 2; //to ignore walls
        int aHeight = height - 2;
        switch (entrance.getDirection()) {
            case "U":
                Point p = new Point(entrance.getX(), entrance.getY() + 2);
                floors.add(new Point(entrance.getX(), entrance.getY() + 1));
                for (int x = 0; x < aWidth; x++) {
                    for (int y = 0; y < aHeight; y++) {
                        floors.add(new Point(x + p.getX(), y + p.getY()));
                    }
                }
                break;
            case "R":
                Point p1 = new Point(entrance.getX() + 2, entrance.getY());
                floors.add(new Point(entrance.getX() + 1, entrance.getY()));
                for (int x = 0; x < aWidth; x++) {
                    for (int y = aHeight; y > 0; y--) {
                        floors.add(new Point(x + p1.getX(), y + p1.getY() - aHeight));
                    }
                }
                break;
            case "L":
                Point p2 = new Point(entrance.getX() - 2, entrance.getY());
                floors.add(new Point(entrance.getX() - 1, entrance.getY()));
                for (int x = 0; x > 0 - aWidth; x--) {
                    for (int y = 0; y > 0 - aHeight; y--) {
                        floors.add(new Point(x + p2.getX(), y + p2.getY()));
                    }
                }
                break;
            case "D":
                Point p3 = new Point(entrance.getX(), entrance.getY() - 2);
                floors.add(new Point(entrance.getX(), entrance.getY() - 1));
                for (int x = 0; x > 0 - aWidth; x--) {
                    for (int y = 0; y > 0 - aHeight; y--) {
                        floors.add(new Point(x + p3.getX(), y + p3.getY()));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**Builds the walls of the room by adding points to the arrayList for walls.*/
    private void buildRoomW() {
        switch (entrance.getDirection()) {
            case "U":
                Point p = new Point(entrance.getX() - 1, entrance.getY() + 1);
                p = buildUp(p, height - 1);
                p = buildRight(p, width - 1);
                p = buildDown(p, height - 1);
                p = buildLeft(p, width - 2); // to not repeat first wall
                break;
            case "R":
                Point p1 = new Point(entrance.getX() + 1, entrance.getY() + 1);
                p1 = buildRight(p1, width - 1);
                p1 = buildDown(p1, height - 1);
                p1 = buildLeft(p1, width - 1);
                p1 = buildUp(p1, height - 2); // to not repeat first wall
                break;
            case "L":
                Point p2 = new Point(entrance.getX() - 1, entrance.getY() - 1);
                p2 = buildDown(p2, height - 3);
                p2 = buildLeft(p2, width - 1);
                p2 = buildUp(p2, height - 1);
                p2 = buildRight(p2, width); // to not repeat first wall
                break;
            case "D":
                Point p3 = new Point(entrance.getX() + 1, entrance.getY() - 1);
                p3 = buildDown(p3, height - 1);
                p3 = buildLeft(p3, width - 1);
                p3 = buildUp(p3, height - 1);
                p3 = buildRight(p3, width - 2); // to not repeat first wall
                break;
            default:
                break;
        }
    }

    private Point buildRight(Point p, int tiles) {
        int y = p.getY();
        int x = p.getX();
        walls.add(new Point(x, y, "U", true));
        for (int n = 1; n < tiles; n++) {
            walls.add(new Point(n + x, y, "U"));
        }
        return new Point(x + tiles, y);
    }

    private Point buildLeft(Point p, int tiles) {
        int y = p.getY();
        int x = p.getX();
        walls.add(new Point(x, y, "D", true));
        for (int n = 1; n < tiles; n++) {
            walls.add(new Point(x - n, y, "D"));
        }
        return new Point(x - tiles, y);
    }

    private Point buildUp(Point p, int tiles) {
        int x = p.getX();
        int y = p.getY();
        walls.add(new Point(x, y, "L", true));
        for (int n = 1; n < tiles; n++) {
            walls.add(new Point(x, y + n, "L"));
        }
        return new Point(x, y + tiles);
    }

    private Point buildDown(Point p, int tiles) {
        int x = p.getX();
        int y = p.getY();
        walls.add(new Point(x, y, "R", true));
        for (int n = 1; n < tiles; n++) {
            walls.add(new Point(x, y - n, "R"));
        }
        return new Point(x, y - tiles);
    }

    private void buildVHall() {

    }

    private void buildHHall() {

    }

    public List<Point> getWalls() {
        return walls;
    }

    public List<Point> getFloors() {
        return floors;
    }

    public Point generateExit(Random rand) {
        if (exitOrder == null) {
            exitOrder = RandomUtils.permutation(rand, walls.size());
        }
        if (exit < exitOrder.length) {
            Point exitPoint = walls.get(exit);
            exit++;
            if (exitPoint.getFlag()) {
                return generateExit(rand);
            } else {
                return exitPoint;
            }
        } else {
            return null;
        }
    }

}
