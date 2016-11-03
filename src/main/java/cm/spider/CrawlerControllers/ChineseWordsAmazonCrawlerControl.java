package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsAmazonCrawl;


public class ChineseWordsAmazonCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsAmazonCrawlerControl.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取亚马逊的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsAmazonCrawl amazonCrawler=new ChineseWordsAmazonCrawl();
		int recnum=0;
		topIndexList=amazonCrawler.getAmazonHourWords("https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn"); //种子文件起始地址
		
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
