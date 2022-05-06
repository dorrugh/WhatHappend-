package com.sprints;

import com.apps.util.Console;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Player {
    // ******** Class Singleton **********
    private static Player player = null;

    // ******** Fields **********
    private String currentRoom = "basement";
    private List<String> inventory = new ArrayList<>();
    private List<String> cluesFound = new ArrayList<>();
    private boolean itemEquipped = false;

    // ******** Business Methods **********
    /* we do not want to instantiate multiple.
    static allows us to use through entire app where needed.*/
    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    // ******** Business Methods **********
    /* takes in all commands along with current room info, rooms.json info,
    synonyms, and valid items */
    public void playerActions(List<String> commands, JSONObject room, JSONObject roomItems, JSONObject roomsObj, JSONArray synonymObj, JSONArray validItems, JSONObject inventoryObj, JSONObject books, JSONObject clueHolder) {
        // separate synonyms.json at indexes
        JSONArray verbObj1 = (JSONArray) synonymObj.get(0); // go
        JSONArray verbObj2 = (JSONArray) synonymObj.get(1); // get
        JSONArray verbObj3 = (JSONArray) synonymObj.get(2); // look
        JSONArray verbObj4 = (JSONArray) synonymObj.get(3); // equip
        JSONArray verbObj5 = (JSONArray) synonymObj.get(4); // drop

        // pass to function depending on which synonym array verb belongs to
        if (verbObj1.contains(commands.get(0))) {
            locationChange(commands.get(1), room, roomsObj);
        }else if (verbObj2.contains(commands.get(0))) {
            getItems(commands.get(1), roomItems, validItems, inventoryObj, books, clueHolder);
        }else if (verbObj3.contains(commands.get(0))) {
            look(commands.get(1), room, roomItems, validItems, inventoryObj, books);
        }else if (verbObj4.contains(commands.get(0))) {
            equip(commands.get(1));
        }else if (verbObj5.contains(commands.get(0))) {
            dropItems(commands.get(1), roomItems, validItems);
        }
    }

    // pick up items
    void getItems(String noun, JSONObject roomItems, JSONArray validItems, JSONObject inventoryObj, JSONObject books, JSONObject clueHolder) {
        // check if item is a valid and something we can hold in inventory
        if(validItems.contains(noun) && inventoryObj.containsKey(noun)) {
            //check if the room had the item player is trying to get
            if (roomItems.containsKey(noun)) {
                inventory.add(noun);
                roomItems.remove(noun);
                System.out.println(noun + " picked up");
            }
            // check location and if noun is the title of one of the books on bookcase
            else if (getCurrentRoom().equals("west hall") && books.containsKey(noun)) {
                inventory.add(noun);
                books.remove(noun);
                System.out.println(noun + " picked up");
            }
            else if (!roomItems.containsKey(noun) && inventory.contains(noun)) {
                System.out.println("You already have " + noun);
            }
            // what to do if noun is not present clearly present in room
            else if (!roomItems.containsKey(noun) && "kitchen".equals(getCurrentRoom()) || "parlor".equals(getCurrentRoom()) || "east hall".equals(getCurrentRoom()) || "west hall".equals(getCurrentRoom())) {
                // switch case for rooms that hold clues inside other objects
                switch (getCurrentRoom()) {
                    case "kitchen":
                        clueHolder = (JSONObject) roomItems.get("cabinets");
                        break;
                    case "east hall":
                        clueHolder = (JSONObject) roomItems.get("vase");
                        break;
                    case "parlor":
                        clueHolder = (JSONObject) roomItems.get("portrait");
                        break;
                    case "west hall":
                        clueHolder = (JSONObject) roomItems.get("bookcase");
                        break;
                    default:
                        break;
                }
                JSONObject clueObj = (JSONObject) clueHolder.get("clue");
                String clue = (String) clueObj.get("name");
                // check is noun is equal to name of one of our clues
                if(noun.equals(clue)) {
                    cluesFound.add(clue);
                    inventory.add(clue);
                    clueHolder.remove("clue");
                    System.out.println(noun + " picked up");
                }
                else {
                    System.out.println(noun + " is not in this room");
                }
            }
        }
        // how we handle it if item is not valid or something player cannot hold in inventory
        else {
            System.out.println("You cannot pick up " + noun);
        }
    }

    // allows player to drop items
    private void dropItems(String noun, JSONObject roomItems, JSONArray validItems) {
        JSONObject itemDescription = (JSONObject) roomItems.get(noun);
        if (validItems.contains(noun) && !roomItems.containsKey(noun)) {
            System.out.println(noun + " dropped");
            inventory.remove(noun);
            roomItems.put(noun, itemDescription);
        }
        if (noun.equals("torch")) {
            itemEquipped = false;
        }

    }

    // allow player to equip items (only torch at this moment)
    void equip(String noun) {
        if (!"torch".equals(noun)){
            System.out.println("You can only equip a torch at this time.");
        }
        else if (!inventory.contains("torch")) {
            System.out.println("You must get the torch to use it!");
        }
        else {
            System.out.println("Torch equipped");
            itemEquipped = true;
        }
    }

    // change player location
    void locationChange(String noun, JSONObject room, JSONObject roomsObj) {
        // if the roomsObj (json file with room info) has a location with a name that matches the player
        // input noun, we set it as the current room
        if(roomsObj.containsKey(noun)) {
            setCurrentRoom(noun);
        }
        // checks if current room has a given direction (north, south, east, west) amd if so
        //changes current room to the value of said direction
        else if (room.containsKey(noun)) {
            setCurrentRoom((String) room.get(noun));
        }
        else {
            System.out.println("You cannot go that way");
        }
    }

    // look at room and items in room
    private void look(String noun, JSONObject room, JSONObject roomItems, JSONArray validItems, JSONObject inventoryObj, JSONObject books) {
        Set<String> bookKeys = books.keySet();

        // checks location as west hall the only place books keyword is valid and
        // outputs the title of the books on the bookcase for player
        if ("west hall".equals(getCurrentRoom()) && "books".equals(noun) && itemEquipped) {
            System.out.println("You see " + bookKeys);
        }
        else if ("west hall".equals(getCurrentRoom()) && bookKeys.contains(noun) && itemEquipped) {
            JSONObject book = (JSONObject) books.get(noun);
            String description = (String) book.get("description");
            System.out.println(description);
        }
        // gives description if so prints out that room description
        else if (noun.equals(getCurrentRoom()) || "here".equals(noun) && itemEquipped) {
            System.out.println(room.get("description"));
            return;
        }
        // gives description of items if they are in player inventory
        else if (inventory.contains(noun) && itemEquipped) {
            JSONObject itemDescription = (JSONObject) inventoryObj.get(noun);
            System.out.println(itemDescription.get("description"));
            return;
        }
        // allows player to get description of items in room
        else if (validItems.contains(noun) && room.containsKey("item") && roomItems.containsKey(noun) && itemEquipped){
            JSONObject item = (JSONObject) roomItems.get(noun);
            System.out.println(item.get("description"));
            return;
        }
        // response if player attempts to
        else if (!noun.contains(getCurrentRoom()) && itemEquipped || !roomItems.containsKey(noun) && itemEquipped) {
            System.out.println("You cannot see " + noun + " from here");
            return;
        }
        else {
            System.out.println("Too dark to see. Some light would help");
            return;
        }
        Utils.pressEnterToContinue();
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public boolean isItemEquipped() {
        return itemEquipped;
    }
}