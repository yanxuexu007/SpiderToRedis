package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsVIPCrawl;


public class ChineseWordsVIPCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsVIPCrawlerControl.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取唯品会的今日热门关注商品列表,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsVIPCrawl vipCrawler=new ChineseWordsVIPCrawl();
		int recnum=0;
		topIndexList=vipCrawler.getVIPHotProductWords("http://category.vip.com/"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
