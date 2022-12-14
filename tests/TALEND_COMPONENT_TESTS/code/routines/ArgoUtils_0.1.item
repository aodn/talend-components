package routines;

import java.util.Date;

import org.talend.sdi.geometry.Geometry;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 * 
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 * 
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 * 
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 * 
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 * 
 * 5. {example} gives a example for the Function. it is optional.
 */
public class ArgoUtils {

    /**
     * parseArgoDate: parse an argo date string
     * 
     * 
     * {talendTypes} Date
     * 
     * {Category} Argo routines
     * 
     * {param} string("195001010000") input: The argo date to be parsed.
     * 
     * {example} parseArgoDate("20120521144450") # 2012-05-22 00:44:50+10.
     */
	
    public static Date parseArgoDate(String argoDateStr) {
    	if (argoDateStr.trim().equals("")) {
    		return null;
    	} else {
    		return TalendDate.parseDate("yyyyMMddHHmmssZ", rpad(argoDateStr, '0', 14) + "+0000");
    	}
    }
    
    /**
     * rpad: right pad string with provided character.
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} Argo routines
     * 
     * {param} string(sourceString) input: the string to be padded
     * 
     * {param} int(length) length : right pad to this length
     * 
     * {example} 
     * 
     * ->> rpad("200610010000",'0',14) returns 20061001000000 #
     */
    public static String rpad(String source, char padChar, int length) {
    	if (source.length()>=length) return source;
    	
    	StringBuilder builder =  new StringBuilder(length);
    	
    	builder.append(source);
    	
    	for (int i = builder.length(); i<length; i++) {
    		builder.append(padChar);
    	}
    	
    	return builder.toString();
    }
    
    /**
     * Creates a point with a specified srs
     * 
     * 
     * {talendTypes} Geometry
     * 
     * {Category} Argo routines
     * 
     * {param} double(longitude) longitude: the longitude of the point
     * {param} double(latitude) latitude: the latitude of the point
     * 
     * {param} string(epsg) epsg code : the epsg code to use
     * 
     */
    public static Geometry makePoint(Double longitude, Double latitude, String epsg) {
    	if (longitude == null || latitude == null) {
    		return null;
        }
    
    	Geometry the_geom = null;
    	
     	try {
			the_geom = new Geometry("POINT("+longitude+" "+latitude+")");
			the_geom.setEPSG(epsg);
		} catch (Exception e) {
			// ignore and return null
		}
    	
    	return the_geom;
    }
    
}
