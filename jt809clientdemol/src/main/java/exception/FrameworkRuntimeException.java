package exception;

public class FrameworkRuntimeException  extends Exception{
    private static final long serialVersionUID = -6293662498600553602L;
    private IError error;
    private String extMessage;

    public IError getError() {
        return error;
    }

    public void setError(IError error) {
        this.error = error;
    }

    public String getExtMessage() {
        return extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public FrameworkRuntimeException(Throwable cause) {
        super(cause);
        //this.error = NEError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        if (cause instanceof FrameworkRuntimeException) {
            FrameworkRuntimeException fe = (FrameworkRuntimeException)cause;
            this.error = fe.getError();
            this.extMessage = fe.getMessage();
        }

    }

}
