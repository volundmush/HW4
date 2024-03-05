/*
 * Author:  Andrew Bastien
 * Email: abastien2021@my.fit.edu
 * Course:  CSE 2010
 * Section: 23
 * Term: Spring 2024
 * Project: HW4, Priority Queue
 */

import java.io.File;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HW4
{

    private static class Timestamp implements Comparable<Timestamp> {
        public int hours;
        public int minutes;
        public int seconds;

        public Timestamp(String timeString) {
            // the timestring is HHMMSS with possibly no leading zeros, so HMMSS or even MSS is possible, so let's add
            // zeros to the left side up until the full six characters if there are less than six characters.
            timeString = String.format("%06d", Integer.parseInt(timeString));
            // now we split into hours, minutes and seconds to become local variables.
            hours = Integer.parseInt(timeString.substring(0, 2));
            minutes = Integer.parseInt(timeString.substring(2, 4));
            seconds = Integer.parseInt(timeString.substring(4, 6));
        }

        @Override
        public int compareTo(Timestamp other) {
            if (this.hours != other.hours) {
                return Integer.compare(this.hours, other.hours);
            } else if (this.minutes != other.minutes) {
                return Integer.compare(this.minutes, other.minutes);
            } else {
                return Integer.compare(this.seconds, other.seconds);
            }
        }
    }

    private static class Key implements Comparable<Key> {
        public Timestamp time;
        public int price;

        public Key(String timeString, int price) {
            time = new Timestamp(timeString);
            this.price = price;
        }

         @Override
         public int compareTo(Key other) {
             // First compare by price. if price matches, compare by time.
               if (this.price != other.price) {
                  return Integer.compare(this.price, other.price);
               } else {
                  return this.time.compareTo(other.time);
               }
         }
    }

    private static class Data {
        public String owner;
        public int quantity;

         public Data(String owner, int quantity) {
               this.owner = owner;
               this.quantity = quantity;
         }

        public String getOwner() {
            return owner;
        }

         public int getQuantity() {
               return quantity;
         }

         public void setQuantity(int quantity) {
               this.quantity = quantity;
         }
    }

    private final Scanner data;

    private final HeapPriorityQueue<Key, Data> buyOrders = new HeapPriorityQueue<>(Collections.reverseOrder());
    private final HeapPriorityQueue<Key, Data> sellOrders = new HeapPriorityQueue<>();


    public HW4(final Scanner data) {
        this.data = data;
    }

    private void EnterBuyOrder(String inTime, String inBuyer, String inPrice, String inQuantity) {
         Key key = new Key(inTime, Integer.parseInt(inPrice));
         Data data = new Data(inBuyer, Integer.parseInt(inQuantity));
         buyOrders.insert(key, data);
         String out = String.format("EnterBuyOrder: %s %s %s %s", inTime, inBuyer, inPrice, inQuantity);
         System.out.println(out);
    }

    private void EnterSellOrder(String inTime, String inSeller, String inPrice, String inQuantity) {
         Key key = new Key(inTime, Integer.parseInt(inPrice));
         Data data = new Data(inSeller, Integer.parseInt(inQuantity));
         sellOrders.insert(key, data);
         String out = String.format("EnterSellOrder: %s %s %s %s", inTime, inSeller, inPrice, inQuantity);
         System.out.println(out);
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

        Entry<Key, Data> buyMin = buyOrders.min();
        Entry<Key, Data> sellMin = sellOrders.min();

        System.out.println("Finished");
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
