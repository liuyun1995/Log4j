package com.liuyun.log4j;

import java.util.Vector;

//规定节点
class ProvisionNode extends Vector {
    private static final long serialVersionUID = -4479121426311014469L;

    ProvisionNode(Logger logger) {
        super();
        this.addElement(logger);
    }

}
