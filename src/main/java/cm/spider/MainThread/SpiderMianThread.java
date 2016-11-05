package cm.spider.MainThread;

import java.util.TreeSet;

import org.apache.log4j.Logger;

import cm.redis.Commons.TimeFormatter;
import cm.spider.ElectBusiServices.CollectCommonWordsToRedis;
import cm.spider.ElectBusiServices.CollectEBHotWordsToRedis;

public class SpiderMianThread {
	
	//用于日志的获取
	public static Logger logger=Logger.getLogger(SpiderMianThread.class);
	
	public static void main(String[] args) {
		CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
		CollectCommonWordsToRedis collectCommonWordsToRedis=new CollectCommonWordsToRedis();
		String prehour=null;
		String curhour=null;
		TreeSet<String> collectwords=null;
		while(true){
			curhour=TimeFormatter.getHour();		
			if(curhour.equals(prehour)==false){
				collectwords=collectEBHotWordsToRedis.collectAllEletronicBusinessHotWords();
				collectCommonWordsToRedis.collectAllBaiduHotWords();
				prehour=curhour;
			}
			if(collectwords!=null&&collectwords.size()>0){
				collectEBHotWordsToRedis.setEBHotWordsToRedis(collectwords); //每10分钟更新
				logger.info(" Complete ElectronicBusiness Hot Words reference opt!!! ");
			}
			collectCommonWordsToRedis.setBiaduHotWordsToRedis();//每10分钟更新
			logger.info(" Complete Baidu Hot Words reference opt!!! ");
			try{					
				Thread.sleep(1000*60*10);//休息10分钟
			}catch(Exception ex){
				logger.info(" Thread SpiderMianThread crashes: "+ex.getMessage());
			}
		}
	}
}
