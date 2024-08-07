package main.java.exceptions;

public class InvalidInputException extends DBAppException {

    public InvalidInputException(){
        super();
    }

    public InvalidInputException(String message){
        super(message);
    }

}
