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

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.crbk.db.common.Constants;
import de.crbk.db.common.DatabaseRoles;
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

    /**
     * checks if the given identification number is given in one of the user tables <br>
     * set right role for this session
     * 
     * @param identificationNumber
     * @throws DataToolException
     */
    public void setRoleForInput(String identificationNumber)
        throws DataToolException
    {
        LOG.debug("Try to find role for following identification number: " + identificationNumber);

        if (existInTable(DatabaseUserTables.ASSISTANT_TABLE, identificationNumber))
        {
            setRoleForSession(DatabaseRoles.ASSISTANT);
            LOG.info("Role set to: " + DatabaseRoles.ASSISTANT);
        }
        else if (existInTable(DatabaseUserTables.PROFESSOR_TABLE, identificationNumber))
        {
            setRoleForSession(DatabaseRoles.PROFESSOR);
            LOG.info("Role set to: " + DatabaseRoles.PROFESSOR);
        }
        else if (existInTable(DatabaseUserTables.STUDENT_TABLE, identificationNumber))
        {
            setRoleForSession(DatabaseRoles.STUDENT);
            LOG.info("Role set to: " + DatabaseRoles.STUDENT);
        }
        else if (existInTable(DatabaseUserTables.ADMIN_EMPLOYEES_TABLE, identificationNumber))
        {
            setRoleForSession(DatabaseRoles.ADMIN_EMPLOYEE);
            LOG.info("Role set to: " + DatabaseRoles.ADMIN_EMPLOYEE);
        }
        else
        {
            AlertDialog.startDialog(AlertType.WARNING, "Identification number does not exist.",
                                    "The given indentification number '" + identificationNumber
                                        + "' does not exist in database.");
        }
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
     * @param role
     * @throws DataToolException
     */
    private void setRoleForSession(String role)
        throws DataToolException
    {
        LOG.info("Set following role for current session: " + role);

        String statement = "SET ROLE '" + role + "'";

        LOG.debug("Execute following statement: " + statement);
        try (PreparedStatement stmt = databaseConnection.prepareStatement(statement))
        {
            stmt.execute();
        }
        catch (SQLException e)
        {
            throw new DataToolException("Error while setting role for current session.", e);
        }
    }

    /**
     * method to get all avaible views
     * 
     * @return
     * @throws DataToolException
     */
    private List<String> getAllViews()
        throws DataToolException
    {
        LOG.info("Get all views for current role.");

        String query = ""; // TODO
        LOG.debug("Execute for views: " + query);
        try (PreparedStatement stmt = databaseConnection.prepareStatement(query);
                        ResultSet result = stmt.executeQuery())
        {

            // TODO

        }
        catch (SQLException e)
        {
            throw new DataToolException("Error while getting avaible views.", e);
        }

        return new ArrayList<>();
    }
}
