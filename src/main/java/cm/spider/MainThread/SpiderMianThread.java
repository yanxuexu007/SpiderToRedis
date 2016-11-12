package cm.spider.MainThread;

//import java.util.TreeSet;

import org.apache.log4j.Logger;

import cm.redis.Commons.RedisClusterObj;
import cm.redis.Commons.TimeFormatter;
import cm.spider.ElectBusiServices.CollectCommonWordsToRedis;
//import cm.spider.ElectBusiServices.CollectEBHotWordsToRedis;

public class SpiderMianThread {
	
	//用于日志的获取
	public static Logger logger=Logger.getLogger(SpiderMianThread.class);
	
	public static void main(String[] args) {
		//CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
		CollectCommonWordsToRedis collectCommonWordsToRedis=new CollectCommonWordsToRedis();
		String prehour=null;
		String curhour=null;
		int num=0;
//		TreeSet<String> collectwords=null;
		while(true){
			num=0;
			curhour=TimeFormatter.getHour();		
			if(curhour.equals(prehour)==false){
				//collectwords=collectEBHotWordsToRedis.collectAllEletronicBusinessHotWords();
				num=collectCommonWordsToRedis.collectAllBaiduHotWords();
				collectCommonWordsToRedis.setBiaduHotWordsToRedis();//每10分钟更新
				prehour=curhour;
				RedisClusterObj.close();
			}
//			if(collectwords!=null&&collectwords.size()>0){
//				collectEBHotWordsToRedis.setEBHotWordsToRedis(collectwords); //每10分钟更新
//				logger.info(" Complete ElectronicBusiness Hot Words reference opt!!! ");
//			}
			if(num>=0)logger.info(" Complete get Baidu Hot Words: "+num+"!!! ");
			try{					
				Thread.sleep(1000*60*60);//休息1小时
			}catch(Exception ex){
				logger.info(" Thread SpiderMianThread crashes: "+ex.getMessage());
			}
		}
	}
}
