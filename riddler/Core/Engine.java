package byow.Core;

import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class Engine {
    /**Renderer for the game.*/
    TERenderer ter = new TERenderer();

    /**Tile for Unsolved Riddle.*/
    public static final TETile UNSOLVED_RIDDLE = new TETile('▲', Color.gray,
            Color.black, "unsolved", "byow/images/unsolvedRiddle.png");
    /**Tile for Solved Riddle.*/
    public static final TETile SOLVED_RIDDLE = new TETile('▒', Color.yellow,
            Color.black, "solved", "byow/images/solvedRiddle.png");
    /**Tile for PacMan avatar.*/
    public static final TETile PACMAN = new TETile('<', Color.yellow,
            Color.black, "pacman", "byow/images/pac.png");
    /**Tile for Riddler avater.*/
    public static final TETile RIDDLER = new TETile('?', Color.green,
            Color.black, "riddler", "byow/images/ThirdRiddler.png");

    /**Tile for Floor.*/
    public static final TETile FLOOR = new TETile('·',
            new Color(216, 128, 128),
            Color.darkGray, "floor", "byow/images/floor.png");

    /**Tile for Wall.*/
    public static final TETile WALL = new TETile('#',
            new Color(216, 128, 128),
            Color.darkGray, "wall", "byow/images/wall.png");

    /**Tile for Locked Door.*/
    public static final TETile LOCKED_DOOR = new TETile('█',
            new Color(216, 128, 128),
            Color.darkGray, "locked door", "byow/images/lockeddoor1.png");

    /**Tile for Unlocked Door.*/
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange,
            Color.black, "unlocked door", "byow/images/unlockedDoor.png");

    /**Tile for Nothing.*/
    public static final TETile NOTHING = new TETile(' ', Color.black,
            Color.black, "nothing", "byow/images/nothing.png");

    /**String of total commands Typed so Far.*/
    private String keysTyped;

    /**TETile[][] array that consists of the current world.*/
    private TETile[][] world;

    /**Location of questions **/
    private Point[] qTiles;

    /**Width of the current board.*/
    private static final int WIDTH = 80;

    /**Height of the current board*/
    private static final int HEIGHT = 30;

    /**Psuedorandom generator based on seed*/
    private Random rand;

    /**Tells us whether the game has finished or not.*/
    private boolean gameOver;

    /**Lets us know if world has been generated. **/
    private boolean gameStart;

    /**Amount of riddles left to answer*/
    private int riddlesLeft = 5;

    /**Instance of the Point class that tells where the location of the avatar is.*/
    private Point avatarLoc;

    /** Tell us what the tile was before the avatar came onto it.
     * Helpful for rendering riddle tiles
     * after moving from them.
     */
    private TETile onTile;

    /**Instance variable for current Avatar so that it can be possibly switched.*/
    private TETile AVATAR = PACMAN;

    /**Instance of point class with x, y, coordinates of the exit for the game.*/
    private Point exit;

    /**Checks if the game is finished or not.*/
    private boolean gameExit;

    /**Number of attempts left to answer riddles*/
    private int attemptsLeft;

    /**Konami code that the user inputs in order to win!*/
    private final String KONAMI = "61bliscool";

    /**HashMap that stores with an integer as a key and an instance of Riddle,
     * which contains a String question and a String answer.*/
    private HashMap<Integer, Riddle> riddlesHash = new HashMap<>();

    /**Checks to see if the user is currently answering a question*/
    private boolean inRiddleState; //checks to see if the game is in riddlestate.

    /**The user's current Avatar that could be switched in the user preference.*/
    private String avatarName;

    /**Versatile variable that we use to make changes
     * to the text at the top of the game screen.*/
    private String dispInput;

    /**Keeps track of the mouse position and updates HUV display.*/
    private MouseThread mt;

    /**Number of hints left that the user has.*/
    private int hintsLeft;
    /** if engine should render world or not **/
    private boolean renderMode;
    /** If engine is reading string input or not **/
    private boolean stringInput;
    /**If engine is interacting with keyboard or not **/
    private boolean keyMode;
    /** If engine is replaying prev game state or not. **/
    private boolean replayMode;
    /** Iterator for string input. **/
    private StringInputDevice gameStringInput;

    /**Initializes the rendering for the game screen.
     * Height is added 6 so that there is space for the text above the world.*/
    public Engine() {
        renderMode = true;
        stringInput = false;
        keyMode = true;
        replayMode = false;
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.startGame();
        System.exit(0);
        //TETile[][] world = engine.interactWithInputString("N123123123123s");
        //System.out.println(engine.toString());
    }


    /** Established game loop and initializes necessary variables. **/
    public void startGame() {
        if (keyMode) {
            ter.initialize(WIDTH, HEIGHT + 6, 0, 2);
        }
        gameOver = false;
        gameStart = false;
        keysTyped = "";
        gameExit = false;
        mt = new MouseThread();
        attemptsLeft = 5;
        hintsLeft = 3;
        avatarName = "Quester";

        drawMenu("");
        while (!gameExit) {
            interactWithKeyboard();
        }
    }

    /**It is a thread object that tracks mouse position for HUV*/
    public class MouseThread extends Thread {

        /**Tells what tile you're hovering over.*/
        String pointingTo = "nothing";

        /**Run method that overrides the Thread interface*/
        @Override
        public void run() {
            while (gameStart && !gameExit) {
                interactWithMouse();
                drawFrameWorld(dispInput);
                StdDraw.pause(100);
            }
        }

        /**Interacts with the mouse to update the Tile you're hovering over*/
        public void interactWithMouse() {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            int tileX = (int) mouseX;
            int tileY = (int) mouseY - 2;
            if (tileX >= 0 && tileX < WIDTH && tileY >= 0 && tileY < HEIGHT) {
                TETile mouseTile = world[tileX][tileY];
                pointingTo = mouseTile.description();
            } else {
                pointingTo = "nothing";
            }
        }

    }


    /** Displays new board state, new riddle count, new attemptsLeft
     * count, and displays user input if riddle tile. **/
    public void drawFrameWorld(String input) {
        if (!renderMode) {
            return;
        }
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontSmall);
        StdDraw.line(0, HEIGHT + 2, WIDTH, HEIGHT + 2);
        StdDraw.line(0,  2, WIDTH, 2);
        if (!gameOver) {
            StdDraw.textLeft(2, HEIGHT + 4, "Riddles Left: "  + riddlesLeft);
            StdDraw.text(WIDTH / 2, HEIGHT + 4, input);
            StdDraw.textRight(WIDTH - 2, HEIGHT + 4,
                    "Attempts Left: " + attemptsLeft);
            StdDraw.textLeft(2, 1, mt.pointingTo);
            StdDraw.textRight(WIDTH - 2, 1, "Name: " + avatarName);
            StdDraw.textLeft(14, 1,
                    "When on a riddle, press h followed by a period to use a hint.");
            StdDraw.text(WIDTH / 2 + 17, 1,
                    "You have " + hintsLeft + " hints left.");

        } else {
            StdDraw.text(WIDTH / 2, HEIGHT + 4, input);
        }
        StdDraw.show();
    }

    /** Displays main menu until input given.
     * @param s this variable is displayed at the top of the main game screen.**/
    public void drawMenu(String s) {
        if (!renderMode) {
            return;
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        Font fontMed = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT + 2, "Triviascape");
        StdDraw.setFont(fontMed);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Replay Game (R)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "User Preferences (U)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, s);
        StdDraw.show();
    }

    /**Displays a menu so that the user can make their preferences to
     * change Avatar Name and Avatar Character.*/
    public void drawUserPref(String input) {
        if (!renderMode) {
            return;
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        Font fontMed = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT + 2, "User Preferences");
        StdDraw.setFont(fontMed);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "Default Name: " + avatarName);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Default Avatar: " + "PacMan");
        StdDraw.picture(WIDTH / 2 + 8, HEIGHT / 2, "byow/images/pac.png");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2,
                "Press C followed by a period to change to Riddler");
        StdDraw.picture(WIDTH / 2 + 16, HEIGHT / 2 - 2,
                "byow/images/ThirdRiddler.png");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4,
                "To change name, press N and followed by your name and a period.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6,
                "To do both, press C, then N, and enter your name followed by a period.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "New Avatar Name: " + input);
        StdDraw.show();
    }

    /**Input that is used specifically for when the user
     * wants to make changes to their name and avatar in the
     * Preferences menu.*/
    public String solicitPreferenceInput() {
        String input = "";
        boolean endInput = false;
        boolean readingName = false;
        while (!endInput) {
            if (hasNextInput()) {
                char inp = getNextInput();
                keysTyped += inp;
                if (inp == '.' || inp == ' ') {
                    if (readingName) {
                        avatarName = input;
                    }
                    endInput = true;
                } else if (!readingName && (inp == 'C' || inp == 'c')) {
                    AVATAR = RIDDLER;
                } else if (!readingName && (inp == 'N' || inp == 'n')) {
                    readingName = true;
                } else if (readingName) {
                    input += inp;
                    drawUserPref(input);
                }

            }
        }
        return input;
    }

    /** Used specifically to read the Seed input and starting game
     * commands. */
    public String solicitSeedInput() {
        String input = "";
        boolean endSeed = false;
        while (!endSeed) {
            if (hasNextInput()) {
                char inp = getNextInput();
                if (inp == 's' || inp == 'S') {
                    endSeed = true;
                } else {
                    input += inp;
                    drawMenu(input);
                }
            }
        }
        return input;
    }


    /** Used specifically to read the user's answer for the riddle question. **/
    public String solicitAnswerInput() {
        String input = "";
        boolean endAnswer = false;
        while (!endAnswer) {
            if (hasNextInput()) {
                char inp = getNextInput();
                keysTyped += inp;
                if (inp == ':') {
                    solicitEscapeSequence();
                    inRiddleState = false;
                    break;
                } else if (inp == '.' || inp == ' ') {
                    endAnswer = true;
                } else {
                    input += inp;
                    dispInput = input;
                }
            } else if (stringInput && keyMode) {
                stringInput = false;
                renderMode = true;
            } else if (!keyMode) {
                endAnswer = true;
            }
        }
        return input;
    }

    /**This method is used to save the current Keys typed.*/
    public void saveGame() {
        File cwd = new File(System.getProperty("user.dir"));
        File gameHist = Utils.join(cwd, "engine.txt");
        Utils.writeContents(gameHist, keysTyped);
    }

    /** Gets keys typed in previous save from engine.txt **/
    public String getPreviousState() {
        File cwd = new File(System.getProperty("user.dir"));
        File gameHist = Utils.join(cwd, "engine.txt");
        String moves = null;
        if (gameHist.exists()) {
            moves = Utils.readContentsAsString(gameHist);
            moves.toLowerCase();
            if (moves.endsWith(":q")) {
                moves = moves.substring(0, moves.length() - 2);
            }
        }
        return moves;
    }

    /** Returns next char input **/
    public char getNextInput() {
        if (replayMode && keyMode && gameStart) {
            StdDraw.pause(300);
        }
        if (stringInput) {
            return gameStringInput.getNextKey();
        } else {
            return StdDraw.nextKeyTyped();
        }
    }

    /** Returns if next input exists **/
    public boolean hasNextInput() {
        if (stringInput) {
            return gameStringInput.possibleNextInput();
        } else {
            return StdDraw.hasNextKeyTyped();
        }
    }

    /**This method specifically checks whether or not the user has inputted
     * the Konami Sequence.*/
    public void konamiSequence() {
        gameOver = true;
        dispInput = "Congrats! You Broke the Game! :Q to Save/Quit or :P to play again";
        for (Point p: qTiles) {
            world[p.getX()][p.getY()] = SOLVED_RIDDLE;
        }
        world[exit.getX()][exit.getY()] = UNLOCKED_DOOR;
        updateAvatar(exit);
    }

    /**Escape method that checks to see the user's commands following
     * a colon.*/
    public String solicitEscapeSequence() {
        dispInput = "";
        boolean endAnswer = false;
        while (!endAnswer) {
            if (hasNextInput()) {
                char inp = getNextInput();
                keysTyped += inp;
                if (inp == 'Q' || inp == 'q') {
                    dispInput = "Saving Game";
                    gameExit = true;
                    endAnswer = true;
                    saveGame();
                } else if (gameOver && (inp == 'p' || inp == 'P')) {
                    dispInput = "Starting new game";
                    StdDraw.pause(1000); //maybe point of error in autograder
                    startGame();
                } else if (dispInput.equals(KONAMI)) {
                    endAnswer = true;
                    konamiSequence();
                } else if (inp == '.' || inp == ' ') {
                    endAnswer = true;
                } else {
                    dispInput += inp;
                }
            } else if (stringInput && keyMode) {
                stringInput = false;
                renderMode = true;
            } else if (!keyMode) {
                endAnswer = true;
            }
        }
        return dispInput;
    }

    /**Method for handling case when we are on riddle box.**/
    public void riddle() {
        int riddleIndex = 0;
        for (int i = 0; i < qTiles.length; i++) {
            if (qTiles[i].equals(avatarLoc)) {
                riddleIndex = i;
                break;
            }
        }
        Riddle riddle = riddlesHash.get(riddleIndex);
        inRiddleState = true;
        while (inRiddleState) {
            dispInput = riddle.getQuestion()
                    + " Enter the answer or 'P.' to continue Playing";
            String inp = solicitAnswerInput();
            inp.toLowerCase();
            if (inp.equals("p")) {
                dispInput = "Keep Going";
                inRiddleState = false;
            } else if (inp.equals("h")) {
                if (hintsLeft > 0) {
                    String hint = "" + riddle.getAnswer().substring(0, 2);
                    dispInput = "The answers first two letters are: " + hint;
                    if (keyMode) {
                        StdDraw.pause(1000);
                    }
                    hintsLeft -= 1;
                } else {
                    dispInput = "You have no more hints!";
                    if (keyMode) {
                        StdDraw.pause(1000);
                    }
                }

            } else if (inp.equals(riddle.getAnswer())) {
                dispInput = "Correct";
                inRiddleState = false;
                riddle.solved = true;
                onTile = SOLVED_RIDDLE;
                riddlesLeft--;
                if (riddlesLeft == 0) {
                    world[exit.getX()][exit.getY()] = UNLOCKED_DOOR;
                }
            } else {
                attemptsLeft--;
                if (attemptsLeft == 0) {
                    gameOver = true;
                    inRiddleState = false;
                    dispInput = "Boohoo. Try again after studying some more. "
                            + ":Q to exit or :P to Play Again";
                } else {
                    dispInput = "Incorrect";
                    if (keyMode) {
                        StdDraw.pause(500);
                    }
                }
            }
        }
        return;
    }

    /** Takes an input, that is W, A, S, or D and updates the TETile[][] world from it.
     * @param input character inputted by the user to move the avatar.
     */
    public void moveAvatar(char input) {
        int potentialMoveX = avatarLoc.getX();
        int potentialMoveY = avatarLoc.getY();
        switch (input) {
            case 'w':
                potentialMoveY += 1;
                break;
            case 'a':
                potentialMoveX -= 1;
                break;
            case 's':
                potentialMoveY -= 1;
                break;
            case 'd':
                potentialMoveX += 1;
                break;
            default:
                break;
        }
        TETile tileTo = world[potentialMoveX][potentialMoveY];
        switch (tileTo.description()) {
            case "locked door":
            case "wall":
                break;
            case "solved":
            case "floor":
                updateAvatar(new Point(potentialMoveX, potentialMoveY));
                onTile = tileTo;
                dispInput = "Keep Going";
                break;
            case "unsolved":
                updateAvatar(new Point(potentialMoveX, potentialMoveY));
                onTile = tileTo;
                riddle();
                break;
            case "unlocked door":
                updateAvatar(new Point(potentialMoveX, potentialMoveY));
                gameOver = true;
                dispInput = "Congratulations. You're going to ACE the final! "
                        + ":Q to Exit or :P to Play Again. ";
                break;
            default:
                break;
        }
    }

    /**Updates the avatar position.
     * @param dest destination for the avatars next spot.*/
    public void updateAvatar(Point dest) {
        world[avatarLoc.getX()][avatarLoc.getY()] = onTile;
        world[dest.getX()][dest.getY()] = AVATAR;
        avatarLoc = dest;
    }


    /**Loading previous world.
     * @param replay if engine is in replay mode or not. **/
    public void loadPrevWorld(boolean replay) {
        replayMode = replay;
        String moves = getPreviousState();
        if (moves.length() == 0) {
            gameExit = true;
        } else if (keyMode) {
            keysTyped = "";
            gameStringInput = new StringInputDevice(moves);
            stringInput = true;
            drawMenu("Loading world");
            renderMode = false;
        } else {
            keysTyped = "";
            String next = gameStringInput.getString().substring(1);
            gameStringInput = new StringInputDevice(moves + next);
            stringInput = true;
            renderMode = false;
        }
    }

    /** Creating new world from main menu. **/
    public void newWorld() {
        boolean validInput = false;
        String seedString = "";
        while (!validInput) {
            try {
                drawMenu("Type New World Seed");
                seedString = solicitSeedInput();
                rand = new Random(Long.parseLong(seedString));
                validInput = true;

            } catch (NumberFormatException e) {
                validInput = false;
            }
        }
        drawMenu("Generating World...");
        keysTyped += seedString + 's';
        WorldGenerator worldMap =  WorldGenerator.generateWorld(rand, WIDTH, HEIGHT);
        qTiles = worldMap.addNQuestionTiles(5);
        world = worldMap.getWorld();
        exit = new Point(WIDTH / 4 + 1, HEIGHT / 2);
        avatarLoc = new Point(WIDTH / 4 + 2, HEIGHT / 2);
        gameStart = true;
        if (replayMode && keyMode) {
            renderMode = true;
        }
        onTile = FLOOR;
        updateAvatar(avatarLoc);
        putRiddles();
        dispInput = "Use WASD keys to move to a riddle boc! ";
        if (keyMode) {
            mt.start();
        }
        if (keyMode) {
            StdDraw.pause(500);
        }
    }
    /**
     * Method used for exploring a fresh world.
     * This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (hasNextInput()) {
            char inp = getNextInput();
            inp = Character.toLowerCase(inp);
            keysTyped += inp;
            switch (inp) {
                case 'n':
                    if (gameStart) {
                        break;
                    }
                    newWorld();
                    break;
                case 'r':
                    if (gameStart) {
                        break;
                    }
                    loadPrevWorld(true);
                    break;
                case 'l':
                    if (gameStart) {
                        break;
                    }
                    loadPrevWorld(false);
                    break;
                case 'q':
                    if (gameStart) {
                        break;
                    }
                    gameExit = true;
                    break;
                case 'u': //User preferences: changing user look and name
                    if (gameStart) {
                        break;
                    }
                    drawUserPref("");
                    solicitPreferenceInput();
                    drawMenu("");
                    break;
                case ':':
                    solicitEscapeSequence();
                    break;
                case 'w':
                case 'a':
                case 's':
                case 'd'://user movement
                    if (gameOver) {
                        break;
                    }
                    moveAvatar(inp);
                    break;
                default:
                    break;
            }
        } else if (!keyMode) {
            gameExit = true;
        } else if (stringInput && keyMode) {
            stringInput = false;
            replayMode = false;
            renderMode = true;
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        gameStringInput = new StringInputDevice(input);
        stringInput = true;
        keyMode = false;
        renderMode = false;

        startGame();

        return world;
    }

    /**Method that basically creates new Riddle objects to store them in the
     * instance variable, riddlesHash.*/
    public void putRiddles() {
        riddlesHash.put(0, new Riddle(
                "What has to be cracked before you use it?", "egg"));
        riddlesHash.put(1, new Riddle(
                "What is always in front of you but can't be seen?", "future"));
        riddlesHash.put(2, new Riddle(
                "What has many keys but can't open a single lock?", "hashmap"));
        riddlesHash.put(3, new Riddle(
                "What has legs, but doesn't walk?", "table"));
        riddlesHash.put(4, new Riddle(
                "What has many teeth, but can't bite?", "comb"));
    }

    /**Class that stores information about a riddle.*/
    private class Riddle {
        /**The riddles question.*/
        private String question;

        /**The riddles answer.*/
        private String answer;

        /**Whether the riddles has been solved or not.*/
        private boolean solved;

        /**Consructor for the riddle class.
         * @param question question for the riddle.
         * @param answer answer for the riddle.*/
        private Riddle(String question, String answer) {
            this.question = question;
            this.answer = answer;
            this.solved = false;
        }

        /**Returns this riddles question.*/
        String getQuestion() {
            return question;
        }

        /**Returns this riddles answer*/
        String getAnswer() {
            return answer;
        }
    }

}
