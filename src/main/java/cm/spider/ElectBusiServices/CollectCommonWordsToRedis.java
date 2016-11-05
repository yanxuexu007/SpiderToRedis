package cm.spider.ElectBusiServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import cm.redis.Commons.RedisClusterObj;
import cm.redis.Commons.TimeFormatter;
import cm.spider.CrawlerControllers.ChineseWordsBaiduCrawlController;


/**
 * 结合百度热搜排行榜，以及storm实时分析数据，提供动态热词信息量
 * @author chinamobile
 */
public class CollectCommonWordsToRedis {
	//百度对应的爬虫机器人
	private ChineseWordsBaiduCrawlController baiduCrawlControl;
	
	//用于日志的获取
	public static Logger logger=Logger.getLogger(CollectCommonWordsToRedis.class);
	
	public static void main(String[] args) {
		//主方法测试ok
	}
	
	/**
	 * 提供给外部调用的 百度热搜热词动态更新与统计搜索指数入口方法
	 * @return
	 */
	public void execCollectEBWordstoRedis(){
		int num=0;
		try{
			CollectCommonWordsToRedis collectEBHotWordsToRedis=new CollectCommonWordsToRedis();
			num=collectEBHotWordsToRedis.collectAllBaiduHotWords();
			collectEBHotWordsToRedis.setBiaduHotWordsToRedis();
			logger.info(" Complete Baidu opt, words number: "+num);
		}catch(Exception ex){
			logger.info(" Thread execCollectEBWordstoRedis crashes: "+ex.getMessage());
		}
	}

	/**
	 *每小时执行一次商品热搜词更新，将各个爬虫节点的热词进行合并归纳，加入redis集合中
	 */
	public int collectAllBaiduHotWords(){
		baiduCrawlControl=new ChineseWordsBaiduCrawlController();
		Map<String, TreeSet<String>> baiduHotWords=null;
		TreeSet<String> typeWords=null;
		int num=-1;

		RedisClusterObj redisClusterObj=null;
		String key=null;
		String getbase64=null;
		try{
			redisClusterObj=RedisClusterObj.getInstance();
			baiduHotWords=new HashMap<String, TreeSet<String>>();

			//对所有的单词进行合并，排序去除重复项
			baiduHotWords=baiduCrawlControl.getBaiduHotSearchWords();
			
			//测试代码,测试ok
			//int recnum=0;for(String str: unionallHotWords){recnum+=1;System.out.println("	"+recnum+":"+str);}
			//数据加入到电商热搜词集合中
			key="mfg4_BaiduSet";
			for(String str:baiduHotWords.keySet())
			{
				typeWords=baiduHotWords.get(str);
				if(typeWords!=null&&typeWords.size()>0){
					for(String value:typeWords){
						value=str+"_"+value;
						getbase64=Base64.encodeBase64URLSafeString(value.getBytes());
						redisClusterObj.sadd(key, getbase64);
					}
				}
			}
		}catch(Exception ex){
			logger.info(" collectAllBaiduHotWords crashes : "+ex.getMessage());
			baiduHotWords=null;
		}finally{
			//释放内存
			typeWords=null;
			redisClusterObj=null;
			key=null;
			getbase64=null;
		}
		return num;
	}
	
	/**
	 *  每10分钟更新一次信息，根据当前用户的拆词排行信息，对电商中涉及的热词进行搜索指数计算，写入对应热搜词中保存
	 *  base来自于互联网用户url中的中文，机器拆词计数之后的数据
	 */
	public void setBiaduHotWordsToRedis(){
		//基本思路：
		//1.取每个网站热搜词进行拆词，获取对应每个分词的base64编码，
		//2.匹配每个分词在redis库中的热度情况
		//3.计算网站热搜词的搜索指数计算公式：min词频*log(min,max)，表达式是最小词频乘以其对应最大词频的指数，
		//表达的意思是，如果最大词频越大，则表示可能搜索之后对min词频产生的影响越大
		RedisClusterObj redisClusterObj=null;
		Map<String, TreeSet<String>> baiduHotWords=null;
		TreeSet<String> typeWords=null;
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
			//对所有的单词进行合并，排序去除重复项
			baiduHotWords=baiduCrawlControl.getBaiduHotSearchWords();
			
			for(String typestr:baiduHotWords.keySet())
			{
				typeWords=baiduHotWords.get(typestr);
				if(typeWords!=null&&typeWords.size()>0){
					for(String str:typeWords){
						min=-1;
						max=0;
						searchindex=0.0;
						words=WordSegmenter.seg(str);
						if(words!=null&&words.isEmpty()==false){
							for(int i=0;i<words.size();i++)
							{
								getbase64=words.get(i).getText();
								getbase64=Base64.encodeBase64URLSafeString(getbase64.getBytes());
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
							str=typestr+"_"+str;
							getbase64=Base64.encodeBase64URLSafeString(str.getBytes());
							key="mfg4_"+tdate+"_baiduw_"+getbase64;
							value=String.valueOf(max);
							redisClusterObj.set(key, value);
						}
					}
				}
			}
		}catch(Exception ex){
			logger.info(" setBiaduHotWordsToRedis crashes: "+ex.getMessage());
		}finally {
			//释放内存
			redisClusterObj=null;
		    baiduHotWords=null;
			typeWords=null;
			words = null;
			getbase64=null;
			key=null;
			value=null;
			tdate=null;
		}
	}
}
