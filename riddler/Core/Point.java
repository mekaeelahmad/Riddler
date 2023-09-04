package byow.Core;

/**Class that keeps track of x, y,
 * coordinates as well as the direction that you want to go.
 * @author Vinay Agrawal, Mekaeel Ahmad*/
public class Point {
    /**X coordinate for this Point.*/
    private int x;
    /**X coordinate for this Point.*/
    private int y;
    /**Direction of this Point.*/
    private String direction = null;
    /**Keeps track of corner walls.*/
    private boolean flag = false;

    /**Constructor for the Point class.
     * @param x x coordinate
     * @param y y coordinate
     * */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**Another constructor for the Point class.
     * @param x x coordinate
     * @param y y coordinate
     * @param direction direction of the Point*/
    public Point(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    /**Another constructor for the Point class.
     * @param x x coordinate
     * @param y y coordinate
     * @param direction direction of the Point
     * @param flag whether it is a corner of not*/
    public Point(int x, int y, String direction, boolean flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
        this.direction = direction;
    }
    /**Returns the x coordinate of this Point.*/
    public int getX() {
        return x;
    }

    /**Returns the y coordinate of this Point.*/
    public int getY() {
        return y;
    }

    /**Returns the flag of this Point.*/
    public boolean getFlag() {
        return flag;
    }

    /**Returns to the direction of this Point.*/
    public String getDirection() {
        return direction;
    }

    /**Returns whether this Point is equal to obj.*/
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point e = (Point) obj;
            return x == e.getX() && y == e.getY();
        } else {
            return false;
        }

    }

    /**Returns the hashcode this Point Object.*/
    @Override
    public int hashCode() {
        return x + y;
    }
}

