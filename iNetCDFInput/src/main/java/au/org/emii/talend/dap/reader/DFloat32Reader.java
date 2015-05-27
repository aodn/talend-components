package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.DFloat32;
import au.org.emii.talend.dap.IndexValue;

public class DFloat32Reader extends DReader {
	private DFloat32 variable;
	
	public DFloat32Reader(DFloat32 variable) {
		this.variable = variable;
	}
	
	public float getFloat32(Set<IndexValue> indexValues) {
		return variable.getValue();
	}
	
	public double getFloat64(Set<IndexValue> indexValues) {
		return variable.getValue();
	}
	
	public String getString(Set<IndexValue> indexValues) {
		return Float.toString(variable.getValue());
	}
};

