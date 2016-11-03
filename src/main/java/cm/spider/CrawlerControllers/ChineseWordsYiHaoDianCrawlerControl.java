package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsYiHaoDianCrawl;


public class ChineseWordsYiHaoDianCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsYiHaoDianCrawlerControl.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取一号店热搜排行,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsYiHaoDianCrawl yihaodianCrawler=new ChineseWordsYiHaoDianCrawl();
		int recnum=0;
		topIndexList=yihaodianCrawler.getYiHaoDianHotSearchTypes("http://www.yhd.com/hotq/"); //种子文件起始地址
		topIndexList=yihaodianCrawler.getYiHaoDianHotSearchWords(topIndexList); //搜集热词
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
