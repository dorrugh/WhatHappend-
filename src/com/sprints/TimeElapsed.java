package com.sprints;

public class TimeElapsed {
    private static final long startTime = System.currentTimeMillis();

    // get the in game timer's current time remaining
    public static String getTime() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        // convert milli to seconds
        elapsedTime = elapsedTime /1000;
        // update remaining game time
        int runtime = 300;
        String remaining = Integer.toString((int) (runtime - elapsedTime));
        if (Integer.parseInt(remaining) < 0) {
            remaining = Integer.toString(0);
        }
        return remaining;
    }
}