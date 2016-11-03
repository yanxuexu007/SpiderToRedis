package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsJingDongCrawl;


public class ChineseWordsJingDongCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsJingDongCrawlerControl.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取京东的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsJingDongCrawl jingdongCrawler=new ChineseWordsJingDongCrawl();
		int recnum=0;
		topIndexList=jingdongCrawler.getJDTodayRankingListWords("https://top.jd.com/#search"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
