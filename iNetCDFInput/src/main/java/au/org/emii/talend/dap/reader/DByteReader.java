package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.DByte;
import au.org.emii.talend.dap.IndexValue;

public class DByteReader extends DReader {
	private DByte variable;
	
	public DByteReader(DByte variable) {
		this.variable = variable;
	}
	
	public byte getByte(Set<IndexValue> indexValues) {
		return variable.getValue();
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
		return Byte.toString(variable.getValue());
	}
};

