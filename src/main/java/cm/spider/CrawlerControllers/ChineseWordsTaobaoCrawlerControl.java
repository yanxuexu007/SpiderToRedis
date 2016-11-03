package cm.spider.CrawlerControllers;

import java.util.Set;
import org.apache.log4j.Logger;

import cm.spider.CrawlerBasis.ChineseWordsTaobaoCrawl;

/**
 * 获取电商所有关键词进行redis缓存，大约每小时更新一次热词字典
 * 时间2016年10月30日，后续业务可能会随着页面做进一步更新
 * @author chinamobile
 */
public class ChineseWordsTaobaoCrawlerControl{
	//用于日志的获取
	public static Logger logger=Logger.getLogger(ChineseWordsTaobaoCrawlerControl.class);

	/**
	 * 封装获取淘宝热搜商品方法
	 * 对应的种子页面是：http://rec.suning.com/show/rank.htm
	 * @return 截获到的关键字
	 */
	public Set<String> getTaobaoHotSearchWords(){
		Set<String> hotSearchWordsList=null;
		ChineseWordsTaobaoCrawl taobaoCrawler=new ChineseWordsTaobaoCrawl();
		try{
			hotSearchWordsList=taobaoCrawler.getTBTodayRankingList("https://top.taobao.com/index.php?topId=HOME"); //种子文件起始地址
			hotSearchWordsList=taobaoCrawler.getTaoBaoHotWords(hotSearchWordsList);
		}catch(Exception ex){
			logger.info(" getTaobaoHotSearchWords crashes :"+ex.getMessage());
		}
		return hotSearchWordsList;
	}
	
	public static void main(String[] args) throws Exception {
		//测试从后台获取淘宝的今日热门关注商品,20161031,ok
		Set<String> topIndexList=null;
		Set<String> hotZhWords=null;
		ChineseWordsTaobaoCrawl taobaoCrawler=new ChineseWordsTaobaoCrawl();
		int recnum=0;
		topIndexList=taobaoCrawler.getTBTodayRankingList("https://top.taobao.com/index.php?topId=HOME"); //种子文件起始地址
		hotZhWords=taobaoCrawler.getTaoBaoHotWords(topIndexList);
		if(hotZhWords!=null&&hotZhWords.size()>0){
			for (String str : hotZhWords) {
				recnum+=1;
				System.out.println("	"+recnum+":	"+str);
			}
		}
	}
}