package com.liuyun.log4j;

/**
 * CategoryKey is a wrapper for String that apparently accellerated
 * hash table lookup in early JVM's.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
class CategoryKey {

    String name;
    int hashCache;

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
