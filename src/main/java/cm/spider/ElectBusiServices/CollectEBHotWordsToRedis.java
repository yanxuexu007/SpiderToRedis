package cm.spider.ElectBusiServices;

import java.util.TreeSet;

import cm.spider.CrawlerControllers.ChineseWordsAmazonCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsJingDongCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsSuNingYiGouCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsTaoBaoCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsVIPCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsYiHaoDianCrawlController;


/**
 * 服务层代码，按照业务需求调用控制器获取热词信息
 * 对电商的热搜词进行爬虫检索，每小时执行一次
 * @author yanxu
 *
 */
public class CollectEBHotWordsToRedis {
	//电商对应的爬虫机器人
	private ChineseWordsAmazonCrawlController amazonCrawlerControl;
	private ChineseWordsVIPCrawlController vipCrawlerControl;
	private ChineseWordsJingDongCrawlController jingdongCrawlerControl;
	private ChineseWordsTaoBaoCrawlController taobaoCrawlerControl;
	private ChineseWordsYiHaoDianCrawlController yihaodianCrawlerControl;
	private ChineseWordsSuNingYiGouCrawlController suningyigouCrawlerControl;
	
	/**
	 * 每小时执行一次商品热搜词更新，将热词的交集统一加入到redis集合中
	 */
	public void setEletronicBusinessHotWordsToRedis(){
		amazonCrawlerControl=new ChineseWordsAmazonCrawlController();
		vipCrawlerControl=new ChineseWordsVIPCrawlController();
		jingdongCrawlerControl=new ChineseWordsJingDongCrawlController();
		taobaoCrawlerControl=new ChineseWordsTaoBaoCrawlController();
		yihaodianCrawlerControl=new ChineseWordsYiHaoDianCrawlController();
		suningyigouCrawlerControl=new ChineseWordsSuNingYiGouCrawlController();
		TreeSet<String> ebusinessHotWords=null;
		TreeSet<String> unionallHotWords=null;
		try{
			ebusinessHotWords=new TreeSet<String>();
			unionallHotWords=new TreeSet<String>();
			//提取其中的中文
			String reg = "[^\u4e00-\u9fa5]"; 
			
			//对所有的单词进行合并，排序去除重复项
			ebusinessHotWords=amazonCrawlerControl.getAmazonHotSearchWords();
			//去除无效的字符
			for(String str: ebusinessHotWords){
				str = str.replaceAll(reg, "");
				if(str!=null&&str.length()>=2)unionallHotWords.add(str);
			}
			ebusinessHotWords=amazonCrawlerControl.getAmazonHotSearchWords();
			//去除无效的字符
			for(String str: ebusinessHotWords){
				str = str.replaceAll(reg, "");
				if(str!=null&&str.length()>=2)unionallHotWords.add(str);
			}
			
		}catch(Exception ex){
			
		}
		//释放内存
		amazonCrawlerControl=null;
		vipCrawlerControl=null;
		jingdongCrawlerControl=null;
		taobaoCrawlerControl=null;
		yihaodianCrawlerControl=null;
		suningyigouCrawlerControl=null;
	}
	
	/**
	 * 根据当前用户的拆词排行信息，对电商中涉及的热词进行排行检索
	 */
	public void setEBHotWordsRankList(){
		
	}
}
