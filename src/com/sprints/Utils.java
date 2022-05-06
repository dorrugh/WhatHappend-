package com.sprints;

import com.apps.util.Console;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Utils {
    // outputs text with delay
    static void printWithDelays(String data) throws InterruptedException {
        try {
            for (char ch : data.toCharArray()) {
                System.out.print(ch);
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    // prompts players to press enter to continue game
    static void pressEnterToContinue() {
        Console.blankLines(1);
        System.out.println("Press ENTER to continue");
        try {
            System.in.read(new byte[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}