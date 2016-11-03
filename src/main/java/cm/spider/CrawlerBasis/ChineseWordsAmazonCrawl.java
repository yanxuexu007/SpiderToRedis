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
* 20161102 使用已有的Selenium方法，对亚马逊每小时各类别的热搜商品进行爬虫获取
* @author chinamobile
*/
public class ChineseWordsAmazonCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsAmazonCrawl.class);
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
	 * 根据排行榜首页url，获取今日关注的完整榜单热搜词l信息
	 * 使用的操作类是Selenium，它的基本原理是模拟浏览器获取文档页面，元素标签等对应的信息，并提供api进行属性获取，适合于快速开发自定义爬虫的工具
	 * @param href 种子首页路径https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn
	 * @return 热搜商品列表
	 */
	/**
	 * 今日关注完整榜单链接获取的页面为
	 * https://www.amazon.cn/gp/bestsellers/，
	 * https://www.amazon.cn/gp/site-directory/ref=nav_shopall_btn
	 * 
	 * 对应热词可以通过页面元素分析，直接Copy Xpath获得：
	 * 榜单热搜词Xpath：//*[@id="siteDirectory"]/div/div/div[2]/div/div/div/div/ul/li/span/span/a
	 * ......
	 */
	public Set<String> getAmazonHourWords(String href){
		Set<String> topWords=null;
		List<WebElement> crawltags=null;		//页面中涉及需要抓取的元素文档对象集合
		WebElement childelement=null;			//页面中对应的元素
		String hotZh=null;								//商品列表的名称
		try{
			initWebDriver();
			topWords=new HashSet<String>();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取首页页面s
		        webDriver.get(href);
		        
		        //亚马逊排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
		        crawltags=webDriver.findElements(By.xpath("//*[@id=\"siteDirectory\"]/div/div/div[2]/div/div/div/div/ul/li/span/span/a")); //获取热搜产品
		        if(crawltags!=null&&crawltags.size()>0){
	        		for(int i=0;i<crawltags.size();i++){
	        			childelement=(crawltags.get(i));
	        			if(childelement!=null){
	        				hotZh=childelement.getText();
	        				if(hotZh!=null&&hotZh.length()>1)topWords.add(hotZh.trim());
	        			}
		        	}
	        	}
			}
		}catch(Exception ex){
			logger.info(" getAmazonHourWords crashes :"+ex.getMessage());
			topWords=null;
		}finally {
			//释放内存
			closeWebDriver();
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotZh=null;				//商品列表的名称
		}
		return topWords;
	}
}
