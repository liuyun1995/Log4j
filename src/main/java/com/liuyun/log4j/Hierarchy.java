package com.liuyun.log4j;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.appender.AsyncAppender;
import com.liuyun.log4j.appender.net.SocketAppender;
import com.liuyun.log4j.or.RendererMap;
import com.liuyun.log4j.spi.LoggerFactory;
import com.liuyun.log4j.spi.HierarchyEventListener;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.RendererSupport;
import com.liuyun.log4j.or.ObjectRenderer;
import com.liuyun.log4j.helpers.LogLog;
import com.liuyun.log4j.spi.ThrowableRendererSupport;
import com.liuyun.log4j.spi.ThrowableRenderer;

//层级日志容器
public class Hierarchy implements LoggerRepository, RendererSupport, ThrowableRendererSupport {

    Hashtable ht;                                            //日志对象集合
    Logger root;                                             //根级日志对象
    RendererMap rendererMap;                                 //渲染器映射集合
    int thresholdInt;                                        //日志级别(int表示)
    Level threshold;                                         //日志级别(Level表示)
    boolean emittedNoAppenderWarning = false;                //是否发送没有输出器警告
    boolean emittedNoResourceBundleWarning = false;          //是否发送未绑定资源警告
    private LoggerFactory defaultFactory;                    //日志工厂
    private Vector listeners;                                //事件监听者集合
    private ThrowableRenderer throwableRenderer = null;      //异常渲染器

    //构造器
    public Hierarchy(Logger root) {
        ht = new Hashtable();
        listeners = new Vector(1);
        this.root = root;
        // Enable all level levels by default.
        setThreshold(Level.ALL);
        this.root.setHierarchy(this);
        rendererMap = new RendererMap();
        defaultFactory = new DefaultCategoryFactory();
    }

    //添加渲染器
    public void addRenderer(Class classToRender, ObjectRenderer or) {
        rendererMap.put(classToRender, or);
    }

    //添加层级事件监听器
    @Override
    public void addHierarchyEventListener(HierarchyEventListener listener) {
        if (listeners.contains(listener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        } else {
            listeners.addElement(listener);
        }
    }

    //清空日志对象
    public void clear() {
        ht.clear();
    }

    //发送没有输出器警告
    @Override
    public void emitNoAppenderWarning(Category cat) {
        //只发送一次警告
        if (!this.emittedNoAppenderWarning) {
            LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
            LogLog.warn("Please initialize the log4j system properly.");
            LogLog.warn("See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.");
            this.emittedNoAppenderWarning = true;
        }
    }

    //是否存在对应日志对象
    @Override
    public Logger exists(String name) {
        Object o = ht.get(new CategoryKey(name));
        if (o instanceof Logger) {
            return (Logger) o;
        } else {
            return null;
        }
    }

    //设置阀值
    @Override
    public void setThreshold(String levelStr) {
        //根据名称获取日志级别
        Level l = Level.toLevel(levelStr, null);
        if (l != null) {
            setThreshold(l);
        } else {
            LogLog.warn("Could not convert [" + levelStr + "] to Level.");
        }
    }

    //移除添加输出器事件
    @Override
    public void fireAddAppenderEvent(Category logger, Appender appender) {
        if (listeners != null) {
            int size = listeners.size();
            HierarchyEventListener listener;
            for (int i = 0; i < size; i++) {
                listener = (HierarchyEventListener) listeners.elementAt(i);
                listener.addAppenderEvent(logger, appender);
            }
        }
    }

    //移除删除输出器事件
    void fireRemoveAppenderEvent(Category logger, Appender appender) {
        if (listeners != null) {
            int size = listeners.size();
            HierarchyEventListener listener;
            for (int i = 0; i < size; i++) {
                listener = (HierarchyEventListener) listeners.elementAt(i);
                listener.removeAppenderEvent(logger, appender);
            }
        }
    }

    //获取阀值
    @Override
    public Level getThreshold() {
        return threshold;
    }

    //设置阀值
    @Override
    public void setThreshold(Level l) {
        if (l != null) {
            thresholdInt = l.level;
            threshold = l;
        }
    }

    //获取日志对象
    public Logger getLogger(String name) {
        return getLogger(name, defaultFactory);
    }

    //获取日志对象
    @Override
    public Logger getLogger(String name, LoggerFactory factory) {
        //System.out.println("getInstance("+name+") called.");
        CategoryKey key = new CategoryKey(name);
        // Synchronize to prevent write conflicts. Read conflicts (in
        // getChainedLevel method) are possible only if variable
        // assignments are non-atomic.
        Logger logger;

        synchronized (ht) {
            Object o = ht.get(key);
            if (o == null) {
                logger = factory.makeNewLoggerInstance(name);
                logger.setHierarchy(this);
                ht.put(key, logger);
                updateParents(logger);
                return logger;
            } else if (o instanceof Logger) {
                return (Logger) o;
            } else if (o instanceof ProvisionNode) {
                //System.out.println("("+name+") ht.get(this) returned ProvisionNode");
                logger = factory.makeNewLoggerInstance(name);
                logger.setHierarchy(this);
                ht.put(key, logger);
                updateChildren((ProvisionNode) o, logger);
                updateParents(logger);
                return logger;
            } else {
                // It should be impossible to arrive here
                return null;  // but let's keep the compiler happy.
            }
        }
    }

    //获取当前日志
    @Override
    public Enumeration getCurrentLoggers() {
        // The accumlation in v is necessary because not all elements in
        // ht are Logger objects as there might be some ProvisionNodes
        // as well.
        Vector v = new Vector(ht.size());

        Enumeration elems = ht.elements();
        while (elems.hasMoreElements()) {
            Object o = elems.nextElement();
            if (o instanceof Logger) {
                v.addElement(o);
            }
        }
        return v.elements();
    }

    //获取当前类型
    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }


