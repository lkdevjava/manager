package com.es.common.encript;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Component
public class Funcs {
   
   
   public static String formatDate(Timestamp time,String format){
	   SimpleDateFormat df = new SimpleDateFormat(format);
	   if(null != time){
		   return df.format(time);
	   }else{
		   return "";
	   }
	   
   }
   /**2015.07.02添加开始*/
 //返回给定时间往后推n天的日期
   public static String getNextNumDate(Date date, Integer dayNum, String parten) {
       if (null == date) {
           return "";
       }
       SimpleDateFormat format = new SimpleDateFormat(parten);
       Calendar c = Calendar.getInstance();
       c.setTime(date);
       c.add(Calendar.DATE, dayNum);
       return format.format(c.getTime());
   }

   //根据数字表示的年周返回特定周的日期
   public static String getDayOfWeekDate(Integer yearWeek, String parten, Integer dayOfWeek) 
   {
       if (0 == yearWeek) 
       {
           return "";
       }
       String tmp = String.valueOf(yearWeek);
       //获取年
       int year = Integer.valueOf(tmp.substring(0, 4));
       int week = Integer.valueOf(tmp.substring(4));
       Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.WEEK_OF_YEAR, week);
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		
       SimpleDateFormat format = new SimpleDateFormat(parten);
       return format.format(c.getTime());
   }
   
   public static String roundDouble(Double num, Integer r) {
       if (null == num) {
           return "";
       }
       BigDecimal b = new BigDecimal(num);
       num = b.setScale(r, BigDecimal.ROUND_HALF_UP).doubleValue();
       return num.toString();
   }

   public static boolean isContains(Object id, List<Object> ids) {
       if (ids == null || ids.size() == 0 || id == null) {
           return false;
       }
       if (ids.contains(id)) {
           return true;
       }
       return false;
   }

  /* public static String getRoleName(Long roleId) {
       if (roleId == null) {
           return "";
       }
       Role r = roleService.getByIdInCache(roleId);
       if (r == null) {
           return "";
       }
       return r.getName();
   }*/

  /* public static String getMenuName(Long menuId) {
       Menu m = menuService.getByIdInCache(menuId);
       if (m != null) {
           return m.getName();
       }
       return "";
   }*/

  /* public static String getCityName(Long cityId){
       City city = cityService.getById(cityId);
       return city == null ? "" : city.getName();
   }*/
   
   public static String formatPrice(Double price){
       DecimalFormat df = new DecimalFormat("0.00");
       return df.format(price / 100);
   }
   
   public static String formatDoubleNumber(Double num, String fmtStr){
       DecimalFormat df = new DecimalFormat(fmtStr);
       return df.format(num);
   }
   
   public static String listToString(List<String> list){
   	if(null != list){
   		StringBuffer listStr = new StringBuffer();
   		for(String temp:list){
   			listStr.append(temp).append(",");
   		}
   		if(listStr.length()>2){
   			return listStr.substring(0,listStr.length()-1);
   		}else{
   			return listStr.toString();
   		}
   	}else{
   		return "";
   	}
   }
   
   /**
    * 转换map到String
    * @param map
    * @return
    */
   public static String mapToString(Map<String,String> map){
   	StringBuffer temp = new StringBuffer();
   	for(int i = 0;i<map.keySet().size();i++){
   		temp.append(map.keySet().toArray()[i]).append("=").append(map.get(map.keySet().toArray()[i])).append(",");
   	}
   	if(temp.length()>2){
   		return temp.substring(0,temp.length()-1);
   	}else{
   		return temp.toString();
   	}
   }
   private final static String DES = "DES";
	private final static String KEY = "!#%&(123";
	/** 单向加密 SHA */
	public static String eccryptSHA(String info) throws NoSuchAlgorithmException{  
       MessageDigest md5 = MessageDigest.getInstance("SHA");  
       byte[] srcBytes = info.getBytes();  
       //使用srcBytes更新摘要  
       md5.update(srcBytes);  
       //完成哈希计算，得到result  
       byte[] resultBytes = md5.digest();  
       BASE64Encoder base64E = new BASE64Encoder();
       return base64E.encode(resultBytes);
   }
	
	/** 单向加密 MD5 */
	public static String eccryptMD5(String info) throws NoSuchAlgorithmException{  
		if(StringUtils.isEmpty(info)){
			return "";
		}
		//根据MD5算法生成MessageDigest对象  
		MessageDigest md5 = MessageDigest.getInstance("MD5");  
		byte[] srcBytes = info.getBytes();  
		//使用srcBytes更新摘要  
		md5.update(srcBytes);  
		//完成哈希计算，得到result  
		byte[] resultBytes = md5.digest();  
		BASE64Encoder base64E = new BASE64Encoder();
       return base64E.encode(resultBytes);
	}  
 
   /**
    * Description 根据键值进行加密
    * @param data 
    * @param key  加密键byte数组
    * @return
    * @throws Exception
    */
   public static String encrypt(String data)  {
	   if(StringUtils.isEmpty(data)){
			return "";
		}
       byte[] bt=null;
	try {
		bt = encrypt(data.getBytes(), KEY.getBytes());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       String strs = new BASE64Encoder().encode(bt);
       return strs;
   }

   /**
    * Description 根据键值进行解密
    * @param data
    * @param key  加密键byte数组
    * @return
    * @throws IOException
    * @throws Exception
    */
   public static String decrypt(String data) throws IOException,
           Exception {
	   if(StringUtils.isEmpty(data)){
			return "";
		}
       if (data == null)
           return null;
       BASE64Decoder decoder = new BASE64Decoder();
       byte[] buf = decoder.decodeBuffer(data);
       byte[] bt = decrypt(buf,KEY.getBytes());
       return new String(bt);
   }
   
   
   public static void main(String[] args) throws IOException, Exception {
	   System.out.println(decrypt("l4WCmBLdQ+o=")+decrypt("TtCFjR21Itk=")+decrypt("7TfLSeWuNqE="));
   }
   
   /**
    * Description 根据键值进行加密
    * @param data
    * @param key  加密键byte数组
    * @return
    * @throws Exception
    */
   private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
       // 生成一个可信任的随机数源
       SecureRandom sr = new SecureRandom();

       // 从原始密钥数据创建DESKeySpec对象
       DESKeySpec dks = new DESKeySpec(key);

       // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
       SecretKey securekey = keyFactory.generateSecret(dks);

       // Cipher对象实际完成加密操作
       Cipher cipher = Cipher.getInstance(DES);

       // 用密钥初始化Cipher对象
       cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

       return cipher.doFinal(data);
   }
    
    
   /**
    * Description 根据键值进行解密
    * @param data
    * @param key  加密键byte数组
    * @return
    * @throws Exception
    */
   private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
       // 生成一个可信任的随机数源
       SecureRandom sr = new SecureRandom();

       // 从原始密钥数据创建DESKeySpec对象
       DESKeySpec dks = new DESKeySpec(key);

       // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
       SecretKey securekey = keyFactory.generateSecret(dks);

       // Cipher对象实际完成解密操作
       Cipher cipher = Cipher.getInstance(DES);

       // 用密钥初始化Cipher对象
       cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

       return cipher.doFinal(data);
   }
   /*@Autowired
   public void setRoleService(RoleService roleService) {
       Funcs.roleService = roleService;
   }
*/
 /*  @Autowired
   public void setMenuService(MenuService menuService) {
       Funcs.menuService = menuService;
   }*/

  /* @Autowired
   public void setCityService(CityService cityService) {
       Funcs.cityService = cityService;
   }
*/
}
