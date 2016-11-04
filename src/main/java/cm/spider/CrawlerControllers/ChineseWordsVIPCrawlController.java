package cm.spider.CrawlerControllers;

import java.util.TreeSet;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsVIPCrawl;


public class ChineseWordsVIPCrawlController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsVIPCrawlController.class);
	
	//测试Crawl方法
	public static void main(String[] args) throws Exception {
		//测试从后台获取唯品会的今日热门关注商品列表,20161031,ok
//		TreeSet<String> topIndexList=null;
//		ChineseWordsVIPCrawl vipCrawler=new ChineseWordsVIPCrawl();
//		int recnum=0;
//		topIndexList=vipCrawler.getVIPHotWords("http://category.vip.com/"); //种子文件起始地址
//		//测试获取搜索词列表的代码段
//		if(topIndexList!=null&&topIndexList.size()>0){
//			for(String url : topIndexList){
//				recnum+=1;
//				System.out.println("	"+recnum+":	"+url);
//			}
//		}
	}
	
	
	/**
	 * 封装获取唯品会热搜商品方法
	 * 对应的种子页面是：http://category.vip.com/
	 * @return 截获到的关键字
	 */
	public TreeSet<String> getVIPHotSearchWords(){
		TreeSet<String> hotSearchWordsList=null;
		ChineseWordsVIPCrawl vipCrawler=new ChineseWordsVIPCrawl();
		try{
			hotSearchWordsList=vipCrawler.getVIPHotWords("http://category.vip.com/");
		}catch(Exception ex){
			logger.info(" getVIPHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
}
