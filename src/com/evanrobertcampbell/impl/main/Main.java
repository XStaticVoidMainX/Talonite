package com.evanrobertcampbell.impl.main;

import com.evanrobertcampbell.impl.framework.ClientServerApp;

/**
 * Talonite is a multi-purpose, modular Java framework with many pluggable features.
 * This implementation example creates a sample client-server chat application demonstrating usage the Talonite framework APIs.
 * This implementation of Talonite uses a JavaFX based UI.
 * 
 * All customized functionality not part of the Talonite API are inside of the impl package.
 * 
 * @author Evan Campbell
 */
public class Main
{
	public static void main(String[] args)
	{
		ClientServerApp.launch(ClientServerApp.class, args);
	}	
}
