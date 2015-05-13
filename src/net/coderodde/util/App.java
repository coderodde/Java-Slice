package net.coderodde.util;

import java.util.Scanner;

public class App {

    public static void main(String... args) {
        printHelp();
        
        final Character[] array = new Character[10];

        for (char c = '0'; c <= '9'; ++c) {
            array[c - '0'] = c;
        }

        final Slice<Character> slice = Slice.<Character>create()
                                            .withArray(array)
                                            .all();
        
        final Scanner scanner = new Scanner(System.in);

        System.out.println(slice);

        while (scanner.hasNext()) {

            final String line = scanner.nextLine().trim().toLowerCase();
            exec(line, slice);
            System.out.println(slice);
        }
    }
    
    private static void exec(String line, Slice<Character> slice) {
        if (line.isEmpty()) {
            return;
        }
        
        String[] parts = line.split("\\s+");
        final int len = parts.length;
        
        if (len == 0) {
            return;
        }
        
        // Handle commands without arguments.
        switch (parts[0]) {
            case "rev":
                slice.reverse();
                return;
                
            case "help":
                printHelp();
                return;
                
            case "quit":
                System.out.println("Bye!");
                System.exit(0);
        }
        
        if (parts[0].equals("set") && len < 3) {
            System.out.println("\"set\" requires two arguments, but only " +
                               (len - 1) + "received.");
            return;
        }
        
        if (len < 2) {
            // Arguments not available.
            System.out.println("Expecting an argument for command \"" + 
                               parts[0] + "\"");
            return;
        }
        
        int argument;
        int argument2 = 0;
        
        try {
            argument = Integer.parseInt(parts[1]);
            
            if (parts[0].equals("set")) {
                argument2 = Integer.parseInt(parts[2]);
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Argument is not an integer: " + parts[1]);
            return;
        }
        
        switch (parts[0]) {
            case "move":
                slice.move(argument);
                break;
                
            case "headshift":
                slice.moveHeadPointer(argument);
                break;
                
            case "tailshift":
                slice.moveTailPointer(argument);
                break;
                
            case "rotate":
                slice.rotate(argument);
                break;
                
            case "rev":
                slice.reverse();
                break;
                
            case "set":
                if (parts[2].length() != 1) {
                    System.out.println(
                            "Error: expected one character, received " + 
                            parts[2].length());
                    return;
                }
                
                slice.set(argument, parts[2].charAt(0));
        }
    }
    
    private static final String nl = "\n";
    
    private static void printHelp() {
        System.out.println(
    "*******************************************************************" + nl +
    "quit --------- Quit this program." + nl +
    "help --------- Print this help." + nl +
    "move N ------- Move the slice N steps." + nl +
    "headshift N -- Shift the head of the slice N steps." + nl +
    "tailshift N -- Shift the tail of the slice N steps." + nl +
    "rotate N ----- Rotate the slice N steps." + nl +
    "rev ---------- Reverse the range covered by the slice." + nl +
    "set N c ------ Set the slice component at index N to character 'c'." + nl +
    "*******************************************************************");
    }
}
