package com.liuyun.log4j;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.config.PropertySetter;
import com.liuyun.log4j.helpers.FileWatchdog;
import com.liuyun.log4j.helpers.LogLog;
import com.liuyun.log4j.helpers.OptionConverter;
import com.liuyun.log4j.layout.Layout;
import com.liuyun.log4j.layout.PatternLayout;
import com.liuyun.log4j.or.ObjectRenderer;
import com.liuyun.log4j.or.RendererMap;
import com.liuyun.log4j.spi.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URLConnection;
import java.util.*;

//属性配置器
public class PropertyConfigurator implements Configurator {

    public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
    static final String CATEGORY_PREFIX = "log4j.category.";
    static final String LOGGER_PREFIX = "log4j.logger.";
    static final String FACTORY_PREFIX = "log4j.factory";
    static final String ADDITIVITY_PREFIX = "log4j.additivity.";
    static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
    static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
    static final String APPENDER_PREFIX = "log4j.appender.";
    static final String RENDERER_PREFIX = "log4j.renderer.";
    static final String THRESHOLD_PREFIX = "log4j.threshold";
    private static final String THROWABLE_RENDERER_PREFIX = "log4j.throwableRenderer";
    private static final String LOGGER_REF = "logger-ref";
    private static final String ROOT_REF = "root-ref";
    private static final String APPENDER_REF_TAG = "appender-ref";
    /**
     * If property set to true, then hierarchy will be reset before configuration.
     */
    private static final String RESET_KEY = "log4j.reset";
    static final private String INTERNAL_ROOT_NAME = "root";
    /**
     * Used internally to keep track of configured appenders.
     */
    protected Hashtable registry = new Hashtable(11);
    protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
    private LoggerRepository repository;

    public static void configure(String configFilename) {
        new PropertyConfigurator().doConfigure(configFilename, LogManager.getLoggerRepository());
    }

