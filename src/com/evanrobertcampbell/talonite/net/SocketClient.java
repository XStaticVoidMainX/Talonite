package com.evanrobertcampbell.talonite.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.*;
import java.text.MessageFormat;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class SocketClient extends SocketSession
{
	//protected Socket socket = null;
	
	protected SocketChannel socket = null;
	
	protected boolean waitingForConnection = false;
	protected boolean disconnecting = false;
	
	@Override
	public void run()
	{
		String user = (String) getProperty(SOCKETSESSION_USER);
		String host = (String) getProperty(SOCKETSESSION_HOST);
		int port = Integer.parseInt((String) getProperty(SOCKETSESSION_PORT));
		
		BaseFramework.GetInstance().pushMessage(MessageFormat.format("Attempting to connect to server: {0}:{1,number,#}", host, port));
		
		try
		{
			InetSocketAddress address = new InetSocketAddress(host, port);
			socket = SocketChannel.open(address);
			
			socket.configureBlocking(false);
			
			BaseFramework.GetInstance().pushMessage(MessageFormat.format("Connected to server: {0}:{1,number,#}", host, port));
			
			connected = true;
			
			uiInterfaceThread.start();
			
			while (connected == true)
			{
				// Make sure socket is still alive
				checkOpenSocket(socket);
				
				// Write to server
				{
					send(socket);
				}
				
				// Make sure socket is still alive
				checkOpenSocket(socket);
				
				// Read from server
				{
					if (receive(socket) == false)
					{
						disconnect();
					}
				}
				
				Thread.sleep(serverTickRate);
			}
		}
		catch (IOException e)
		{
			// No response from server when trying to connect
			if (e instanceof ConnectException)
			{
				BaseFramework.GetInstance().pushMessage("Unable to connect to server. Reason: " + e.getMessage());
			}
			else if (e instanceof SocketException)
			{
				BaseFramework.GetInstance().pushMessage("Connection error: " + e.getMessage());
			}
			// Exception thrown when closing application without disconnecting socket.
			else if (e instanceof ClosedChannelException)
			{
				BaseFramework.GetInstance().pushMessage("Disconnected from server. Reason: " + e.getMessage());
			}
			// Other unknown reason for IOException
			else
			{
				BaseFramework.GetInstance().pushMessage("Disconnected from server. Reason: " + e.getMessage());
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect();
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
		if (socket.isOpen() == false)
			return false;
		if (disconnecting)
			return false;
		if (connected == false)
			return false;
		return true;
	}

	@Override
	public void disconnect()
	{
		if (disconnecting == false)
		{
			disconnecting = true;
			
			try
			{
				if (socket != null && socket.isOpen() == true)
				{
					outMessageBuffer.add(DISCONNECT_STRING);
					send(socket);
					
					socket.close();
					
					BaseFramework.GetInstance().pushMessage("Disconnected from server.");
				}
			}
			catch (IOException e)
			{
				// Disconnecting from socket, exception handled.
			}
	
			// Stops the uiInterfaceThread
			connected = false;
		}
	}

	@Override
	public boolean isHost()
	{
		return false;
	}
}
