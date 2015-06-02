package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.DFloat64;
import au.org.emii.talend.dap.IndexValue;

public class DFloat64Reader extends DReader {
    private DFloat64 variable;
    
    public DFloat64Reader(DFloat64 variable) {
        this.variable = variable;
    }
    
    public double getFloat64(Set<IndexValue> indexValues) {
        return variable.getValue();
    }
    
    public String getString(Set<IndexValue> indexValues) {
        return Double.toString(variable.getValue());
    }
};

