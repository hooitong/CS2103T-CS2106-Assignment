import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Yeap Hooi Tong
 * Metric Number: A0111736M
 *
 * This application allows the user to store information into the list
 * in a numbered format. The user is allowed to add, display, delete, clear
 * information in the list.
 *
 * Assumptions: The file can be saved to the disk after each user operation
 * to avoid loss of information due to unforeseen circumstances.
 *
 * We only accept the first argument provided in the command line interface
 * and ignore the rest that are provided.
 *
 * List of possible commands
 * -------------------------
 * - add [name]    (adds the string into the list)
 * - display       (display all strings in the list)
 * - delete [id]   (delete string from list based on id)
 * - clear         (remove all strings in list)
 * - exit          (exit application)
 *
 */

public class TextBuddy {

    /** These are system messages and can be formatted for uniformity. */
    private static final String MESSAGE_WELCOME_STRING =
            "Welcome to TextBuddy. %1$s is ready for use";
    private static final String MESSAGE_INPUT_INVALID =
            "Invalid Command: %1$s ";
    private static final String MESSAGE_CLEAR_FILE =
            "all content deleted from %1$s";
    private static final String MESSAGE_ADD_SUCCESS =
            "added to %1$s: \"%2$s\"";
    private static final String MESSAGE_DELETE_SUCCESS =
            "deleted from %1$s: \"%2$s\"";
    private static final String MESSAGE_DELETE_INVALID_ID =
            "Please enter a valid ID";
    private static final String MESSAGE_DISPLAY_EMPTY =
            "%1$s is empty";
    private static final String MESSAGE_DISPLAY_ITEM =
            "%1$s. %2$s";
    private static final String FILEPATH_INVALID_STRING =
            "You entered an invalid filename / filepath";
    private static final String FILEPATH_MISSING_STRING =
            "Please enter the path to the textfile to start.";
    private static final String SYSTEM_UNKNOWN_COMMAND =
            "System has encountered an unknown command type";
    private static final String SYSTEM_IO_ERROR =
            "It seems that we encountered an IO Exception";

    /** These variables are used to maintain information of the text file. */
    private static String fileName;
    private static File fileRef;
    private static final int FILE_INDEX = 0;

    /** This scanner object is declared as cvar due to its wide scope. */
    private static Scanner scanner = new Scanner(System.in);

    /** This list is used as a direct cache between the file and application. */
    private static ArrayList<String> fileCache = new ArrayList<String>();

    /** This is used to determine whether the input integer is valid. */
    private static final int INVALID_INTEGER = -1;

    /** This enum is used to store all possible commands in this application. */
    private enum CommandType {
        ADD_STRING, DISPLAY, DELETE, CLEAR, EXIT, INVALID
    };

    public static void main(String[] args) {
        checkFirstArgument(args);
        initFile(args[FILE_INDEX]);
        showToUser(String.format(MESSAGE_WELCOME_STRING, fileName));
        showMenu();
    }

    private static void showMenu() {
        while (true) {
            String feedback = handleUserCommand();
            showToUser(feedback);
        }
    }

    /**
     * This method is used to handle the user command by prompting
     * for input and check whether it is valid or not.
     *
     * If it is valid, the command will be executed accordingly
     * else if will return an invalid message.
     *
     * @return feedback after execution of command
     */
    private static String handleUserCommand() {
        String userInput = promptInput();

        // return an error message if user entered an empty string
        if (userInput.trim().equals("")) {
            returnInvalidMessage(userInput);
        }

        CommandType commandType = getCommandType(userInput);
        String argument = removeFirstWord(userInput);

        switch (commandType) {
            case ADD_STRING :
                return addString(argument);

            case DISPLAY :
                return display();

            case DELETE :
                return delete(argument);

            case CLEAR :
                return clear();

            case EXIT :
                System.exit(0);

            case INVALID :
                return returnInvalidMessage(userInput);

            default:
                throw new Error(SYSTEM_UNKNOWN_COMMAND);
        }
    }

    /**
     * This method is used to handle the user command
     * 'add' which appends the given argument to the cache
     * and perform a write operation to the file from cache.
     *
     * @param argument the string that user would like to append
     * @return feedback on the add operation
     */
    private static String addString(String argument) {
        addToCache(argument);
        writeToFile(true, argument);

        return String.format(MESSAGE_ADD_SUCCESS, fileName, argument);
    }

    /**
     * This method is used to handle the user command
     * 'display' where the program retrieves the string
     * from cache and return the string or system message (if empty).
     *
     * @return feedback on the display operation
     */
    private static String display() {
        String cacheResult = readFromCache();

        if (cacheResult.equals("")) {
            return String.format(MESSAGE_DISPLAY_EMPTY, fileName);
        } else {
            return cacheResult;
        }
    }

    /**
     * This method is used to handle the user command
     * 'delete' where the program checks whether it is
     * a valid id and if so, removes the line from the
     * cache based on the id. A write operation to the file
     * is then performed from cache.
     *
     * @param argument id / line number of the file
     * @return feedback on the delete operation
     */
    private static String delete(String argument) {
        int cacheId = stringToInt(argument);

        if (!checkValidId(cacheId)) {
            return MESSAGE_DELETE_INVALID_ID;
        } else {
            String deletedString = deleteFromCache(Integer.parseInt(argument));
            writeToFile(false, null);

            return String.format(MESSAGE_DELETE_SUCCESS, fileName,
                    deletedString);
        }
    }

