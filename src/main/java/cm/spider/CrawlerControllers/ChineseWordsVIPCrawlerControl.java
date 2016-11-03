package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsVIPCrawl;


public class ChineseWordsVIPCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsVIPCrawlerControl.class);
	
	/**
	 * 封装获取唯品会热搜商品方法
	 * 对应的种子页面是：http://rec.suning.com/show/rank.htm
	 * @return 截获到的关键字
	 */
	public Set<String> getVIPHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsVIPCrawl vipCrawler=new ChineseWordsVIPCrawl();
		try{
			hotSearchWordsList=vipCrawler.getVIPHotWords("http://category.vip.com/");
		}catch(Exception ex){
			logger.info(" getVIPHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取唯品会的今日热门关注商品列表,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsVIPCrawl vipCrawler=new ChineseWordsVIPCrawl();
		int recnum=0;
		topIndexList=vipCrawler.getVIPHotWords("http://category.vip.com/"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
