package main.java.exceptions;

public class TableAlreadyExistsException extends DBAppException {

    public TableAlreadyExistsException(){
        super();
    }

    public TableAlreadyExistsException(String message){
        super(message);
    }

}