    /**
     * This method is used to handle the user command
     * 'clear' where the program clears the cache and performs
     * a write operation to the file from cache.
     *
     * @return feedback on the clear operation
     */
    private static String clear() {
        fileCache.clear();
        writeToFile(false, null);

        return String.format(MESSAGE_CLEAR_FILE, fileName);
    }

    /**
     * This method is used to load every line of String from
     * the specified file into the cache via BufferedReader.
     */
    private static void readFromFile() {
        BufferedReader bReader = null;

        try {
            bReader = new BufferedReader(new FileReader(fileRef));
            String line = bReader.readLine();

            while (line != null) {
                addToCache(line);
                line = bReader.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new Error(FILEPATH_INVALID_STRING);
        } catch (IOException e) {
            throw new Error(SYSTEM_IO_ERROR);
        } finally {
            try {
                bReader.close();
            } catch (IOException e) {
                throw new Error(SYSTEM_IO_ERROR);
            }
        }
    }

    /**
     * This method is used to write either from the cache to the file
     * or appends a specified argument to the end of the file
     * via BufferedWriter.
     *
     * @param isAppend determines a append or write operation to the file
     * @param newString if isAppend, this string will be appended to file
     */
    private static void writeToFile(boolean isAppend, String newString) {
        BufferedWriter bWriter = null;

        try {
            bWriter = new BufferedWriter(new FileWriter(fileRef, isAppend));

            if (isAppend) {
                bWriter.append(newString);
            } else {
                // insert every line of string from cache to file
                for (String line : fileCache) {
                    bWriter.append(line);
                    bWriter.newLine();
                }
            }
        } catch (IOException e) {
            throw new Error(SYSTEM_IO_ERROR);
        } finally {
            if (bWriter != null) {
                try {
                    bWriter.close();
                } catch (IOException e) {
                    throw new Error(SYSTEM_IO_ERROR);
                }
            }
        }
    }

    private static void addToCache(String line) {
        fileCache.add(line);
    }

    private static String deleteFromCache(int id) {
        // array id is one less than cache id
        int arrayId = id - 1;

        return fileCache.remove(arrayId);
    }

    /**
     * This method utilizes a StringBuilder to concatenate all the strings
     * in the cache in the given display format meant for the end-user.
     *
     * @return the concatenated string from the cache
     */
    private static String readFromCache() {
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 1; i <= fileCache.size(); i++) {
            // array id is one less than cache id
            int arrayId = i - 1;

            sBuilder.append(String.format(MESSAGE_DISPLAY_ITEM, i,
                    fileCache.get(arrayId)));
            sBuilder.append(System.lineSeparator());
        }

        removeNewLine(sBuilder);

        return sBuilder.toString();
    }

    /**
     * This method is used to initialize the file as well as performing
     * a fetch from the file to the direct cache of the program.
     *
     * @param filePath the specified file path provided by user
     */
    private static void initFile(String filePath) {
        fileRef = new File(filePath);

        // if file does not exist, create a new file
        if (!fileRef.exists()) {
            try {
                fileRef.createNewFile();
            } catch (IOException e) {
                showToUser(FILEPATH_INVALID_STRING);
                System.exit(0);
            }
        } else {
            readFromFile();
        }

        // save the filename as a class variable for wider scope
        fileName = fileRef.getName();
    }

    /**
     * This method is used to check whether user specifies an argument
     * if no argument is found, show feedback and exit the program.
     *
     * @param args arguments provided in command line interface
     */
    private static void checkFirstArgument(String[] args) {
        if (args.length == 0) {
            showToUser(FILEPATH_MISSING_STRING);
            System.exit(0);
        }
    }

    /**
     * This method is used to check the cache Id and it is invalid
     * if it is not an integer, or exceed the boundary of the cache.
     *
     * @param cacheId the cache id specified by the user
     * @return true if valid else false
     */
    private static boolean checkValidId(int cacheId) {
        return !(cacheId == INVALID_INTEGER || cacheId <= 0
                || cacheId > fileCache.size());
    }

    private static void showToUser(String message) {
        System.out.println(message);
    }

    /**
     * This method is used to determine what is the command
     * to be performed by the user.
     *
     * @param userInput user's raw input
     * @return the command type based on user's input
     */
    private static CommandType getCommandType(String userInput) {
        String command = getFirstWord(userInput);

        if (command.equalsIgnoreCase("add")) {
            return CommandType.ADD_STRING;
        } else if (command.equalsIgnoreCase("delete")) {
            return CommandType.DELETE;
        } else if (command.equalsIgnoreCase("clear")) {
            return CommandType.CLEAR;
        } else if (command.equalsIgnoreCase("display")) {
            return CommandType.DISPLAY;
        } else if (command.equalsIgnoreCase("exit")) {
            return CommandType.EXIT;
        } else {
            return CommandType.INVALID;
        }
    }

    private static String promptInput() {
        System.out.print("Enter command: ");
        String userInput = scanner.nextLine();
        return userInput;
    }

    private static void removeNewLine(StringBuilder sBuilder) {
        if (sBuilder.length() > 0) {
            sBuilder.setLength(sBuilder.length() - 1);
        }
    }

    private static String removeFirstWord(String userInput) {
        return userInput.replace(getFirstWord(userInput), "").trim();
    }

    private static String getFirstWord(String userInput) {
        String commandTypeString = userInput.trim().split("\\s+")[0];
        return commandTypeString;
    }

    private static String returnInvalidMessage(String userInput) {
        return String.format(MESSAGE_INPUT_INVALID, userInput);
    }

    private static int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return INVALID_INTEGER;
        }
    }
}
