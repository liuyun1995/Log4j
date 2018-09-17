package com.liuyun.log4j.spi;

import java.util.Enumeration;
import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.Category;
import com.liuyun.log4j.Level;
import com.liuyun.log4j.Logger;

//日志实例容器
public interface LoggerRepository {

    //添加层级事件监听器
    public void addHierarchyEventListener(HierarchyEventListener listener);

    //是否可用
    boolean isDisabled(int level);

    //设置阀值
    public void setThreshold(Level level);

    //设置阀值
    public void setThreshold(String val);

    //发送无输出器的警告
    public void emitNoAppenderWarning(Category cat);

    //获取阀值
    public Level getThreshold();

    //获取日志对象
    public Logger getLogger(String name);

    //获取日志对象
    public Logger getLogger(String name, LoggerFactory factory);

    //获取根级日志对象
    public Logger getRootLogger();

    //判断日志对象是否存在
    public abstract Logger exists(String name);

    //关闭日志容器
    public abstract void shutdown();

    //获取当前日志对象
    public Enumeration getCurrentLoggers();

    //获取当前分类
    public Enumeration getCurrentCategories();

    //移除添加输出器事件
    public abstract void fireAddAppenderEvent(Category logger, Appender appender);

    //重置配置信息
    public abstract void resetConfiguration();

}
