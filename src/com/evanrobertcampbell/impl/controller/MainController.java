package com.evanrobertcampbell.impl.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.evanrobertcampbell.talonite.ui.BaseController;
import com.evanrobertcampbell.talonite.ui.BaseFramework;
import com.evanrobertcampbell.talonite.ui.SceneManager;
import com.evanrobertcampbell.impl.framework.Scenes;

import javafx.application.Platform;
import javafx.fxml.Initializable;

/**
 * @author Evan Campbell
 */
public class MainController extends BaseController implements Initializable
{	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		BaseFramework.GetPrimaryStage().setTitle("Talonite Client-Server Example");
	}
	
	public void HostGameClicked()
	{
		System.out.println("HostGameClicked");
		
		try
		{
			SceneManager.GetInstance().loadScene(Scenes.HOST_GAME_SCENE_ID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void JoinGameClicked()
	{
		System.out.println("JoinGameClicked");
		
		try
		{
			SceneManager.GetInstance().loadScene(Scenes.JOIN_GAME_SCENE_ID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void ExitClicked()
	{
		Platform.exit();
	}
}
