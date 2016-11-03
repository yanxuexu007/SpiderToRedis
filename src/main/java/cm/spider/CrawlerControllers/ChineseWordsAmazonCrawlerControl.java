package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsAmazonCrawl;

/**
 * 获取电商所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsAmazonCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsAmazonCrawlerControl.class);
	
	/**
	 * 封装获取亚马逊热搜商品方法
	 * 对应的种子页面是：https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn
	 * @return 截获到的关键字
	 */
	public Set<String> getAmazonHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsAmazonCrawl amazonCrawler=new ChineseWordsAmazonCrawl();
		try{
			hotSearchWordsList=amazonCrawler.getAmazonHotWords("https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn");
		}catch(Exception ex){
			logger.info(" getAmazonHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	
	//测试区域，用于调试对应网站的热词爬虫结果方法
	public static void main(String[] args) throws Exception {
		
		//测试从后台获取亚马逊的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsAmazonCrawl amazonCrawler=new ChineseWordsAmazonCrawl();
		int recnum=0;
		topIndexList=amazonCrawler.getAmazonHotWords("https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn"); //种子文件起始地址
		
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
