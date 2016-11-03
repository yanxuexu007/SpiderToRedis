package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsSuNingYiGouCrawl;

/**
 * 获取电商所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsSuNingYiGouCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsSuNingYiGouCrawlerControl.class);
	
	/**
	 * 封装获取苏宁易购热搜商品方法
	 * 对应的种子页面是：http://rec.suning.com/show/rank.htm
	 * @return 截获到的关键字
	 */
	public Set<String> getSuNingYiGouHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsSuNingYiGouCrawl suningyigouCrawler=new ChineseWordsSuNingYiGouCrawl();
		try{
			hotSearchWordsList=suningyigouCrawler.getSNYGHotWords("http://rec.suning.com/show/rank.htm"); 
		}catch(Exception ex){
			logger.info(" getSuNingYiGouHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取苏宁易购的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsSuNingYiGouCrawl suningyigouCrawler=new ChineseWordsSuNingYiGouCrawl();
		int recnum=0;
		topIndexList=suningyigouCrawler.getSNYGHotWords("http://rec.suning.com/show/rank.htm"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
