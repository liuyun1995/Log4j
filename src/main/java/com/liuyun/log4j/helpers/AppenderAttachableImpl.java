package com.liuyun.log4j.helpers;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.spi.AppenderAttachable;
import com.liuyun.log4j.spi.LoggingEvent;
import java.util.Enumeration;
import java.util.Vector;

//可附着输出源实现类
public class AppenderAttachableImpl implements AppenderAttachable {

    protected Vector appenderList;    //输出源列表

    //添加输出源
    public void addAppender(Appender newAppender) {
        if (newAppender == null) {
            return;
        }
        if (appenderList == null) {
            appenderList = new Vector(1);
        }
        if (!appenderList.contains(newAppender)) {
            appenderList.addElement(newAppender);
        }
    }

    //将日志事件输出到所有输出源
    public int appendLoopOnAppenders(LoggingEvent event) {
        int size = 0;
        Appender appender;

        //遍历所有输出器，执行日志事件的输出
        if (appenderList != null) {
            size = appenderList.size();
            for (int i = 0; i < size; i++) {
                appender = (Appender) appenderList.elementAt(i);
                appender.doAppend(event);
            }
        }
        return size;
    }


    //获取所有输出源
    public Enumeration getAllAppenders() {
        if (appenderList == null) {
            return null;
        } else {
            return appenderList.elements();
        }
    }

    //根据名称获取输出源
    public Appender getAppender(String name) {
        if (appenderList == null || name == null) {
            return null;
        }

        int size = appenderList.size();
        Appender appender;
        for (int i = 0; i < size; i++) {
            appender = (Appender) appenderList.elementAt(i);
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }


    //输出源是否可以附着
    public boolean isAttached(Appender appender) {
        if (appenderList == null || appender == null) {
            return false;
        }

        int size = appenderList.size();
        Appender a;
        for (int i = 0; i < size; i++) {
            a = (Appender) appenderList.elementAt(i);
            if (a == appender) {
                return true;
            }
        }
        return false;
    }

    //移除全部输出源
    public void removeAllAppenders() {
        if (appenderList != null) {
            int len = appenderList.size();
            for (int i = 0; i < len; i++) {
                Appender a = (Appender) appenderList.elementAt(i);
                a.close();
            }
            appenderList.removeAllElements();
            appenderList = null;
        }
    }

    //移除指定输出源
    public void removeAppender(Appender appender) {
        if (appender == null || appenderList == null) {
            return;
        }
        appenderList.removeElement(appender);
    }

    //移除指定名称输出源
    public void removeAppender(String name) {
        if (name == null || appenderList == null) {
            return;
        }
        int size = appenderList.size();
        for (int i = 0; i < size; i++) {
            if (name.equals(((Appender) appenderList.elementAt(i)).getName())) {
                appenderList.removeElementAt(i);
                break;
            }
        }
    }

}
