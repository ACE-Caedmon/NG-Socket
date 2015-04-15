package com.ace.ng.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 * JSON与Java对象序列化反序列化解析类
 * */
public class JSONParser implements Parser{
	private static final SerializerFeature[] FEATURES=new SerializerFeature[]{SerializerFeature.WriteClassName};
	@Override
	public byte[] getBytes(Object object) {
		return getString(object).getBytes();
	}

	@Override
	public Object getObject(byte[] buf) {
		String json=new String(buf);
		return JSON.parse(json);
	}

	@Override
	public String getString(Object object) {
		// TODO Auto-generated method stub
		return JSON.toJSONString(object, FEATURES);
	}

	@Override
	public Object getObject(String text) {
		// TODO Auto-generated method stub
		try {
			return JSON.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
