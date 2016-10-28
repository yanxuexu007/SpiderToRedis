package cm.crawler.commons;


import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 20161028 扩展已有的WebCrawler接口，当前类是对带有固定域名前缀的网页进行中文热词的抓取
 * @author chinamobile
 *
 */
public class ChineseWordsBaiduCrawl extends WebCrawler {
	
	//过滤器，用于识别不需要过滤的网络爬虫到的web资源
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");

	 /**
	  * 爬虫的逻辑应该是先种下种子url，也就是起始的url，带有主域名，存放在urlIndexList中，只检索相关的网页即可
	  * 在此过程中，shoulVisit方法用于过滤哪些url不需要采集，哪些需要采集，是否采集有Filter定义并由其中的代码决定
      */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
        //忽略带有资源类型的url
		if(FILTERS.matcher(href).matches())return false;

		return href.startsWith("http://top.baidu.com/");
	}
	
	@Override
	/**
	 * 测试页面
	 */
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
	}
}

//过滤器，用于识别加入的url是否符合域名解析规则
//private final static Pattern URLFILTERS=Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
