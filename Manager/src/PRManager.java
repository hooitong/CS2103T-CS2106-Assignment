import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Core Process & Resource Manager for the project.
 *
 * @author Yeap Hooi Tong
 */

public class PRManager {

    /**
     * Number of resources in the manager.
     */
    private static final int NUM_RESOURCE = 4;

    /**
     * The Ready List.
     */
    @SuppressWarnings({"unchecked"})
    private static LinkedList<Process>[] readyList =
        new LinkedList[Process.NUM_PRIORITY];

    /**
     * Pointers to all resources in the system.
     */
    private static Resource[] resources = new Resource[NUM_RESOURCE];

    /**
     * Pointers to all processes in the system.
     */
    private static ArrayList<Process> processes = new ArrayList<Process>();

    /**
     * Pointer to the running process in the system (Reference purposes).
     */
    private static Process runningProcess;

    /**
     * Initialisation method that is called explicitly or at the start of
     * the test driver.
     */
    public static void init() {

        /* Create LinkedList object for each priority in the Ready List */
        for (int i = 0; i < Process.NUM_PRIORITY; i++) {
            readyList[i] = new LinkedList<Process>();
        }

        /* Create ArrayList object to store all pointers of process in the manager */
        processes = new ArrayList<Process>();

        /* Create new Resource object and store as pointers in the Array for Ref */
        for (int i = 1; i <= NUM_RESOURCE; i++) {
            resources[i - 1] = new Resource("R" + i, i);
        }

        /* Reset pointer of runningProcess */
        runningProcess = null;

        /* Create a 'init' process at the first process in the system */
        createProcess("init", Process.INIT);
    }

    /**
     * Method called by the driver to execute a create process
     * by the system's running process.
     *
     * @param pid      the new process id
     * @param priority the new priority
     */
    public static void createProcess(String pid, int priority) {
        /* Priority should only be SYSTEM or USER or INIT */
        if (priority >= Process.NUM_PRIORITY || priority < 0 || priority == Main.INVALID_NUM) {
            showError();
            return;
        }

        /* Priority should only be INIT if PID is init */
        if (!(pid.equalsIgnoreCase("init")) && priority == Process.INIT) {
            showError();
            return;
        }

        /* pid should not exist in the system/manager */
        for (Process p : processes) {
            if (p.getPid().equalsIgnoreCase(pid)) {
                showError();
                return;
            }
        }

        /* Create new PCB and initialise with given parameters */
        Process p = new Process(pid, priority);

        /* Add pointer to new process into ArrayList for Reference */
        processes.add(p);

        /* Add pointer to new process into ReadyList */
        addToRL(p);

        /* Update creation tree based on current running process and new process */
        modifyTree(runningProcess, p);

        /* Call scheduler at the end of every kernel call */
        scheduler();
    }

    /**
     * Method called by the driver to destroy the provided
     * valid process and killing the sub-tree.
     *
     * @param pid pid of the process to be destroyed
     */
    public static void destroyProcess(String pid) {
        /* Search for process given pid */
        Process p = getProcess(pid);

        /* If no such process or init process, return and show error */
        if (p == null || p.getPid().equalsIgnoreCase("init")) {
            showError();
            return;
        }

        /* Kill the process subtree with p as the root of tree */
        killProcessTree(p);

        /* Call scheduler at the end of every kernel call */
        scheduler();
    }

    /**
     * Private recursive method that removes each process from
     * resources, running process pointer (if applicable),
     * all ReadyList & BlockList. Lastly, release all resources
     * used by the process.
     *
     * @param p root of process sub-tree
     */
    private static void killProcessTree(Process p) {
        /* Reached base case where there is no more process */
        if (p == null) {
            return;
        }

        /* For each child in current process, kill recursively */
        for (Iterator<Process> i = p.getChild().iterator(); i.hasNext(); ) {
            Process c = i.next();
            i.remove();
            killProcessTree(c);
        }

        /* Release resource(s) units of the current process */
        Map<Resource, Integer> pResources = p.getResources();
        for (Map.Entry<Resource, Integer> e : pResources.entrySet()) {
            releaseResources(e.getKey(), e.getValue(), p, false);
        }

        /* Remove all resource pointers in the process */
        pResources.clear();

        /* Remove from Blocked List or Ready List */
        removeFromList(p);

        /* Remove from List of Pointers to Resources */
        processes.remove(p);

        /* Remove pointer from parent if not already done */
        p.getParent().getChild().remove(p);

        /* If current process is running, remove from runningProcess pointer */
        if (p.getType() == Process.RUNNING) {
            runningProcess = null;
        }

        /* Await for Garbage Collection by JVM to remove object from memory */

    }

