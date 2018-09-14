package com.liuyun.log4j.appender.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import com.liuyun.log4j.AppenderSkeleton;
import com.liuyun.log4j.PatternLayout;
import com.liuyun.log4j.spi.ErrorCode;
import com.liuyun.log4j.spi.LoggingEvent;

//日志输出器(输出到数据库)
public class JDBCAppender extends AppenderSkeleton {

    protected String databaseURL = "jdbc:odbc:myDB";    //数据库url
    protected String databaseUser = "me";               //用户名
    protected String databasePassword = "mypassword";   //密码
    protected Connection connection = null;

    /**
     * Stores the string given to the pattern layout for conversion into a SQL
     * statement, eg: insert into LogTable (Thread, Class, Message) values
     * ("%t", "%c", "%m").
     * <p>
     * Be careful of quotes in your messages!
     * <p>
     * Also see PatternLayout.
     */
    protected String sqlStatement = "";

    /**
     * size of LoggingEvent buffer before writting to the database.
     * Default is 1.
     */
    protected int bufferSize = 1;

    /**
     * ArrayList holding the buffer of Logging Events.
     */
    protected ArrayList buffer;

    /**
     * Helper object for clearing out the buffer
     */
    protected ArrayList removes;

    private boolean locationInfo = false;

    public JDBCAppender() {
        super();
        buffer = new ArrayList(bufferSize);
        removes = new ArrayList(bufferSize);
    }

    /**
     * Gets whether the location of the logging request call
     * should be captured.
     *
     * @return the current value of the <b>LocationInfo</b> option.
     * @since 1.2.16
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * The <b>LocationInfo</b> option takes a boolean value. By default, it is
     * set to false which means there will be no effort to extract the location
     * information related to the event. As a result, the event that will be
     * ultimately logged will likely to contain the wrong location information
     * (if present in the log format).
     * <p/>
     * <p/>
     * Location information extraction is comparatively very slow and should be
     * avoided unless performance is not a concern.
     * </p>
     *
     * @param flag true if location information should be extracted.
     * @since 1.2.16
     */
    public void setLocationInfo(final boolean flag) {
        locationInfo = flag;
    }


    /**
     * Adds the event to the buffer.  When full the buffer is flushed.
     */
    public void append(LoggingEvent event) {
        event.getNDC();
        event.getThreadName();
        // Get a copy of this thread's MDC.
        event.getMDCCopy();
        if (locationInfo) {
            event.getLocationInformation();
        }
        event.getRenderedMessage();
        event.getThrowableStrRep();
        buffer.add(event);

        if (buffer.size() >= bufferSize) {
            flushBuffer();
        }
    }

    /**
     * By default getLogStatement sends the event to the required Layout object.
     * The layout will format the given pattern into a workable SQL string.
     * <p>
     * Overriding this provides direct access to the LoggingEvent
     * when constructing the logging statement.
     */
    protected String getLogStatement(LoggingEvent event) {
        return getLayout().format(event);
    }

    /**
     * Override this to provide an alertnate method of getting
     * connections (such as caching).  One method to fix this is to open
     * connections at the start of flushBuffer() and close them at the
     * end.  I use a connection pool outside of JDBCAppender which is
     * accessed in an override of this method.
     */
    protected void execute(String sql) throws SQLException {

        Connection con = null;
        Statement stmt = null;

        try {
            con = getConnection();

            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            closeConnection(con);
        }

        //System.out.println("Execute: " + sql);
    }


    /**
     * Override this to return the connection to a pool, or to clean up the
     * resource.
     * <p>
     * The default behavior holds a single connection open until the appender
     * is closed (typically when garbage collected).
     */
    protected void closeConnection(Connection con) {
    }

    /**
     * Override this to link with your connection pooling system.
     * <p>
     * By default this creates a single connection which is held open
     * until the object is garbage collected.
     */
    protected Connection getConnection() throws SQLException {
        if (!DriverManager.getDrivers().hasMoreElements()) {
            setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
        }

        if (connection == null) {
            connection = DriverManager.getConnection(databaseURL, databaseUser,
                    databasePassword);
        }

        return connection;
    }

    /**
     * Closes the appender, flushing the buffer first then closing the default
     * connection if it is open.
     */
    public void close() {
        flushBuffer();

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            errorHandler.error("Error closing connection", e, ErrorCode.GENERIC_FAILURE);
        }
        this.closed = true;
    }

    /**
     * loops through the buffer of LoggingEvents, gets a
     * sql string from getLogStatement() and sends it to execute().
     * Errors are sent to the errorHandler.
     * <p>
     * If a statement fails the LoggingEvent stays in the buffer!
     */
    public void flushBuffer() {
        //Do the actual logging
        removes.ensureCapacity(buffer.size());
        for (Iterator i = buffer.iterator(); i.hasNext(); ) {
            LoggingEvent logEvent = (LoggingEvent) i.next();
            try {
                String sql = getLogStatement(logEvent);
                execute(sql);
            } catch (SQLException e) {
                errorHandler.error("Failed to excute sql", e,
                        ErrorCode.FLUSH_FAILURE);
            } finally {
                removes.add(logEvent);
            }
        }

        // remove from the buffer any events that were reported
        buffer.removeAll(removes);

        // clear the buffer of reported events
        removes.clear();
    }


    /**
     * closes the appender before disposal
     */
    public void finalize() {
        close();
    }


    /**
     * JDBCAppender requires a layout.
     */
    public boolean requiresLayout() {
        return true;
    }


    /**
     *
     */
    public void setSql(String sql) {
        sqlStatement = sql;
        if (getLayout() == null) {
            this.setLayout(new PatternLayout(sql));
        } else {
            ((PatternLayout) getLayout()).setConversionPattern(sql);
        }
    }


    /**
     * Returns pre-formated statement eg: insert into LogTable (msg) values ("%m")
     */
    public String getSql() {
        return sqlStatement;
    }


    public void setUser(String user) {
        databaseUser = user;
    }


    public void setURL(String url) {
        databaseURL = url;
    }


    public void setPassword(String password) {
        databasePassword = password;
    }


    public void setBufferSize(int newBufferSize) {
        bufferSize = newBufferSize;
        buffer.ensureCapacity(bufferSize);
        removes.ensureCapacity(bufferSize);
    }


    public String getUser() {
        return databaseUser;
    }


    public String getURL() {
        return databaseURL;
    }


    public String getPassword() {
        return databasePassword;
    }


    public int getBufferSize() {
        return bufferSize;
    }


    /**
     * Ensures that the given driver class has been loaded for sql connection
     * creation.
     */
    public void setDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (Exception e) {
            errorHandler.error("Failed to load driver", e,
                    ErrorCode.GENERIC_FAILURE);
        }
    }
}

