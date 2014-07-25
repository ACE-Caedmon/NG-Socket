package com.ace.ng.codec.encrypt;

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.WithCodeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * @author Chenlong
 * 密码表传输对象封装类，用于客户端通过验证后向客户端发送密码表
 * */
public class PassportTable extends WithCodeMessage {
	private List<Short> passports;
	private static Logger logger=LoggerFactory.getLogger(PassportTable.class);
	public PassportTable(short cmd, byte code) {
		super(cmd, code);
		// TODO Auto-generated constructor stub
	}
	public void setPassports(List<Short> passports) {
		this.passports = passports;
	}

	@Override
	public void encode(CustomBuf buf) {
		if(code==0){
			if(passports.size()==256){
				for(Short passport:passports){
					buf.writeShort(passport);
				}	
			}else{
				logger.error("密码表内容长度不合法"+passports.size());
			}
		}
	
	}

}
