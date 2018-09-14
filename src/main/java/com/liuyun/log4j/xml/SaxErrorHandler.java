package com.liuyun.log4j.xml;

import com.liuyun.log4j.helpers.LogLog;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class SaxErrorHandler implements ErrorHandler {

    private static void emitMessage(final String msg, final SAXParseException ex) {
        LogLog.warn(msg + ex.getLineNumber() + " and column " + ex.getColumnNumber());
        LogLog.warn(ex.getMessage(), ex.getException());
    }

    @Override
    public void error(final SAXParseException ex) {
        emitMessage("Continuable parsing error ", ex);
    }

    @Override
    public void fatalError(final SAXParseException ex) {
        emitMessage("Fatal parsing error ", ex);
    }

    @Override
    public void warning(final SAXParseException ex) {
        emitMessage("Parsing warning ", ex);
    }

}
