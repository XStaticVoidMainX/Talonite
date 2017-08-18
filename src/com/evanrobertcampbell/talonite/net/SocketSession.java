package com.evanrobertcampbell.talonite.net;

import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

import javafx.application.Platform;

/**
 * @author Evan Campbell
 */
public abstract class SocketSession implements Runnable
{
	public static final String SOCKETSESSION_USER = "session.userName";
	public static final String SOCKETSESSION_HOST = "session.socketSession.host";
	public static final String SOCKETSESSION_PORT = "session.socketSession.port";
	public static final String SOCKETSESSION_UPDATE_UI_INTERVAL = "socketSession.uiInterval";
	public static final String SOCKETSESSION_SERVER_TICK_RATE = "socketSession.tickRate";
	public static final String DISCONNECT_STRING = "/disconnect";
	
	public static final long DEFAULT_UPDATE_UI_INTERVAL = 100;
	public static final long MIN_UPDATE_UI_INTERVAL = 1000 / 60;
	
	public static final long DEFAULT_SERVER_TICK_RATE = 100;
	public static final long MIN_SERVER_TICK_RATE = 1000 / 60;
	
	protected boolean connected = false;
	
	public abstract boolean isWaitingForConnection();
	
	public abstract boolean isSessionOpen();
	
	public abstract void disconnect();
	
	protected Queue<String> inMessageBuffer;
	protected Queue<String> outMessageBuffer;
	
	protected Thread uiInterfaceThread = null;
	
	protected long serverTickRate = -1;
	
	public SocketSession()
	{
		BaseFramework.GetInstance().copyPropertiesToSession(
			SOCKETSESSION_UPDATE_UI_INTERVAL,
			SOCKETSESSION_SERVER_TICK_RATE
		);

		Object property = getProperty(SOCKETSESSION_SERVER_TICK_RATE);
		if (property != null)
		{
			serverTickRate = Long.parseLong(property.toString());
		}
		if (serverTickRate < MIN_SERVER_TICK_RATE)
		{
			serverTickRate = MIN_SERVER_TICK_RATE;
		}
		System.out.println("serverTickRate: " + serverTickRate);
		
		inMessageBuffer = new LinkedList<String>();
		outMessageBuffer = new LinkedList<String>();
		
		// This thread will periodically update the UI with new messages at a set interval.
		uiInterfaceThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				long threadSleepInterval = -1;
				Object property = getProperty(SOCKETSESSION_UPDATE_UI_INTERVAL);
				if (property != null)
				{
					threadSleepInterval = Long.parseLong(property.toString());
				}
				if (threadSleepInterval < MIN_UPDATE_UI_INTERVAL)
				{
					threadSleepInterval = MIN_UPDATE_UI_INTERVAL;
				}
				System.out.println("threadSleepInterval: " + threadSleepInterval);
				
				// TODO change loop condition
				while (connected == true)
				{
					try
					{
						// Waits for new messages.
						// As soon as there are messages available, add them on UI thread.
						if (inMessageBuffer.peek() != null)
						{
							Platform.runLater(new Runnable()
							{
								@Override
								public void run()
								{
									while (connected == true && inMessageBuffer.peek() != null)
									{
										BaseFramework.GetInstance().pushMessage(inMessageBuffer.remove());
									}
								}
							});
						}
						
						// TODO Tune this interval
						Thread.sleep(threadSleepInterval);
					}
					catch (InterruptedException e)
					{
						
					}
				}
			}
		});
	}
	
	public Object getProperty(String key)
	{
		return BaseFramework.GetSessionVariable(key);
	}
	
	public String popIncomingMessage()
	{
		if (inMessageBuffer != null && inMessageBuffer.peek() != null)
		{
			return inMessageBuffer.remove();
		}
		return null;
	}
	
	public void pushOutgoingMessage(String message)
	{
		if (outMessageBuffer != null)
		{
			outMessageBuffer.add(message);
		}
	}
	
	protected void send(SocketChannel socket) throws IOException
	{
		while (outMessageBuffer.peek() != null)
		{
			String message = outMessageBuffer.remove();
			
			byte[] messageBytes = message.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(messageBytes);
			
			socket.write(buffer);
			buffer.clear();
		}
	}
	
	// Returns false if receiving disconnect string.
	protected boolean receive(SocketChannel socket) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(256);
		
		socket.read(buffer);
		
		String result = new String(buffer.array()).trim();
		
		if (result.equals(DISCONNECT_STRING))
		{
			return false;
		}
		else
		{
			if (result.isEmpty() == false)
			{
				inMessageBuffer.add(result);
			}
			return true;
		}
	}
	
	protected void checkOpenSocket(SocketChannel socket) throws IOException
	{
		if (socket.isOpen() == false ||
			socket.isConnected() == false ||
			socket.socket().isClosed() ||
			socket.socket().isConnected() == false)
		{
			throw new ConnectException("Connection to server was lost.");
		}
	}
	
	public abstract boolean isHost();
}
