package javabase.staticblock;

/**
 * @Package com.store.javabase.staticblock
 * @ClassName: Parent
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月7日 下午4:29:48
 * @Description:静态父类。
 */
public class Parent {

	static String name="parent";
	
	{
		System.out.println("parnet block");
	}
	
	static{
		 System.out.println("parent static block");
	}

	/**
	 * 父类的构造方法。 
	 * Description:
	 */
	public Parent() {
		super();
		System.out.println("parent constructor");
	}
	 
}
