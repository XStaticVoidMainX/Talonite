package com.evanrobertcampbell.talonite.io;

import java.io.File;
import java.util.Properties;

import com.evanrobertcampbell.talonite.ui.BaseFramework;

/**
 * @author Evan Campbell
 */
public class FileHelper
{
	public static final String FILES_DELIMETER = ",";
	public static final String FOLDER_SEPARATOR = "/";
	public static final String EMPTY_STRING = "";
	
	public static String[] buildFilesListFromProperties(Properties properties, String folderKey, String filesKey)
	{
		// Load
		String filesFolder = BaseFramework.GetFrameworkProperties().getProperty(folderKey);
		String filesDelimitedString = BaseFramework.GetFrameworkProperties().getProperty(filesKey);
		String[] filesList = null;
		
		// Start with valid path
		if (filesFolder == null)
		{
			filesFolder = EMPTY_STRING;
		}
		if (!filesFolder.trim().equals(EMPTY_STRING))
		{
			if (!filesFolder.endsWith(FOLDER_SEPARATOR))
			{
				filesFolder += FOLDER_SEPARATOR;
			}
		}
		
		// Get list of files from properties.
		if (filesDelimitedString != null && filesDelimitedString.trim() != EMPTY_STRING)
		{
			filesList = filesDelimitedString.split(FILES_DELIMETER);
			// prepend the scenesFolder to each file name
			for (int i = 0; i < filesList.length; i++)
			{
				filesList[i] = filesFolder + filesList[i];
			}
		}
		
		// Generate list based on all files in the directory.
		if ((filesList == null || filesList.length <= 0) && filesFolder.trim() != FOLDER_SEPARATOR)
		{
			try
			{
				File folder = new File(filesFolder);
				File[] listOfFiles = folder.listFiles();
				if (listOfFiles != null && listOfFiles.length != 0)
				{
					filesList = new String[listOfFiles.length];
					for (int i = 0; i < listOfFiles.length; i++)
					{
						File file = listOfFiles[i];
						if (file.isFile())
						{
							filesList[i] = file.getName();
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return filesList;
	}
}
