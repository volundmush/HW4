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

    // This class is used by the priority queue's Key data structure to store the time in a sortable manner.
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

        public String toString() {
            return String.format("%02d%02d%02d", hours, minutes, seconds);
        }
    }

    // Combination of price and Time that can be sorted by the priority queue.
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
                  return (this.time.compareTo(other.time) * -1);
               }
         }
    }

    // Extra data/value used in the Priority queue as it's Value for entries.
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

    // Called by EnterBuyOrder and EnterSellOrder to check if we have any good sales to perform.
    private void checkExchange() {
       // First check for both a valid buy and a valid sell order...
       Entry<Key, Data> buyMin = buyOrders.min();
       if(buyMin == null) return;
       Entry<Key, Data> sellMin = sellOrders.min();
       if(sellMin == null) return;

       Data buyData = buyMin.getValue();
       Data sellData = sellMin.getValue();

       // Now we will check to see if the buy price is greater than or equal to the sell price. If not, return.
       if(buyMin.getKey().price < sellMin.getKey().price) return;

       // The transaction price is the average of the two prices. No reason to do an if-check for inequality as the
       // average of 5000 and 5000 is 5000.
       int transactionPrice = (buyMin.getKey().price + sellMin.getKey().price) / 2;

       // The buyer and seller both have a posted quantity under the Data (value) of the orders. You can't but more
       // than what's being sold and you also aren't going to buy more than you want. Let's determine how many
       // will be transferred.
       int transferQuantity = Math.min(buyData.quantity, sellData.quantity);

       // Reduce the quantity of the buyer and seller by the transfer quantity. We can use setQuantity on the Data class.
       buyData.setQuantity(buyData.quantity - transferQuantity);
       sellData.setQuantity(sellData.quantity - transferQuantity);

       // Let's report the sale.
       System.out.println(String.format("ExecuteBuySellOrders %s %s", transactionPrice, transferQuantity));
       System.out.println(String.format("Buyer: %s %s", buyData.owner, buyData.quantity));
       System.out.println(String.format("Seller: %s %s", sellData.owner, sellData.quantity));

       // check both buyMin and sellMin to see if there is remainning quantity. If there is no remaining quantity, then
       // removeMin() from the priority queues.
       if(buyData.quantity == 0) {
          buyOrders.removeMin();
       }
       if(sellData.quantity == 0) {
          sellOrders.removeMin();
       }

       // there might be more transactions to perform, so let's call checkExchange again.
       checkExchange();

    }

    private void EnterBuyOrder(String inTime, String inBuyer, String inPrice, String inQuantity) {
         Key key = new Key(inTime, Integer.parseInt(inPrice));
         Data data = new Data(inBuyer, Integer.parseInt(inQuantity));
         buyOrders.insert(key, data);
         String out = String.format("EnterBuyOrder: %s %s %s %s", inTime, inBuyer, inPrice, inQuantity);
         System.out.println(out);
         checkExchange();
    }

    private void EnterSellOrder(String inTime, String inSeller, String inPrice, String inQuantity) {
         Key key = new Key(inTime, Integer.parseInt(inPrice));
         Data data = new Data(inSeller, Integer.parseInt(inQuantity));
         sellOrders.insert(key, data);
         String out = String.format("EnterSellOrder: %s %s %s %s", inTime, inSeller, inPrice, inQuantity);
         System.out.println(out);
         checkExchange();
    }

    private void DisplayHighestBuyOrder(String inTime) {
       Entry<Key, Data> min = buyOrders.min();
       System.out.println(String.format("DisplayHighestBuyOrder %s %s %s %s %s", inTime, min.getValue().owner, min.getKey().time, min.getKey().price, min.getValue().quantity));
    }

    private void DisplayLowestSellOrder(String inTime) {
       Entry<Key, Data> min = sellOrders.min();
       System.out.println(String.format("DisplayLowestSellOrder %s %s %s %s %s", inTime, min.getValue().owner, min.getKey().time, min.getKey().price, min.getValue().quantity));
    }

    private void handleDataLine () {
        final String line = data.nextLine().trim();
        // Split by spaces...
        if(line.isEmpty()) return;
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
