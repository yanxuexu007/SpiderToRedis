package cm.crawler.controllers;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

//import org.apache.commons.codec.binary.Base64;
import cm.crawler.commons.ChineseWordsBaiduCrawl;


/**
 * 百度热门搜索词种子url与对应热词链接获取，并调用后台专门的百度热搜数据爬虫设计类，获取所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsBaiduController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduController.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取关键词,20161031,ok
		Map<String, String> topIndexList=null;
		Map<String,  Set<String>> typesTopZhWords=null;
		Set<String> hotZhWords=null;
		ChineseWordsBaiduCrawl baiduCrawler=new ChineseWordsBaiduCrawl();
		int recnum=0;
		topIndexList=baiduCrawler.getBDHotZhTypesAndLinks("http://top.baidu.com/boards?fr=topindex"); //种子文件起始地址
		typesTopZhWords=baiduCrawler.getBDHotZhDetail(topIndexList);
		if(typesTopZhWords!=null&&typesTopZhWords.size()>0){
			for (String key : typesTopZhWords.keySet()){
				System.out.println(key+": ");
				hotZhWords=typesTopZhWords.get(key);
				recnum=0;
				for (String str : hotZhWords) {
					  recnum+=1;
				      System.out.println("	"+recnum+":	"+str);
				}
			}
		}
	}
}
