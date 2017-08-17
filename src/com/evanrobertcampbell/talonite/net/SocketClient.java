package com.evanrobertcampbell.talonite.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class SocketClient extends SocketSession
{
	protected Socket socket = null;
	
	protected boolean waitingForConnection = false;
	
	@Override
	public void run()
	{
		BaseFramework.GetInstance().pushMessage("Starting client");
		
		try
		{
			waitingForConnection = true;
			
			socket = new Socket((String) getProperty(SOCKETSESSION_HOST), Integer.parseInt((String) getProperty(SOCKETSESSION_PORT)));
			
			waitingForConnection = false;
			
			BaseFramework.GetInstance().pushMessage("Just connected to: " + socket.getRemoteSocketAddress());
			
			PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
			
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			toServer.println("Hello from " + socket.getLocalSocketAddress());
			
			String line = fromServer.readLine();
			
			BaseFramework.GetInstance().pushMessage("Client received: " + line + " from server");
			
			toServer.close();
			
			fromServer.close();
			
			socket.close();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isWaitingForConnection()
	{
		return waitingForConnection;
	}

	@Override
	public boolean isSessionOpen()
	{
		if (socket == null)
			return false;
		if (socket.isClosed())
			return false;
		return true;
	}

	@Override
	public void disconnect()
	{
		BaseFramework.GetInstance().pushMessage("Client: attempting to disconnect from server");
		
		try
		{
			if (socket != null && socket.isClosed() == false)
			{
				socket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
