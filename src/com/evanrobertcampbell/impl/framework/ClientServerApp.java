package com.evanrobertcampbell.impl.framework;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * User-defined framework class with customized functionality.
 * 
 * @author Evan Campbell
 */
public class ClientServerApp extends BaseFramework
{
	public ClientServerApp()
	{
		copyPropertiesToSession(
			SessionVariableKeys.USER_NAME,
			SessionVariableKeys.HOST_NAME,
			SessionVariableKeys.PORT_NUMBER
		);
	}
}
