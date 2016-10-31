package cm.crawler.controllers;

import java.util.List;

import org.apache.log4j.Logger;

import cm.crawler.commons.ChineseWordsJingDongCrawl;


public class ChineseWordsJingDongController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsJingDongController.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取淘宝的今日热门关注商品,20161031,ok
		List<String> topIndexList=null;
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
