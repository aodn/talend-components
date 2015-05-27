package au.org.emii.talend.dap;

import java.util.Iterator;
import java.util.Set;

// Class used to iterate through the possible index values for a set of index ranges
//
// Cheat - skip iterating through each index range - just iterate through the 
// corresponding single dimension vector and convert to index values for those ranges

public class IndexValuesIterator implements Iterator<Set<IndexValue>> {
	private long next = 0L;
	private long size;
	
	private IndexValuesConverter indexConverter;
	
	public IndexValuesIterator(Set<IndexRange> indexRanges) {
		// Instantiate converter for supplied index range
		indexConverter = new IndexValuesConverter(indexRanges);
		
		// Calculate size of single dimension vector 
		size = 1L;
		
		for (IndexRange indexRange : indexRanges ) {
			size *= indexRange.getSize();
		}
	}

	public boolean hasNext() {
		return next < size;
	}

	public Set<IndexValue> next() {
		return indexConverter.getArrayIndexValues(next++);
	}

	public void remove() {
		// Not Implemented
	}
}

