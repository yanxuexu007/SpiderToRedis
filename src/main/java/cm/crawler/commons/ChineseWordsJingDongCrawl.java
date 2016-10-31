package cm.crawler.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ChineseWordsJingDongCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsJingDongCrawl.class);
	//http和https的正则表达式
	private final static Pattern URLFILTER=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	//模拟浏览器客户端变量
	private WebClient webClient;	
	
	/**
	 * 初始化模拟的爬虫浏览器客户端
	 */
	private void initWebClient() {
		webClient=new WebClient(BrowserVersion.CHROME,"cmproxy.gmcc.net",8081); //如果是内网则需要配置代理,10.244.155.137 
		//htmlunit 淘宝页面需要使用js生成，需要配置js enable及相关参数，具体如下
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDoNotTrackEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);		//支持https
        webClient.getOptions().setTimeout(60000); 		  		//设置连接超时时间30S。如果为0，则无限期等待
        webClient.waitForBackgroundJavaScript(60000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setJavaScriptTimeout(60000);
	}
	
	/**
	 * 关闭清理模拟的爬虫浏览器客户端
	 */
	private void closeWebClient(){
		if(webClient!=null)webClient.close();
		webClient=null;
	}
	
	/**
	 * 根据排行榜首页url，获取今日关注的完整榜单热搜词l信息
	 * 使用的操作类是HtmlUnit，它的基本原理是模拟浏览器获取文档页面，元素标签等对应的信息，并提供api进行属性获取，适合于快速开发自定义爬虫的工具
	 * @param href 种子首页路径https://top.jd.com/#search
	 * @return 前5页的url列表
	 */
	/**
	 * 今日关注完整榜单链接获取的页面为https://top.jd.com/#search，
	 * 对应热词可以通过页面元素分析，直接Copy Xpath获得：
	 * 今日关注完整榜单Xpath：//*[@id="topSearchListcate9999_1DAY"]/li/div[1]/a/div[1]/div/p[1]
	 */
	@SuppressWarnings("unchecked")
	public List<String> getJDTodayRankingListWords(String href){
		List<String> topWords=null;
		HtmlPage page=null;						//页面对象
		DomElement childelement=null;		//存放子节点对象
		List<DomElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
		String hotZh=null;							//商品列表的页面url
		try{
			initWebClient();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取首页页面
		        page = webClient.getPage(href);
		        Thread.sleep(10000);//主要是这个线程的等待 因为js加载也是需要时间的
		        
		        //京东排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
		        if(page!=null){
		        	topWords=new ArrayList<String>();
		        	crawltags=(List<DomElement>)page.getByXPath("//*[@id=\"topSearchListcate9999_1DAY\"]/li/div[1]/a/div[1]/div/p[1]"); //获取热搜产品
		        	if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++){
		        			childelement=(crawltags.get(i));
		        			if(childelement!=null){
		        				hotZh=childelement.asText();
		        				if(hotZh!=null)topWords.add(hotZh);
		        			}
			        	}
		        	}
		        }
			}
		}catch(Exception ex){
			logger.info(" getTBTodayRankingList crashes :"+ex.getMessage());
			topWords=null;
		}finally {
			//释放内存
			if(page!=null)page.cleanUp();
			closeWebClient();
			page=null;
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotZh=null;				//热搜词类别对应的url
		}
		return topWords;
	}

}
