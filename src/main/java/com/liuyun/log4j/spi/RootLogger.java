package com.liuyun.log4j.spi;

import com.liuyun.log4j.Level;
import com.liuyun.log4j.Logger;
import com.liuyun.log4j.helpers.LogLog;

//根级日志类
public final class RootLogger extends Logger {

    //构造器
    public RootLogger(Level level) {
        super("root");
        setLevel(level);
    }

    //获取日志级别
    public final Level getChainedLevel() {
        return level;
    }

    //设置日志级别
    public final void setLevel(Level level) {
        if (level == null) {
            LogLog.error("You have tried to set a null level to root.", new Throwable());
        } else {
            this.level = level;
        }
    }

}
