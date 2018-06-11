package de.crbk.db.exceptions;

/**
 * Exception for this program extends form 'Exception' class
 */
public class DataToolException extends Exception
{

    public DataToolException()
    {
        super();
    }

    public DataToolException(String errorMessage)
    {
        super(errorMessage);
    }

    public DataToolException(Throwable e)
    {
        super(e);
    }

    public DataToolException(String errorMessage, Throwable e)
    {
        super(errorMessage, e);
    }

}
