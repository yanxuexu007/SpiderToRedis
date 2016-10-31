package cm.crawler.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

//import org.apache.commons.codec.binary.Base64;
import cm.crawler.commons.ChineseWordsTaobaoCrawl;

/**
 * 百度热门搜索词种子url与对应热词链接获取，并调用后台专门的百度热搜数据爬虫设计类，获取所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsTaobaoController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsTaobaoController.class);
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取淘宝的今日热门关注商品,20161031,ok
		List<String> topIndexList=null;
		Set<String> hotZhWords=null;
		ChineseWordsTaobaoCrawl taobaoCrawler=new ChineseWordsTaobaoCrawl();
		int recnum=0;
//		topIndexList=taobaoCrawler.getTBTodayRankingList("http://top.taobao.com/index.php?topId=HOME"); //种子文件起始地址
//		//测试获取url列表的代码段
//		if(topIndexList!=null&&topIndexList.size()>0){
//			for(String url : topIndexList){
//				recnum+=1;
//				System.out.println("	"+recnum+":	"+url);
//			}
//		}
		topIndexList=new ArrayList<String>();
		topIndexList.add("http://top.taobao.com/index.php?spm=a1z5i.1.7.11.IofwAd&rank=focus&type=up&from=&s=80#ToSwitch");
		hotZhWords=taobaoCrawler.getTBHotProductsDetail(topIndexList);
		if(hotZhWords!=null&&hotZhWords.size()>0){
			for (String str : hotZhWords) {
				recnum+=1;
				System.out.println("	"+recnum+":	"+str);
			}
		}
	}
}

