package com.evanrobertcampbell.talonite.net;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public abstract class SocketSession implements Runnable
{
	public static final String SOCKETSESSION_USER = "session.userName";
	public static final String SOCKETSESSION_HOST = "session.socketSession.host";
	public static final String SOCKETSESSION_PORT = "session.socketSession.port";
	
	public abstract boolean isWaitingForConnection();
	
	public abstract boolean isSessionOpen();
	
	public abstract void disconnect();
	
	public Object getProperty(String key)
	{
		return BaseFramework.GetSessionVariable(key);
	}
}
