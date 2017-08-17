package com.evanrobertcampbell.talonite.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Evan Campbell
 */
public class BaseFramework extends Application
{
	private static BaseFramework s_Instance = null;
	
	private static Stage s_PrimaryStage = null;
	
	private static Properties s_FrameworkProperties = null;
	
	private static final String PATH_TO_PROPERTIES = "resources/settings/FrameworkProperties.properties";
	
	private static HashMap<String, Object> sessionVariables;
	
	protected BaseFramework()
	{
		s_Instance = this;
	}
	
	public static BaseFramework GetInstance()
	{
		if (s_Instance == null)
		{
			throw new IllegalStateException("Critical error: Framework instance is null! Make sure that BaseFramework's constructor gets called to set instance!");
		}
		return s_Instance;
	}

	public static Stage GetPrimaryStage()
	{
		return s_PrimaryStage;
	}
	
	public static Properties GetFrameworkProperties()
	{
		if (s_FrameworkProperties == null)
		{
			loadFrameworkProperties(PATH_TO_PROPERTIES);
		}
		return s_FrameworkProperties;
	}
	
	public static HashMap<String, Object> GetSessionVariables()
	{
		if (sessionVariables == null)
		{
			sessionVariables = new HashMap<String, Object>();
		}
		return sessionVariables;
	}
	
	public static void GetSessionVariables(HashMap<String, Object> newSessionVariables)
	{
		sessionVariables = newSessionVariables;
	}
	
	public static Object GetSessionVariable(String key)
	{
		if (sessionVariables == null)
		{
			sessionVariables = new HashMap<String, Object>();
		}
		return sessionVariables.get(key);
	}
	
	public static void SetSessionVariable(String key, Object value)
	{
		if (sessionVariables == null)
		{
			sessionVariables = new HashMap<String, Object>();
		}
		sessionVariables.put(key, value);
	}
	
	private static void loadFrameworkProperties(String pathToProperties)
	{
		try (final InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(pathToProperties))
		{
			s_FrameworkProperties = new Properties();
			s_FrameworkProperties.load(stream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void copyPropertyToSession(String propertyKey)
	{
		GetSessionVariables().put(propertyKey, GetFrameworkProperties().getProperty(propertyKey));
	}
	
	public void copyPropertiesToSession(String... propertyKeys)
	{
		for (String propertyKey : propertyKeys)
		{
			copyPropertyToSession(propertyKey);
		}
	}
	
	public void pushMessage(String message)
	{
		SceneManager.GetInstance().pushMessage(message);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		if (s_Instance == null)
		{
			throw new IllegalStateException("Critical error: Framework instance is null! Make sure that BaseFramework's constructor gets called to set instance!");
		}
		s_PrimaryStage = primaryStage;
		
		SceneManager sceneManager = SceneManager.GetInstance();
		
		sceneManager.loadScene(0);
	}
	
}
