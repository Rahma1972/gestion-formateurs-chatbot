package tn.enicarthage.gestionformation.common.exception;

public class BusinessException extends RuntimeException {
    private final Object[] args;

    public BusinessException(String messageKey) {
        this(messageKey, new Object[0]);
    }

    public BusinessException(String messageKey, Object... args) {
        super(messageKey);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}