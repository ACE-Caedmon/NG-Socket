package com.ace.ng.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * @author Chenlong
 * 密码表传输对象封装类，用于客户端通过验证后向客户端发送密码表
 * */
public class PassportTable implements Output{
	private List<Short> passports;
	private static Logger logger=LoggerFactory.getLogger(PassportTable.class);
	public PassportTable(List<Short> passports) {
		this.passports = passports;
	}
	public void encode(CustomBuf buf) {
		if(passports.size()==256){
			for(Short passport:passports){
				buf.writeShort(passport);
			}
		}else{
			logger.error("密码表长度错误:length={}",passports.size());
		}
	
	}

}