    //获取渲染器映射
    public RendererMap getRendererMap() {
        return rendererMap;
    }


    //获取根级日志
    public Logger getRootLogger() {
        return root;
    }

    //是否开启
    public boolean isDisabled(int level) {
        return thresholdInt > level;
    }

    /**
     * @deprecated Deprecated with no replacement.
     */
    public void overrideAsNeeded(String override) {
        LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
    }

    //重置配置信息
    @Override
    public void resetConfiguration() {
        getRootLogger().setLevel(Level.DEBUG);
        root.setResourceBundle(null);
        setThreshold(Level.ALL);

        // the synchronization is needed to prevent JDK 1.2.x hashtable
        // surprises
        synchronized (ht) {
            shutdown(); // nested locks are OK

            Enumeration cats = getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger) cats.nextElement();
                c.setLevel(null);
                c.setAdditivity(true);
                c.setResourceBundle(null);
            }
        }
        rendererMap.clear();
        throwableRenderer = null;
    }

    /**
     * Does nothing.
     *
     * @deprecated Deprecated with no replacement.
     */
    public void setDisableOverride(String override) {
        LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
    }


    //设置渲染器
    public void setRenderer(Class renderedClass, ObjectRenderer renderer) {
        rendererMap.put(renderedClass, renderer);
    }

    /**
     * {@inheritDoc}
     */
    public ThrowableRenderer getThrowableRenderer() {
        return throwableRenderer;
    }

    /**
     * {@inheritDoc}
     */
    public void setThrowableRenderer(final ThrowableRenderer renderer) {
        throwableRenderer = renderer;
    }

    //关闭日志容器
    public void shutdown() {
        Logger root = getRootLogger();

        // begin by closing nested appenders
        root.closeNestedAppenders();

        synchronized (ht) {
            Enumeration cats = this.getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger) cats.nextElement();
                c.closeNestedAppenders();
            }

            // then, remove all appenders
            root.removeAllAppenders();
            cats = this.getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger) cats.nextElement();
                c.removeAllAppenders();
            }
        }
    }


    /**
     * This method loops through all the *potential* parents of
     * 'cat'. There 3 possible cases:
     * <p>
     * 1) No entry for the potential parent of 'cat' exists
     * <p>
     * We create a ProvisionNode for this potential parent and insert
     * 'cat' in that provision node.
     * <p>
     * 2) There entry is of type Logger for the potential parent.
     * <p>
     * The entry is 'cat's nearest existing parent. We update cat's
     * parent field with this entry. We also break from the loop
     * because updating our parent's parent is our parent's
     * responsibility.
     * <p>
     * 3) There entry is of type ProvisionNode for this potential parent.
     * <p>
     * We add 'cat' to the list of children for this potential parent.
     */
    private final void updateParents(Logger cat) {
        String name = cat.name;
        int length = name.length();
        boolean parentFound = false;

        //System.out.println("UpdateParents called for " + name);

        // if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not "w.x.y.z"
        for (int i = name.lastIndexOf('.', length - 1); i >= 0;
             i = name.lastIndexOf('.', i - 1)) {
            String substr = name.substring(0, i);

            //System.out.println("Updating parent : " + substr);
            CategoryKey key = new CategoryKey(substr); // simple constructor
            Object o = ht.get(key);
            // Create a provision node for a future parent.
            if (o == null) {
                //System.out.println("No parent "+substr+" found. Creating ProvisionNode.");
                ProvisionNode pn = new ProvisionNode(cat);
                ht.put(key, pn);
            } else if (o instanceof Category) {
                parentFound = true;
                cat.parent = (Category) o;
                //System.out.println("Linking " + cat.name + " -> " + ((Category) o).name);
                break; // no need to update the ancestors of the closest ancestor
            } else if (o instanceof ProvisionNode) {
                ((ProvisionNode) o).addElement(cat);
            } else {
                Exception e = new IllegalStateException("unexpected object type " +
                        o.getClass() + " in ht.");
                e.printStackTrace();
            }
        }
        // If we could not find any existing parents, then link with root.
        if (!parentFound) {
            cat.parent = root;
        }
    }

    /**
     * We update the links for all the children that placed themselves
     * in the provision node 'pn'. The second argument 'cat' is a
     * reference for the newly created Logger, parent of all the
     * children in 'pn'
     * <p>
     * We loop on all the children 'c' in 'pn':
     * <p>
     * If the child 'c' has been already linked to a child of
     * 'cat' then there is no need to update 'c'.
     * <p>
     * Otherwise, we set cat's parent field to c's parent and set
     * c's parent field to cat.
     */
    private final void updateChildren(ProvisionNode pn, Logger logger) {
        //System.out.println("updateChildren called for " + logger.name);
        final int last = pn.size();

        for (int i = 0; i < last; i++) {
            Logger l = (Logger) pn.elementAt(i);
            //System.out.println("Updating child " +p.name);

            // Unless this child already points to a correct (lower) parent,
            // make cat.parent point to l.parent and l.parent to cat.
            if (!l.parent.name.startsWith(logger.name)) {
                logger.parent = l.parent;
                l.parent = logger;
            }
        }
    }

}


