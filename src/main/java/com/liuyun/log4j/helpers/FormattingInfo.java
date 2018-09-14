package com.liuyun.log4j.helpers;


public class FormattingInfo {
    int min = -1;
    int max = 0x7FFFFFFF;
    boolean leftAlign = false;

    void reset() {
        min = -1;
        max = 0x7FFFFFFF;
        leftAlign = false;
    }

    void dump() {
        LogLog.debug("min=" + min + ", max=" + max + ", leftAlign=" + leftAlign);
    }

}
 
