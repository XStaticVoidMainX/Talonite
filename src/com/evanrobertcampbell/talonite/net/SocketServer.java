package com.evanrobertcampbell.talonite.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class SocketServer extends SocketSession
{
	protected Socket server = null;
	protected ServerSocket serverSocket = null;
	
	protected boolean waitingForConnection = false;
	protected boolean disconnecting = false;
	
	@Override
	public void run()
	{
		try
		{
			BaseFramework.GetInstance().pushMessage("Starting server");
			
			waitingForConnection = true;
			
			serverSocket = new ServerSocket(Integer.parseInt((String) getProperty(SOCKETSESSION_PORT)));
			
			while (true)
			{
				BaseFramework.GetInstance().pushMessage("Waiting for client on port: " + serverSocket.getLocalPort() + "...");
				
				server = serverSocket.accept();
				
				waitingForConnection = false;
				
				BaseFramework.GetInstance().pushMessage("Just connected to: " + server.getRemoteSocketAddress());
				
				PrintWriter toClient = new PrintWriter(server.getOutputStream(), true);
				
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(server.getInputStream()));
				
				String line = fromClient.readLine();
				
				BaseFramework.GetInstance().pushMessage("Server received: " + line);
				
				toClient.println("Thank you for connecting to " + server.getLocalSocketAddress() + "\n");
			}
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// Handle SocketException on accept() when disconnecting
			if (disconnecting == true)
			{
				BaseFramework.GetInstance().pushMessage("Server disconnected.");
			}
			// Other error
			else
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
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
		if (serverSocket == null)
			return false;
		if (serverSocket.isClosed())
			return false;
		if (server == null)
			return false;
		if (server.isClosed())
			return false;
		if (disconnecting)
			return false;
		return true;
	}

	@Override
	public void disconnect()
	{
		if (disconnecting == false)
		{
			// Catch SocketException in IOException on accept()
			disconnecting = true;
			
			BaseFramework.GetInstance().pushMessage("Server: attempting to disconnect from client");
			
			try
			{
				if (server != null && server.isClosed() == false)
				{
					server.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (serverSocket != null && serverSocket.isClosed() == false)
				{
					serverSocket.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
