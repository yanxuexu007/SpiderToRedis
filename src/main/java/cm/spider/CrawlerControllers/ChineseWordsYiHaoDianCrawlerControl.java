package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsYiHaoDianCrawl;


public class ChineseWordsYiHaoDianCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsYiHaoDianCrawlerControl.class);
	
	/**
	 * 封装获取一号店会热搜商品方法
	 * 对应的种子页面是：http://rec.suning.com/show/rank.htm
	 * @return 截获到的关键字
	 */
	public Set<String> getYiHaoDianHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsYiHaoDianCrawl yihaodianCrawler=new ChineseWordsYiHaoDianCrawl();
		try{
			hotSearchWordsList=yihaodianCrawler.getYiHaoDianHotSearchTypes("http://www.yhd.com/hotq/"); //种子文件起始地址
			hotSearchWordsList=yihaodianCrawler.getYiHaoDianHotWords(hotSearchWordsList); //搜集热词
		}catch(Exception ex){
			logger.info(" getYiHaoDianHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取一号店热搜排行,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsYiHaoDianCrawl yihaodianCrawler=new ChineseWordsYiHaoDianCrawl();
		int recnum=0;
		topIndexList=yihaodianCrawler.getYiHaoDianHotSearchTypes("http://www.yhd.com/hotq/"); //种子文件起始地址
		topIndexList=yihaodianCrawler.getYiHaoDianHotWords(topIndexList); //搜集热词
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
