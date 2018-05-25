package ahodanenok.ejb.invoke;

public final class EjbMethodResponse {

    public static EjbMethodResponse success(Object data, long executionTimeMs) {
        EjbMethodResponse response = new EjbMethodResponse();
        response.status = Status.SUCCESS;
        response.data = data;
        response.executionTimeMs = executionTimeMs;

        return response;
    }

    public static EjbMethodResponse error(Throwable error, long executionTimeMs) {
        EjbMethodResponse response = new EjbMethodResponse();
        response.status = Status.ERROR;
        response.error = error;
        response.executionTimeMs = executionTimeMs;

        return response;
    }

    public enum Status { SUCCESS, ERROR };

    private Object data;
    private Throwable error;
    private Status status;
    private long executionTimeMs;

    private EjbMethodResponse() { }

    public Object getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public Status getStatus() {
        return status;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String toString() {
        if (status == Status.SUCCESS) {
            return String.format("EjbMethodResponse | status=%s, data=%s, executionTimeMs=%d",
                    status, (data != null ? data.getClass().getName() : null), executionTimeMs);
        } else if (status == Status.ERROR) {
            return String.format("EjbMethodResponse | status=%s, error=%s, executionTimeMs=%d",
                    status, error.getMessage(), executionTimeMs);
        } else {
            throw new IllegalStateException("Unknown status: " + status);
        }
    }
}
