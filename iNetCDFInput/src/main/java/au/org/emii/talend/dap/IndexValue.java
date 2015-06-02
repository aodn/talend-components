package au.org.emii.talend.dap;

// Class used to encapsulate the value of an index

public class IndexValue {
    private String name;
    private int value;
    
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public IndexValue(String name, int value) {
        this.name = name;
        this.value = value;
    }
};
