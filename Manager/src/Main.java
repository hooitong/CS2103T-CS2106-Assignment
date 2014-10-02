import java.util.Scanner;

/**
 * Test Driver for Simplified Process & Resource Manager
 *
 * @author Yeap Hooi Tong
 */

public class Main {

    /**
     * List of possible commands executed by user
     */
    private enum CommandType {
        INIT, CREATE, DELETE, REQUEST, RELEASE, TIME_OUT, ALL_PROCESS, ALL_RESOURCE, GET_PROCESS, GET_RESOURCE, INVALID
    }


    public static final int INVALID_NUM = -1;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        /* Initialise the PRManager */
        PRManager.init();

        /* Read entire input file */
        while (scanner.hasNextLine()) {
            handleUserCommand();
        }
    }

    /**
     * Handle user input and call the correct command to PRManager
     */
    private static void handleUserCommand() {
        String userInput = promptInput();

        /* if input contains empty line, ignore and return to parent */
        if (userInput.trim().equals("")) {
            return;
        }

        CommandType commandType = getCommandType(userInput);
        String[] arguments = splitArguments(removeFirstWord(userInput));

        /* Based on user's command, execute correct method in PRManager */
        switch (commandType) {
            case INIT:
                 /* create new line for new test sequence */
                System.out.print("\r\n\r\n");
                PRManager.init();
                break;

            case CREATE:
                if (arguments.length != 2) {
                    showError();
                } else {
                    PRManager.createProcess(arguments[0], stringToInt(arguments[1]));
                }
                break;

            case DELETE:
                if (arguments.length != 1) {
                    showError();
                } else {
                    PRManager.destroyProcess(arguments[0]);
                }
                break;

            case REQUEST:
                if (arguments.length != 2) {
                    showError();
                } else {
                    PRManager.requestResources(arguments[0], stringToInt(arguments[1]));
                }
                break;

            case RELEASE:
                if (arguments.length != 2) {
                    showError();
                } else {
                    PRManager.releaseResources(arguments[0], stringToInt(arguments[1]));
                }
                break;

            case TIME_OUT:
                PRManager.timeOut();
                break;

            case ALL_PROCESS:
                PRManager.printAllProcess();
                break;

            case ALL_RESOURCE:
                PRManager.printAllResource();
                break;

            case GET_PROCESS:
                if (arguments.length != 1) {
                    showError();
                } else {
                    PRManager.printProcess(arguments[0]);
                }
                break;

            case GET_RESOURCE:
                if (arguments.length != 1) {
                    showError();
                } else {
                    PRManager.printResource(arguments[0]);
                }
                break;

            case INVALID:
                showError();
                break;

            default:
                break;
        }
    }

    /**
     * Method to return input from user
     *
     * @return the next line in the given input
     */
    private static String promptInput() {
        return scanner.nextLine();
    }

    /**
     * Method to determine command type based on user's input
     *
     * @param userInput the input from the user
     * @return the CommandType the user specifies
     */
    private static CommandType getCommandType(String userInput) {
        String command = getFirstWord(userInput);

        if (command.equalsIgnoreCase("init")) {
            return CommandType.INIT;
        } else if (command.equalsIgnoreCase("cr")) {
            return CommandType.CREATE;
        } else if (command.equalsIgnoreCase("de")) {
            return CommandType.DELETE;
        } else if (command.equalsIgnoreCase("req")) {
            return CommandType.REQUEST;
        } else if (command.equalsIgnoreCase("rel")) {
            return CommandType.RELEASE;
        } else if (command.equalsIgnoreCase("to")) {
            return CommandType.TIME_OUT;
        } else if (command.equalsIgnoreCase("allp")) {
            return CommandType.ALL_PROCESS;
        } else if (command.equalsIgnoreCase("allr")) {
            return CommandType.ALL_RESOURCE;
        } else if (command.equalsIgnoreCase("getp")) {
            return CommandType.GET_PROCESS;
        } else if (command.equalsIgnoreCase("getr")) {
            return CommandType.GET_RESOURCE;
        } else {
            return CommandType.INVALID;
        }
    }

    /* Helper Methods - Self Explanatory */

    private static String getFirstWord(String userInput) {
        return userInput.trim().split("\\s+")[0];
    }

    private static String removeFirstWord(String userInput) {
        String[] split = userInput.trim().split("\\s+", 2);
        if (split.length == 1) {
            return "";
        } else {
            return split[1];
        }
    }

    private static String[] splitArguments(String userInput) {
        return userInput.trim().split("\\s+", 2);
    }

    private static int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return INVALID_NUM;
        }
    }

    private static void showError() {
        System.out.print("error ");
    }
}
