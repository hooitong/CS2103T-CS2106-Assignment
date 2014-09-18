import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Class that represent a single process in the system.
 *
 * @author Yeap Hooi Tong
 */

public class Process {

    /**
     * The number of types of priorities for processes
     */
    public static final int NUM_PRIORITY = 3;

    /**
     * Priorities available for each process
     */
    public static final int INIT = 0;
    public static final int USER = 1;
    public static final int SYSTEM = 2;

    /**
     * Running statuses available for each process
     */
    public static final int RUNNING = 0;
    public static final int READY = 1;
    public static final int BLOCKED = 2;

    /**
     * PCB required states & pointers
     */
    private String pid;
    private int priority;
    private LinkedHashMap<Resource, Integer> other_resources;
    private int status_type;
    private LinkedList<Process> status_list;
    private Process parent;
    private LinkedList<Process> child;
    private int blockedReqUnit;

    /**
     * Sole constructor that requires two input
     * for creation of object. All new processes
     * are put as READY state by default. Parent
     * Pointer not updated directly by constructor.
     *
     * @param pid      process identification tag
     * @param priority any priority listed above
     */
    public Process(String pid, int priority) {
        this.pid = pid;
        this.priority = priority;
        this.other_resources = new LinkedHashMap<Resource, Integer>();
        this.status_type = Process.READY;
        this.status_list = null;
        this.parent = null;
        this.child = new LinkedList<Process>();
        this.blockedReqUnit = 0;
    }

    /* Getters & Setters Methods */

    public String getPid() {
        return pid;
    }

    public int getPriority() {
        return priority;
    }

    public LinkedHashMap<Resource, Integer> getResources() {
        return other_resources;
    }

    public int getType() {
        return status_type;
    }

    public void setType(int status_type) {
        this.status_type = status_type;
    }

    public LinkedList<Process> getList() {
        return status_list;
    }

    public void setList(LinkedList<Process> status_list) {
        this.status_list = status_list;
    }

    public Process getParent() {
        return parent;
    }

    public void setParent(Process parent) {
        this.parent = parent;
    }

    public LinkedList<Process> getChild() {
        return child;
    }

    public void addChild(Process c) {
        this.child.add(c);
    }

    public int getBlockedReqUnit() {
        return blockedReqUnit;
    }

    public void setBlockedReqUnit(int blockedReqUnit) {
        this.blockedReqUnit = blockedReqUnit;
    }
}
