package com.evanrobertcampbell.talonite.net;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class SocketServer extends SocketSession
{	
	//protected SocketChannel client = null;
	protected ServerSocket serverSocket = null;
	protected Selector selector = null;
	
	protected boolean waitingForConnection = false;
	protected boolean disconnecting = false;
	
	@Override
	public void run()
	{
		String user = (String) getProperty(SOCKETSESSION_USER);
		String host = (String) getProperty(SOCKETSESSION_HOST);
		int port = Integer.parseInt((String) getProperty(SOCKETSESSION_PORT));
		
		// TODO : setup pooled threads with max number of connected clients.
		int maxRemoteClients = 1;
		
		try
		{
			BaseFramework.GetInstance().pushMessage("Starting Server");
			
			selector = Selector.open();
			
			ServerSocketChannel socket = ServerSocketChannel.open();
			InetSocketAddress address = new InetSocketAddress(host, port);
			
			socket.bind(address);
			
			socket.configureBlocking(false);
			
			int ops = socket.validOps();
			
			SelectionKey key = socket.register(selector, ops);
			
			BaseFramework.GetInstance().pushMessage("Server waiting for new connection");
			
			connected = true;
			
			while (connected == true)
			{
				//if (selector.selectedKeys().size() < maxRemoteClients)
				//{
					System.out.println("Server waiting for new connection");
					
					selector.select();
				//}
				
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = keys.iterator();
				
				while (keyIterator.hasNext() && connected == true)
				{	
					SelectionKey myKey = keyIterator.next();
					
					try
					{
						if (myKey.isAcceptable())
						{
							SocketChannel client = socket.accept();
							
							// If this is the first time through, start the worker thread.
							if (uiInterfaceThread.isAlive() == false)
							{
								uiInterfaceThread.start();
							}
							
							client.configureBlocking(false);
							
							client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
							
							BaseFramework.GetInstance().pushMessage("Connection accepted: " + client.getLocalAddress());
						}
						else if (myKey.isReadable())
						{
							SocketChannel client = (SocketChannel) myKey.channel();
	
							if (receive(client) == false)
							{
								inMessageBuffer.add("Disconnected from client");
								disconnectOneClient(myKey);
							}
						}
						else if (myKey.isWritable())
						{
							SocketChannel client = (SocketChannel) myKey.channel();
							send(client);
						}
						keyIterator.remove();
					}
					// Exception handling for ONE client.
					catch (IOException e)
					{
						BaseFramework.GetInstance().pushMessage("Disconnected from client. Reason: " + e.getMessage());
						disconnectOneClient(myKey);
						keyIterator.remove();
					}
				}
				Thread.sleep(serverTickRate);
			}
		}
		catch (IOException e)
		{
			// Port in use
			if (e instanceof BindException)
			{
				BaseFramework.GetInstance().pushMessage("Server port is already in use! Cannot start server.");
			}
			// Unknown / other IOException with server
			else
			{
				e.printStackTrace();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			if (e instanceof ClosedSelectorException)
			{
				// Handled closing the selector.
			}
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
		if (serverSocket == null)
			return false;
		if (serverSocket.isClosed())
			return false;
		if (selector == null)
			return false;
		if (selector.isOpen() == false)
			return false;
		if (isAtLeastOneClientConnected() == false)
			return false;
		if (disconnecting)
			return false;
		if (connected == false)
			return false;
		return true;
	}

	public boolean isAtLeastOneClientConnected()
	{
		Set<SelectionKey> keys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = keys.iterator();
		
		while (keyIterator.hasNext())
		{	
			SelectionKey myKey = keyIterator.next();
			
			if (myKey.isReadable() ||
				myKey.isWritable())
			{
				SocketChannel socket = (SocketChannel) myKey.channel();
				
				try
				{
					checkOpenSocket(socket);
					
					// If we get here we have a connected socket.
					return true;
				}
				catch (IOException e)
				{
					// We know this socket is closed.
				}
			}
			keyIterator.remove();
		}
		return false;
	}
	
	@Override
	public void disconnect()
	{	
		if (disconnecting == false)
		{
			// Catch SocketException in IOException on accept()
			disconnecting = true;
			
			BaseFramework.GetInstance().pushMessage("Server shutting down.");

			// Disconnect all clients
			Set<SelectionKey> keys = selector.keys();
			
			for (SelectionKey myKey : keys)
			{	
				disconnectOneClient(myKey);
			}
			
			// Disconnect server socket
			try
			{
				if (serverSocket != null && serverSocket.isClosed() == false)
				{
					serverSocket.close();
					
					BaseFramework.GetInstance().pushMessage("Server stopped.");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			// Disconnect selector
			try
			{
				if (selector != null && selector.isOpen())
				{
					selector.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			// Stops the uiInterfaceThread
			connected = false;
		}
	}
	
	protected void disconnectOneClient(SelectionKey key)
	{
		if (key.isReadable() ||
			key.isWritable())
		{
			SocketChannel client = (SocketChannel) key.channel();
			
			try
			{
				if (client != null && client.isOpen() == true)
				{
					outMessageBuffer.add(DISCONNECT_STRING);
					send(client);
					
					client.close();
				}
			}
			catch (IOException e)
			{
				// We know this socket is closed.
			}
			key.cancel();
		}
	}
	
	@Override
	public boolean isHost()
	{
		return true;
	}
}
