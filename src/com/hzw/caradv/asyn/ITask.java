package com.hzw.caradv.asyn;

public abstract class ITask {

	public int mId;
	public String mIdentification;

	public ITask(int id, String identification) {
		this.mId = id;
		mIdentification = identification;
	}

	public abstract Object doTask();
}
