package com.evanrobertcampbell.talonite.ui;

import java.net.URL;
import com.evanrobertcampbell.talonite.io.FileHelper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Evan Campbell
 */
public class SceneManager
{
	private static SceneManager s_Instance;
	
	private static final String SCENES_FOLDER_KEY = "sceneManager.scenesFolder";
	private static final String SCENES_LIST_KEY = "sceneManager.scenes";
	private static final String STYLESHEETS_FOLDER_KEY = "sceneManager.stylesheetsFolder";
	private static final String STYLESHEETS_KEY = "sceneManager.stylesheets";
	
	private String[] sceneNames;
	private String[] stylesheetNames;
	
	private BaseController currentController;
	
	public SceneManager()
	{
		buildFilesLists();
	}
	
	private void buildFilesLists()
	{
		sceneNames = FileHelper.buildFilesListFromProperties(BaseFramework.GetFrameworkProperties(), SCENES_FOLDER_KEY, SCENES_LIST_KEY);
		stylesheetNames = FileHelper.buildFilesListFromProperties(BaseFramework.GetFrameworkProperties(), STYLESHEETS_FOLDER_KEY, STYLESHEETS_KEY);
	}
	
	public static SceneManager GetInstance()
	{
		if (s_Instance == null)
		{
			s_Instance = new SceneManager();
		}
		
		return s_Instance;
	}
	
	public void loadScene(int sceneID, int stylesheetID) throws Exception
	{
		if (sceneID < 0 || sceneID >= sceneNames.length)
		{
			// TODO: Handle sceneID out of bounds
			throw new Exception("SceneManager::LoadScene - Scene ID is out of bounds: " + sceneID);
		}
		if (stylesheetID < 0 || stylesheetID >= stylesheetNames.length)
		{
			// TODO: Handle stylesheetID out of bounds
			throw new Exception("SceneManager::LoadScene - Stylesheet ID is out of bounds: " + stylesheetID);
		}
		
		URL sceneLocation = ClassLoader.getSystemClassLoader().getResource(sceneNames[sceneID]);
		URL stylesheetLocation = ClassLoader.getSystemClassLoader().getResource(stylesheetNames[stylesheetID]);
		
		// TODO: make loading scenes work from absolute path, relative path, and from classpath.
		
		//sceneLocation = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/scenes/Main.fxml");
		//sceneLocation = new URL("C:/Users/campb/OneDrive/Documents/Work/EclipseWorkspaces/Demo/JavaFXExample/assets/resources/scenes/Main.fxml");
		//sceneLocation = new URL()
		//sceneLocation = Paths.get("C:/Users/campb/OneDrive/Documents/Work/EclipseWorkspaces/Demo/JavaFXExample/assets/resources/scenes/Main.fxml").toUri().toURL();
		
		//FileInputStream sceneLocation = (FileInputStream) getClass().getClassLoader().getResourceAsStream("resources/scenes/Main.fxml");
		
		
		// Load valid scene
		//Parent root = (Parent)FXMLLoader.load(sceneLocation);
		FXMLLoader loader = new FXMLLoader(sceneLocation);
		Parent root = (Parent)loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(stylesheetLocation.toExternalForm());
		currentController = loader.<BaseController>getController();
		
		Stage primaryStage = BaseFramework.GetPrimaryStage();
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		pushMessage("SceneManager - Loaded scene: " + sceneNames[sceneID]);
	}
	
	public void loadScene(int sceneID) throws Exception
	{
		loadScene(sceneID, 0);
	}
	
	public void pushMessage(String message)
	{
		currentController.pushMessage(message);
	}
	
	public BaseController getCurrentController()
	{
		return currentController;
	}
}
