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
    private boolean isSound = true;     // music is ON by default
    private static final String ROOMS = "/rooms.json";
    Scanner myObj = new Scanner(System.in);   // instantiate scanner to read console input

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
                showStatus();
                bookShelfCheck();
                String playerCommand = promptPlayer();

                parser.playerInput(playerCommand);
                Thread.sleep(1000);
                //Clear function coming from external jar
                Console.clear();

                //if player inputs "quit" it will break out of the while loop and exit the game----
                if ("quit".equals(playerCommand) || ("q".equals(playerCommand))) {
                    quit();
                }
                else if ("restart".equals(playerCommand)) {
                    restart();
                }
                else if ("help".equals(playerCommand)) {
                    getCommands();
                }
                else if ("music".equals(playerCommand)) {
                    toggleSound();
                }
            }
            else {
                gameOver = true;
                endGame();
                return;
            }
        }
    }

    // ends the game with Ascii art if timer ends
    private void endGame() throws IOException, InterruptedException {
        if (TimeElapsed.getTime().equals("0")) {
            Utils.printWithDelays("Your body begins to stiffen and agony takes the name of each breath. Your world fades to black\nas you fall to the ground...");
        }
        else {
            System.out.println("You chose..... Poorly.");
        }
        Console.blankLines(1);
        TimeUnit.SECONDS.sleep(2);
        Console.blankLines(1);
        TextFileReader.getInstance().txtFileReader("/gameover.txt");
        System.exit(0);
    }

    // End game with a win if select correct book
    private void checkWin() throws IOException {
        if (Player.getInstance().getInventory().contains("it")) {
            System.out.println("You pull the book out of the shelf and it opens a secret door.");
            System.out.println();
            gameOver = true;
            TextFileReader.getInstance().txtFileReader("/escaped.txt");
            System.exit(0);
        }
    }

    // End game with a loss if select 2 incorrect books
    private void bookShelfCheck() throws IOException, InterruptedException {
        Set<String> remainingBooks = OurJSONParser.getBooks().keySet();
        int bookSelections = remainingBooks.size() - (remainingBooks.size() - 2);
        if (remainingBooks.size() <= bookSelections && remainingBooks.contains("it")) {
            endGame();
        }
        else {
            checkWin();
        }
    }

    // welcomes to game by displaying ascii and break description of game
    private void welcome() throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        TextFileReader.getInstance().txtFileReader("/title.txt");
        Utils.pressEnterToContinue();
        Console.clear();
        //read from txt later
        String description = "You awake to find yourself in a twisted escape game. Can you gather all the clues and escape\nwith your life in tact before time runs out?";
        Utils.printWithDelays(description);
        Console.blankLines(2);
        System.out.println("-----------------------------");
        getCommands();
        Utils.pressEnterToContinue();
        Console.clear();
        Utils.printWithDelays("...You feel a sharp prick.");
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

    // prompts the user to enter commands until timer ends
    private String promptPlayer() {
        // encrypted default command calls endGame() when timer ends
        String playerCommand = "";
        if (!TimeElapsed.getTime().equals("0")) {
            System.out.printf(">");
            playerCommand = myObj.nextLine();
        }
        return playerCommand;
    }

    // start in game music
    private void playSound(String fileName) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        InputStream is = getClass().getResourceAsStream(fileName);
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
        clip.open(ais);
        clip.start();
        clip.loop(-1);
    }

    // stop in game music
    private void toggleSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if (isSound) {
            clip.stop();
            isSound = false;
        } else {
            clip.start();
            isSound = true;
        }
    }

}