package com.sprints;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OurJSONParser {

    // ******** Class Singleton **********
    private static OurJSONParser ourParser = null;

    // ******** Constants **********
    private static final String COMMANDS = "/commands.json";
    private static final String ROOMS = "/rooms.json";
    private static final String INVENTORY = "/inventory.json";
    private static final String SYN = "/synonyms.json";

    // ******** Fields **********
    private static JSONParser jsonParser = new JSONParser();
    private static JSONObject roomsJSON;
    private static JSONObject books;
    private JSONObject commandJSON;
    private JSONObject inventoryJSON;
    private JSONArray synJSON;
    private List<String> commands;


    //******** CTOR **********
    // read JSON info as streams and parse
    private OurJSONParser() throws IOException, ParseException {
        commandJSON = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(COMMANDS))));
        roomsJSON = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(ROOMS))));
        inventoryJSON = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(INVENTORY))));
        synJSON = (JSONArray) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(SYN))));
        JSONObject westHall = (JSONObject) roomsJSON.get("west hall");
        JSONObject hallItems = (JSONObject) westHall.get("item");
        JSONObject bookcase = (JSONObject) hallItems.get("bookcase");
        books = (JSONObject) bookcase.get("books");
        commands = new ArrayList<>();
    }

    // ******** Singleton Instantiation **********
    /* we do not want to instantiate multiple.
    static allows us to use through entire app where needed.*/
    static OurJSONParser instantiate() {
        if (ourParser == null) {
            try {
                ourParser = new OurJSONParser();
            }
            catch (IOException | ParseException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
        return ourParser;
    }


    // ******** Business Methods **********
    void commandParser(List<String> command) {
        String verb;
        String noun;

        // clear commands list to ensure there's only ever a singular set of commands present
        commands.clear();
        // access out valid verbs, nouns, and items arrays inside commands.json
        JSONArray verbs = (JSONArray) commandJSON.get("verbs");
        JSONArray nouns = (JSONArray) commandJSON.get("nouns");
        JSONArray items = (JSONArray) commandJSON.get("items");
        JSONObject room = (JSONObject) roomsJSON.get(Player.getInstance().getCurrentRoom());
        JSONObject roomItems = (JSONObject) room.get("item");
//        JSONObject westHall = (JSONObject) roomsJSON.get("west hall");
//        JSONObject hallItems = (JSONObject) westHall.get("item");
//        JSONObject bookcase = (JSONObject) hallItems.get("bookcase");
//        books = (JSONObject) bookcase.get("books");
        JSONObject clueHolder = null;

        if (command.size() == 1) {
            commands.add(command.get(0));
        }

        else if (!verbs.contains(command.get(0)) && !nouns.contains(command.get(1))) {
            System.out.println(command.get(0) + " " + command.get(1) + " is not a valid command");
        }
        else {
            commands.add(command.get(0));
            commands.add(command.get(1));
            Player.getInstance().playerActions(commands, room, roomItems, roomsJSON, synJSON, items, inventoryJSON, getBooks(), null);
        }
    }

    static JSONObject getRoomsJSON() {
        return roomsJSON;
    }

    static JSONObject getBooks() {
        return books;
    }
}