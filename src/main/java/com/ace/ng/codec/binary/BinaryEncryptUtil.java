package com.ace.ng.codec.binary;

import java.util.*;


public class BinaryEncryptUtil {
	public static final String KEY = "xiaotong";
	/**
	 *
	 * @param passBody passBody
	 * @return
	 */
	public static int countOddNumber(List<Short> passBody){
		int count=0;
		for(int i=0;i<passBody.size();i++){
			if(i%2==0){
				count+=passBody.get(i);
				//logger.info("count="+count);
			}
		}
		return count;
	}
    /**
     * @return 生成密码表
     * */
	public static List<Short> getPassBody(){
		Set<Short> set = new HashSet<Short>();
		while( set.size() < 256) {
			int r = new Random().nextInt(65535);
			set.add((short)r);
		}
		
		List<Short> passBody = new ArrayList<Short>();
		for(short i:set){
			passBody.add( i );
			//logger.info("passBody="+i);
		}
		return passBody;
	}


	public static void main(String[] args) {
		String abc = "abcd";
		byte[] szText = abc.getBytes();
		int iDataLen = abc.length();
		String key = "jcwx";
		int iOffset = 0;
		
		encode(szText, iDataLen, key, iOffset);
		System.out.println(szText);
		decode(szText, iDataLen, key, iOffset);
		
		System.out.println(new String(szText));
	}
	/**
     * 加密byte数组
     * @param szText 需要加密的byte数组
     * @param iDataLen 数组长度
     * @param key 秘钥
     * @param iOffset 偏移量
     * @return 是否加密成功
     * */
	public static boolean encode(byte[] szText , int iDataLen , String key , int iOffset){
		boolean		  bRet     = false;
		byte[] pDest   =  szText;
		String szKey   =  key;
		int iIndex		       = 0;
		int kIndex			   = 0;
		int iTextLen		   = iDataLen;
		int iKeyLen			   = szKey.length();
		int	iKeySumValue	   = 0;
		do 
		{
			if (szText == null || iTextLen <= 0 || szKey == null) {
				break;
			}

			for(iIndex = 0 ; iIndex < iKeyLen ; iIndex ++)
			{
				iKeySumValue += key.charAt(iIndex);
			}

			//加密
			for (iIndex = 0 ,  kIndex = 0 ; iIndex < iDataLen ; iIndex ++ )
			{
				
				pDest[iIndex] = (byte)(pDest[iIndex] + iTextLen + iOffset);
				pDest[iIndex] = (byte)(pDest[iIndex]^key.charAt(kIndex));
				pDest[iIndex] = (byte)(pDest[iIndex]^iKeySumValue);
//				System.out.println(pDest[iIndex]);
				kIndex	 ++;
				iTextLen --;
				if(kIndex >= iKeyLen)
				{
					kIndex = 0;
				}
			}
			bRet = true;
		} while ( true );
		
		return bRet;
	}
    /**
     * 解密byte数组
     * @param szText 需要加密的byte数组
     * @param iDataLen 数组长度
     * @param key 秘钥
     * @param iOffset 偏移量
     * @return 是否解密成功
     * */
	public static boolean decode(byte[] szText , int iDataLen , String key , int iOffset)
	{
		boolean		  bRet     = false;
		byte[] pDest   =  szText;
		String szKey   =  key;
		int iIndex		       = 0;
		int kIndex			   = 0;
		int iTextLen		   = iDataLen;
		int iKeyLen			   = szKey.length();
		int	iKeySumValue	   = 0;
		do 
		{
			if (szText == null || iTextLen <= 0 || szKey == null)
			{
				break;
			}

			for(iIndex = 0 ; iIndex < iKeyLen ; iIndex ++)
			{
				iKeySumValue += szKey.charAt(iIndex);
			}

			//解密 
			for (iIndex = 0 ,  kIndex = 0 ; iIndex < iDataLen ; iIndex ++ )
			{
				pDest[iIndex] = (byte)(pDest[iIndex]^iKeySumValue);
				pDest[iIndex] = (byte)(pDest[iIndex]^key.charAt(kIndex));
				pDest[iIndex] = (byte)(pDest[iIndex] - iTextLen - iOffset);

				kIndex	 ++;
				iTextLen --;
				if(kIndex >= iKeyLen)
				{
					kIndex = 0;
				}
			}
			bRet = true;
		} while ( true );
		
		return bRet;
	}
}
