package com.liuyun.log4j.helpers;

import java.io.Writer;
import java.io.FilterWriter;
import com.liuyun.log4j.spi.ErrorHandler;
import com.liuyun.log4j.spi.ErrorCode;

/**
 * QuietWriter does not throw exceptions when things go
 * wrong. Instead, it delegates error handling to its {@link ErrorHandler}.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.7.3
 */
public class QuietWriter extends FilterWriter {

    protected ErrorHandler errorHandler;

    public QuietWriter(Writer writer, ErrorHandler errorHandler) {
        super(writer);
        setErrorHandler(errorHandler);
    }

    public void write(String string) {
        if (string != null) {
            try {
                out.write(string);
            } catch (Exception e) {
                errorHandler.error("Failed to write [" + string + "].", e,
                        ErrorCode.WRITE_FAILURE);
            }
        }
    }

    public void flush() {
        try {
            out.flush();
        } catch (Exception e) {
            errorHandler.error("Failed to flush writer,", e,
                    ErrorCode.FLUSH_FAILURE);
        }
    }


    public void setErrorHandler(ErrorHandler eh) {
        if (eh == null) {
            // This is a programming error on the part of the enclosing appender.
            throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
        } else {
            this.errorHandler = eh;
        }
    }
}
