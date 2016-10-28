package cm.crawler.controllers;

import org.apache.log4j.Logger;

import cm.crawler.commons.ChineseWordsBaiduCrawl;
import cm.redis.commons.ResourcesConfig;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 百度热门搜索词抓取与加入redis库操作类
 * @author chinamobile
 */
public class ChineseWordsBaiduController {
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsBaiduController.class);
	
	public static void getBaiduFengYunKeyWords(){
		String crawlStorageFolder =ResourcesConfig.WEBPAGE_SERVER;
	    int numberOfCrawlers = 7;
	    try {
		    /*
	         * Instantiate the ChineseWordsBaiduController for ChinesesWordsCrawler.
	         */
		    CrawlConfig config = new CrawlConfig();
	        config.setCrawlStorageFolder(crawlStorageFolder);
	        config.setMaxDepthOfCrawling(2);
	        config.setMaxPagesToFetch(100);
	        PageFetcher pageFetcher = new PageFetcher(config);
	        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

	        /*
	         * For each ChinesesWordsCrawler, you need to add some seed urls. These are the first
	         * URLs that are fetched and then the crawler starts following links
	         * which are found in these pages
	         */
	        controller.addSeed("http://top.baidu.com/buzz?b=2&fr=topboards");
	
	        /*
	         * Start the crawl. This is a blocking operation, meaning that your code
	         * will reach the line after this only when crawling is finished.
	         */
	        controller.start(ChineseWordsBaiduCrawl.class, numberOfCrawlers);
	    } catch (Exception ex) {
	    	logger.info(" ChineseWordsBaiduController crashes :"+ex.getMessage());
		}
	}
	
}
