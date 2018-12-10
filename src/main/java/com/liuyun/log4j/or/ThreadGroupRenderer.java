package com.liuyun.log4j.or;

import com.liuyun.log4j.layout.Layout;

//线程组渲染器
public class ThreadGroupRenderer implements ObjectRenderer {

    //构造器
    public ThreadGroupRenderer() {}

    //渲染方法
    public String doRender(Object o) {
        if (o instanceof ThreadGroup) {
            StringBuffer sbuf = new StringBuffer();
            ThreadGroup tg = (ThreadGroup) o;
            sbuf.append("java.lang.ThreadGroup[name=");
            sbuf.append(tg.getName());
            sbuf.append(", maxpri=");
            sbuf.append(tg.getMaxPriority());
            sbuf.append("]");
            Thread[] t = new Thread[tg.activeCount()];
            tg.enumerate(t);
            for (int i = 0; i < t.length; i++) {
                sbuf.append(Layout.LINE_SEP);
                sbuf.append("   Thread=[");
                sbuf.append(t[i].getName());
                sbuf.append(",");
                sbuf.append(t[i].getPriority());
                sbuf.append(",");
                sbuf.append(t[i].isDaemon());
                sbuf.append("]");
            }
            return sbuf.toString();
        } else {
            try {
                // this is the best we can do
                return o.toString();
            } catch (Exception ex) {
                return ex.toString();
            }
        }
    }

}  
