package cm.spider.CrawlerControllers;

import java.util.Set;

import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsJingDongCrawl;

/**
 * 获取电商所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsJingDongCrawlerControl {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsJingDongCrawlerControl.class);
	
	/**
	 * 封装获取京东热搜商品方法
	 * 对应的种子页面是：ttps://top.jd.com/#search
	 * @return 截获到的关键字
	 */
	public Set<String> getJingDongHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsJingDongCrawl jingdongCrawler=new ChineseWordsJingDongCrawl();
		try{
			hotSearchWordsList=jingdongCrawler.getJingDongHotWords("ttps://top.jd.com/#search");
		}catch(Exception ex){
			logger.info(" getJingDongHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取京东的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		ChineseWordsJingDongCrawl jingdongCrawler=new ChineseWordsJingDongCrawl();
		int recnum=0;
		topIndexList=jingdongCrawler.getJingDongHotWords("https://top.jd.com/#search"); //种子文件起始地址
		//测试获取搜索词列表的代码段
		if(topIndexList!=null&&topIndexList.size()>0){
			for(String url : topIndexList){
				recnum+=1;
				System.out.println("	"+recnum+":	"+url);
			}
		}
	}
}
