package cm.crawler.controllers;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

//import org.apache.commons.codec.binary.Base64;
//import cm.crawler.commons.ChineseWordsTaobaoCrawl;

/**
 * 百度热门搜索词种子url与对应热词链接获取，并调用后台专门的百度热搜数据爬虫设计类，获取所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsTaobaoController implements PageProcessor {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsTaobaoController.class);

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://g\\.com/\\w+/\\w+)").all());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
        page.putField("readme", page.getHtml().xpath("//*[@id=\"bang-wbang\"]/div/div/div/ul/li/div/div[2]/div/a").toString());
    }

    @Override
    public Site getSite() {
    	site.setHttpProxy(new HttpHost("10.244.155.137",8081));
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ChineseWordsTaobaoController()).addUrl("http://top.taobao.com/index.php?spm=a1z5i.1.7.11.IofwAd&rank=focus&type=up&from=&s=80#ToSwitch").thread(5).run();
    }

//	public static void main(String[] args) throws Exception {
		//测试从后台获取淘宝的今日热门关注商品,20161031,ok
//		List<String> topIndexList=null;
//		Set<String> hotZhWords=null;
//		ChineseWordsTaobaoCrawl taobaoCrawler=new ChineseWordsTaobaoCrawl();
//		int recnum=0;
////		topIndexList=taobaoCrawler.getTBTodayRankingList("http://top.taobao.com/index.php?topId=HOME"); //种子文件起始地址
////		//测试获取url列表的代码段
////		if(topIndexList!=null&&topIndexList.size()>0){
////			for(String url : topIndexList){
////				recnum+=1;
////				System.out.println("	"+recnum+":	"+url);
////			}
////		}
//		topIndexList=new ArrayList<String>();
//		topIndexList.add("http://top.taobao.com/index.php?spm=a1z5i.1.7.11.IofwAd&rank=focus&type=up&from=&s=80#ToSwitch");
//		hotZhWords=taobaoCrawler.getTBHotProductsDetail(topIndexList);
//		if(hotZhWords!=null&&hotZhWords.size()>0){
//			for (String str : hotZhWords) {
//				recnum+=1;
//				System.out.println("	"+recnum+":	"+str);
//			}
//		}
//	}
}

