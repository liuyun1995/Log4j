package com.liuyun.log4j.or;

//默认渲染器
class DefaultRenderer implements ObjectRenderer {

    //构造器
    DefaultRenderer() {}

    //渲染方法
    public String doRender(final Object o) {
        try {
            return o.toString();
        } catch (Exception ex) {
            return ex.toString();
        }
    }

}  
