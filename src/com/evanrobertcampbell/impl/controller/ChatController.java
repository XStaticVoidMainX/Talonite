package com.evanrobertcampbell.impl.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.evanrobertcampbell.impl.framework.Scenes;
import com.evanrobertcampbell.impl.framework.SessionVariableKeys;
import com.evanrobertcampbell.talonite.net.SocketSession;
import com.evanrobertcampbell.talonite.net.SocketUtils;
import com.evanrobertcampbell.talonite.ui.BaseController;
import com.evanrobertcampbell.talonite.ui.BaseFramework;
import com.evanrobertcampbell.talonite.ui.SceneManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChatController extends BaseController implements Initializable
{
	public TextField chatEntryText;
	public TextArea chatLogText;
	public Button chatSendButton;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		// Setup send on enter
		chatEntryText.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER)
				{
					SendClicked();
				}
			}
		});
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{				
				chatEntryText.requestFocus();
			}
		});
		
		// Setup autoscrolling text area
		// https://stackoverflow.com/a/20568196
		chatLogText.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				Platform.runLater(() -> chatLogText.setScrollTop(Double.MAX_VALUE));
			}
		});
		
		// Start the socket session
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{				
				SocketSession session = SocketUtils.createSocketSession();
				
				BaseFramework.SetSessionVariable(SessionVariableKeys.SOCKET_SESSION, session);
				
				session.run();
			}
		});
		thread.start();
	}
	
	public void SendClicked()
	{
		System.out.println("Send clicked");
		
		chatLogText.appendText(chatEntryText.getText() + "\n");
		chatEntryText.setText("");
		
		chatEntryText.requestFocus();
	}
	
	public void DisconnectClicked()
	{
		System.out.println("Disconnect clicked");
		
		Alert alert = new Alert(AlertType.CONFIRMATION, "Disconnect?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES)
		{
			disconnect();
		}
	}
	
	protected void disconnect()
	{
		System.out.println("Disconnecting");
		
		SocketSession socketSession = (SocketSession) BaseFramework.GetSessionVariable(SessionVariableKeys.SOCKET_SESSION);
		socketSession.disconnect();
		socketSession = null;
		BaseFramework.SetSessionVariable(SessionVariableKeys.SOCKET_SESSION, null);
		
		try
		{
			SceneManager.GetInstance().loadScene(Scenes.MAIN_SCENE_ID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void pushMessage(String message)
	{
		chatLogText.appendText(message + "\n");
	}
}
