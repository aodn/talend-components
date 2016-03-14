package au.org.emii.talend.dap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class IndexRangesBuilderTest {
    private IndexRangesBuilder builder;
    private Variable variable;

    @Before
    public void buildTestFile() throws IOException {
        builder = new IndexRangesBuilder();
        NetcdfFileWriter writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, "netcdf-test.nc", null);
        Dimension timeDim = writer.addDimension(null, "time", 10);
        Dimension profileDim = writer.addDimension(null, "profile", 1);
        ArrayList<Dimension> dims = new ArrayList<Dimension>();
        dims.add(profileDim);
        dims.add(timeDim);
        variable = writer.addVariable(null, "pres_qc", DataType.CHAR, dims);
    }
    
    @Test
    public void addStringDimensionsAddsNonStringIndexesForChar() {
        builder.addStringDimensions(variable);
        Set<IndexRange> indexRanges = builder.getIndexRanges();
        assertThat(indexRanges.size(), is(1));
        IndexRange indexRange = indexRanges.toArray(new IndexRange[0])[0];
        assertThat(indexRange.getName(), is("profile"));
    }

    @Test
    public void addDimensionsAddsAllDimensionsForChar() {
        builder.addDimensions(variable);
        Set<IndexRange> indexRanges = builder.getIndexRanges();
        assertThat(indexRanges.size(), is(2));
        IndexRange profileIndexRange = indexRanges.toArray(new IndexRange[0])[0];
        assertThat(profileIndexRange.getName(), is("profile"));
        IndexRange timeIndexRange = indexRanges.toArray(new IndexRange[0])[1];
        assertThat(timeIndexRange.getName(), is("time"));
    }

}
