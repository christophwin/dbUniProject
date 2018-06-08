package de.crbk.db.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.crbk.db.common.Constants;
import de.crbk.db.ui.UserMainInterface;
import javafx.application.Application;

/**
 * Only for application launch
 */
public class UniversityData
{

    private static final Logger LOG = Logger.getLogger(UniversityData.class);

    private static UniversityData instance = null;

    public static UniversityData getInstance()
    {
        if (instance == null)
        {
            instance = new UniversityData();
        }
        return instance;
    }

    public static void main(String[] args)
    {
        UniversityData.getInstance().execute();
    }

    public void execute()
    {
        try
        {
            DOMConfigurator.configure(this.getClass().getResource(Constants.LOG4J));
            Application.launch(UserMainInterface.class);
        }
        catch (Exception e)
        {
            LOG.error(e);
        }
        finally
        {
            //close db connection
        }

    }
}
