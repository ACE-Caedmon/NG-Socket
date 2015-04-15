package com.ace.ng.parse;
/**
 * 数据类型转换解析接口
 * @author Chenlong
 * */
public interface Parser {
	/**
	 * 将Object转换为byte数组
	 * */
	byte[] getBytes(Object object);
	/**
	 * 根据byte数组转换为Object
	 * */
	Object getObject(byte[] buf);
	/**
	 * 将Object转换为String
	 * */
	String getString(Object object);
	/**
	 * 将String转换为Object
	 * */
	Object getObject(String text);
}
