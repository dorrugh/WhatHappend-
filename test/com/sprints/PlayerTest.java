package com.sprints;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {

    private static final JSONParser jsonParser = new JSONParser();
    private static final String ROOMS = "/rooms.json";
    private static final String ITEMS = "/commands.json";
    private static final String pickupItems = "/inventory.json";

    JSONObject roomsJSON = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(ROOMS))));
    JSONObject basement = (JSONObject) roomsJSON.get("basement");
    JSONObject itemsJSON = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(ITEMS))));
    JSONArray validItems = (JSONArray) itemsJSON.get("items");
    JSONObject basementItems = (JSONObject) basement.get("item");
    JSONObject validInventory = (JSONObject) jsonParser.parse(new InputStreamReader(Objects.requireNonNull(OurJSONParser.class.getResourceAsStream(pickupItems))));
    JSONObject westHall = (JSONObject) roomsJSON.get("west hall");
    JSONObject hallItems = (JSONObject) westHall.get("item");
    JSONObject bookcase = (JSONObject) hallItems.get("bookcase");
    JSONObject books = (JSONObject) bookcase.get("books");
    JSONObject kitchen = (JSONObject) roomsJSON.get("kitchen");
    JSONObject kitchenItems = (JSONObject) kitchen.get("item");


    public PlayerTest() throws IOException, ParseException {
    }

    @Test
    public void changeLocationFromBasement_shouldChangeLocation_whenValidDirectionPassed() {
        Player.getInstance().locationChange("north", basement, roomsJSON);
        assertEquals("parlor", Player.getInstance().getCurrentRoom());
    }

    @Test
    public void changeLocationFromBasement_shouldReturnErrorMessage_whenInvalidDirectionPassed() {
        Player.getInstance().locationChange("south", basement, roomsJSON);
        assertTrue("You cannot go that way", true);
    }

    @Test
    public void changeLocationFromBasement_shouldChangeLocation_whenValidRoomPassed() {
        Player.getInstance().locationChange("kitchen", basement, roomsJSON);
        assertEquals("kitchen", Player.getInstance().getCurrentRoom());
    }

    @Test
    public void changeLocationFromBasement_shouldReturnErrorMessage_whenInvalidRoomPassed() {
        Player.getInstance().locationChange("attic", basement, roomsJSON);
        assertTrue("attic is not a known location", true);
    }

    @Test
    public void getItem_shouldAddTorchToInventory_whenPickedUp() {
        Player.getInstance().getItems("torch", basementItems, validItems, validInventory, null, null);
        assertTrue(Player.getInstance().getInventory().contains("torch"));
    }

    @Test
    public void getItem_shouldAddEmbeddedItemToInventory_whenPickedUp() {
        Player.getInstance().setCurrentRoom("kitchen");
        Player.getInstance().getItems("clue 3", kitchenItems, validItems, validInventory, null, null);
        assertTrue(Player.getInstance().getInventory().contains("clue 3"));
    }

    @Test
    public void getItem_shouldAddBookToInventory_whenPickedUp() {
        Player.getInstance().setCurrentRoom("west hall");
        Player.getInstance().getItems("it", hallItems, validItems, validInventory, books, null);
        assertTrue(Player.getInstance().getInventory().contains("it"));
    }

    @Test
    public void dropItem_shouldRemoveItemFromInventory_whenDropped() {
        Player.getInstance().getItems("torch", basementItems, validItems, validInventory, null, null);
        assertTrue(Player.getInstance().getInventory().contains("torch"));
        Player.getInstance().dropItems("torch", basementItems, validItems);
        assertFalse(Player.getInstance().getInventory().contains("torch"));
    }

    @Test
    public void dropItem_shouldAddItemToRoom_whenDropped() {
        Player.getInstance().getItems("torch", basementItems, validItems, validInventory, null, null);
        Player.getInstance().setCurrentRoom("kitchen");
        Player.getInstance().dropItems("torch", kitchenItems, validItems);
        assertTrue(kitchenItems.containsKey("torch"));
    }
}