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

/**
* 20161031 使用已有的htmlunit接口方法，对淘宝的今日关注上升排行榜进行爬虫
* @author chinamobile
*/
public class ChineseWordsTaobaoCrawl {
	//日志记录
	public static Logger logger=Logger.getLogger(ChineseWordsTaobaoCrawl.class);
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
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDoNotTrackEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);		//支持https
        webClient.getOptions().setTimeout(30000); 		  		//设置连接超时时间30S。如果为0，则无限期等待
        webClient.waitForBackgroundJavaScript(60*1000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setJavaScriptTimeout(30000);
	}
	
	/**
	 * 关闭清理模拟的爬虫浏览器客户端
	 */
	private void closeWebClient(){
		if(webClient!=null)webClient.close();
		webClient=null;
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
	@SuppressWarnings("unchecked")
	public List<String> getTBTodayRankingList(String href){
		List<String> topPagesAndLinks=null;
		HtmlPage page=null;						//页面对象
		DomElement childelement=null;		//存放子节点对象
		List<DomElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
		String hotUrl=null;							//商品列表的页面url
		int pos=0;										//截取url字段位置标识
		try{
			initWebClient();
			if(href!=null&&URLFILTER.matcher(href).matches()){
		        //获取首页页面
		        page = webClient.getPage(href);
		        
		        //淘宝排行首页页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
		        //20161031深度定制爬虫逻辑如下：
		        if(page!=null){
		        	topPagesAndLinks=new ArrayList<String>();
		        	//测试代码开始
//		        	hotUrl=page.asXml();					
//		        	topPagesAndLinks.add(hotUrl);
		        	//测试代码结束
		        	crawltags=(List<DomElement>)page.getByXPath("//*[@id=\"bang-tubang\"]/div/div[1]/div[2]/div[2]/div[2]/a"); //获取main中所有的h3标签下的a内容
		        	if(crawltags!=null&&crawltags.size()>0){
		        		for(int i=0;i<crawltags.size();i++){
		        			childelement=(crawltags.get(i));
		        			if(childelement!=null){
		        				hotUrl=childelement.getAttribute("href");
		        				if(hotUrl!=null)pos=hotUrl.trim().indexOf("rank");
		        				if(pos>0){
		        					hotUrl=hotUrl.substring(pos);	//获取完整榜单的相对路径
		        					hotUrl="http://top.taobao.com/index.php?"+hotUrl; //完整榜单地址就是今日关注榜的第一页
		        					topPagesAndLinks.add(hotUrl);
		        				}
		        			}
			        	}
		        	}
		        	if(page!=null)page.cleanUp();
		        	page=null;
		        	
			        if(hotUrl!=null&&URLFILTER.matcher(hotUrl).matches()){
			        	//获取完整榜单的页面
			        	page = webClient.getPage(hotUrl);
			        	if(page!=null){
			        		crawltags=(List<DomElement>)page.getByXPath("//*[@id=\"bang-pager\"]/div/div/div/ul/li/a");
			        		for(int i=0;i<crawltags.size();i++){//从第2页开始才有a元素，能够获取所有li对应的标签信息
			        			childelement=(crawltags.get(i));
			        			if(childelement!=null){
			        				childelement=(crawltags.get(i));
			        				if(childelement.getAttribute("class").equals("num")==true){
			        					hotUrl=childelement.getAttribute("href");
				        				if(hotUrl!=null)pos=hotUrl.trim().indexOf("spm");
				        				if(pos>0){
				        					hotUrl=hotUrl.substring(pos);	//获取完整榜单的相对路径
				        					hotUrl="http://top.taobao.com/index.php?"+hotUrl; //完整榜单地址就是今日关注榜的第一页
				        					topPagesAndLinks.add(hotUrl);
				        				}
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
			if(page!=null)page.cleanUp();
			closeWebClient();
			page=null;
			childelement=null;		//存放子节点对象
			crawltags=null;			//div中涉及需要抓取的元素集合
			hotUrl=null;				//热搜词类别对应的url
			pos=0;						//截取url字段位置标识
		}
		return topPagesAndLinks;
	}

////截取中文大类别
//hotZh=childelement.asText();
//if(hotZh!=null){
//	hotZh=hotZh.replaceAll("[\\s\b\r\n\t]*", "");//去除多余的空格
//	//hotZh=Base64.encodeBase64URLSafeString(hotZh.getBytes("UTF-8"));//转换为BASE64增强型编码
//}
////截取中文大类别对应的url
//hotUrl=childelement.getAttribute("href");
//if(hotUrl!=null&&hotUrl.contains("category")==true){
//	pos=hotUrl.indexOf("category");
//	hotUrl=hotUrl.substring(pos);
//	hotUrl="http://top.baidu.com/"+hotUrl;
//}
//if(hotZh!=null&&hotUrl!=null&&hotZh.length()>1&&hotUrl.startsWith("http://top.baidu.com/category?c=")==true){	
//	allTypesAndLinks.put(hotZh, hotUrl);	
//	//System.out.println(hotZh+" : "+hotUrl); //测试代码段
	
	/**
	 * 根据从http://top.baidu.com/boards?fr=topindex种子页面获取的类别与url哈希信息，统计url中包含的热词，返回
	 * 由于百度的每个热搜词类别的统计页面，都是相同的生成格式，具体参考注释，因此统一在getBDHotZhDetail方法中执行
	 * 目前只统计大类别下每个小分类的排名前10的热词，20161031
	 * @param ZhTypesLinks 从getBDHotZhTypesAndLinks方法中生成的类别
	 * @return 返回大类别与对应热词集合构成的哈希信息结构
	 */
	/**
	 * <ul class="item-list">
	 * 		<li class="current">
	 * 			<div class="item-hd">
	 * 				<span class="num-top">1<//span>
	 * 				<a target="_blank" title="跨界喜剧王" data="2|2" class="list-title" href="http://www.baidu.com/baidu?cl=3&tn=SE_baiduhomet8_jmjb7mjw&fr=top1000&wd=%BF%E7%BD%E7%CF%B2%BE%E7%CD%F5" href_top="./detail?b=2&w=%BF%E7%BD%E7%CF%B2%BE%E7%CD%F5&c=12">跨界喜剧王<//a>
	 * 				<span class="icon-rise">916602<//span>
	 * 				<a href="./detail?b=2&w=%BF%E7%BD%E7%CF%B2%BE%E7%CD%F5&c=12" class="icon-search icon-xiang-imp" target="_blank">search<//a>
	 * 			<//div>
	 * 		<div class="item-bd" style="display: block; height: 122px;"><//div>
	 * 		<//li>
	 * 		<li>
	 * 			<div class="item-hd">
	 * 				<span class="num-top">2<//span>
	 * 				<a target="_blank" title="斗鱼tv" data="2|2" class="list-title" href="http://www.baidu.com/baidu?cl=3&tn=SE_baiduhomet8_jmjb7mjw&fr=top1000&wd=%B6%B7%D3%E3tv" href_top="./detail?b=2&w=%B6%B7%D3%E3tv&c=12">斗鱼tv<//a>
	 * 				<span class="icon-fall">575693</span>
	 * 				<a href="./detail?b=2&w=%B6%B7%D3%E3tv&c=12" class="icon-search icon-xiang-imp" target="_blank">search<//a>
	 * 			<//div>
	 * 			<div class="item-bd" style="display:none;"><//div>
	 * 		<//li>
	 *  ......
	 */
//	@SuppressWarnings("unchecked")
//	public Map<String, Set<String>> getBDHotZhDetail(Map<String, String> ZhTypesLinks){
//		if(ZhTypesLinks==null||ZhTypesLinks.size()<=0)return null;
//		Map<String,  Set<String>> tYpeAndZhWords=null;
//		String hotZh=null;   						//热搜词顶级类别
//		String hotUrl=null;							//热搜类别对应的页面url
//		HtmlPage page=null;						//页面对象
//		List<DomElement> crawltags=null;	//页面中涉及需要抓取的元素文档对象集合
//		String ZhWord=null;						//标签中的热词
//		Set<String> ZhWords=null;			//热搜词集合，无重复
//		try{
//			tYpeAndZhWords=new HashMap<String,  Set<String>>();
//			initWebClient();
//	        for (String key : ZhTypesLinks.keySet()){
//	        	hotZh=key;
//	        	ZhWords=new HashSet<String>();
//	        	//获取关键词对应的url
//	        	hotUrl= ZhTypesLinks.get(key);  
//	        	//获取url下载到的页面
//	        	if(hotUrl!=null&&URLFILTER.matcher(hotUrl).matches()){
//			        //获取页面
//					page = webClient.getPage(hotUrl);
//
//			        //百度热搜页面规律分析，详见本方法中有关页面的注释说明，以下代码针对页面分析之后做的开发，页面发生变化，则代码需要修改
//			        //20161030深度定制爬虫逻辑如下：
//			        if(page!=null){
//			        	crawltags=(List<DomElement>)page.getByXPath("//*[@class=\"item-list\"]/li/div/a[1]"); 		//获取main中所有的ul标签
//			        	if(crawltags!=null&&crawltags.size()>0){
//			        		for(int i=0;i<crawltags.size();i++)
//			        		{	
//			        			ZhWord=crawltags.get(i).getAttribute("title");	//后台已经完成将网页的gb2312转成了GBK，并且将GBK转成了UTF-8
//			        			//System.out.println(i+": "+ZhWord); 				//测试代码段测试ok，20161031
//			        			ZhWords.add(ZhWord);
//			        		}
//			        		if(ZhWords!=null&&ZhWords.size()>0)tYpeAndZhWords.put(hotZh, ZhWords);
//			        	}
//			        }
//	        	}
//		        ZhWords=null;
//		        if(page!=null)page.cleanUp();
//	        }
//		}catch(Exception ex){
//			logger.info(" getBDHotZhDetail crashes :"+ex.getMessage());
//			tYpeAndZhWords= null;
//		}finally {
//			//释放内存
//			if(page!=null)page.cleanUp();
//			closeWebClient();
//			hotZh=null;   			//热搜词顶级类别
//			hotUrl=null;				//热搜类别对应的页面url
//			page=null;					//页面对象
//			crawltags=null;			//div中涉及需要抓取的元素集合
//			ZhWord=null;			//标签中的热词
//			ZhWords=null;			//热搜词集合，无重复
//		}
//		return tYpeAndZhWords;
//	} 
}
