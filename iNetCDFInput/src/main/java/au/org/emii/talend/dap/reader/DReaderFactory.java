package au.org.emii.talend.dap.reader;

import opendap.dap.BaseType;
import opendap.dap.DArray;
import opendap.dap.DByte;
import opendap.dap.DFloat32;
import opendap.dap.DFloat64;
import opendap.dap.DGrid;
import opendap.dap.DInt16;
import opendap.dap.DInt32;
import opendap.dap.DString;

public class DReaderFactory {
    public DReader getInstance(BaseType variable) {
        if (variable instanceof DByte) {
            return new DByteReader((DByte) variable);
        } else if (variable instanceof DFloat32) {
            return new DFloat32Reader((DFloat32) variable);
        } else if (variable instanceof DFloat64) {
            return new DFloat64Reader((DFloat64) variable);
        } else if (variable instanceof DInt16) {
            return new DInt16Reader((DInt16) variable);
        } else if (variable instanceof DInt32) {
            return new DInt32Reader((DInt32) variable);
        } else if (variable instanceof DString) {
            return new DStringReader((DString) variable);
        } else if (variable instanceof DArray) {
            return new DArrayReader((DArray) variable);
        } else if (variable instanceof DGrid) {
            return new DGridReader((DGrid) variable);
        }
        
        throw new UnsupportedOperationException();
    }
}

