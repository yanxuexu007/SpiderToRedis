package cm.spider.redis.test;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

public class spiderToRedisTest {
	public static Logger logger=Logger.getLogger(spiderToRedisTest.class);

	public static void main(String[] args) {
		try{
//			String test="MP3/MP4";
//			test=Base64.encodeBase64URLSafeString(test.getBytes());
//			System.out.println(test);
//			test=new String(Base64.decodeBase64(test));
//			System.out.println(test);
//			String test="惠氏启赋奶";
//			String res=null;
//			List<Word> words = null;
//			words=WordSegmenter.seg(test);
//			//2.对热词做md5转码，然后存入集合中，同时每个字符做计数
//			if(words!=null&&words.isEmpty()==false){
//				for(int i=0;i<words.size();i++)
//				{
//					res=words.get(i).getText();
//					res=Base64.encodeBase64URLSafeString(res.getBytes("UTF-8"));
//					System.out.println(res);
//					res=new String(Base64.decodeBase64(res),"UTF-8");
//					System.out.println(res);
//				}
//			}
		}catch(Exception ex){
			logger.info(" spiderToRedisTest crashes :"+ex.getMessage());
		}
	}
}
