package com.sprints.controller;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class App {
    public static String currentRoom = "Basement";
    private boolean tutorial = true;
    private boolean gameOver = false;

    Scanner myObj = new Scanner(System.in);

    public void execute() {
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
            System.out.println(">");
            String playerCommand = myObj.nextLine(); //changed variable name from "command" to "playerCommand" for better readability
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

    private void welcome(){

        //read from txt later
        System.out.println("Kidnapped");
        //read from txt later
        System.out.println("You awake to find yourself in a twisted escape game.\n Can you gather all the clues and escape with your life in tact before time runs out?");
    }

    private void parseInput(List<String> input) {
        String noun;
        String verb;

        if (input.size() > 2) {
            System.out.println("Please enter a two word command");
        } else {
            verb = input.get(0);
            noun = input.get(1);

            JSONParser parser = new JSONParser();
            try {
                FileReader commandReader = new FileReader("data/commands.json"); //
                JSONObject commandObj = (JSONObject) parser.parse(commandReader);
                JSONArray validVerbs = (JSONArray) commandObj.get("verbs");
                JSONArray validNouns = (JSONArray) commandObj.get("nouns");

                FileReader roomReader = new FileReader("data/rooms.json"); //
                JSONObject roomObj = (JSONObject) parser.parse(roomReader);
                JSONObject room = (JSONObject) roomObj.get(currentRoom);


                if (!validVerbs.contains(verb)) {
                    System.out.println(verb + " is not recognized verb");
                }
                if (!validNouns.contains(noun)) {
                    System.out.println(noun + " is not recognized noun");
                }
                else {
                    playerActions(noun, verb, room);
                }

            } catch (IOException | ParseException e) {
                System.out.println(e);
            }
        }

    }

    private void playerActions(String noun, String verb, JSONObject room) {
        switch (verb) {
            case "go":
                locationChange(noun, room);
                break;
            case "get":
                getItems(noun, room);
                break;
            case "look":
                look(noun, room);
                break;
        }
    }

    private void getItems(String noun, JSONObject room) {
        if (room.containsKey("item")) {
            System.out.println(room.get("item") + " picked up");
        }
        else {
            System.out.println("There is no item in this room");
        }
    }

    private void look(String noun, JSONObject room) {
        if (noun.equals("here")) {
            System.out.println(room.get("description"));
        }
    }

    private void locationChange(String noun, JSONObject room) {
        currentRoom = (String) room.get(noun);
    }

    private static void showStatus() {
            System.out.println("---------------------------");
            System.out.println("You are in the " + currentRoom);
            System.out.println("You see a " + "[item]");
            System.out.println("-----------------------------");
        }
    }
