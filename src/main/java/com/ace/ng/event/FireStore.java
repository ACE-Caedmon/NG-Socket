package com.ace.ng.event;

import java.util.HashMap;
import java.util.Map;
/**
 * 所有事件触发器的管理类
 * */
public class FireStore {
	private Map<String,Fire<?>> fires;
	public static final FireStore INSTANCE =new FireStore();
	private FireStore(){
		fires=new HashMap<String, Fire<?>>();
	}
	public void addEventFire(String fireName,Fire<?> fire){
		if(!fires.containsKey(fireName)){
			fires.put(fireName, fire);
		}else{
			throw new IllegalArgumentException("EventFire 已存在，不能添加相同名称的EventFire( fireName = "+fireName+" )");
		}
	}
	public Fire<?> getEventFire(String name){
		return fires.get(name);
	}
	public void removeFireStore(String fireName){
		fires.remove(fireName);
	}
}
