package au.org.emii.talend.dap;

import opendap.dap.DArrayDimension;
import ucar.nc2.Dimension;

// Class used to encapsulate the range of an index

public class IndexRange {
	private String name;
	private int size;
	
	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public IndexRange(String name, int size) {
		this.name = name;
		this.size = size;
	}
	
	public IndexRange(DArrayDimension dodsDimension) {
		name = dodsDimension.getEncodedName();
		size = dodsDimension.getSize();
	}
	public IndexRange(Dimension ncDimension) {
		name = ncDimension.getName();
		size = ncDimension.getLength();
	}
};

