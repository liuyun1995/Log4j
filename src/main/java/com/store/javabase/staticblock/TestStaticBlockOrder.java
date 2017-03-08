package com.store.javabase.staticblock;

/**
 * @Package com.store.javabase.staticblock
 * @ClassName: com.store.javabase.staticblock.TestStaticBlockOrder  
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月7日 下午4:37:49
 * @Description:测试静态代码块执行的顺序。
 */
public class TestStaticBlockOrder {
	
	public static void main(String[] args) {
		new Child();
	}
	
	/**
	 执行后的输出为：	 
	parent static block
	child static block
	parnet block
	parent constructor
	child block
	child constructor

           分析:当执行new Child()时，它首先去看父类里面有没有静态代码块，如果有，它先去执行父类里面静态代码块里面的内容，
           当父类的静态代码块里面的内容执行完毕之后，接着去执行子类(自己这个类)里面的静态代码块，当子类的静态代码块执行完毕之后，
           它接着又去看父类有没有非静态代码块，如果有就执行父类的非静态代码块，父类的非静态代码块执行完毕，接着执行父类的构造方法；
           父类的构造方法执行完毕之后，它接着去看子类有没有非静态代码块，如果有就执行子类的非静态代码块。
           子类的非静态代码块执行完毕再去执行子类的构造方法，这个就是一个对象的初始化顺序。
    
    
             注意:子类的构造方法，不管这个构造方法带不带参数，默认的它都会先去寻找父类的不带参数的构造方法。如果父类没有不带参数的构造方法，
             那么子类必须用supper关键子来调用父类带参数的构造方法，否则编译不能通过。
             
     参考 http://www.cnblogs.com/panjun-Donet/archive/2010/08/10/1796209.html
	 */
}
