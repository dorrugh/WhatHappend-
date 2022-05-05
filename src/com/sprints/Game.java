package com.sprints;

import com.apps.util.Console;
import com.sprints.controller.App;
import org.json.simple.JSONObject;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Game {
    // ******** Class Singleton **********
    private static Game game = null;

    // ******** Fields **********
    private boolean gameOver = false;
    private TextParser parser = new TextParser();
    private Clip clip = AudioSystem.getClip();
    private static final String ROOMS = "/rooms.json";
    // instantiate scanner to read console input
    Scanner myObj = new Scanner(System.in);

    // ******** CTOR **********
    public Game() throws LineUnavailableException {
    }

    // ******** Singleton Instantiation **********
    public static Game getInstance() throws LineUnavailableException {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    // ******** Business Methods **********
    public void start() throws IOException, ParseException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        welcome();
        while (!gameOver) {
            if (!TimeElapsed.getTime().equals("0")) {
                checkWin();
                showStatus();
                bookLoss();
                String playerCommand = promptPlayer();

                //if player inputs "quit" it will break out of the while loop and exit the game----
                if ("quit".equals(playerCommand) || ("q".equals(playerCommand))) {
                    quit();
                }
                if ("restart".equals(playerCommand)) {
                    restart();
                }
                if ("help".equals(playerCommand)) {
                    getCommands();
                }
                //encrypted command to end game if timer ends
                if ("mcdhapwt123".equals(playerCommand)) {
                    endGame();
                }
                //Clear function coming from external jar
                parser.playerInput(playerCommand);
                Thread.sleep(1000);
                com.apps.util.Console.clear();
            } else {
                gameOver = true;
                endGame();
                return;
            }
        }
    }

    // ends the game with Ascii art if timer ends
    private void endGame() throws IOException {
        if (TimeElapsed.getTime().equals("0")) {
            System.out.println("Time's Up! You just fell through the trap door!!!");
        }
        else {
            System.out.println("You chose..... Poorly.");
        }
        System.out.println();
        TextFileReader.getInstance().txtFileReader("/gameover.txt");
        System.exit(0);
    }

    private void checkWin() throws IOException {
        if (Player.getInstance().getInventory().contains("it")) {
            System.out.println("You pull the book out of the shelf and it opens a secret door.");
            System.out.println();
            gameOver = true;
            TextFileReader.getInstance().txtFileReader("/escaped.txt");
            System.exit(0);
        }
    }

    private void bookLoss() throws IOException {
        JSONObject bookCheck = OurJSONParser.getRoomsJSON();
        JSONObject westHall = (JSONObject) bookCheck.get("west hall");
        JSONObject hallItems = (JSONObject) westHall.get("item");
        JSONObject bookcase = (JSONObject) hallItems.get("bookcase");
        JSONObject books = (JSONObject) bookcase.get("books");
        Set<String> remainingBooks = books.keySet();
        if (remainingBooks.size() == 2 && remainingBooks.contains("it")) {
            endGame();
        }
    }

    // welcomes to game by displaying ascii and break description of game
    private void welcome() throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        TextFileReader.getInstance().txtFileReader("/title.txt");
        pressEnterToContinue();
        Console.clear();
        //read from txt later
        String description = "You awake to find yourself in a twisted escape game. Can you gather all the clues and escape\nwith your life in tact before time runs out?";
        printWithDelays(description);
        Console.blankLines(2);
        System.out.println("-----------------------------");
        getCommands();
        playSound("/Sound.wav");
    }

    // used to display status (current room, inventory, room description, etc)
    private static void showStatus () throws IOException, ParseException, InterruptedException {
        // the entire rooms.json
        JSONObject roomObj = OurJSONParser.instantiate().getRoomsJSON();
        // json within current room
        JSONObject room = (JSONObject) roomObj.get(Player.getInstance().getCurrentRoom());
        // json items within current room
        JSONObject items = (JSONObject) room.get("item");

        // check if torch is equipped and present ascii based on current room
        if (Player.getInstance().isItemEquipped()) {
            switch (Player.getInstance().getCurrentRoom()){
                case "parlor" :
                    TextFileReader.getInstance().txtFileReader("/parlor.txt");
                    break;
                case "east hall":
                case "west hall":
                    TextFileReader.getInstance().txtFileReader("/hallway.txt");
                    break;
                case "kitchen":
                    TextFileReader.getInstance().txtFileReader("/kitchen.txt");
                    break;
                default:
                    TextFileReader.getInstance().txtFileReader("/basement.txt");
            }

            System.out.println("---------------------------");
            System.out.println("You are in the " + Player.getInstance().getCurrentRoom());
            Console.blankLines(1);
            System.out.println(room.get("description"));
            Console.blankLines(1);
        } else {
            System.out.println("Too dark to see everything here, you need some light");
            Console.blankLines(2);
        }
        if (room.containsKey("item")) {
            Set<String> roomItems = items.keySet();
            System.out.println("You notice: " + roomItems);
            Console.blankLines(1);
        }
        System.out.println("Inventory:" + Player.getInstance().getInventory());
        System.out.println("Time Remaining: " + TimeElapsed.getTime());
        System.out.println("-----------------------------");
    }

    // restart game
    private void restart() throws IOException, ParseException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        System.out.println("Are you sure you want to restart?");
        String q = myObj.nextLine();
        if ("yes".equals(q) || "y".equals(q)) {
            System.out.println("Restarting game...");
            TimeUnit.SECONDS.sleep(2);
            clip.stop();
            clip.close();
            Player.getInstance().setCurrentRoom("basement");
            Player.getInstance().getInventory().clear();
            Console.clear();
            start();
        }
        else {
            showStatus();
        }
    }

    // quit game
    private void quit() throws IOException, ParseException, InterruptedException {
        System.out.println("Are you sure you want to quit?");
        String q = myObj.nextLine();
        if ("yes".equals(q) || "y".equals(q)) {
            System.out.println("quiting the game...");
            TimeUnit.SECONDS.sleep(2);
            gameOver = true;
            System.exit(0);
        }
        else {
            showStatus();
        }
    }

    // shows player command that can be used in game
    private void getCommands() {
        // help should call this method
        System.out.println("go [direction]\nget [item]\nlook [item]\nequip [item]\nhelp (allows you to view in game commands)");
    }

    //prompts the user to enter commands until timer ends
    private String promptPlayer() {
        // encrypted default command calls endGame() when timer ends
        String playerCommand = "mcdhapwt123";
        if (!TimeElapsed.getTime().equals("0")) {
            System.out.printf(">");
            playerCommand = myObj.nextLine();
        }
        return playerCommand;
    }

    //play in game music
    private void playSound(String fileName) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        InputStream is = getClass().getResourceAsStream(fileName);
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
        clip.open(ais);
        clip.start();
        clip.loop(-1);
    }

    private static void printWithDelays(String data)
            throws InterruptedException {
        for (char ch:data.toCharArray()) {
            System.out.print(ch);
            TimeUnit.MILLISECONDS.sleep(20);
        }
    }

    private void pressEnterToContinue() {
        Console.blankLines(1);
        System.out.println("Press ENTER to continue");
        try {
            System.in.read(new byte[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}