package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.log(Level.INFO,"Wordle application runs");
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("[a-z]{4}")) {
                    logger.log(Level.INFO, "Added valid word: {0}", line); // log valid added words
                    wordleDatabaseConnection.addValidWord(i, line);
                    i++;
                }else {
                    logger.log(Level.SEVERE, "Invalid user input ignored: {0}", line); // log invalid words
                }
            }

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            logger.log(Level.SEVERE, "Failed to load file.", e); // log file loading failure
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            String guess = "";

            // Loop to continuously propmt user guesses
            while (!guess.equals("q")) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine(); // read user input

               // if (guess.equals("q")){ 
                //    break; // break loop if user input = q
                if(!guess.matches("[a-z]{4}")){ //check if user input doesnt contain only 4 lowercase letters
                    System.out.println("'" + guess + "' is an invalid input. Please only use 4 lowercase letters\n");
                    logger.log(Level.WARNING, "Invalid user input: {0}", guess); // Log unexpected user input (maybe use info???)
                    continue; // skip current loop iteration
                }

                System.out.println("You've guessed '" + guess+"'.");

                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }
            }

        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "Unexpected error in input.", e); 
        }

    }
}