    public static void configure(java.net.URL configURL) {
        new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository());
    }

    public static void configure(InputStream inputStream) {
        new PropertyConfigurator().doConfigure(inputStream, LogManager.getLoggerRepository());
    }

    public static void configure(Properties properties) {
        new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
    }

    public static void configureAndWatch(String configFilename) {
        configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
    }

    public static void configureAndWatch(String configFilename, long delay) {
        PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
        pdog.setDelay(delay);
        pdog.start();
    }


    public void doConfigure(String configFileName, LoggerRepository hierarchy) {
        Properties props = new Properties();
        FileInputStream istream = null;
        try {
            istream = new FileInputStream(configFileName);
            props.load(istream);
            istream.close();
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not read configuration file [" + configFileName + "].", e);
            LogLog.error("Ignoring configuration file [" + configFileName + "].");
            return;
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (InterruptedIOException ignore) {
                    Thread.currentThread().interrupt();
                } catch (Throwable ignore) {
                }

            }
        }
        // If we reach here, then the config file is alright.
        doConfigure(props, hierarchy);
    }

    public void doConfigure(Properties properties, LoggerRepository hierarchy) {
        repository = hierarchy;
        //DEBUG模式
        String value = properties.getProperty(LogLog.DEBUG_KEY);
        if (value == null) {
            value = properties.getProperty("log4j.configDebug");
            if (value != null) {
                LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
            }
        }

        if (value != null) {
            LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
        }

        //
        //   if log4j.reset=true then
        //        reset hierarchy
        String reset = properties.getProperty(RESET_KEY);
        if (reset != null && OptionConverter.toBoolean(reset, false)) {
            hierarchy.resetConfiguration();
        }
        //threshold
        String thresholdStr = OptionConverter.findAndSubst(THRESHOLD_PREFIX, properties);
        if (thresholdStr != null) {
            hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
            LogLog.debug("Hierarchy threshold set to [" + hierarchy.getThreshold() + "].");
        }
        //root
        configureRootCategory(properties, hierarchy);
        //
        configureLoggerFactory(properties);
        parseCatsAndRenderers(properties, hierarchy);

        LogLog.debug("Finished configuring.");
        // We don't want to hold references to appenders preventing their
        // garbage collection.
        registry.clear();
    }

    public void doConfigure(InputStream inputStream, LoggerRepository hierarchy) {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not read configuration file from InputStream [" + inputStream
                    + "].", e);
            LogLog.error("Ignoring configuration InputStream [" + inputStream + "].");
            return;
        }
        this.doConfigure(props, hierarchy);
    }

    @Override
    public void doConfigure(java.net.URL configURL, LoggerRepository hierarchy) {
        Properties props = new Properties();
        LogLog.debug("Reading configuration from URL " + configURL);
        InputStream istream = null;
        URLConnection uConn = null;
        try {
            uConn = configURL.openConnection();
            uConn.setUseCaches(false);
            istream = uConn.getInputStream();
            props.load(istream);
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not read configuration file from URL [" + configURL
                    + "].", e);
            LogLog.error("Ignoring configuration file [" + configURL + "].");
            return;
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (InterruptedIOException ignore) {
                    Thread.currentThread().interrupt();
                } catch (IOException ignore) {
                } catch (RuntimeException ignore) {
                }
            }
        }
        doConfigure(props, hierarchy);
    }


    // --------------------------------------------------------------------------
    // Internal stuff
    // --------------------------------------------------------------------------

    /**
     * Check the provided <code>Properties</code> object for a
     * {@link LoggerFactory LoggerFactory}
     * entry specified by {@link #LOGGER_FACTORY_KEY}.  If such an entry
     * exists, an attempt is made to create an instance using the default
     * constructor.  This instance is used for subsequent Category creations
     * within this configurator.
     *
     * @see #parseCatsAndRenderers
     */
    protected void configureLoggerFactory(Properties props) {
        String factoryClassName = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY, props);
        if (factoryClassName != null) {
            LogLog.debug("Setting category factory to [" + factoryClassName + "].");
            loggerFactory = (LoggerFactory)
                    OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, loggerFactory);
            PropertySetter.setProperties(loggerFactory, props, FACTORY_PREFIX + ".");
        }
    }


    void configureRootCategory(Properties props, LoggerRepository hierarchy) {
        String effectiveFrefix = ROOT_LOGGER_PREFIX;
        String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, props);

        if (value == null) {
            value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
            effectiveFrefix = ROOT_CATEGORY_PREFIX;
        }

        if (value == null) {
            LogLog.debug("Could not find root logger information. Is this OK?");
        } else {
            Logger root = hierarchy.getRootLogger();
            synchronized (root) {
                parseCategory(props, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
            }
        }
    }


    /**
     * Parse non-root elements, such non-root categories and renderers.
     */
    protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy) {
        Enumeration enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
                String loggerName = null;
                if (key.startsWith(CATEGORY_PREFIX)) {
                    loggerName = key.substring(CATEGORY_PREFIX.length());
                } else if (key.startsWith(LOGGER_PREFIX)) {
                    loggerName = key.substring(LOGGER_PREFIX.length());
                }
                String value = OptionConverter.findAndSubst(key, props);
                Logger logger = hierarchy.getLogger(loggerName, loggerFactory);
                synchronized (logger) {
                    parseCategory(props, logger, key, loggerName, value);
                    parseAdditivityForLogger(props, logger, loggerName);
                }
            } else if (key.startsWith(RENDERER_PREFIX)) {
                String renderedClass = key.substring(RENDERER_PREFIX.length());
                String renderingClass = OptionConverter.findAndSubst(key, props);
                if (hierarchy instanceof RendererSupport) {
                    RendererMap.addRenderer((RendererSupport) hierarchy, renderedClass,
                            renderingClass);
                }
            } else if (key.equals(THROWABLE_RENDERER_PREFIX)) {
                if (hierarchy instanceof ThrowableRendererSupport) {
                    ThrowableRenderer tr = (ThrowableRenderer)
                            OptionConverter.instantiateByKey(props, THROWABLE_RENDERER_PREFIX,
                                    ThrowableRenderer.class, null);
                    if (tr == null) {
                        LogLog.error(
                                "Could not instantiate throwableRenderer.");
                    } else {
                        PropertySetter setter = new PropertySetter(tr);
                        setter.setProperties(props, THROWABLE_RENDERER_PREFIX + ".");
                        ((ThrowableRendererSupport) hierarchy).setThrowableRenderer(tr);

                    }
                }
            }
        }
    }

    /**
     * Parse the additivity option for a non-root category.
     */
    void parseAdditivityForLogger(Properties props, Logger cat, String loggerName) {
        String value = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + loggerName, props);
        LogLog.debug("Handling " + ADDITIVITY_PREFIX + loggerName + "=[" + value + "]");
        // touch additivity only if necessary
        if ((value != null) && (!value.equals(""))) {
            boolean additivity = OptionConverter.toBoolean(value, true);
            LogLog.debug("Setting additivity for \"" + loggerName + "\" to " +
                    additivity);
            cat.setAdditivity(additivity);
        }
    }

    /**
     * This method must work for the root category as well.
     */
    void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value) {

        LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
        // We must skip over ',' but not white space
        StringTokenizer st = new StringTokenizer(value, ",");

        // If value is not in the form ", appender.." or "", then we should set
        // the level of the loggeregory.

        if (!(value.startsWith(",") || value.equals(""))) {

            // just to be on the safe side...
            if (!st.hasMoreTokens()) {
                return;
            }

            String levelStr = st.nextToken();
            LogLog.debug("Level token is [" + levelStr + "].");

            // If the level value is inherited, set category level value to
            // null. We also check that the user has not specified inherited for the
            // root category.
            if (INHERITED.equalsIgnoreCase(levelStr) ||
                    NULL.equalsIgnoreCase(levelStr)) {
                if (loggerName.equals(INTERNAL_ROOT_NAME)) {
                    LogLog.warn("The root logger cannot be set to null.");
                } else {
                    logger.setLevel(null);
                }
            } else {
                logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
            }
            LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
        }

        // Begin by removing all existing appenders.
        logger.removeAllAppenders();

        Appender appender;
        String appenderName;
        while (st.hasMoreTokens()) {
            appenderName = st.nextToken().trim();
            if (appenderName == null || appenderName.equals(",")) {
                continue;
            }
            LogLog.debug("Parsing appender named \"" + appenderName + "\".");
            appender = parseAppender(props, appenderName);
            if (appender != null) {
                logger.addAppender(appender);
            }
        }
    }

    Appender parseAppender(Properties props, String appenderName) {
        Appender appender = registryGet(appenderName);
        if ((appender != null)) {
            LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
            return appender;
        }
        // Appender was not previously initialized.
        String prefix = APPENDER_PREFIX + appenderName;
        String layoutPrefix = prefix + ".layout";

        appender = (Appender) OptionConverter.instantiateByKey(props, prefix,
                Appender.class,
                null);
        if (appender == null) {
            LogLog.error(
                    "Could not instantiate appender named \"" + appenderName + "\".");
            return null;
        }
        appender.setName(appenderName);

        if (appender instanceof OptionHandler) {
            if (appender.requiresLayout()) {
                Layout layout = (Layout) OptionConverter.instantiateByKey(props,
                        layoutPrefix,
                        Layout.class,
                        null);
                if (layout != null) {
                    appender.setLayout(layout);
                    LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
                    //configureOptionHandler(layout, layoutPrefix + ".", props);
                    PropertySetter.setProperties(layout, props, layoutPrefix + ".");
                    LogLog.debug("End of parsing for \"" + appenderName + "\".");
                }
            }
            final String errorHandlerPrefix = prefix + ".errorhandler";
            String errorHandlerClass = OptionConverter.findAndSubst(errorHandlerPrefix, props);
            if (errorHandlerClass != null) {
                ErrorHandler eh = (ErrorHandler) OptionConverter.instantiateByKey(props,
                        errorHandlerPrefix,
                        ErrorHandler.class,
                        null);
                if (eh != null) {
                    appender.setErrorHandler(eh);
                    LogLog.debug("Parsing errorhandler options for \"" + appenderName + "\".");
                    parseErrorHandler(eh, errorHandlerPrefix, props, repository);
                    final Properties edited = new Properties();
                    final String[] keys = new String[]{
                            errorHandlerPrefix + "." + ROOT_REF,
                            errorHandlerPrefix + "." + LOGGER_REF,
                            errorHandlerPrefix + "." + APPENDER_REF_TAG
                    };
                    for (Iterator iter = props.entrySet().iterator(); iter.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        int i = 0;
                        for (; i < keys.length; i++) {
                            if (keys[i].equals(entry.getKey())) {
                                break;
                            }
                        }
                        if (i == keys.length) {
                            edited.put(entry.getKey(), entry.getValue());
                        }
                    }
                    PropertySetter.setProperties(eh, edited, errorHandlerPrefix + ".");
                    LogLog.debug("End of errorhandler parsing for \"" + appenderName + "\".");
                }

            }
            //configureOptionHandler((OptionHandler) appender, prefix + ".", props);
            PropertySetter.setProperties(appender, props, prefix + ".");
            LogLog.debug("Parsed \"" + appenderName + "\" options.");
        }
        parseAppenderFilters(props, appenderName, appender);
        registryPut(appender);
        return appender;
    }

    private void parseErrorHandler(
            final ErrorHandler eh,
            final String errorHandlerPrefix,
            final Properties props,
            final LoggerRepository hierarchy) {
        boolean rootRef = OptionConverter.toBoolean(
                OptionConverter.findAndSubst(errorHandlerPrefix + ROOT_REF, props), false);
        if (rootRef) {
            eh.setLogger(hierarchy.getRootLogger());
        }
        String loggerName = OptionConverter.findAndSubst(errorHandlerPrefix + LOGGER_REF, props);
        if (loggerName != null) {
            Logger logger = (loggerFactory == null) ? hierarchy.getLogger(loggerName)
                    : hierarchy.getLogger(loggerName, loggerFactory);
            eh.setLogger(logger);
        }
        String appenderName = OptionConverter.findAndSubst(errorHandlerPrefix + APPENDER_REF_TAG, props);
        if (appenderName != null) {
            Appender backup = parseAppender(props, appenderName);
            if (backup != null) {
                eh.setBackupAppender(backup);
            }
        }
    }


    void parseAppenderFilters(Properties props, String appenderName, Appender appender) {
        // extract filters and filter options from props into a hashtable mapping
        // the property name defining the filter class to a list of pre-parsed
        // name-value pairs associated to that filter
        final String filterPrefix = APPENDER_PREFIX + appenderName + ".filter.";
        int fIdx = filterPrefix.length();
        Hashtable filters = new Hashtable();
        Enumeration e = props.keys();
        String name = "";
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith(filterPrefix)) {
                int dotIdx = key.indexOf('.', fIdx);
                String filterKey = key;
                if (dotIdx != -1) {
                    filterKey = key.substring(0, dotIdx);
                    name = key.substring(dotIdx + 1);
                }
                Vector filterOpts = (Vector) filters.get(filterKey);
                if (filterOpts == null) {
                    filterOpts = new Vector();
                    filters.put(filterKey, filterOpts);
                }
                if (dotIdx != -1) {
                    String value = OptionConverter.findAndSubst(key, props);
                    filterOpts.add(new NameValue(name, value));
                }
            }
        }

        // sort filters by IDs, insantiate filters, set filter options,
        // add filters to the appender
        Enumeration g = new SortedKeyEnumeration(filters);
        while (g.hasMoreElements()) {
            String key = (String) g.nextElement();
            String clazz = props.getProperty(key);
            if (clazz != null) {
                LogLog.debug("Filter key: [" + key + "] class: [" + props.getProperty(key) + "] props: " + filters.get(key));
                Filter filter = (Filter) OptionConverter.instantiateByClassName(clazz, Filter.class, null);
                if (filter != null) {
                    PropertySetter propSetter = new PropertySetter(filter);
                    Vector v = (Vector) filters.get(key);
                    Enumeration filterProps = v.elements();
                    while (filterProps.hasMoreElements()) {
                        NameValue kv = (NameValue) filterProps.nextElement();
                        propSetter.setProperty(kv.key, kv.value);
                    }
                    propSetter.activate();
                    LogLog.debug("Adding filter of type [" + filter.getClass()
                            + "] to appender named [" + appender.getName() + "].");
                    appender.addFilter(filter);
                }
            } else {
                LogLog.warn("Missing class definition for filter: [" + key + "]");
            }
        }
    }


    void registryPut(Appender appender) {
        registry.put(appender.getName(), appender);
    }

    Appender registryGet(String name) {
        return (Appender) registry.get(name);
    }
}

class PropertyWatchdog extends FileWatchdog {

    PropertyWatchdog(String filename) {
        super(filename);
    }

    /**
     * Call {@link PropertyConfigurator#configure(String)} with the
     * <code>filename</code> to reconfigure log4j.
     */
    public void doOnChange() {
        new PropertyConfigurator().doConfigure(filename,
                LogManager.getLoggerRepository());
    }
}

class NameValue {
    String key, value;

    public NameValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return key + "=" + value;
    }
}

class SortedKeyEnumeration implements Enumeration {

    private Enumeration e;

    public SortedKeyEnumeration(Hashtable ht) {
        Enumeration f = ht.keys();
        Vector keys = new Vector(ht.size());
        for (int i, last = 0; f.hasMoreElements(); ++last) {
            String key = (String) f.nextElement();
            for (i = 0; i < last; ++i) {
                String s = (String) keys.get(i);
                if (key.compareTo(s) <= 0) {
                    break;
                }
            }
            keys.add(i, key);
        }
        e = keys.elements();
    }

    public boolean hasMoreElements() {
        return e.hasMoreElements();
    }

    public Object nextElement() {
        return e.nextElement();
    }
}
