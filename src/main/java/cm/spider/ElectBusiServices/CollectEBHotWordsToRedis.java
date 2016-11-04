package cm.spider.ElectBusiServices;

import java.util.List;
import java.util.TreeSet;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import cm.spider.CrawlerControllers.ChineseWordsAmazonCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsJingDongCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsSuNingYiGouCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsTaoBaoCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsVIPCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsYiHaoDianCrawlController;


/**
 * 服务层代码，按照业务需求调用控制器获取热词信息
 * 对电商的热搜词进行爬虫检索，每小时执行一次
 * @author yanxu
 *
 */
public class CollectEBHotWordsToRedis {
	//电商对应的爬虫机器人
	private ChineseWordsAmazonCrawlController amazonCrawlerControl;
	private ChineseWordsVIPCrawlController vipCrawlerControl;
	private ChineseWordsJingDongCrawlController jingdongCrawlerControl;
	private ChineseWordsTaoBaoCrawlController taobaoCrawlerControl;
	private ChineseWordsYiHaoDianCrawlController yihaodianCrawlerControl;
	private ChineseWordsSuNingYiGouCrawlController suningyigouCrawlerControl;
	
	public static void main(String[] args) {
		CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
		collectEBHotWordsToRedis.setEletronicBusinessHotWordsToRedis();
	}
	
	/**
	 *  每小时执行一次商品热搜词更新，根据当前用户的拆词排行信息，对电商中涉及的热词进行排行检索
	 */
	public void setEBHotWordsRankList(){
		
	}
	
	/**
	 *将各个爬虫节点的热词进行合并归纳，为业务需求，用于后续录入到redis做准备
	 */
	public void setEletronicBusinessHotWordsToRedis(){
		amazonCrawlerControl=new ChineseWordsAmazonCrawlController();
		vipCrawlerControl=new ChineseWordsVIPCrawlController();
		jingdongCrawlerControl=new ChineseWordsJingDongCrawlController();
		taobaoCrawlerControl=new ChineseWordsTaoBaoCrawlController();
		yihaodianCrawlerControl=new ChineseWordsYiHaoDianCrawlController();
		suningyigouCrawlerControl=new ChineseWordsSuNingYiGouCrawlController();
		TreeSet<String> ebusinessHotWords=null;
		TreeSet<String> unionallHotWords=null;
		try{
			ebusinessHotWords=new TreeSet<String>();
			unionallHotWords=new TreeSet<String>();

			//对所有的单词进行合并，排序去除重复项
			ebusinessHotWords=amazonCrawlerControl.getAmazonHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			ebusinessHotWords=vipCrawlerControl.getVIPHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			ebusinessHotWords=jingdongCrawlerControl.getJingDongHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			ebusinessHotWords=taobaoCrawlerControl.getTaobaoHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			ebusinessHotWords=yihaodianCrawlerControl.getYiHaoDianHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			ebusinessHotWords=suningyigouCrawlerControl.getSuNingYiGouHotSearchWords();
			unionallHotWords=removeunvalidwords(ebusinessHotWords, unionallHotWords);
			
			//测试代码
			int recnum=0;
			for(String str: unionallHotWords)
			{
				recnum+=1;
				System.out.println("	"+recnum+":"+str);
			}
		}catch(Exception ex){
			
		}
		//释放内存
		amazonCrawlerControl=null;
		vipCrawlerControl=null;
		jingdongCrawlerControl=null;
		taobaoCrawlerControl=null;
		yihaodianCrawlerControl=null;
		suningyigouCrawlerControl=null;
	}
	
	/**
	 * 对当前的热词进行无效词，符号的过滤，拆分
	 * @param ebwords 当前爬虫获得的热词集合
	 * @param curtreeset 当前要合并的集合，最后会返回
	 */
	public TreeSet<String> removeunvalidwords(TreeSet<String> ebwords, TreeSet<String> curtreeset){
		//提取其中的中文
		String reg = "[^\u4e00-\u9fa5]"; 
		TreeSet<String> unionallHotWords=null;
		List<Word> words = null;
		
		//去除无效的字符
		if(curtreeset==null)unionallHotWords=new TreeSet<String>();
		else unionallHotWords=curtreeset;
		for(String str: ebwords){
			String [] tmp=null;
			words=null;
			if(str.contains("/"))tmp=str.split("/");
			else if(str.contains("\t"))tmp=str.split("\t");
			else if(str.contains(" "))tmp=str.split(" ");
			if(tmp!=null){
				for(int i=0;i<tmp.length;i++)
				{
					tmp[i]=tmp[i].replaceAll(reg, "");
					if(tmp[i]!=null&&tmp[i].length()>=2){
						words=WordSegmenter.seg(tmp[i]);
						if(words!=null&&words.isEmpty()==false)unionallHotWords.add(tmp[i]);
						words=null;
					}
				}
			}else{
				str = str.replaceAll(reg, "");
				if(str!=null&&str.length()>=2){
					words=WordSegmenter.seg(str);
					if(words!=null&&words.isEmpty()==false)unionallHotWords.add(str);
					words=null;
				}
			}
		}
		return unionallHotWords;
	}
}
