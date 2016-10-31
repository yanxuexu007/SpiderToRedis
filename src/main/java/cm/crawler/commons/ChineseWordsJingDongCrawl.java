package cm.crawler.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
* 20161031 由于京东页面同样涉及比较复杂的js生成，使用已有的Selenium方法，对淘宝的今日关注上升排行榜进行爬虫
* @author chinamobile
*/
public class ChineseWordsJingDongCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsJingDongCrawl.class);
	//http和https的正则表达式
	private final static Pattern URLFILTER=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	//模拟浏览器客户端变量
	//模拟浏览器客户端变量
	private WebDriver webDriver;	

	/**
	 * 初始化模拟的爬虫浏览器客户端Selenium
	 */
	private void initWebDriver() {
		System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe"); //必须配置chrome的路径
		webDriver=new ChromeDriver(); 
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		//如果是内网则需要配置代理,10.244.155.137 ,"cmproxy.gmcc.net",8081
//		String proxyIpAndPort= "10.244.155.137:8081";
//		DesiredCapabilities cap = new DesiredCapabilities();
//		Proxy proxy=new Proxy();
//		proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
//		cap.setCapability(CapabilityType.PROXY, proxy);	//手动添加代理
//		cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
//		cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
//		System.setProperty("http.nonProxyHosts", "localhost"); //某些不需要代理的配置
	}
	
	/**
	 * 关闭清理模拟的爬虫浏览器客户端
	 */
	private void closeWebDriver(){
		if(webDriver!=null)webDriver.close();
		webDriver=null;
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
	 * ......
	 */
	public List<String> getJDTodayRankingListWords(String href){
		List<String> topWords=null;
		List<WebElement> crawltags=null;		//页面中涉及需要抓取的元素文档对象集合
		WebElement childelement=null;			//页面中对应的元素
		String hotZh=null;							//商品列表的名称
		try{
			initWebDriver();
			topWords=new ArrayList<String>();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取首页页面
		        webDriver.get(href);
		        
		        //京东排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
		        crawltags=webDriver.findElements(By.xpath("//*[@id=\"topSearchListcate9999_1DAY\"]/li/div[1]/a/div[1]/div/p[1]")); //获取热搜产品
		        if(crawltags!=null&&crawltags.size()>0){
	        		for(int i=0;i<crawltags.size();i++){
	        			childelement=(crawltags.get(i));
	        			if(childelement!=null){
	        				hotZh=childelement.getText();
	        				if(hotZh!=null)topWords.add(hotZh);
	        			}
		        	}
	        	}
			}
		}catch(Exception ex){
			logger.info(" getTBTodayRankingList crashes :"+ex.getMessage());
			topWords=null;
		}finally {
			//释放内存
			closeWebDriver();
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotZh=null;				//热搜词类别对应的url
		}
		return topWords;
	}
}
