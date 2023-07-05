package ru.practicum.shareit.exception;

public class ExceptionDataRequest extends RuntimeException {

    private final String nameExcept;

    public String getNameExcept() {
        return nameExcept;
    }

    public ExceptionDataRequest(String nameExcept, String message) {
        super(message);
        this.nameExcept = nameExcept;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
