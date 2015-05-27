package au.org.emii.talend.dap;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import opendap.dap.BaseType;
import opendap.dap.DArray;
import opendap.dap.DArrayDimension;
import opendap.dap.DGrid;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;

// Class used to build a set of index ranges from OPenDAP array/grid dimensions or NetCDF variable dimensions 

public class IndexRangesBuilder {
	
	Set<IndexRange> indexRanges = new LinkedHashSet<IndexRange>();
	
	// Methods for adding OPeNDAP array/grid dimensions 
	// to the list of indices to be operated on
	
	public void addDimensions(BaseType variable) {
		if (variable instanceof DArray) {
			addDimensions((DArray) variable);
		} else if (variable instanceof DGrid) {
			addDimensions((DGrid) variable);
		}
	}
	
	public void addDimensions(DArray dArray) {
		@SuppressWarnings("unchecked")
		Enumeration<DArrayDimension> dArrayDimensions = (Enumeration<DArrayDimension>)dArray.getDimensions();
		
		while (dArrayDimensions.hasMoreElements()) {
			DArrayDimension dArrayDimension = dArrayDimensions.nextElement();
			
			String name = dArrayDimension.getEncodedName();
			
			IndexRange index = getIndexRange(name);
			
			if (index == null) {
				indexRanges.add(new IndexRange(dArrayDimension));
			} else if (dArrayDimension.getSize() != index.getSize()) {
				throw new IllegalArgumentException("Size of dimension '" + name + "' for variable '" + dArray.getEncodedName() + "' incompatible with existing index");
			}
		}
	}
	
	public void addDimensions(DGrid variable) {
		addDimensions(variable.getArray());
	}
	
	public void addDimensions(Variable variable) {
		Iterator<Dimension> iterator = variable.getDimensions().listIterator();
		
		while (iterator.hasNext()) {
			Dimension dimension = iterator.next();
			
			// Ignore last dimension on String/Char this is the string index
			if (variable.getDataType().isString() && !iterator.hasNext()) break;
			
			addDimension(dimension);
		}
	}
	
	private void addDimension(Dimension dimension) {
		IndexRange index = getIndexRange(dimension.getName());
		
		if (index == null) {
			indexRanges.add(new IndexRange(dimension));
		} else if (dimension.getLength() != index.getSize()) {
			throw new IllegalArgumentException("Size of dimension '" + dimension.getLength() + "' for variable '" + dimension.getName() + "' incompatible with existing index");
		}
	}
	
	public Set<IndexRange> getIndexRanges() {
		return indexRanges;
	}
	
	public Iterator<Set<IndexValue>> getIterator() {
		return new IndexValuesIterator(getIndexRanges());
	}
	
	private IndexRange getIndexRange(String name) {
		IndexRange index = null;
		
		for (IndexRange indexRange : indexRanges) {
			if (indexRange.getName().equals(name)) {
				index = indexRange;
				break;
			}
		}
		return index;
	}
	
};

