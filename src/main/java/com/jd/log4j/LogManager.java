package com.jd.log4j;

import com.jd.log4j.helpers.OptionConverter;
import com.jd.log4j.spi.LoggerFactory;
import com.jd.log4j.spi.RootLogger;
import com.jd.log4j.spi.LoggerRepository;
import com.jd.log4j.spi.RepositorySelector;
import com.jd.log4j.spi.DefaultRepositorySelector;
import com.jd.log4j.spi.NOPLoggerRepository;
import com.jd.log4j.helpers.Loader;
import com.jd.log4j.helpers.LogLog;

import java.net.URL;
import java.net.MalformedURLException;


import java.util.Enumeration;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Gets {@link Logger} instances and operates on the current {@link LoggerRepository}.
 * <p>
 * <p>
 * When the <code>LogManager</code> class is loaded into memory the default initialization procedure runs. The default initialization
 * procedure</a> is described in the <a href="../../../../manual.html#defaultInit">short log4j manual</a>.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LogManager {

	/**
	 * @deprecated This variable is for internal use only. It will
	 * become package protected in future versions.
	 */
	static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
	/**
	 * @deprecated This variable is for internal use only. It will
	 * become private in future versions.
	 */
	static final public String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
	/**
	 * @deprecated This variable is for internal use only. It will
	 * become private in future versions.
	 */
	static final public String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
	/**
	 * @deprecated This variable is for internal use only. It will
	 * become private in future versions.
	 */
	public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
	static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
	/**
	 * private，私有的访问权限，也是最严格的访问权限，仅只能在设置了该权限的类中访问，利用这个访问权限，表现出封装思想。
	 * <p>
	 * default，默认的访问权限，也是可以省略的访问权限，它不仅能在设置了该权限的类中访问，也可以在同一包中的类或子类中访问。
	 * <p>
	 * protected，受保护的访问权限，它除了具有default的访问权限外，还可以在不同包中所继承的子类访问。
	 * <p>
	 * public，公有的访问权限，也是最宽松的访问权限，不仅可以是同一个类或子类，还是同一个包中的类或子类，又还是不同包中的类或子类，都可以访问。
	 */
	static private Object guard = null;
	static private RepositorySelector repositorySelector;


	// By default we use a DefaultRepositorySelector which always returns 'h'.

	/**
	 * 静态块，在类被加载的时候会先执行静态代码块。
	 */
	static {
		//记录日志的等级
		Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
		repositorySelector = new DefaultRepositorySelector(h);

		/** Search for the properties file log4j.properties in the CLASSPATH.  */
		String override = OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY, null);

		// if there is no default init override, then get the resource
		// specified by the user or the default config file.
		if (override == null || "false".equalsIgnoreCase(override)) {

		//  获取 log4j.configuration 文件
			String configurationOptionStr = OptionConverter.getSystemProperty(DEFAULT_CONFIGURATION_KEY, null);

		//  获取 log4j.configuratorClass 文件
			String configuratorClassName = OptionConverter.getSystemProperty(CONFIGURATOR_CLASS_KEY, null);

			URL url = null;

			// if the user has not specified the log4j.configuration
			// property, we search first for the file "log4j.xml" and then
			// "log4j.properties"
			if (configurationOptionStr == null) {
				//获取 log4j.xml
				url = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);
				//获取 log4j.properties
				if (url == null) {
					url = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
				}
			} else {
				try {
					url = new URL(configurationOptionStr);
				} catch (MalformedURLException ex) {
					// so, resource is not a URL:
					// attempt to get the resource from the class path
					url = Loader.getResource(configurationOptionStr);
				}
			}

			// If we have a non-null url, then delegate the rest of the
			// configuration to the OptionConverter.selectAndConfigure
			// method.
			if (url != null) {
				LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");
				try {
					OptionConverter.selectAndConfigure(url, configuratorClassName, LogManager.getLoggerRepository());
				} catch (NoClassDefFoundError e) {
					LogLog.warn("Error during default initialization", e);
				}
			} else {
				LogLog.debug("Could not find resource: [" + configurationOptionStr + "].");
			}
		} else {
			LogLog.debug("Default initialization of overridden by " + DEFAULT_INIT_OVERRIDE_KEY + "property.");
		}
	}

	/**
	 * Sets <code>LoggerFactory</code> but only if the correct
	 * <em>guard</em> is passed as parameter.
	 * <p>
	 * <p>Initally the guard is null.  If the guard is
	 * <code>null</code>, then invoking this method sets the logger
	 * factory and the guard. Following invocations will throw a {@link
	 * IllegalArgumentException}, unless the previously set
	 * <code>guard</code> is passed as the second parameter.
	 * <p>
	 * <p>This allows a high-level component to set the {@link
	 * RepositorySelector} used by the <code>LogManager</code>.
	 * <p>
	 * <p>For example, when tomcat starts it will be able to install its
	 * own repository selector. However, if and when Tomcat is embedded
	 * within JBoss, then JBoss will install its own repository selector
	 * and Tomcat will use the repository selector set by its container,
	 * JBoss.
	 */

	public static void setRepositorySelector(RepositorySelector selector, Object guard) throws IllegalArgumentException {
		if ((LogManager.guard != null) && (LogManager.guard != guard)) {
			throw new IllegalArgumentException(
					"Attempted to reset the LoggerFactory without possessing the guard.");
		}

		if (selector == null) {
			throw new IllegalArgumentException("RepositorySelector must be non-null.");
		}

		LogManager.guard = guard;
		LogManager.repositorySelector = selector;
	}


	/**
	 * This method tests if called from a method that
	 * is known to result in class members being abnormally
	 * set to null but is assumed to be harmless since the
	 * all classes are in the process of being unloaded.
	 *
	 * @param ex exception used to determine calling stack.
	 * @return true if calling stack is recognized as likely safe.
	 */
	private static boolean isLikelySafeScenario(final Exception ex) {
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.toString();
		return msg.indexOf("org.apache.catalina.loader.WebappClassLoader.stop") != -1;
	}

	/**
	 * @return
	 * @author 张明明
	 * @date 2016年5月7日 下午4:15:17
	 * @Description: 返回Logger对象的容器，为控制台或磁盘文件。
	 */
	public static LoggerRepository getLoggerRepository() {
		if (repositorySelector == null) {
			repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
			guard = null;
			Exception ex = new IllegalStateException("Class invariant violation");
			String msg = "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
			if (isLikelySafeScenario(ex)) {
				LogLog.debug(msg, ex);
			} else {
				LogLog.error(msg, ex);
			}
		}
		return repositorySelector.getLoggerRepository();
	}

	/**
	 * Retrieve the appropriate root logger.
	 */
	public static Logger getRootLogger() {
		// Delegate the actual manufacturing of the logger to the logger repository.
		return getLoggerRepository().getRootLogger();
	}

	/**
	 Retrieve the appropriate {@link Logger} instance.
	 */
	// Delegate the actual manufacturing of the logger to the logger repository.

	/**
	 * @param name
	 * @return
	 * @author 张明明
	 * @date 2016年5月7日 下午5:18:47
	 * @Description:
	 */
	public static Logger getLogger(final String name) {
		return getLoggerRepository().getLogger(name);
	}

	/**
	 Retrieve the appropriate {@link Logger} instance.
	 */
	/**
	 * Delegate  代理，委托。。。为代表
	 *
	 * @param clazz
	 * @return
	 * @author 张明明
	 * @date 2016年5月7日 下午4:12:48
	 * @Description:委托正真的创建logger的动作。 Delegate  the actual manufacturing of the logger to the logger repository.
	 */
	public static Logger getLogger(final Class clazz) {
		return getLoggerRepository().getLogger(clazz.getName());
	}


	/**
	 * Retrieve the appropriate {@link Logger} instance.
	 */
	public static Logger getLogger(final String name, final LoggerFactory factory) {
		// Delegate the actual manufacturing of the logger to the logger repository.
		return getLoggerRepository().getLogger(name, factory);
	}

	public static Logger exists(final String name) {
		return getLoggerRepository().exists(name);
	}

	public static Enumeration getCurrentLoggers() {
		return getLoggerRepository().getCurrentLoggers();
	}

	public static void shutdown() {
		getLoggerRepository().shutdown();
	}

	public static void resetConfiguration() {
		getLoggerRepository().resetConfiguration();
	}
}

