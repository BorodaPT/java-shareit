package ru.practicum.exception;

public class ExceptionNotFound extends RuntimeException {

    private final String nameExcept;

    public String getNameExcept() {
        return nameExcept;
    }

    public ExceptionNotFound(String nameExcept, String message) {
        super(message);
        this.nameExcept = nameExcept;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
