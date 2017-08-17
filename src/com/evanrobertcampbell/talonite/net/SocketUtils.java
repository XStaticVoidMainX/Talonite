package com.evanrobertcampbell.talonite.net;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class SocketUtils
{
	public static final String SOCKETSESSION = "socketSession";
	public static final String IS_HOST = "socketSession.isHost";
	
	//private static Thread worker = null;
	
	public static SocketSession createSocketSession()
	{
		BaseFramework.GetInstance().pushMessage("SocketUtils: Creating session.");
		
		SocketSession session = (SocketSession)BaseFramework.GetSessionVariable(SOCKETSESSION);
		
		try
		{
			if (session != null)
			{
				if (session.isWaitingForConnection())
				{
					BaseFramework.GetInstance().pushMessage("Already trying to connect...");
					return session;
				}
				if (session.isSessionOpen())
				{
					session.disconnect();
				}
			}
			
			boolean isHost = (boolean)BaseFramework.GetSessionVariable(IS_HOST);
			
			if (isHost)
			{
				session = new SocketServer();
			}
			else
			{
				session = new SocketClient();
			}
			
			return session;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
