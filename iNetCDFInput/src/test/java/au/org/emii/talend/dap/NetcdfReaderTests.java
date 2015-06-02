package au.org.emii.talend.dap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import au.org.emii.talend.dap.reader.NetcdfReader;
import au.org.emii.talend.dap.utils.FileUtils;

public class NetcdfReaderTests {
    
    private static final String[] ARGO_QC_TEST_DATA = {
        "0123456789"
    };

    private static final String[] ARGO_PARAMETER_TEST_DATA = { 
        "TEMP",
        "PSAL",
        "PRES"
    };

    private static final String[] ABOS_QC_TEST_DATA = {
        "0",
        "1",
        "2",
        "3"
    };
    
    private static final String ABOS_STATION_NAME_TEST_DATA = "Pulse-Pulse-9-2012";

    @Test
    public void argoQCTest() throws IOException {
        test2DGetCharAndGetString(ARGO_QC_TEST_DATA);
    }

    @Test
    public void argoParameterTest() throws IOException {
        test2DGetCharAndGetString(ARGO_PARAMETER_TEST_DATA);
    }

    @Test
    public void abosQCTest() throws IOException {
        test2DGetCharAndGetString(ABOS_QC_TEST_DATA);
    }
    
    @Test
    public void abosStationNameTest() throws IOException {
        test1DGetCharAndGetString(ABOS_STATION_NAME_TEST_DATA);
    }

    private void test2DGetCharAndGetString(String[] data) throws IOException {
        create2DVariableAndAssertThatGetStringMatchesData(data);
        create2DVariableAndAssertThatGetCharMatchesData(data);
    }

    private void test1DGetCharAndGetString(String data) throws IOException {
        create1DVariableAndAssertThatGetStringMatchesData(data);
        create1DVariableAndAssertThatGetCharMatchesData(data);
    }

    private void create2DVariableAndAssertThatGetStringMatchesData(
            String[] data) throws IOException {
        NetcdfFileWriteable ncFile = createFileWith2DCharVariable(data);
        try {
            assertThatGetStringMatchesData2D(ncFile, data);
        } finally {
            closeAndDelete(ncFile);
        }
    }

    private void create2DVariableAndAssertThatGetCharMatchesData(
            String[] data) throws IOException {
        NetcdfFileWriteable ncFile = createFileWith2DCharVariable(data);
        try {
            assertThatGetCharMatchesData2D(ncFile, data);
        } finally {
            closeAndDelete(ncFile);
        }
    }

    private void create1DVariableAndAssertThatGetStringMatchesData(
            String data) throws IOException {
        NetcdfFileWriteable ncFile = createFileWith1DCharVariable(data);
        try {
            assertThatGetStringMatchesData1D(ncFile, data);
        } finally {
            closeAndDelete(ncFile);
        }
    }

    private void create1DVariableAndAssertThatGetCharMatchesData(
            String data) throws IOException {
        NetcdfFileWriteable ncFile = createFileWith1DCharVariable(data);
        try {
            assertThatGetCharMatchesData1D(ncFile, data);
        } finally {
            closeAndDelete(ncFile);
        }
    }

    private void assertThatGetCharMatchesData2D(NetcdfFileWriteable ncfile, String[] data) 
            throws IOException {
        Variable variable = ncfile.findVariable("variable");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i < data.length; i++) {
            for (int j=0; j < data[i].length(); j++) {
                Set<IndexValue> indexValues = new HashSet<IndexValue>();
                indexValues.add(new IndexValue("dim1", i));
                indexValues.add(new IndexValue("dim2", j));
                char result = reader.getChar(indexValues);
                assertThat(result, is(data[i].charAt(j)));
            }
        }
    }

    private void assertThatGetStringMatchesData2D(NetcdfFileWriteable ncfile, String[] data)
            throws IOException {
        Variable variable = ncfile.findVariable("variable");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i < data.length; i++) {
            Set<IndexValue> indexValues = new HashSet<IndexValue>();
            indexValues.add(new IndexValue("dim1", i));
            String result = reader.getString(indexValues);
            assertThat(result, is(data[i]));
        }
    }

    private void assertThatGetCharMatchesData1D(NetcdfFileWriteable ncfile, String data) 
            throws IOException {
        Variable variable = ncfile.findVariable("variable");
        NetcdfReader reader = new NetcdfReader(variable);

        for (int i=0; i < data.length(); i++) {
            Set<IndexValue> indexValues = new HashSet<IndexValue>();
            indexValues.add(new IndexValue("dim1", i));
            char result = reader.getChar(indexValues);
            assertThat(result, is(data.charAt(i)));
        }
    }

    private void assertThatGetStringMatchesData1D(NetcdfFileWriteable ncfile, String data)
            throws IOException {
        Variable variable = ncfile.findVariable("variable");
        NetcdfReader reader = new NetcdfReader(variable);
        Set<IndexValue> indexValues = new HashSet<IndexValue>();
        String result = reader.getString(indexValues);
        assertThat(result, is(data));
    }

    private void closeAndDelete(NetcdfFileWriteable ncFile) {
        File tempFile = new File(ncFile.getLocation());
        FileUtils.closeQuietly(ncFile);
        tempFile.delete();
    }


    private NetcdfFileWriteable createFileWith2DCharVariable(String[] data)
            throws IOException {
        File tempFile = File.createTempFile("test", ".nc");
        
        try {
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(tempFile.getAbsolutePath(), false);

            Dimension dim1 = ncfile.addDimension("dim1", data.length);
            Dimension dim2 = ncfile.addDimension("dim2", calcMaxLength(data));
            
            ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
            dimensions.add(dim1);
            dimensions.add(dim2);
            
            ncfile.addVariable("variable", DataType.CHAR, dimensions);
            
            ncfile.create();
            
            ArrayChar dataArray = new ArrayChar.D2(dim1.getLength(), dim2.getLength());
            for (int i=0; i < data.length; i++) {
                dataArray.setString(i, data[i]);
            }

            ncfile.write("variable", dataArray);
            
            ncfile.flush();
            
            return ncfile;
        } catch (Exception e) {
            tempFile.delete();
            throw new RuntimeException(e);
        }
    }

    private NetcdfFileWriteable createFileWith1DCharVariable(String data)
            throws IOException {
        File tempFile = File.createTempFile("test", ".nc");
        
        try {
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(tempFile.getAbsolutePath(), false);

            Dimension dim1 = ncfile.addDimension("dim1", data.length());
            
            ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
            dimensions.add(dim1);
            
            ncfile.addVariable("variable", DataType.CHAR, dimensions);
            
            ncfile.create();
            
            ArrayChar dataArray = new ArrayChar.D1(dim1.getLength());
            dataArray.setString(data);

            ncfile.write("variable", dataArray);
            
            ncfile.flush();
            
            return ncfile;
        } catch (Exception e) {
            tempFile.delete();
            throw new RuntimeException(e);
        }
    }
    
    private int calcMaxLength(String[] parameterData) {
        int result = 0;
        for (String element : parameterData) {
            if (element.length() > result) {
                result = element.length();
            }
        }
        return result;
    }

}
