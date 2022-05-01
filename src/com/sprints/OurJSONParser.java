package com.sprints;

import com.sprints.controller.App;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class OurJSONParser {

    private static OurJSONParser ourParser = null;

    private static final String COMMANDS = "/commands.json";
    private static final String ROOMS = "/rooms.json";
    private static final String SYN = "/synonyms.json";

    private JSONParser jsonParser = new JSONParser();
    private JSONObject roomsJSON;
    private JSONObject commandJSON;
    private JSONArray synJSON;
    private List<String> commands;

    public static OurJSONParser instantiate() {
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

    private OurJSONParser() throws IOException, ParseException {
        commandJSON = (JSONObject) jsonParser.parse(new InputStreamReader(OurJSONParser.class.getResourceAsStream(COMMANDS)));
        roomsJSON = (JSONObject) jsonParser.parse(new InputStreamReader(OurJSONParser.class.getResourceAsStream(ROOMS)));
        synJSON = (JSONArray) jsonParser.parse(new InputStreamReader(OurJSONParser.class.getResourceAsStream(SYN)));
        commands = new ArrayList<>();
    }

    public List<String> commandParser(String noun, String verb) {
        commands.clear();
        JSONArray verbs = (JSONArray) commandJSON.get("verbs");
        JSONArray nouns = (JSONArray) commandJSON.get("nouns");
        JSONArray items = (JSONArray) commandJSON.get("items");
        JSONObject room = (JSONObject) roomsJSON.get(App.currentRoom);

        if (!verbs.contains(verb) && !nouns.contains(noun)) {
            System.out.println(verb + " " + noun + " is not a valid command");
        }
        else {
            commands.add(verb);
            commands.add(noun);
        }

        App.playerActions(commands.get(0), commands.get(1), room, roomsJSON, synJSON, items);
        return commands;
    }

//   public List<Location> locationCreator(String file, String noun) {
//        List<Location> locations = new ArrayList<>();
//
//        try {
////            InputStream in = getFileFromData(file);
////            InputStreamReader reader = new InputStreamReader(in);
////            roomsJSON = (JSONArray) jsonParser.parse(reader);
//            roomsJSON = (JSONArray) jsonParser.parse(new InputStreamReader(OurJSONParser.class.getResourceAsStream(ROOMS)));
//        }
//        catch (Exception e) {
//            System.out.println("Please check location of file: " + file);
//        }
//
//        for (Object o : roomsJSON) {
//            JSONObject obj = (JSONObject) o;
//            JSONObject insideRoom = (JSONObject) obj.get(noun);
//            JSONObject items = (JSONObject) insideRoom.get("item");
//            Set<String> itemsSet = items.keySet();
//            ArrayList<String> roomItems = new ArrayList<>(itemsSet);
//            String desc = (String) insideRoom.get("description");
//
//
//            Location location = new Location (noun, desc, roomItems);
//
//            locations.add(location);
//
//        }
//        return locations;
//   }

}