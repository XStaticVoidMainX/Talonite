package com.evanrobertcampbell.impl.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.evanrobertcampbell.talonite.ui.BaseController;

import javafx.fxml.*;
import javafx.scene.control.*;

/**
 * @author Evan Campbell
 */
public class SampleController extends BaseController implements Initializable
{
	public ToggleGroup toggleGroup;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		System.out.println("Loading user data");
	}

}
