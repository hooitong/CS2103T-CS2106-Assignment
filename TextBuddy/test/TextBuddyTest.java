import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
* @author Yeap Hooi Tong
*
* This class is used to test all commands in TextBuddy
*
* List of commands to test
* -------------------------
* - add <name>    (adds the string into the list)
* - display       (display all strings in the list)
* - delete <id>   (delete string from list based on id)
* - sort          (sort all strings in the list)
* - search <word> (search and return all strings with word)
* - clear         (remove all strings in list)
*
*/
public class TextBuddyTest {
    private static final String TEST_FILE = "testDefault.txt";

    @BeforeClass
    public static void initTextBuddy() {
        TextBuddy.initFile(TEST_FILE);
    }

    @Test
    public void addLine() {
        assertEquals("Test that no arguments should fail",
                "you cannot add empty text", TextBuddy.executeCommand("add"));

        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"abc\"", TextBuddy.executeCommand("add abc"));
    }

    @Test
    public void display() {
        clearFile();

        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"line 1\"", TextBuddy.executeCommand("add line 1"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"line 2\"", TextBuddy.executeCommand("add line 2"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"line 3\"", TextBuddy.executeCommand("add line 3"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"line 4\"", TextBuddy.executeCommand("add line 4"));

        assertEquals("Test whether the display function works", ""
                + "1. line 1\n2. line 2\n3. line 3\n4. line 4",
                TextBuddy.executeCommand("display"));
    }

    @Test
    public void deleteLine() {
        display();
        assertEquals("Test whether delete work", "deleted from " + TEST_FILE
                + ": \"line 3\"", TextBuddy.executeCommand("delete 3"));

        assertEquals("Test whether line 3 have been deleted", ""
                + "1. line 1\n2. line 2\n3. line 4",
                TextBuddy.executeCommand("display"));
    }

    @Test
    public void clearFile() {
        assertEquals("Test whether clear works", "all content deleted from "
                + TEST_FILE, TextBuddy.executeCommand("clear"));
        assertEquals("Check whether file is empty", TEST_FILE + " is empty",
                TextBuddy.executeCommand("display"));
    }

    @Test
    public void sortAlphabetically() {
        clearFile();
        assertEquals("Test empty sort should return message",
                "nothing to sort", TextBuddy.executeCommand("sort"));

        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"z\"", TextBuddy.executeCommand("add z"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"c\"", TextBuddy.executeCommand("add c"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"g\"", TextBuddy.executeCommand("add g"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"a\"", TextBuddy.executeCommand("add a"));

        assertEquals("Test whether sort works", "all lines have been sorted",
                TextBuddy.executeCommand("sort"));

        assertEquals("Returned result must be sorted alphabetically",
                "1. a\n2. c\n3. g\n4. z", TextBuddy.executeCommand("display"));
    }

    @Test
    public void searchWord() {
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"i am groot\"",
                TextBuddy.executeCommand("add i am groot"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"i am tom\"", TextBuddy.executeCommand("add i am tom"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"i am jack\"", TextBuddy.executeCommand("add i am jack"));
        assertEquals("Test whether add function works", "added to " + TEST_FILE
                + ": \"i is scrub\"",
                TextBuddy.executeCommand("add i is scrub"));

        assertEquals("Test whether searching works", "1. i am groot",
                TextBuddy.executeCommand("search groot"));

        assertEquals("Test whether searching works", "1. i is scrub",
                TextBuddy.executeCommand("search scrub"));

        assertEquals("Test whether searching works",
                "1. i am groot\n2. i am tom\n3. i am jack",
                TextBuddy.executeCommand("search am"));

        assertEquals("Test whether searching works",
                "1. i am groot\n2. i am tom\n3. i am jack\n4. i is scrub",
                TextBuddy.executeCommand("search i"));

        assertEquals("Test proper message returned when no results",
                "no line containing 'you' is found",
                TextBuddy.executeCommand("search you"));

        assertEquals("Return error message when no keyword entered",
                "please enter something to search",
                TextBuddy.executeCommand("search"));
    }

}
