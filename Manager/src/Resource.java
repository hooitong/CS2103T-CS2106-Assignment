import java.util.LinkedList;

/**
 * Class that represent a single resource in the system.
 *
 * @author Yeap Hooi Tong
 */

public class Resource {

    private String rid;
    private int maxUnits;
    private int freeUnits;
    private LinkedList<Process> blockList;

    /**
     * Sole constructor of this class that requires
     * two input.
     *
     * @param rid  identification of resource
     * @param unit the number units the resource have
     */
    public Resource(String rid, int unit) {
        this.rid = rid;
        maxUnits = unit;
        freeUnits = maxUnits;
        blockList = new LinkedList<Process>();
    }

    /* Getters & Setters Methods */

    public String getRid() {
        return rid;
    }

    public int getMaxUnits() {
        return maxUnits;
    }

    public int getFreeUnits() {
        return freeUnits;
    }

    public void setFreeUnits(int freeUnits) {
        this.freeUnits = freeUnits;
    }

    public LinkedList<Process> getBlockList() {
        return blockList;
    }
}
