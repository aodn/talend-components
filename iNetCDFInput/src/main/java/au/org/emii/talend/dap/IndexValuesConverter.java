package au.org.emii.talend.dap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// Class used to convert multi-dimensional array index values into a single dimension 
// vector index and vice versa as required by OPeNDAP to access elements in an OPeNDAP  
// multi-dimensional array which are physically accessed using a single dimension vector
//
// e.g. for an array with elements indexed over two dimensions [0-1][0-2]
//
// Array      Vector
// Index      Index
// Values     Values
//
// [0][0] <-> [0]
// [0][1] <-> [1]
// [0][2] <-> [2]
// [1][0] <-> [3]
// [1][1] <-> [4]
// [1][2] <-> [5]

public class IndexValuesConverter {
	
	private Map<String, Long> indexMultipliers;
	
	public IndexValuesConverter(Set<IndexRange> indexRanges) {
		// build list of multipliers to be used for each index value on instantiation
		indexMultipliers = new LinkedHashMap<String, Long>();
		
		for (IndexRange indexRange : indexRanges) {
			// each index value must be multiplied by the size of each following index
			for (String name : indexMultipliers.keySet()) {
				long existingValue = indexMultipliers.get(name).longValue();
				long newValue = existingValue * indexRange.getSize();
				indexMultipliers.put(name, new Long(newValue));
			}

			indexMultipliers.put(indexRange.getName(), new Long(1L));
		}
	}
	
	// Method to return the corresponding vector index for the given array index values
	//
	// Note: ignores index values not in supplied indexes making this suitable 
	// for use in determining variable values when iterating through a larger set of index ranges
	// than those applicable for the variable (the latitude for latitude index value 1 is the same 
	// regardless of what the index values are for depth, time, latitude etc.)
	
	public int getVectorIndexValue(Set<IndexValue> arrayIndexValues) {
		int result = 0;

		for (IndexValue indexValue : arrayIndexValues) {
			Long multiplier = indexMultipliers.get(indexValue.getName());
			// Skip index values not included in the supplied ranges
			if (multiplier != null) {
				result += indexValue.getValue() * multiplier;
			}
		}
		
		return result;
	}
	
	// Method to return the corresponding array index values for the given vector index value
	
	public Set<IndexValue> getArrayIndexValues(long vectorIndexValue) {
		LinkedHashSet<IndexValue> result = new LinkedHashSet<IndexValue>(); 
		
		long vectorIndexValueRemainder = vectorIndexValue;
		
		for (String name : indexMultipliers.keySet()) {
			long multiplier = indexMultipliers.get(name).longValue();
			result.add(new IndexValue(name, (int)(vectorIndexValueRemainder / multiplier)));
			vectorIndexValueRemainder %= multiplier;
		}
		
		return result;
	}
	
};

