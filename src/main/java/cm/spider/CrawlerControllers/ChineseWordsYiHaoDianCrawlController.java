package cm.spider.CrawlerControllers;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsYiHaoDianCrawl;


public class ChineseWordsYiHaoDianCrawlController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsYiHaoDianCrawlController.class);
	
	//测试Crawl的方法
	public static void main(String[] args) throws Exception {
		//测试从后台获取一号店热搜排行,20161031,ok
//		Set<String> topIndexList=null;
//		TreeSet<String> hotSearchWordsList=null;
//		ChineseWordsYiHaoDianCrawl yihaodianCrawler=new ChineseWordsYiHaoDianCrawl();
//		int recnum=0;
//		topIndexList=yihaodianCrawler.getYiHaoDianHotSearchTypes("http://www.yhd.com/hotq/"); //种子文件起始地址
//		hotSearchWordsList=yihaodianCrawler.getYiHaoDianHotWords(topIndexList); //搜集热词
//		//测试获取搜索词列表的代码段
//		if(hotSearchWordsList!=null&&hotSearchWordsList.size()>0){
//			for(String url : hotSearchWordsList){
//				recnum+=1;
//				System.out.println("	"+recnum+":	"+url);
//			}
//		}
	}
	
	/**
	 * 封装获取一号店会热搜商品方法
	 * 对应的种子页面是：http://rec.suning.com/show/rank.htm
	 * @return 截获到的关键字
	 */
	public TreeSet<String> getYiHaoDianHotSearchWords(){
		Set<String> topIndexList=null;
		TreeSet<String> hotSearchWordsList=null;
		ChineseWordsYiHaoDianCrawl yihaodianCrawler=new ChineseWordsYiHaoDianCrawl();
		try{
			topIndexList=yihaodianCrawler.getYiHaoDianHotSearchTypes("http://www.yhd.com/hotq/"); //种子文件起始地址
			hotSearchWordsList=yihaodianCrawler.getYiHaoDianHotWords(topIndexList); //搜集热词
		}catch(Exception ex){
			logger.info(" getYiHaoDianHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
}
