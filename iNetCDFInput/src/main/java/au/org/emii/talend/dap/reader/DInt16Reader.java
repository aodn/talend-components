package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.DInt16;
import au.org.emii.talend.dap.IndexValue;

public class DInt16Reader extends DReader {
	private DInt16 variable;
	
	public DInt16Reader(DInt16 variable) {
		this.variable = variable;
	}
	
	public short getInt16(Set<IndexValue> indexValues) {
		return variable.getValue();
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
		return Short.toString(variable.getValue());
	}
};

