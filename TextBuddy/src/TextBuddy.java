import java.io.*;
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

    private static final String WELCOME_MESSAGE_STRING =
            "Welcome to TextBuddy. %1$s is ready for use";
    private static final String INVALID_FILEPATH_STRING =
            "You entered an invalid filename / filepath";
    private static final String MISSING_FILEPATH_STRING =
            "Please enter the path to the textfile to start.";

    // These are used to maintain information of the provided text file
    private static String filePath;
    private static File fileRef;

    private static Scanner scanner = new Scanner(System.in);

    // This enum type is used to store all possible commands in this application
    private enum COMMAND_TYPE{
        ADD_STRING, DISPLAY, DELETE, CLEAR, EXIT
    };

    public static void main(String[] args) {
        checkFirstArgument(args);
        initFile(args[0]);
        showToUser(String.format(WELCOME_MESSAGE_STRING, fileRef.getName()));
        acceptUserCommand();
    }

    private static void acceptUserCommand() {
        while(true){
            System.out.print("Enter command:");
            String input = scanner.nextLine();
            String
            switch (userCommand) {
                case value:

                    break;

                default:
                    break;
            }
        }
    }

    private static void initFile(String fp) {
        filePath = fp;
        fileRef = new File(filePath);
        if(!fileRef.exists()){
            showToUser(INVALID_FILEPATH_STRING);
        }
    }

    private static void checkFirstArgument(String[] args) {
        if (args.length == 0) {
            showToUser(MISSING_FILEPATH_STRING);
            System.exit(0);
        }
    }

    private static void showToUser(String message) {
        System.out.println(message);
    }

    private static void acceptInput() {

    }

}
