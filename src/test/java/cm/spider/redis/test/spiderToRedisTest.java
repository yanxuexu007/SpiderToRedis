package cm.spider.redis.test;

import org.apache.log4j.Logger;

public class spiderToRedisTest {
	
	public static Logger logger=Logger.getLogger(spiderToRedisTest.class);
	
	//http://top.baidu.com/buzz?b=2&fr=topboards
	public static void main(String[] args) {
		try{
			logger.info(" spiderToRedisTest starts ");
			String crawlArg="";
			
			logger.info(" spiderToRedisTest ends ");
		}catch(Exception ex){
			logger.info(" spiderToRedisTest crashes :"+ex.getMessage());
		}
	}
}
