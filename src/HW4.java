/*
 * Author:  Andrew Bastien
 * Email: abastien2021@my.fit.edu
 * Course:  CSE 2010
 * Section: 23
 * Term: Spring 2024
 * Project: HW4, Priority Queue
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HW4
{
    private final Scanner data;


    public HW4(final Scanner data) {
        this.data = data;
    }

    private void EnterBuyOrder(String inTime, String inBuyer, String inPrice, String inQuantity) {

    }

    private void EnterSellOrder(String inTime, String inSeller, String inPrice, String inQuantity) {

    }

    private void DisplayHighestBuyOrder(String inTime) {

    }

    private void DisplayLowestSellOrder(String inTime) {

    }

    private void handleDataLine () {
        final String line = data.nextLine();
        // Split by spaces...
        String[] parts = line.split(" " );
        String cmd = parts[0].trim();

        // First part is the command...
        // I would love to use switch-case here but the java IDE in use doesn't like it.
        if(cmd.equals("EnterBuyOrder")) {
            EnterBuyOrder(parts[1], parts[2], parts[3], parts[4]);
        } else if(cmd.equals("EnterSellOrder")) {
            EnterSellOrder(parts[1], parts[2], parts[3], parts[4]);
        } else if(cmd.equals("DisplayHighestBuyOrder")) {
            DisplayHighestBuyOrder(parts[1]);
        } else if(cmd.equals("DisplayLowestSellOrder")) {
            DisplayLowestSellOrder(parts[1]);
        } else {
            System.out.println("Invalid command: " + cmd);
            return;
        }
    }

    public void run() {
        // First gather up the data...
        while (data.hasNextLine()) {
            handleDataLine();
        }

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No file path provided.");
            return;
        }

        // use java.util.Scanner because dang this is complicated.
        try {
            Scanner data = new Scanner(new File(args[0]), StandardCharsets.US_ASCII.name());
            HW4 program = new HW4(data);
            program.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
        }
    }

}
