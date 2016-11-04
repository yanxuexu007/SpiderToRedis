package cm.spider.CrawlerBasis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import cm.redis.Commons.ResourcesConfig;

/**
* 20161031 由于淘宝页面涉及比较复杂的js生成，使用已有的Selenium方法，对淘宝的今日关注上升排行榜进行爬虫
* @author chinamobile
*/
public class ChineseWordsTaoBaoCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsTaoBaoCrawl.class);
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
	 * 根据排行榜首页url，获取完整榜单的url路径，进入之后，存放后4页的url链接，当前获取今日关注的完整榜单前5页的url信息
	 * 使用的操作类是HtmlUnit，它的基本原理是模拟浏览器获取文档页面，元素标签等对应的信息，并提供api进行属性获取，适合于快速开发自定义爬虫的工具
	 * @param href 种子首页路径http://top.taobao.com/index.php?topId=HOME
	 * @return 前5页的url列表
	 */
	/**
	 * 今日关注完整榜单链接获取的页面为http://top.taobao.com/index.php?topId=HOME，完整榜单链接包含在div class为中
	 * 对应可以通过页面元素分析，直接Copy Xpath获得：
	 * 今日关注完整榜单Xpath：//*[@id="bang-tubang"]/div/div[1]/div[2]/div[2]/div[2]/a
	 * 后续页面的Xpath：//*[@id="bang-pager"]/div/div/div/ul/li/a
	 */
	public Set<String> getTBTodayRankingList(String href){
		Set<String> topPagesAndLinks=null;
		List<WebElement> crawltags=null;		//页面中涉及需要抓取的元素文档对象集合
		WebElement childelement=null;			//页面中对应的元素
		String hotUrl=null;								//商品列表的页面url
		int pos=0;											//截取url字段位置标识
		try{
			initWebDriver();
			topPagesAndLinks=new HashSet<String>();
			if(href!=null&&URLFILTER.matcher(href).matches()){
				//获取首页页面
				webDriver.get(href);
				//淘宝排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
				childelement = webDriver.findElement(By.xpath("//*[@id=\"bang-tubang\"]/div/div[1]/div[2]/div[2]/div[2]/a"));
    			if(childelement!=null){
    				hotUrl=childelement.getAttribute("href");
    				if(hotUrl!=null)pos=hotUrl.trim().indexOf("rank");
    				if(pos>0){
    					hotUrl=hotUrl.substring(pos);	//获取完整榜单的相对路径
    					hotUrl="http://top.taobao.com/index.php?"+hotUrl; //完整榜单地址就是今日关注榜的第一页
    					topPagesAndLinks.add(hotUrl);
    				}
    			}
    			
		        if(hotUrl!=null&&URLFILTER.matcher(hotUrl).matches()){
		        	//获取完整榜单的页面
		        	webDriver.get(hotUrl);
		        	crawltags = webDriver.findElements(By.xpath("//*[@id=\"bang-pager\"]/div/div/div/ul/li/a"));
		        	if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++){//从第2页开始才有a元素，能够获取所有li对应的标签信息
		        			childelement=(crawltags.get(i));
		        			if(childelement!=null){
		        				childelement=(crawltags.get(i));
		        				if(childelement.getAttribute("class").equals("num")==true){
		        					hotUrl=childelement.getAttribute("href");
			        				if(hotUrl!=null)pos=hotUrl.trim().indexOf("spm");
			        				if(pos>0){
			        					hotUrl=hotUrl.substring(pos);	//获取完整榜单的相对路径
			        					hotUrl="https://top.taobao.com/index.php?"+hotUrl; //完整榜单地址就是今日关注榜的第一页
			        					topPagesAndLinks.add(hotUrl);
			        				}
		        				}
		        			}
			        	}
			        }
		        }
		    }
		}catch(Exception ex){
			logger.info(" getTBTodayRankingList crashes :"+ex.getMessage());
			topPagesAndLinks=null;
		}finally {
			//释放内存
			closeWebDriver();
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotUrl=null;				//热搜词类别对应的url
			pos=0;						//截取url字段位置标识
		}
		return topPagesAndLinks;
	}

	/**
	 * 根据从http://top.taobao.com/index.php?topId=HOME种子页面获取url信息，统计url中包含的全部热词集合，返回
	 * 具体参考注释，因此统一在getTBHotProductsDetail方法中执行
	 * 目前只统计每天关注热点商品的前五页的全部内容，20161031
	 * @param topPagesAndLinks 从getTBTodayRankingList方法中生成的页面url
	 * @return 返回大类别与对应热词集合构成的哈希信息结构
	 */
	/**
	 * 对应页面的Xpath：//*[@id="bang-wbang"]/div/div/div/ul/li/div/div[2]/div/a
	 *  ......
	 */
	public TreeSet<String> getTaoBaoHotWords(Set<String> topPagesAndLinks){
		if(topPagesAndLinks==null||topPagesAndLinks.size()<=0)return null;
		TreeSet<String> hotProductsWords=null;
		String hotUrl=null;							//商品列表对应的页面url
		List<WebElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
		String ZhWord=null;						//标签中的热词
		try{
			initWebDriver();
			hotProductsWords=new TreeSet<String>();
	        for (String url : topPagesAndLinks){
	        	hotUrl=url;
	        	//获取url下载到的页面
	        	if(hotUrl!=null&&URLFILTER.matcher(hotUrl).matches()){
			        //获取页面
					webDriver.get(hotUrl);
			        //淘宝今日关注热门商品页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
			        //20161031深度定制爬虫逻辑如下：
			        crawltags=webDriver.findElements(By.xpath("//*[@id=\"bang-wbang\"]/div/div/div/ul/li/div/div[2]/div/a")); 		//获取main中所有的ul标签
		        	if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++)
		        		{	
		        			ZhWord=crawltags.get(i).getText();	
		        			if(ZhWord!=null&&ZhWord.length()>1)hotProductsWords.add(ZhWord);
		        		}
			        }
	        	}
	        }
		}catch(Exception ex){
			logger.info(" getTaoBaoHotWords crashes :"+ex.getMessage());
			hotProductsWords= null;
		}finally {
			//释放内存
			closeWebDriver();
			hotUrl=null;					//商品列表对应的页面url
			crawltags=null;				//页面中涉及需要抓取的元素文档对象集合
			ZhWord=null;				//标签中的热词
		}
		return hotProductsWords;
	} 
}
