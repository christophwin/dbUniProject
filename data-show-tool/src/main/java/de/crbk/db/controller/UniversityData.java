package de.crbk.db.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.crbk.db.common.Constants;
import de.crbk.db.exceptions.DataToolException;
import de.crbk.db.ui.UserMainInterface;
import javafx.application.Application;

/**
 * Only for application launch
 */
public class UniversityData
{

    private static final Logger LOG = Logger.getLogger(UniversityData.class);

    private static UniversityData instance = null;

    private Connection databaseConnection = null;

    /**
     * start method
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        UniversityData.getInstance().execute();
    }

    /**
     * method to get controller instance
     * 
     * @return
     */
    public static UniversityData getInstance()
    {
        if (instance == null)
        {
            instance = new UniversityData();
        }
        return instance;
    }

    /**
     * main execution method to start program
     */
    public void execute()
    {
        try
        {
            DOMConfigurator.configure(this.getClass().getResource(Constants.LOG4J));
            Application.launch(UserMainInterface.class); // launches the main frame
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        finally
        {
            closeDatabaseConnection();
        }

    }

    /**
     * create the initial database connection <br>
     * set 'auto-commit' to 'false'
     * 
     * @throws DataToolException if a error occur while connection
     */
    public void createDatabaseConnection()
        throws DataToolException
    {
        LOG.info("Start to connect to database. Read properties.");
        Properties props = new Properties();
        try
        {
            if (databaseConnection != null && databaseConnection.isValid(1000))
            {
                LOG.debug("A database connection already exist.");
                return;
            }

            props.load(new FileInputStream(new File(this.getClass().getResource(Constants.PROPERTIES_FILE).toURI())));

            String connectionUrl = props.getProperty(Constants.DATABASE_URL_PROP);
            LOG.info("URL: " + connectionUrl);

            databaseConnection = DriverManager.getConnection(connectionUrl, props.getProperty(Constants.USER_PROP),
                                                             props.getProperty(Constants.PASSWORD_PROP));

            databaseConnection.setAutoCommit(false);
            LOG.info("Database connection is valid: " + databaseConnection.isValid(1000));
        }
        catch (IOException | URISyntaxException | SQLException e)
        {
            throw new DataToolException("An error occour while creating database connection", e);
        }
    }

    /**
     * close the database connection if one exist <br>
     * no commit will be executed before
     */
    private void closeDatabaseConnection()
    {
        LOG.info("Database connection will be closed.");

        if (databaseConnection != null)
        {
            try
            {
                databaseConnection.close();
            }
            catch (SQLException e)
            {
                LOG.error("Error while closing database connection.", e);
            }
        }
    }
}
