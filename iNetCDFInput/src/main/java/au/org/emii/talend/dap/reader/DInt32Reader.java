package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.DInt32;
import au.org.emii.talend.dap.IndexValue;

public class DInt32Reader extends DReader {
	private DInt32 variable;
	
	public DInt32Reader(DInt32 variable) {
		this.variable = variable;
	}
	
	public int getInt32(Set<IndexValue> indexValues) {
		return variable.getValue();
	}
	
	public float getFloat32(Set<IndexValue> indexValues) {
		return variable.getValue();
	}
	
	public double getFloat64(Set<IndexValue> indexValues) {
		return variable.getValue();
	}
	
	public String getString(Set<IndexValue> indexValues) {
		return Integer.toString(variable.getValue());
	}
	
};

