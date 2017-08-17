package com.evanrobertcampbell.impl.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import com.evanrobertcampbell.talonite.net.SocketSession;
import com.evanrobertcampbell.talonite.net.SocketUtils;
import com.evanrobertcampbell.talonite.ui.BaseController;
import com.evanrobertcampbell.talonite.ui.BaseFramework;
import com.evanrobertcampbell.talonite.ui.SceneManager;
import com.evanrobertcampbell.impl.framework.Scenes;
import com.evanrobertcampbell.impl.framework.SessionVariableKeys;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * @author Evan Campbell
 */
public class HostGameController extends BaseController implements Initializable
{
	public TextField playerNameTextField;
	public TextField hostNameTextField;
	public TextField portNumberTextField;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		HashMap<String, Object> sessionVars = BaseFramework.GetSessionVariables();
		Properties properties = BaseFramework.GetFrameworkProperties();
		
		playerNameTextField.setText((String) sessionVars.get(SessionVariableKeys.USER_NAME));
		hostNameTextField.setText(properties.getProperty(SessionVariableKeys.HOST_NAME));
		portNumberTextField.setText((String) sessionVars.get(SessionVariableKeys.PORT_NUMBER));
	}
	
	public void HostGameClicked()
	{
		System.out.println("HostGameClicked");

		PersistData();
		
		try
		{
			SceneManager.GetInstance().loadScene(Scenes.CHAT_SCENE_ID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
//		Thread thread = new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{				
//				SocketSession session = SocketUtils.createSocketSession();
//				
//				session.run();
//			}
//		});
//		thread.start();
	}
	
	public void BackClicked()
	{
		System.out.println("BackClicked");
		
		PersistData();
		
		try
		{
			SceneManager.GetInstance().loadScene(Scenes.MAIN_SCENE_ID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void PersistData()
	{
		HashMap<String, Object> sessionVars = BaseFramework.GetSessionVariables();
		
		sessionVars.put(SocketUtils.IS_HOST, true);
		sessionVars.put(SessionVariableKeys.USER_NAME, playerNameTextField.getText());
		sessionVars.put(SessionVariableKeys.HOST_NAME, hostNameTextField.getText());
		sessionVars.put(SessionVariableKeys.PORT_NUMBER, portNumberTextField.getText());
	}
}
