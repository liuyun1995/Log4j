package com.liuyun.log4j.rewrite;

import com.liuyun.log4j.helpers.AppenderAttachableImpl;
import com.liuyun.log4j.spi.AppenderAttachable;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.xml.DomConfigurator;
import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.AppenderSkeleton;
import com.liuyun.log4j.spi.OptionHandler;
import com.liuyun.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;
import java.util.Enumeration;
import java.util.Properties;

//重写输出器
public class RewriteAppender extends AppenderSkeleton implements AppenderAttachable, UnrecognizedElementHandler {

    private RewritePolicy policy;                        //重写策略
    private final AppenderAttachableImpl appenders;      //可附着输出器

    //构造器
    public RewriteAppender() {
        appenders = new AppenderAttachableImpl();
    }

    //追加事件
    protected void append(final LoggingEvent event) {
        LoggingEvent rewritten = event;
        if (policy != null) {
            rewritten = policy.rewrite(event);
        }
        if (rewritten != null) {
            synchronized (appenders) {
                appenders.appendLoopOnAppenders(rewritten);
            }
        }
    }

    //添加输出器
    public void addAppender(final Appender newAppender) {
        synchronized (appenders) {
            appenders.addAppender(newAppender);
        }
    }

    //获取所有输出器
    public Enumeration getAllAppenders() {
        synchronized (appenders) {
            return appenders.getAllAppenders();
        }
    }

    //获取指定输出器
    public Appender getAppender(final String name) {
        synchronized (appenders) {
            return appenders.getAppender(name);
        }
    }

    //关闭输出器
    public void close() {
        closed = true;
        synchronized (appenders) {
            Enumeration iter = appenders.getAllAppenders();
            if (iter != null) {
                while (iter.hasMoreElements()) {
                    Object next = iter.nextElement();
                    if (next instanceof Appender) {
                        ((Appender) next).close();
                    }
                }
            }
        }
    }

    //是否可附着
    public boolean isAttached(final Appender appender) {
        synchronized (appenders) {
            return appenders.isAttached(appender);
        }
    }

    //是否请求格式化
    public boolean requiresLayout() {
        return false;
    }

    //移除所有输出器
    public void removeAllAppenders() {
        synchronized (appenders) {
            appenders.removeAllAppenders();
        }
    }

    //移除指定输出器
    public void removeAppender(final Appender appender) {
        synchronized (appenders) {
            appenders.removeAppender(appender);
        }
    }

    //移除指定输出器
    public void removeAppender(final String name) {
        synchronized (appenders) {
            appenders.removeAppender(name);
        }
    }

    //设置重写策略
    public void setRewritePolicy(final RewritePolicy rewritePolicy) {
        policy = rewritePolicy;
    }

    //解析未识别元素
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("rewritePolicy".equals(nodeName)) {
            Object rewritePolicy = DomConfigurator.parseElement(element, props, RewritePolicy.class);
            if (rewritePolicy != null) {
                if (rewritePolicy instanceof OptionHandler) {
                    ((OptionHandler) rewritePolicy).activateOptions();
                }
                this.setRewritePolicy((RewritePolicy) rewritePolicy);
            }
            return true;
        }
        return false;
    }

}
