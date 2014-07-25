package com.ace.ng.dispatch;

public interface IAction<T> {
	void excute(T playerOnline);
}
