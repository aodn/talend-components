package au.org.emii.talend.dap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import au.org.emii.talend.dap.reader.NetcdfReader;
import au.org.emii.talend.dap.utils.FileUtils;

public class NetcdfReaderTests {
    
    private static final String QC_TEST_DATA = "0123456789";
    private static final String[] PARAMETER_DATA = { 
        "TEMP                                                            ",
        "PSAL                                                            ",
        "PRES                                                            "
    };
    private File tempFile;
    private NetcdfFileWriteable ncfile;

    @Before
    public void buildTestFile() throws IOException, InvalidRangeException {
        tempFile = File.createTempFile("test", ".nc");
        ncfile = NetcdfFileWriteable.createNew(tempFile.getAbsolutePath(), false);
        
        Dimension timeDim = ncfile.addDimension("time", QC_TEST_DATA.length());
        Dimension profileDim = ncfile.addDimension("profile", 1);

        ArrayList<Dimension> presQCdims = new ArrayList<Dimension>();
        presQCdims.add(profileDim);
        presQCdims.add(timeDim);

        ncfile.addVariable("pres_qc", DataType.CHAR, presQCdims);

        Dimension paramDim = ncfile.addDimension("param", 3);
        Dimension string64Dim = ncfile.addDimension("string64", 64);
        
        ArrayList<Dimension> parameterDims = new ArrayList<Dimension>();
        parameterDims.add(paramDim);
        parameterDims.add(string64Dim);
        
        ncfile.addVariable("parameter", DataType.CHAR, parameterDims);
        
        ncfile.create();
        
        ArrayChar presQCArray = new ArrayChar.D2(profileDim.getLength(), timeDim.getLength());
        presQCArray.setString(0, QC_TEST_DATA);
        
        ncfile.write("pres_qc", presQCArray);
        
        ArrayChar parameterArray = new ArrayChar.D2(paramDim.getLength(), string64Dim.getLength());
        for (int i=0; i<PARAMETER_DATA.length; i++) {
            parameterArray.setString(i, PARAMETER_DATA[i]);
        }

        ncfile.write("parameter", parameterArray);
        
        ncfile.flush();
    }
    
    @After
    public void deleteTestFile() {
        FileUtils.closeQuietly(ncfile);
        
        if (tempFile != null) {
            tempFile.delete();
        }
    }

    @Test
    public void readCharVariableAsString() throws IOException {
        Variable variable = ncfile.findVariable("pres_qc");
        NetcdfReader reader = new NetcdfReader(variable);
        Set<IndexValue> indexValues = new HashSet<IndexValue>();
        indexValues.add(new IndexValue("profile", 0));
        String result = reader.getString(indexValues);
        assertThat(result, is(QC_TEST_DATA));
    }

    @Test
    public void readCharVariableAsChar() throws IOException {
        Variable variable = ncfile.findVariable("pres_qc");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i<QC_TEST_DATA.length(); i++) {
            Set<IndexValue> indexValues = new HashSet<IndexValue>();
            indexValues.add(new IndexValue("profile",0));
            indexValues.add(new IndexValue("time",i));
            char result = reader.getChar(indexValues);
            assertThat(result, is(QC_TEST_DATA.charAt(i)));
        }
    }
    
    @Test
    public void readStringVariableAsString() throws IOException {
        Variable variable = ncfile.findVariable("parameter");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i<PARAMETER_DATA.length; i++) {
            Set<IndexValue> indexValues = new HashSet<IndexValue>();
            indexValues.add(new IndexValue("param", i));
            String result = reader.getString(indexValues);
            assertThat(result, is(PARAMETER_DATA[i]));
        }
    }

    @Test
    public void readStringVariableAsChar() throws IOException {
        Variable variable = ncfile.findVariable("parameter");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i<PARAMETER_DATA.length; i++) {
            for (int j=0; j<PARAMETER_DATA[i].length(); j++) {
                Set<IndexValue> indexValues = new HashSet<IndexValue>();
                indexValues.add(new IndexValue("param", i));
                indexValues.add(new IndexValue("string64", j));
                char result = reader.getChar(indexValues);
                assertThat(result, is(PARAMETER_DATA[i].charAt(j)));
            }
        }
    }
}
