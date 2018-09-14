import com.liuyun.log4j.Logger;

public class TestLogger {

	private static final Logger logger=Logger.getLogger(TestLogger.class);
	
	public static void main(String[] args) {
		TestLogger test=new TestLogger();
		test.divide(10, 2);
	}
	
	public int divide(int a,int b){
		int result=0;
		if(b!=0){
			result=a/b;
			logger.info("除数为"+a+" 被除数为"+b+" 除后的结果为："+result);
		}else{
			logger.error("被除数不能为零！！！");
			throw new RuntimeException("被除数不能为零！！！");
		}
		return result;
	}
	
	
	
	
	
}
