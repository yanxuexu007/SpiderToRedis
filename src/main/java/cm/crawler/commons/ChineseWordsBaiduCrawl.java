package cm.crawler.commons;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
* 20161028 使用已有的htmlunit接口方法，对百度带有固定域名前缀的网页进行中文热词的抓取
* @author chinamobile
*
*/
public class ChineseWordsBaiduCrawl  { //extends WebCrawler
	//
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduCrawl.class);
	
	private final static Pattern URLFILTER=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	
	/**
	 * 根据热搜类别对应的url，截取url中对应的中文热词排行榜中的所有中文热词，并保存在url中
	 * @param href 热搜类别对应的url,包含所需的中文热词
	 * @return
	 */
	public List<String> getBaiduHotSearchZhWords(String href){
		List<String> searchZhWords=null;
		WebClient  webClient=null;
		HtmlPage page=null;
		try{
			webClient=new WebClient(BrowserVersion.CHROME);
			if(href!=null&&URLFILTER.matcher(href).matches()){
				//htmlunit 对css和javascript的支持不好，请关闭
		        webClient.getOptions().setJavaScriptEnabled(false);
		        webClient.getOptions().setCssEnabled(false);
		        webClient.getOptions().setDoNotTrackEnabled(true);
		        webClient.getOptions().setUseInsecureSSL(true);	//支持https
		        webClient.getOptions().setTimeout(30000); 		  	//设置连接超时时间30S。如果为0，则无限期等待
		        
		        //获取页面
		        page = webClient.getPage(href);
		        
		        //百度热搜页面规律分析：
		        /**
		         * 
		         */
		        
			}
			
			if(webClient!=null)webClient.close();
	        
		}catch(Exception ex){
			logger.info(" getBaiduHotSearchZhWords crashes :"+ex.getMessage());
			if(webClient!=null)webClient.close();
			webClient=null;
			page=null;
			return null;
		}
		webClient=null;
		page=null;
		return searchZhWords;
	}
}
