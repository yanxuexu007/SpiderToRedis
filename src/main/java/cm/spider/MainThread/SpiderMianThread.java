package cm.spider.MainThread;

import org.apache.log4j.Logger;

import cm.spider.ElectBusiServices.CollectCommonWordsToRedis;
import cm.spider.ElectBusiServices.CollectEBHotWordsToRedis;

public class SpiderMianThread {
	
	//用于日志的获取
	public static Logger logger=Logger.getLogger(SpiderMianThread.class);
	
	public static void main(String[] args) {
		CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
		CollectCommonWordsToRedis collectCommonWordsToRedis=new CollectCommonWordsToRedis();
		while(true){
			try{
				collectEBHotWordsToRedis.execCollectEBWordstoRedis();
				collectCommonWordsToRedis.execCollectCommonWordstoRedis();
				Thread.sleep(1000*60*60);//休息一个小时
			}catch(Exception ex){
				logger.info(" SpiderMianThread crashes : "+ex.getMessage());
			}
		}
	}
}
