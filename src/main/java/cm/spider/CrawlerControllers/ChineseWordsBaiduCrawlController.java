package cm.spider.CrawlerControllers;

import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import cm.spider.CrawlerBasis.ChineseWordsBaiduCrawl;
import cm.spider.CrawlerBasis.ChineseWordsJingDongCrawl;


/**
 * 百度热门搜索词种子url与对应热词链接获取，并调用后台专门的百度热搜数据爬虫设计类，获取所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsBaiduCrawlController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduCrawlController.class);
	
	//测试Crawl方法
	public static void main(String[] args) throws Exception {
		//测试从后台获取百度热门搜索关键词,20161031,ok
//		Map<String, String> topIndexList=null;
//		Map<String,  TreeSet<String>> typesTopZhWords=null;
//		TreeSet<String> hotZhWords=null;
//		ChineseWordsBaiduCrawl baiduCrawler=new ChineseWordsBaiduCrawl();
//		int recnum=0;
//		topIndexList=baiduCrawler.getBDHotZhTypesAndLinks("http://top.baidu.com/boards?fr=topindex"); //种子文件起始地址
//		typesTopZhWords=baiduCrawler.getBDHotZhDetail(topIndexList);
//		if(typesTopZhWords!=null&&typesTopZhWords.size()>0){
//			for (String key : typesTopZhWords.keySet()){
//				System.out.println(key+": ");
//				hotZhWords=typesTopZhWords.get(key);
//				recnum=0;
//				for (String str : hotZhWords) {
//					  recnum+=1;
//				      System.out.println("	"+recnum+":	"+str);
//				}
//			}
//		}
	}
	
	/**
	 * 封装获取百度各类热搜词方法
	 * 对应的种子页面是：http://top.baidu.com/boards?fr=topindex
	 * @return 截获到的类别，关键字列表组成的集合
	 */
	public Map<String,  TreeSet<String>> getBaiduHotSearchWords(){
		Map<String, String> topIndexList=null;
		Map<String,  TreeSet<String>> typesTopZhWords=null;
		ChineseWordsBaiduCrawl baiduCrawler=new ChineseWordsBaiduCrawl();
		try{
			topIndexList=baiduCrawler.getBDHotZhTypesAndLinks("http://top.baidu.com/boards?fr=topindex"); //种子文件起始地址
			typesTopZhWords=baiduCrawler.getBDHotZhDetail(topIndexList);
		}catch(Exception ex){
			logger.info(" getBaiduHotSearchWords crashes :"+ex.getMessage());
		}
		return typesTopZhWords;
	}
}
