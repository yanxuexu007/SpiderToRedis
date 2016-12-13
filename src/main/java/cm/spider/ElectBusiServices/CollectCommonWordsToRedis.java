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
	
	private static String not=null;
	private static String[] nottmp=null;

	public CollectCommonWordsToRedis(){
		not="赌博,奇葩,六合,假牌,假证,迷药,杀人,放火,仿真枪,成人网,抢劫,偷盗,枪支,弹药,假冒,事变,政变,老千,法轮,全能神,全能教,邪教,"
				+ "冰毒,摇头丸,大麻,造反,色吧,鸡鸡,手淫,性吧,性福,性欲,狠狠插,红灯区,卖淫,淫乱,爆乳,约炮,色情,情色,吞精,精液,艳照,淫荡,勾引,"
				+ "爱爱,做爱,偷情,偷性,交配,撸管,色系,鸡巴,飞机杯,车震,露阴,震动棒,性用品,假阳具,龟头,毒品,吸毒,叫鸡,洗钱,黑钱,赌钱,性骚扰,裸奔,裸照,轮奸,强奸,色图,淫娃,"
				+ "爆乳,妖姬,海天盛筵,生殖器,插插,壮阳,性故事,不雅照,一夜情,造爱,草榴,咪咪爱,阴蒂,阴唇,色色,走光,少妇,熟妇,熟女,日逼,操逼,黄图,"
				+ "黄片,强暴,强奸,迷奸,乱伦,阴茎,性交,裸体,射精,鸡婆,性侵,打飞机,奶子,吸奶,喂奶,巨乳,乳交,口交,口爆";
		nottmp=not.split(",");
	}
	
	public static void main(String[] args) {
		//主方法测试ok
	}
	
	/**
	 * 提供给外部调用的 百度热搜热词动态更新与统计搜索指数入口方法
	 * @return
	 */
//	public void execCollectCommonWordstoRedis(){
//		int num=0;
//		try{
//			CollectCommonWordsToRedis collectCommonHotWordsToRedis=new CollectCommonWordsToRedis();
//			num=collectCommonHotWordsToRedis.collectAllBaiduHotWords();
//			collectCommonHotWordsToRedis.setBiaduHotWordsToRedis();
//			logger.info(" Complete Baidu opt, words number: "+num);
//		}catch(Exception ex){
//			logger.info(" Thread execCollectEBWordstoRedis crashes: "+ex.getMessage());
//		}
//	}

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
		boolean unvalid=false;
		try{
			num=0;
			redisClusterObj=RedisClusterObj.getInstance();
			baiduHotWords=new HashMap<String, TreeSet<String>>();

			//对所有的单词进行合并，排序去除重复项
			baiduHotWords=baiduCrawlControl.getBaiduHotSearchWords();
			
			//测试代码,测试ok
			//int recnum=0;for(String str: unionallHotWords){recnum+=1;System.out.println("	"+recnum+":"+str);}
			//数据加入到baidu热搜词集合中
			key="mfg4_BaiduSet";
			for(String str:baiduHotWords.keySet())
			{
				typeWords=baiduHotWords.get(str);
				if(typeWords!=null&&typeWords.size()>0){
					for(String value:typeWords){
						//判断是会否为无效信息
						unvalid=fillterUnValidWords(value);
						if(unvalid==false){
							value=str+"_"+value;
							getbase64=Base64.encodeBase64URLSafeString(value.getBytes("UTF-8"));//对字符串按照UTF-8编码后再获取base64
							redisClusterObj.sadd(key, getbase64);
							num+=1;
						}
					}
				}
			}
		}catch(Exception ex){
			logger.info(" collectAllBaiduHotWords crashes : "+ex.getMessage());
			baiduHotWords=null;
			num=-1;
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
								getbase64=Base64.encodeBase64URLSafeString(getbase64.getBytes("UTF-8"));//对字符串按照UTF-8编码后再获取base64
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
							getbase64=Base64.encodeBase64URLSafeString(str.getBytes("UTF-8"));//对字符串按照UTF-8编码后再获取base64
							key="mfg4_"+tdate+"_baiduw_"+getbase64;
							value=String.valueOf(max);
							redisClusterObj.set(key, value);
						}
					}
				}
			}
		}catch(Exception ex){
			logger.info(" setBaiduHotWordsToRedis crashes: "+ex.getMessage());
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
	
	/**
	 * 过滤 邪黄赌毒，敏感信息，同时可减少数据量
	 * @param str
	 * @return true代表包含黄赌毒，或者敏感信息，不做热词统计
	 */
	public boolean fillterUnValidWords(String str)
	{
		if(str==null||str.trim().equals("")==true)return true;
		str=str.trim();
		if(nottmp!=null&&nottmp.length>0){
			for(int i=0;i<nottmp.length;i++)
			{
				if(str.contains(nottmp[i])==true)return true;
			}
		}
		return false;
	}
}