    /**
     * Private method that returns pointer of process if found
     * given the requested pid.
     *
     * @param pid the pid of the requested process
     * @return pointer of the requested process
     */
    private static Process getProcess(String pid) {
        for (Process p : processes) {
            if (p.getPid().equalsIgnoreCase(pid)) {
                return p;
            }
        }

        /* Return null if no such process with given pid in system */
        return null;
    }

    /**
     * Private method that returns pointer of resource if found
     * given the requested rid.
     *
     * @param rid the rid of the requested resource
     * @return pointer of requested resource
     */
    private static Resource getResource(String rid) {
        for (Resource r : resources) {
            if (r.getRid().equalsIgnoreCase(rid)) {
                return r;
            }
        }

        /* Return null if no such resource with given rid in system */
        return null;
    }

    /**
     * Scheduler method that is called at the end of every kernel call.
     * Based on certain criteria, the scheduler can choose to swap
     * the running process with another.
     */
    private static void scheduler() {
        for (int i = Process.NUM_PRIORITY - 1; i >= 0; i--) {
            LinkedList<Process> levelList = readyList[i];
            if (!levelList.isEmpty()) {
                /* Grab the highest priority process in the system (first in queue) */
                Process p = levelList.getFirst();

                /* If highest priority process is fulfils any of the 3 criteria */
                if (runningProcess == null || p.getPriority() > runningProcess.getPriority()
                    || runningProcess.getType() != Process.RUNNING) {

                    /* Proceed to swap p with runningProcess */
                    /* If there is a running process and it is running, set to ready */
                    if (runningProcess != null && runningProcess.getType() == Process.RUNNING) {
                        runningProcess.setType(Process.READY);
                    }

                    /* Swap p with running process */
                    runningProcess = p;
                    p.setType(Process.RUNNING);
                }

                /* Since we found the highest priority process, break loop */
                break;
            }

            /* Else continue to search lower priority lists */
        }

        /* At end of scheduler call, print running process */
        System.out.print(runningProcess.getPid() + ' ');
    }

    /**
     * Public helper method that driver executes command with
     * reference to current running process to release resources.
     *
     * @param rid  the rid of the requested resource
     * @param unit the number of requested units
     */
    public static void releaseResources(String rid, int unit) {
        Resource r = getResource(rid);

        /* If pointer is null, there is no such resource with the given rid */
        if (r == null) {
            showError();
            return;
        }

        boolean isSuccess = releaseResources(r, unit, runningProcess, true);

        /* Call scheduler at the end of every kernel call */
        if (isSuccess) {
            scheduler();
        }
    }

    /**
     * Private method that releases the requested resource units
     * held by the given process and update the hash table of
     * the process and process the block list of Resource to allocate
     * the next one if possible.
     *
     * @param resource the pointer of requested resource
     * @param unit     the number of requested units to release
     * @param p        the process making the release
     * @return whether release operation is successful
     */
    private static boolean releaseResources(Resource resource, int unit, Process p,
        boolean isRemove) {
        /* If request process is init, deny and return */
        if (p.getPid().equalsIgnoreCase("init")) {
            showError();
            return false;
        }

        /* If resource to be released is not held by process p, show error */
        if (!p.getResources().containsKey(resource)) {
            showError();
            return false;
        }

        /* Get the number of units held by resource */
        int usedUnits = p.getResources().get(resource);

        /* If its not all the units held by process, just minus from total */
        if (unit < usedUnits && unit > 0) {
            p.getResources().put(resource, usedUnits - unit);
        } else if (unit == usedUnits) { /* if all the units, remove from table */
            if (isRemove) {
                p.getResources().remove(resource);
            }
        } else { /* if not a valid unit to release, show error and return */
            /* If unit == 0, just release no resource & return true */
            if (unit == 0) {
                return true;
            } else {
                showError();
                return false;
            }
        }

        /* Update the number of free units the resource have */
        resource.setFreeUnits(resource.getFreeUnits() + unit);

        /* Proceed to process block list of resource for possible allocation */
        while (!resource.getBlockList().isEmpty()
            && resource.getBlockList().getFirst().getBlockedReqUnit() <= resource.getFreeUnits()) {
            /* Get first process from block list */
            Process successP = resource.getBlockList().getFirst();

            /* Release requested units from resource */
            resource.setFreeUnits(resource.getFreeUnits() - successP.getBlockedReqUnit());

            /* Add resource pointer to the resource list in Process p */
            successP.getResources().put(resource, successP.getBlockedReqUnit());

            /* Remove from Blocked List */
            removeFromBL(successP);

            /* Add to Ready List */
            addToRL(successP);
        }

        /* Release operation successfully completed */
        return true;
    }

