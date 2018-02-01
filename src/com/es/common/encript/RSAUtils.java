package com.es.common.encript;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 实现：java RSA生成文件密钥对与分段加解密超长字符串
 * @author YANGWEIPING
 *
 */
public class RSAUtils {

	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 128;
	
	/**
	 * 实现：获取公钥
	 * @param key 公钥字符串
	 * @return 公钥对象
	 * @throws Exception
	 */
	public static RSAPublicKey getPublicKey(String key) throws Exception{
		byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		return publicKey;
	}
	/**
	 * 实现：获取私钥
	 * @param key 私钥对象
	 * @return 私钥对象
	 * @throws Exception
	 */
	public static RSAPrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
	/**
	 * 实现：获取密钥对应的字符串
	 * @param key 密钥
	 * @return 密钥字符串
	 * @throws Exception
	 */
	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		String keyStr = (new BASE64Encoder()).encode(keyBytes);
		return keyStr;
	}
	/**
	 * 实现：获取密钥对应的字符串
	 * @param key 密钥
	 * @return 密钥字符串
	 * @throws Exception
	 */
	public static byte[] getKeyByte(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		return keyBytes;
	}
	/**
	 * 实现：随机生成公钥与私钥，并保存密钥文件
	 * @throws Exception
	 */
	public static void genaralKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 密钥位数
		keyPairGen.initialize(1024, new SecureRandom());
		// 密钥对
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 转16进制
		String module = publicKey.getModulus().toString();//printHexString(publicKey.getEncoded());
		String primodule = privateKey.getModulus().toString();
		String empoent = publicKey.getPublicExponent().toString();//printHexString(publicKey.getPublicExponent());
		String priempoent = privateKey.getPrivateExponent().toString();

		String publicKeyString = getKeyString(publicKey);
		String privateKeyString = getKeyString(privateKey);
		System.out.println("生成密钥时的公钥="+publicKeyString);
		System.out.println("生成密钥时的私钥="+privateKeyString);
		
		System.out.println("生成密钥时的公钥module="+module);
		System.out.println("生成密钥时的公钥empoent="+empoent);
		
		System.out.println("生成密钥时的私钥module="+primodule);
		System.out.println("生成密钥时的私钥empoent="+priempoent);
		

		FileOutputStream pubos = new FileOutputStream("E:\\public_key.key");
		ObjectOutputStream puoos = new ObjectOutputStream(pubos);
		puoos.writeObject(publicKeyString);
		puoos.flush();
		puoos.close();
		FileOutputStream privbos = new FileOutputStream("E:\\private_key.key");
		ObjectOutputStream privoos = new ObjectOutputStream(privbos);
		privoos.writeObject(privateKeyString);
		privoos.flush();
		privoos.close();
		System.out.println("生成完毕");
	}
	/**
	 * 实现：公钥加密数据
	 * @param publicKey 加密公钥
	 * @param data 需要加密的数据
	 * @return 加密后的结果
	 * @throws Exception
	 */
	public static byte[] encodeData(RSAPublicKey publicKey, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] enBytes = out.toByteArray();
		out.close();
		return enBytes;
	}
	/**
	 * 实现：私钥解密数据
	 * @param privateKey 解密私钥
	 * @param encodeData 需要解密的数据
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public static byte[] decodeData(RSAPrivateKey privateKey, byte[] encodeData) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int inputLen = encodeData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i=0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encodeData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encodeData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] deBytes = out.toByteArray();
		out.close();
		return deBytes;
	}
	/**
	 * 实现：通过私钥分段加密数据
	 * @param privateKey 私钥
	 * @param data 需要加密数据
	 * @return 加密后的数据
	 * @throws Exception 
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] encryptByPrivateKey(RSAPrivateKey privateKey, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] enBytes = out.toByteArray();
		out.close();
		return enBytes;
	}
	/**
	 * 实现：通过公钥解密数据
	 * @param publicKey 公钥
	 * @param encodeData 加密了的数据
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(RSAPublicKey publicKey, byte[] encodeData)throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		int inputLen = encodeData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i=0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encodeData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encodeData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decodeData = out.toByteArray();
		out.close();
		return decodeData;
	}
	public static void main(String args[]) throws Exception {
//		byte[] plainText = "4414444444444444".getBytes();
//		String pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8qxzsoxPXwISUkNANkr7YFjRX6dd/lgao9NX+EnBKg1ppQLIzu+qv0PGQVBl6n/2dnsVJpP1yFKDx+VDPt7OdTGxuWa2uYsBmbTMCOg2USWSufAuheNRJvJIJBjXyFqOS25Iw7rNf9QPr8np6Kjg9KTvQaZFwZCJSLTbi0x6ZNQIDAQAB";
//		String prikey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALyrHOyjE9fAhJSQ0A2SvtgWNFfp13+WBqj01f4ScEqDWmlAsjO76q/Q8ZBUGXqf/Z2exUmk/XIUoPH5UM+3s51MbG5Zra5iwGZtMwI6DZRJZK58C6F41Em8kgkGNfIWo5LbkjDus1/1A+vyenoqOD0pO9BpkXBkIlItNuLTHpk1AgMBAAECgYEAlMA4qt4cq/Ou1E+kqb70VvySwmmtLXvEvntjKNGy1RzhMLEAj0duzrXnb0rPV95CoH5owBqkdMiM6JsQOuV0Z4sdz3pbATZ7Q1rU3NGUUGk2nxv7AtvGhQCKV9pzL14uFhfoteVK3AErxrE+nM1U5oo87Nzg5RMtQY+3JEkUxpUCQQDfGiage09Tynrcc+MfVOFKTWRGSD2NXV3dXs7zs8VK2m24Uahq/KS7dZHMg3onmUh3cDfjvh3nLN1GN2xpwWHXAkEA2H0gxqA1Q/Xvk/nMaG0OHFJoz30NnB4Zfe2exaAlK1v30dJ4wmnJpaIIIzvDIrE/ilDvBeN72OgEP7GGRkgT0wJBAMW/Xyd1xM1nNlk6msDFsl7ylAO7ulsXu8Agbd0SpbgAPapXLqmNv9GslwuoKdj5g4LiQ5eohsbGPGclbwGZ98cCQC2g6eiTWcayLkW9D6Uu3xt/DxI0ZEr0WSEelSlIIK1r2+bAeq1XSKX9acOrU+ya+S548ngGQ1MrS8H0870acfsCQQDBkGMMnvsB/c7d0W/otNBJt50CzQ5jh5/WyjfApWReTkjqcajhsOX4MRaoxE0xflyV+oJ3FJi1PHyYb+qV8dfY";
//		RSAPublicKey publicKey = getPublicKey(pubkey);
//		RSAPrivateKey privateKey = getPrivateKey(prikey);
//		System.out.println("原文=" + new String(plainText));
//		byte[] enBytes = encodeData(publicKey, plainText);
//		System.out.println("密文=" + HexTools.bytes2HexString(enBytes));
////		String ss = "49048a8c9a3b3be8382a084bb465764348967fe145f56902b0d25e2e73364ab67e85c64a4935d857680a59c7d95cad4226f69b197d406f895d72851da543106f75969c276d68f6fa22c9d5b6340e8ac94d8b28a335342524b2a0e1f65f264748920fba15016ccae0b5a36c7dd299a26424a87e5e5223ece0c35171161518231b";
//		String ss = "478E8F9DE9697E74CE8CCB23E2F340D9DA5FD2B4D8774CAA2C6F49712F8C71149E0ED64DD9C1F921D389C22895CCD0462698AAD692F7008161855E9A45FB226DD0C4A4F6E5ADAA319AE1CB3B7308DFEB33338F45C71481ADAEA407FB45B53DF3531BC7B4520649B05C386C6E43B7F73195F852B3B5DE3907A887EA97A6E1A514";
//		System.out.println("密文长度=" + ss.length());
//		byte[] deBytes = decodeData(privateKey, HexTools.hexString2Bytes(ss));
//		String str = new String(deBytes);
//		System.out.println("解密=" + str);
	}
}