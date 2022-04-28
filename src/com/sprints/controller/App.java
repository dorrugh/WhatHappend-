package com.sprints.controller;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class App {
    public static String currentRoom = "basement";
    private boolean tutorial = true;
    private boolean gameOver = false;

    Scanner myObj = new Scanner(System.in);

    public void execute() throws IOException {
        //displays splash screen
        welcome();
        start();
    }

    private void start() {

        // possible start tutorial here where player prompted to look, get, and move
        // standard gave play starts after tutorial portion ends
        tutorial = false;

        while (!gameOver) {
            showStatus();
            System.out.printf(">");
            String playerCommand = myObj.nextLine().toLowerCase(); //changed variable name from "command" to "playerCommand" for better readability
            List<String> input = new ArrayList<>(Arrays.asList(playerCommand.split(" ")));
            parseInput(input);
            //if player inputs "quit" it will break out of the while loop and exit the game----
            // we can integrate the "start over" logic with this, if the group decides
            if (playerCommand.equals("quit")) {
                break;
            }

            if (playerCommand.equals("restart")) {
                start();
            }
        }
    }


    private void getCommands() {
        // later use file to read synonyms of the commands
        // help should call this method
        System.out.println("go [direction]\nget [item]\nlook [item]\nhelp (allows you to view in game commands)");
    }

    private void welcome() throws IOException {
        txtFileReader("title.txt");
        //read from txt later
        System.out.println("You awake to find yourself in a twisted escape game.\n Can you gather all the clues and escape with your life in tact before time runs out?");
        System.out.println("---------------------");
        getCommands();
    }

    private void parseInput(List<String> input) {
        String noun;
        String verb;
        String location = "";

        //if input greater than 2 words
        if (input.size() > 2) {
            location = input.get(1) + " " + input.get(2);
        }
        if (input.size() >3) {
            System.out.println("Please enter a valid 2 or 3 word command. Ex: [go north, go west hall, look room]");
        }
        else
        {
            verb = input.get(0);
            noun = input.get(1);

            JSONParser parser = new JSONParser();
            try {
                //reads from commands.json file
                FileReader commandReader = new FileReader("data/commands.json");
                //reads from synonyms.json
                FileReader synonymReader = new FileReader("data/synonyms.json");

                JSONObject commandObj = (JSONObject) parser.parse(commandReader);

                //convert the parsed json file into a JSONArray to access values
                JSONArray synonymObj = (JSONArray) parser.parse(synonymReader);

                //valid verbs array
//                JSONArray validVerbs = (JSONArray) commandObj.get("verbs");
                //valid nouns array
                JSONArray validNouns = (JSONArray) commandObj.get("nouns");

                //reads from room.json file
                FileReader roomReader = new FileReader("data/rooms.json"); //
                // entire json
                JSONObject roomsObj = (JSONObject) parser.parse(roomReader);
                // individual room
                JSONObject room = (JSONObject) roomsObj.get(currentRoom);

                // if input verb is not inside validVerbs array
                if (synonymObj.contains(verb)) {
                    System.out.println(verb + " is not recognized verb");
                }
                // if input noun is not inside validNouns array
                if (!validNouns.contains(noun)) {
                    System.out.println(noun + " is not recognized noun");
                }
                // pass info playerActions function
                else {
                    playerActions(noun, verb, room, roomsObj, location, synonymObj);
                }

            } catch (IOException | ParseException e) {
                System.out.println(e);
            }
        }

    }

<<<<<<< HEAD
    private void playerActions(String noun, String verb, JSONObject room, JSONObject roomsObj, String location, JSONArray synonymObj) {
        JSONArray verbObj1 = (JSONArray) synonymObj.get(0);
        JSONArray verbObj2 = (JSONArray) synonymObj.get(1);
        JSONArray verbObj3 = (JSONArray) synonymObj.get(2);

        if (verbObj1.contains(verb)) {
            locationChange(noun, room, roomsObj, location);
        }else if (verbObj2.contains(verb)) {
            getItems(room);
        }else {

=======
    private void playerActions(String noun, String verb, JSONObject room, JSONObject roomsObj, String location) {
        switch (verb) {
            // if verb is go pass to locationChange function
            case "go":
                locationChange(noun, room, roomsObj, location);
                break;
            case "get":
                getItems(room);
                break;
            case "look":
                look(noun, room);
                break;
>>>>>>> Dev
        }
    }

    private void getItems(JSONObject room) {
        if (room.containsKey("item")) {
            System.out.println(room.get("item") + " picked up");
        }
        else {
            System.out.println("There is no item in this room");
        }
    }

    private void locationChange(String noun, JSONObject room, JSONObject roomsObj, String location) {
        if (roomsObj.containsKey(location)) {
            currentRoom = location;
        }
        else if (room.containsKey(noun)) {
            currentRoom = (String) room.get(noun);
        }
        else {
            System.out.println("You cannot go that way");
        }
    }

    private void look(String noun, JSONObject room) {
        if (noun.equals("here")) {
            System.out.println(room.get("description"));
        }
    }

    private static void showStatus () {
        System.out.println("---------------------------");
        System.out.println("You are in the " + currentRoom);
        System.out.println("-----------------------------");
    }

    private String txtFileReader (String filename) throws IOException {
        if (Files.exists(Path.of("data/text_file/"+ filename))) {
            String file = Files.readString(Path.of("data/text_file/" + filename));
            System.out.println("\u001B[31m" + file + "\u001B[37m");
            return file;
        } else {
            throw new IOException("Please verify welcome.txt location");
        }
    }
}