    /**
     * Public helper method that driver executes command with
     * reference to the current running process to request resource.
     *
     * @param rid  the rid of the requested resource
     * @param unit the number of requested units to request
     */
    public static void requestResources(String rid, int unit) {
        Resource r = getResource(rid);

        /* If pointer is null, there is no such resource with the given rid */
        if (r == null) {
            showError();
            return;
        }

        requestResources(r, unit, runningProcess);
    }

    /**
     * Private method that request the specified units by
     * provided process pointer to the provided resource pointer.
     *
     * @param resource the pointer of requested resource
     * @param unit     the number of requested units to request
     * @param p        the process making the request
     */
    private static void requestResources(Resource resource, int unit, Process p) {
        /* If request process is init, deny and return */
        if (p.getPid().equalsIgnoreCase("init")) {
            showError();
            return;
        }

        /* If the requested unit is not valid, show error and return */
        if (unit > resource.getMaxUnits() || unit <= 0) {
            /* If unit == 0, just allocate no resource & run scheduler */
            if (unit == 0) {
                scheduler();
            } else { /* Else just show error */
                showError();
            }

            return;
        }

        /* If request process already contain the resource, the total requested must not be max units */
        if (p.getResources().containsKey(resource)) {
            if (p.getResources().get(resource) + unit > resource.getMaxUnits()) {
                showError();
                return;
            }
        }

        /* If the resource have enough free units for allocation */
        if (resource.getFreeUnits() >= unit) {
            /* Release requested units from resource */
            resource.setFreeUnits(resource.getFreeUnits() - unit);

            /* Add resource pointer to the resource list in Process p */
            if (p.getResources().containsKey(resource)) {
                p.getResources().put(resource, p.getResources().get(resource) + unit);
            } else {
                p.getResources().put(resource, unit);
            }

        } else { /* If there is not enough units for allocation, add to BL */
            /* Set process status to BLOCKED */
            p.setType(Process.BLOCKED);

            /* Remove from Ready List & Add to back of Resource's Block List */
            removeFromRL(p);
            addToBL(resource, p, unit);
        }

        /* Call scheduler at the end of every kernel call */
        scheduler();
    }

    /**
     * Method called by driver to simulate RR time-sharing
     * by changing the process state to ready and add to the back
     * of the ready list.
     */
    public static void timeOut() {
        /* Remove process from its current position in the Ready List */
        removeFromRL(runningProcess);

        /* Set the process to READY */
        runningProcess.setType(Process.READY);

        /* Add to back of Ready List */
        addToRL(runningProcess);

        /* Call scheduler at the end of every kernel call */
        scheduler();
    }

    public static void printAllProcess() {
        for (Process p : processes) {
            printProcess(p.getPid());
        }
    }

    public static void printAllResource() {
        for (Resource r : resources) {
            printResource(r.getRid());
        }
    }

    public static void printProcess(String argument) {
        Process p = getProcess(argument);
        System.out.println(p.getPid() + " " + p.getPriority() + " " + p.getType());
    }

    public static void printResource(String argument) {
        Resource r = getResource(argument);
        System.out.println(
            r.getRid() + " " + r.getFreeUnits() + "/" + r.getMaxUnits() + " " + r
                .getBlockList().size());
    }

    /* Helper Methods - Self Explanatory */

    private static void showError() {
        System.out.print("error ");
    }

    private static void removeFromList(Process p) {
        if (p.getType() == Process.BLOCKED) {
            removeFromBL(p);
        } else {
            removeFromRL(p);
        }
    }

    private static void addToRL(Process p) {
        readyList[p.getPriority()].add(p);
        p.setList(readyList[p.getPriority()]);
    }

    private static void removeFromRL(Process p) {
        readyList[p.getPriority()].remove(p);
        p.setList(null);
    }

    private static void addToBL(Resource r, Process p, int unit) {
        r.getBlockList().add(p);
        p.setBlockedReqUnit(unit);
        p.setList(r.getBlockList());
    }

    private static void removeFromBL(Process p) {
        p.getList().remove(p);
        p.setBlockedReqUnit(0);
        p.setList(null);
    }

    private static void modifyTree(Process parent, Process child) {
        if (parent != null) {
            parent.addChild(child);
            child.setParent(parent);
        }
    }

}
