package com.ace.ng.dispatch;

public interface IAction<T> {
	void execute (T user) throws Throwable;
}
