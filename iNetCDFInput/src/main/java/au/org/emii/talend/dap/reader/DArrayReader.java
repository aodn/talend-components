package au.org.emii.talend.dap.reader;

import java.util.Set;

import opendap.dap.BaseType;
import opendap.dap.BaseTypePrimitiveVector;
import opendap.dap.BytePrimitiveVector;
import opendap.dap.DArray;
import opendap.dap.Float32PrimitiveVector;
import opendap.dap.Float64PrimitiveVector;
import opendap.dap.Int16PrimitiveVector;
import opendap.dap.Int32PrimitiveVector;
import opendap.dap.PrimitiveVector;
import au.org.emii.talend.dap.IndexRange;
import au.org.emii.talend.dap.IndexRangesBuilder;
import au.org.emii.talend.dap.IndexValue;
import au.org.emii.talend.dap.IndexValuesConverter;

public class DArrayReader extends DReader {
	private PrimitiveVector primitiveVector;
	
	private IndexValuesConverter indexValuesConverter;
	
	public DArrayReader(DArray variable) {
		primitiveVector = variable.getPrimitiveVector();
		
		IndexRangesBuilder indexRangesBuilder = new IndexRangesBuilder();
		indexRangesBuilder.addDimensions(variable);
		Set<IndexRange> indexRanges = indexRangesBuilder.getIndexRanges();
		
		indexValuesConverter = new IndexValuesConverter(indexRanges);
	}

	public byte getByte(Set<IndexValue> indexValues) {
		return ((BytePrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
	}
	
	public float getFloat32(Set<IndexValue> indexValues) {
		if (primitiveVector instanceof BytePrimitiveVector) {
			return getByte(indexValues);
		} else if (primitiveVector instanceof Int16PrimitiveVector) {
			return getInt16(indexValues);
		} else if (primitiveVector instanceof Int32PrimitiveVector) {
			return getInt32(indexValues);
		} else if (primitiveVector instanceof Float32PrimitiveVector) {
			return ((Float32PrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public double getFloat64(Set<IndexValue> indexValues) {
		if (primitiveVector instanceof BytePrimitiveVector) {
			return getByte(indexValues);
		} else if (primitiveVector instanceof Int16PrimitiveVector) {
			return getInt16(indexValues);
		} else if (primitiveVector instanceof Int32PrimitiveVector) {
			return getInt32(indexValues);
		} else if (primitiveVector instanceof Float32PrimitiveVector) {
			return getFloat32(indexValues);
		} else if (primitiveVector instanceof Float64PrimitiveVector) {
			return ((Float64PrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public short getInt16(Set<IndexValue> indexValues) {
		if (primitiveVector instanceof BytePrimitiveVector) {
			return getByte(indexValues);
		} else if (primitiveVector instanceof Int16PrimitiveVector) {
			return ((Int16PrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public int getInt32(Set<IndexValue> indexValues) {
		if (primitiveVector instanceof BytePrimitiveVector) {
			return getByte(indexValues);
		} else if (primitiveVector instanceof Int16PrimitiveVector) {
			return getInt16(indexValues);
		} else if (primitiveVector instanceof Int32PrimitiveVector) {
			return ((Int32PrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public String getString(Set<IndexValue> indexValues) {
		if (primitiveVector instanceof Float64PrimitiveVector) {
			return Double.toString(getFloat64(indexValues));
		} else if (primitiveVector instanceof Float32PrimitiveVector) {
			return Float.toString(getFloat32(indexValues));
		} else if (primitiveVector instanceof BytePrimitiveVector) {
			return Byte.toString(getByte(indexValues));
		} else if (primitiveVector instanceof Int16PrimitiveVector) {
			return Short.toString(getInt16(indexValues));
		} else if (primitiveVector instanceof Int32PrimitiveVector) {
			return Integer.toString(getInt32(indexValues));
		} else if (primitiveVector instanceof BaseTypePrimitiveVector) {
			BaseType baseType = ((BaseTypePrimitiveVector) primitiveVector).getValue(indexValuesConverter.getVectorIndexValue(indexValues));
			DReaderFactory factory = new DReaderFactory();
			DReader reader = factory.getInstance(baseType);
			return reader.getString(indexValues);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
};

