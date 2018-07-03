package de.crbk.db.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.print.URIException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.crbk.db.common.Constants;
import de.crbk.db.common.DatabaseUsers;
import de.crbk.db.common.DatabaseUserTables;
import de.crbk.db.exceptions.DataToolException;
import de.crbk.db.ui.AlertDialog;
import de.crbk.db.ui.UserMainInterface;
import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;

/**
 * Only for application launch
 */
public class UniversityData
{

    private static final Logger LOG = Logger.getLogger(UniversityData.class);

    private static UniversityData instance = null;

    private Connection databaseConnection = null;

    private String currentUser;

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
     * create conection for main user
     * 
     * @throws DataToolException
     */
    public void createDatabaseConnection()
        throws DataToolException
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(this.getClass().getResource(Constants.PROPERTIES_FILE).toURI())));
            createDatabaseConnection(props.getProperty(Constants.USER_PROP),
                                     props.getProperty(Constants.PASSWORD_PROP));
        }
        catch (IOException | URISyntaxException e)
        {
            throw new DataToolException("Error while getting root connection.");
        }
    }

    /**
     * create the initial database connection <br>
     * set 'auto-commit' to 'false'
     * 
     * @throws DataToolException if a error occur while connection
     */
    public void createDatabaseConnection(String user, String password)
        throws DataToolException
    {
        LOG.info("Start to connect to database. Read properties.");

        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(this.getClass().getResource(Constants.PROPERTIES_FILE).toURI())));

            String connectionUrl = props.getProperty(Constants.DATABASE_URL_PROP);
            LOG.info("URL: " + connectionUrl);

            databaseConnection = DriverManager.getConnection(connectionUrl, user, password);
            
            databaseConnection.setAutoCommit(false);
            LOG.info("Database connection is valid: " + databaseConnection.isValid(1000));
        }
        catch (SQLException | IOException | URISyntaxException e)
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

    /**
     * checks if the given identification number is given in one of the user tables <br>
     * set right role for this session
     * 
     * @param identificationNumber
     * @return true -> number is known false -> number is nor known
     * @throws DataToolException
     */
    public boolean setUserForInput(String identificationNumber)
        throws DataToolException
    {
        LOG.debug("Try to find role for following identification number: " + identificationNumber);

        if (existInTable(DatabaseUserTables.ASSISTANT_TABLE, identificationNumber))
        {
            setUser(DatabaseUsers.ASSISTANT);
            LOG.info("User set to: " + DatabaseUsers.ASSISTANT);
        }
        else if (existInTable(DatabaseUserTables.PROFESSOR_TABLE, identificationNumber))
        {
            setUser(DatabaseUsers.PROFESSOR);
            LOG.info("User set to: " + DatabaseUsers.PROFESSOR);
        }
        else if (existInTable(DatabaseUserTables.STUDENT_TABLE, identificationNumber))
        {
            setUser(DatabaseUsers.STUDENT);
            LOG.info("User set to: " + DatabaseUsers.STUDENT);
        }
        else if (existInTable(DatabaseUserTables.ADMIN_EMPLOYEES_TABLE, identificationNumber))
        {
            setUser(DatabaseUsers.ADMIN_EMPLOYEE);
            LOG.info("User set to: " + DatabaseUsers.ADMIN_EMPLOYEE);
        }
        else
        {
            AlertDialog.startDialog(AlertType.WARNING, "Identification number does not exist.",
                                    "The given indentification number '" + identificationNumber
                                        + "' does not exist in database.");
            return false;
        }

        return true;
    }

    /**
     * check if given identification number exist in this table
     * 
     * @param table
     * @param identificationNumber
     * @return
     * @throws DataToolException
     */
    private boolean existInTable(String table, String identificationNumber)
        throws DataToolException
    {
        String statement =
            "SELECT * FROM " + table + " WHERE " + DatabaseUserTables.ID_COLUMN + " = '" + identificationNumber + "'";

        LOG.debug("Execute following statement: " + statement);

        try (PreparedStatement stmt = databaseConnection.prepareStatement(statement);
                        ResultSet result = stmt.executeQuery())
        {
            return result.next();
        }
        catch (SQLException e)
        {
            throw new DataToolException("Error occur while checking if currnet identification number '"
                + identificationNumber + "' exist in following table '" + table + "'.", e);
        }
    }

    /**
     * set given role for session
     * 
     * @param user
     * @throws DataToolException
     */
    private void setUser(String user)
        throws DataToolException
    {
        LOG.info("Set following user for current session: " + user);
        closeDatabaseConnection();
        createDatabaseConnection(user, user);
        this.currentUser = user;
    }

    /**
     * method to get all avaible views
     * 
     * @return
     * @throws DataToolException
     */
    public List<String> getAllViews()
        throws DataToolException
    {
        LOG.info("Get all views for current user.");

        String databaseName;
        try
        {
            databaseName = databaseConnection.getCatalog();
        }
        catch (SQLException e)
        {
            throw new DataToolException("Error while getting database name.", e);
        }

        String query = "SHOW FULL TABLES IN " + databaseName + " WHERE TABLE_TYPE LIKE 'VIEW'";
        LOG.debug("Execute for views: " + query);
        List<String> views = new ArrayList<>();
        try (PreparedStatement stmt = databaseConnection.prepareStatement(query);
                        ResultSet result = stmt.executeQuery())
        {
            result.beforeFirst();
            while (result.next())
            {
                views.add(result.getString(1));
            }
        }
        catch (SQLException e)
        {
            throw new DataToolException("Error while getting avaible views.", e);
        }

        LOG.info("Views for current role.");
        return views;
    }

    /**
     * execute SQL statement without result
     * 
     * @param statement SQL statement
     * @throws SQLException thrown if exception occurs
     */
    public void executeSqlStatement(String statement)
        throws SQLException
    {
        LOG.info("Execute following SQL-statement: " + statement);
        try (PreparedStatement stmt = databaseConnection.prepareStatement(statement))
        {
            stmt.execute();
        }
        databaseConnection.commit();
        LOG.debug("SQL executed successful.");
    }

    /**
     * get database connection
     * 
     * @return
     */
    public Connection getDatabaseConnection()
    {
        return databaseConnection;
    }

    public String getCurrentRole()
    {
        return currentUser;
    }
}
