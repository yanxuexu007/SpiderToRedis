package cm.crawler.controllers;

import java.util.Map;

import org.apache.log4j.Logger;

import cm.crawler.commons.ChineseWordsBaiduCrawl;


/**
 * 百度热门搜索词种子url与对应热词链接获取，并调用后台专门的百度热搜数据爬虫设计类，获取所有关键词进行保存
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsBaiduController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduController.class);
	
	public static void main(String[] args) throws Exception {
		Map<String, String> topIndexList=null;
		ChineseWordsBaiduCrawl baiduCrawler=new ChineseWordsBaiduCrawl();
		topIndexList=baiduCrawler.getBDHotZhTypesAndLinks("http://top.baidu.com/boards?fr=topindex"); //种子文件起始地址
		baiduCrawler.getBDHotZhDetail(topIndexList);
	}
}
