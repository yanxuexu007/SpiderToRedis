package cm.crawler.commons;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
* 20161028 使用已有的htmlunit接口方法，对百度带有固定域名前缀的网页进行中文热词的抓取，百度网页较为简单的html静态页，直接htmlunit解析即可
* @author chinamobile
*/
public class ChineseWordsBaiduCrawl  {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduCrawl.class);
	//http和https的正则表达式
	private final static Pattern URLFILTER=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
	//模拟浏览器客户端变量
	private WebClient webClient;	
	
	/**
	 * 初始化模拟的爬虫浏览器客户端
	 */
	private void initWebClient() {
		webClient=new WebClient(BrowserVersion.CHROME); //如果是内网则需要配置代理,10.244.155.137 ,"cmproxy.gmcc.net",8081
		//htmlunit 对css和javascript的支持不好，请关闭
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDoNotTrackEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);	//支持https
        webClient.getOptions().setTimeout(30000); 		  	//设置连接超时时间30S。如果为0，则无限期等待
	}
	
	/**
	 * 关闭清理模拟的爬虫浏览器客户端
	 */
	private void closeWebClient(){
		if(webClient!=null)webClient.close();
		webClient=null;
	}
	
	/**
	 * 根据热搜类别对应的url，截取url中对应的中文热词排行榜中的所有中文热词，并保存对应的url
	 * 使用的操作类是HtmlUnit，它的基本原理是模拟浏览器获取文档页面，元素标签等对应的信息，并提供api进行属性获取，适合于快速开发自定义爬虫的工具
	 * @param href 热搜类别对应的url,包含所需的中文热词
	 * @return
	 */
	/**
	 * 所有的热词类别所在页面为http://top.baidu.com/boards?fr=topindex，类别与链接均包含在div id为main的层中
	 * 分析页面获取的Xpath为//*[@class=\"item-list\"]/li/div/a[1]
	 *  ...
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getBDHotZhTypesAndLinks(String href){
		Map<String, String> allTypesAndLinks=null;
		HtmlPage page=null;						//页面对象
		DomElement childelement=null;		//存放子节点对象
		List<DomElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
		String hotUrl=null;						//热搜词类别对应的url
		String hotZh=null;   						//热搜词类别
		int pos=0;									//截取url字段位置标识
		try{
			initWebClient();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取页面
		        page = webClient.getPage(href);
		        
		        //百度热搜页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161030深度定制爬虫逻辑如下：
		        if(page!=null){
		        	allTypesAndLinks=new HashMap<String, String>();
		        	crawltags=(List<DomElement>)page.getByXPath("//*[@class=\"all-list\"]/div[1]/h3/a"); //获取main中所有的h3标签下的a内容
		        	if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++){
		        			childelement=(crawltags.get(i));
		        			if(childelement!=null){
		        				//截取中文大类别
			        			hotZh=childelement.asText();
			        			if(hotZh!=null){
			        				hotZh=hotZh.replaceAll("[\\s\b\r\n\t]*", "");//去除多余的空格
			        				//hotZh=Base64.encodeBase64URLSafeString(hotZh.getBytes("UTF-8"));//转换为BASE64增强型编码
			        			}
			        			//截取中文大类别对应的url
			        			hotUrl=childelement.getAttribute("href");
			        			if(hotUrl!=null&&hotUrl.contains("category")==true){
			        				pos=hotUrl.indexOf("category");
			        				hotUrl=hotUrl.substring(pos);
				        			hotUrl="http://top.baidu.com/"+hotUrl;
			        			}
			        			if(hotZh!=null&&hotUrl!=null&&hotZh.length()>1&&hotUrl.startsWith("http://top.baidu.com/category?c=")==true){	
			        				allTypesAndLinks.put(hotZh, hotUrl);	
			        				//System.out.println(hotZh+" : "+hotUrl); //测试代码段
			        			}
		        			}
			        	}
		        	}
		        }
			}
		}catch(Exception ex){
			logger.info(" getBDHotZhTypesAndLinks crashes :"+ex.getMessage());
			allTypesAndLinks=null;
		}finally {
			//释放内存
			if(page!=null)page.cleanUp();
			closeWebClient();
			page=null;
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotUrl=null;				//热搜词类别对应的url
			hotZh=null;   			//热搜词类别
			pos=0;						//截取url字段位置标识
		}
		return allTypesAndLinks;
	}
	
	/**
	 * 根据从http://top.baidu.com/boards?fr=topindex种子页面获取的类别与url哈希信息，统计url中包含的热词，返回
	 * 由于百度的每个热搜词类别的统计页面，都是相同的生成格式，具体参考注释，因此统一在getBDHotZhDetail方法中执行
	 * 目前只统计大类别下每个小分类的排名前10的热词，20161031
	 * @param ZhTypesLinks 从getBDHotZhTypesAndLinks方法中生成的类别
	 * @return 返回大类别与对应热词集合构成的哈希信息结构
	 */
	/**
	 * 分析页面获取的Xpath为//*[@class=\"all-list\"]/div[1]/h3/a
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Set<String>> getBDHotZhDetail(Map<String, String> ZhTypesLinks){
		if(ZhTypesLinks==null||ZhTypesLinks.size()<=0)return null;
		Map<String,  Set<String>> tYpeAndZhWords=null;
		String hotZh=null;   						//热搜词顶级类别
		String hotUrl=null;							//热搜类别对应的页面url
		HtmlPage page=null;						//页面对象
		List<DomElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
		String ZhWord=null;						//标签中的热词
		Set<String> ZhWords=null;			//热搜词集合，无重复
		try{
			tYpeAndZhWords=new HashMap<String,  Set<String>>();
			initWebClient();
	        for (String key : ZhTypesLinks.keySet()){
	        	hotZh=key;
	        	ZhWords=new HashSet<String>();
	        	//获取关键词对应的url
	        	hotUrl= ZhTypesLinks.get(key);  
	        	//获取url下载到的页面
	        	if(hotUrl!=null&&URLFILTER.matcher(hotUrl).matches()){
			        //获取页面
					page = webClient.getPage(hotUrl);

			        //百度热搜页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
			        //20161030深度定制爬虫逻辑如下：
			        if(page!=null){
			        	crawltags=(List<DomElement>)page.getByXPath("//*[@class=\"item-list\"]/li/div/a[1]"); 		//获取main中所有的ul标签
			        	if(crawltags!=null&&crawltags.size()>0){
			        		for(int i=0;i<crawltags.size();i++)
			        		{	
			        			ZhWord=crawltags.get(i).getAttribute("title");	//后台已经完成将网页的gb2312转成了GBK，并且将GBK转成了UTF-8
			        			//System.out.println(i+": "+ZhWord); 				//测试代码段测试ok，20161031
			        			ZhWords.add(ZhWord);
			        		}
			        		if(ZhWords!=null&&ZhWords.size()>0)tYpeAndZhWords.put(hotZh, ZhWords);
			        	}
			        }
	        	}
		        ZhWords=null;
		        if(page!=null)page.cleanUp();
	        }
		}catch(Exception ex){
			logger.info(" getBDHotZhDetail crashes :"+ex.getMessage());
			tYpeAndZhWords= null;
		}finally {
			//释放内存
			if(page!=null)page.cleanUp();
			closeWebClient();
			hotZh=null;   			//热搜词顶级类别
			hotUrl=null;				//热搜类别对应的页面url
			page=null;					//页面对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			ZhWord=null;			//标签中的热词
			ZhWords=null;			//热搜词集合，无重复
		}
		return tYpeAndZhWords;
	}
}


