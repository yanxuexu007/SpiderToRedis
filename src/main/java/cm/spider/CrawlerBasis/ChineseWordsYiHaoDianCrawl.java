package cm.spider.CrawlerBasis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import cm.redis.Commons.ResourcesConfig;

/**
* 20161031 使用已有的Selenium方法，对一号店的今日关注信息进行爬虫
* @author chinamobile
*/
public class ChineseWordsYiHaoDianCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsYiHaoDianCrawl.class);
	//http和https的正则表达式
	private final static Pattern URLFILTER=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	//模拟浏览器客户端变量
	private WebDriver webDriver;	

	/**
	 * 初始化模拟的爬虫浏览器客户端Selenium
	 */
	private void initWebDriver() {
		System.setProperty(ResourcesConfig.BROWSER_DRIVER_NAME, ResourcesConfig.BROWSER_DRIVER_POSITION); //必须配置chromedriver的路径
		webDriver=new ChromeDriver(); 
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		//如果内网需要配置代理，信息是,10.244.155.137 ,"cmproxy.gmcc.net",8081
		//因为selenium模拟了浏览器，所以只要浏览器配置了代理即可，代码中不需要配置
	}
	
	/**
	 * 关闭清理模拟的爬虫浏览器客户端
	 */
	private void closeWebDriver(){
		if(webDriver!=null)webDriver.quit();
		webDriver=null;
	}
	
	/**
	 * 根据排行榜首页url，获取用户热搜的链接
	 * 使用的操作类是Selenium，它的基本原理是模拟浏览器获取文档页面，元素标签等对应的信息，并提供api进行属性获取，适合于快速开发自定义爬虫的工具
	 * @param href 种子首页路径http://www.yhd.com/hotq/
	 * @return 热搜索商品列表
	 */
	/**
	 * 链接获取的页面为http://www.yhd.com/hotq/
	 * 今日关注完整榜单Xpath：//*[@id="main"]/div/ul[2]/li[4]/a
	 * ......
	 */
	public Set<String> getYiHaoDianHotSearchTypes(String href){
		Set<String> topWords=null;
		List<WebElement> crawltags=null;		//页面中涉及需要抓取的元素文档对象集合
		WebElement childelement=null;			//页面中对应的元素
		String hotUrl=null;								//热搜链接
		try{
			initWebDriver();
			topWords=new HashSet<String>();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取首页页面
		        webDriver.get(href);
		        
		        //一号店排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
		        crawltags=webDriver.findElements(By.xpath("//*[@id=\"main\"]/div/ul[2]/li/a")); //获取热搜产品对应的目录
		        if(crawltags!=null&&crawltags.size()>0){
	        		for(int i=0;i<crawltags.size();i++){
	        			childelement=(crawltags.get(i));
	        			hotUrl=childelement.getAttribute("href");
	        			if(hotUrl!=null&&hotUrl.length()>1)topWords.add(hotUrl.trim());
		        	}
	        	}
			}
		}catch(Exception ex){
			logger.info(" getYiHaoDianHotSearchTypes crashes :"+ex.getMessage());
			topWords=null;
		}finally {
			//释放内存
			closeWebDriver();
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotUrl=null;				//热搜链接对应的url
		}
		return topWords;
	}
	
	
	/**
	 * 获取每个链接中对应的热搜商品
	 * 今日关注完整榜单Xpath：//*[@id="main"]/div/ul[3]/li/ul/li/a
	 * 									 //*[@id="main"]/div/ul[3]/li/a
	 * ......
	 */
	public Set<String> getYiHaoDianHotSearchWords(Set<String> hrefs){
		if(hrefs==null||hrefs.size()<=0)return null;
		Set<String> topWords=null;
		List<WebElement> crawltags=null;		//页面中涉及需要抓取的元素文档对象集合
		WebElement childelement=null;			//页面中对应的元素
		String hotZh=null;								//热搜商品
		String page_xpath=null;						//页面上检索对应的xpath
		try{
			initWebDriver();
			topWords=new HashSet<String>();
			for(String href: hrefs){
				if(href.equals("http://www.yhd.com/hotq/")==true)page_xpath="//*[@id=\"main\"]/div/ul[3]/li/ul/li/a";
				else page_xpath="//*[@id=\"main\"]/div/ul[3]/li/a";
				if(href!=null&&URLFILTER.matcher(href).matches()){
			        //获取页面
			        webDriver.get(href);
			        
			        //一号店排行页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
			        //20161031深度定制爬虫逻辑如下：
			        crawltags=webDriver.findElements(By.xpath(page_xpath)); //获取热搜产品
			        if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++){
		        			childelement=(crawltags.get(i));
		        			hotZh=childelement.getText();
		        			if(hotZh!=null&&hotZh.length()>1)topWords.add(hotZh.trim());
			        	}
		        	}
				}
			}
		}catch(Exception ex){
			logger.info(" getYiHaoDianHotSearchWords crashes :"+ex.getMessage());
			topWords=null;
		}finally {
			//释放内存
			closeWebDriver();
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotZh=null;				//热搜商品
			page_xpath=null;		//页面上检索对应的xpath
		}
		return topWords;
	}
}
