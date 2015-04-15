package com.ace.ng.parse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Java对象序列化与反序列化解析类（对象必须实现Serializable或者Externalizable接口）
 * @author Chenlong
 * */
public class JavaParser implements Parser{
	private static Logger logger=LoggerFactory.getLogger(JavaParser.class);
	@Override
	public byte[] getBytes(Object object) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ObjectOutputStream output=null;
		try {
			output=new ObjectOutputStream(baos);
			output.writeObject(object);
			byte[] result=baos.toByteArray();		
			return result;
		}catch(Exception e){
			logger.error("序列化异常( class = "+object.getClass().getName()+")",e);
			return null;
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("关闭输出流异常",e);
			}
		}
	}

	@Override
	public Object getObject(byte[] buf) {
		ByteArrayInputStream bais=new ByteArrayInputStream(buf);
		ObjectInputStream ois=null;
		Object result=null;
		try{
			ois=new ObjectInputStream(bais);
			result=ois.readObject();
			return result;
		}catch(Exception e){
			logger.error("反序列化异常",e);
			return null;
		}finally{
			try {
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("关闭输出流异常",e);
			}
		}
	}

	@Override
	public String getString(Object object) {
		// TODO Auto-generated method stub
		return new String(getBytes(object));
	}
	
	@Override
	public Object getObject(String text) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("对象Java反序列化不支持字符串形式");
	}

}
