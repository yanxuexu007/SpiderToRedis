package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsSuNingYiGouCrawl;


public class ChineseWordsSuNingYiGouCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsSuNingYiGouCrawlerControl.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取苏宁易购的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsSuNingYiGouCrawl suningyigouCrawler=new ChineseWordsSuNingYiGouCrawl();
		int recnum=0;
		topIndexList=suningyigouCrawler.getSNYGTodayRankingListWords("http://rec.suning.com/show/rank.htm"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}