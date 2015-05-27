package au.org.emii.talend.dap.reader;

import opendap.dap.DGrid;

public class DGridReader extends DArrayReader {

	public DGridReader(DGrid variable) {
		super(variable.getArray());
	}
	
}

