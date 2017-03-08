package com.store.javabase.staticblock;

/**
 * @Package com.store.javabase.staticblock
 * @ClassName: com.store.javabase.staticblock.Child  
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月7日 下午4:34:20
 * @Description:
 */
public class Child extends Parent {

	static String name="child";
	
	{
		System.out.println("child block");
	}
	
	static {
		System.out.println("child static block");
	}

	/**
	 * Description:子类的构造方法。
	 */
	public Child() { 
		System.out.println("child constructor");
	}
	
}
