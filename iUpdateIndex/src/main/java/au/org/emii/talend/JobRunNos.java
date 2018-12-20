package au.org.emii.talend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* A list of the last run numbers used for indexing jobs run by this harvester  */

public class JobRunNos {
    static private Map<String, Long> jobRunNos = new ConcurrentHashMap<String, Long>();

    static public void setRunNo(String jobName, Long runNo) {
        jobRunNos.put(jobName, runNo);
    }

    static public Long getRunNo(String jobName) {
        Long runNo = jobRunNos.get(jobName);

        if (runNo == null) {
            throw new RuntimeException("No run number recorded for '" + jobName + "' indexing job");
        }

        return runNo;
    }
}
