package com.xxx.transaction.tranfser;

public class Daemon extends Thread {

	Runnable runnable = null;

	{
		// always a daemon
		setDaemon(true);
	} 

	/** Construct a daemon thread. */
	public Daemon() {
		super();
	}

	/** A Construct with a daemon thread. */
	public Daemon(Runnable runnable) {
		super(runnable);
		this.runnable = runnable;
		
		this.setName(((Object)runnable).toString());
	}

	public Runnable getRunnable() {
		return this.runnable;
	}
}
