package com.liuyun.log4j;

//日志类型键
class CategoryKey {

    String name;    //日志对象名称
    int hashCache;  //日志对象哈希吗

    CategoryKey(String name) {
        this.name = name;
        hashCache = name.hashCode();
    }

    public final int hashCode() {
        return hashCache;
    }

    public final boolean equals(Object rArg) {
        if (this == rArg) {
            return true;
        }
        if (rArg != null && CategoryKey.class == rArg.getClass()) {
            return name.equals(((CategoryKey) rArg).name);
        } else {
            return false;
        }
    }

}
