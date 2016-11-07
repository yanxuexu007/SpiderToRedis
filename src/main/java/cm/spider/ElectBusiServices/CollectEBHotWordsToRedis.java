package cm.spider.ElectBusiServices;

import java.util.List;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import cm.redis.Commons.RedisClusterObj;
import cm.redis.Commons.TimeFormatter;
import cm.spider.CrawlerControllers.ChineseWordsAmazonCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsJingDongCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsSuNingYiGouCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsTaoBaoCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsVIPCrawlController;
import cm.spider.CrawlerControllers.ChineseWordsYiHaoDianCrawlController;


/**
 * 服务层代码，按照业务需求调用控制器获取热词信息
 * 对电商的热搜词进行爬虫检索，每小时执行一次
 * @author chinamobile
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
	
	//用于日志的获取
	public static Logger logger=Logger.getLogger(CollectEBHotWordsToRedis.class);
	
	public static void main(String[] args) {
		//主方法测试ok
//		String prehour=null;
//		String curhour=null;
//		TreeSet<String> collectwords=null;
//		CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
//		while(true){
//			curhour=TimeFormatter.getHour();		
//			if(curhour.equals(prehour)==false){
//				collectwords=collectEBHotWordsToRedis.collectAllEletronicBusinessHotWords();
//				prehour=curhour;
//			}
//			if(collectwords!=null&&collectwords.size()>0){
//				collectEBHotWordsToRedis.setEBHotWordsToRedis(collectwords); //每10分钟更新
//			}
//			try{					
//				Thread.sleep(1000*60*10);//休息10分钟
//			}catch(Exception ex){
//				logger.info(" Thread CollectEBHotWordsToRedis crashes: "+ex.getMessage());
//			}
//		}
	}
	
	/**
	 * 提供给外部调用的 电商热词动态更新与统计搜索指数入口方法
	 * @return
	 */
//	public void execCollectEBWordstoRedis(){
//		TreeSet<String> collectwords=null;
//		try{
//			CollectEBHotWordsToRedis collectEBHotWordsToRedis=new CollectEBHotWordsToRedis();
//			collectwords=collectEBHotWordsToRedis.collectAllEletronicBusinessHotWords();
//			if(collectwords!=null&&collectwords.size()>0){
//				collectEBHotWordsToRedis.setEBHotWordsToRedis(collectwords); 
//			}
//			logger.info(" Complete EB opt, words number: "+collectwords.size());
//		}catch(Exception ex){
//			logger.info(" Thread CollectEBHotWordsToRedis crashes: "+ex.getMessage());
//		}
//	}

	/**
	 *每小时执行一次商品热搜词更新，将各个爬虫节点的热词进行合并归纳，加入redis集合中
	 */
	public TreeSet<String> collectAllEletronicBusinessHotWords(){
		amazonCrawlerControl=new ChineseWordsAmazonCrawlController();
		vipCrawlerControl=new ChineseWordsVIPCrawlController();
		jingdongCrawlerControl=new ChineseWordsJingDongCrawlController();
		taobaoCrawlerControl=new ChineseWordsTaoBaoCrawlController();
		yihaodianCrawlerControl=new ChineseWordsYiHaoDianCrawlController();
		suningyigouCrawlerControl=new ChineseWordsSuNingYiGouCrawlController();
		TreeSet<String> ebusinessHotWords=null;
		TreeSet<String> unionallHotWords=null;
		RedisClusterObj redisClusterObj=null;
		String key=null;
		String getbase64=null;
		try{
			redisClusterObj=RedisClusterObj.getInstance();
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
			
			//测试代码,测试ok
			//int recnum=0;for(String str: unionallHotWords){recnum+=1;System.out.println("	"+recnum+":"+str);}
			//数据加入到电商热搜词集合中
			key="mfg4_EBusiSet";
			for(String str: unionallHotWords)
			{
				getbase64=Base64.encodeBase64URLSafeString(str.getBytes("UTF-8")); //对字节流按照UTF8编码
				redisClusterObj.sadd(key, getbase64);
			}
		}catch(Exception ex){
			logger.info(" collectAllEletronicBusinessHotWords crashes : "+ex.getMessage());
			unionallHotWords=null;
		}finally{
			//释放内存
			amazonCrawlerControl=null;
			vipCrawlerControl=null;
			jingdongCrawlerControl=null;
			taobaoCrawlerControl=null;
			yihaodianCrawlerControl=null;
			suningyigouCrawlerControl=null;
			ebusinessHotWords=null;
			redisClusterObj=null;
			key=null;
			getbase64=null;
		}
		return unionallHotWords;
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
			str = str.replaceAll("[\\s\b\r\n\f\t]*", "");  //去除空白符号
			if(str.contains("/"))tmp=str.split("/");
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
	
	/**
	 *  每10分钟更新一次信息，根据当前用户的拆词排行信息，对电商中涉及的热词进行搜索指数计算，写入对应热搜词中保存
	 *  base来自于互联网用户url中的中文，机器拆词计数之后的数据
	 */
	public void setEBHotWordsToRedis(TreeSet<String> collectwords){
		if(collectwords==null||collectwords.size()<=0)return;
		//基本思路：
		//1.取每个网站热搜词进行拆词，获取对应每个分词的base64编码，
		//2.匹配每个分词在redis库中的热度情况
		//3.计算网站热搜词的搜索指数计算公式：min词频*log(min,max)，表达式是最小词频乘以其对应最大词频的指数，
		//表达的意思是，如果最大词频越大，则表示可能搜索之后对min词频产生的影响越大
		RedisClusterObj redisClusterObj=null;
		List<Word> words = null;
		String getbase64=null;
		int min=-1;
		int max=0;
		int tmp=0;
		double searchindex=0.0;
		String key=null;
		String value=null;
		String tdate=null;
		try{
			redisClusterObj=RedisClusterObj.getInstance();
			tdate=TimeFormatter.getDate2();//获取当前日期
			for(String str: collectwords){
				min=-1;
				max=0;
				searchindex=0.0;
				words=WordSegmenter.seg(str);
				if(words!=null&&words.isEmpty()==false){
					for(int i=0;i<words.size();i++)
					{
						getbase64=words.get(i).getText();
						getbase64=Base64.encodeBase64URLSafeString(getbase64.getBytes("UTF-8")); //对字符串按照UTF-8编码后再获取base64
						key="mfg4_"+tdate+"_Zh_"+getbase64;
						value=redisClusterObj.get(key);
						if(value!=null&&value.equals("nil")==false){
							tmp=Integer.valueOf(value);
							if(min<0||tmp<min)min=tmp;
							if(tmp>max)max=tmp;
						}
					}
					if(min>1&&max>0)searchindex=min*(Math.log(max)/Math.log(min));
					max=(int) searchindex;
				}
				if(max>0){
					getbase64=Base64.encodeBase64URLSafeString(str.getBytes("UTF-8"));//对字符串按照UTF-8编码后再获取base64
					key="mfg4_"+tdate+"_ebusiw_"+getbase64;
					value=String.valueOf(max);
					redisClusterObj.set(key, value);
				}
			}
		}catch(Exception ex){
			logger.info(" setEBHotWordsToRedis crashes: "+ex.getMessage());
		}finally {
			//释放内存
			redisClusterObj=null;
			words = null;
			getbase64=null;
			key=null;
			value=null;
			tdate=null;
		}
	}
}
