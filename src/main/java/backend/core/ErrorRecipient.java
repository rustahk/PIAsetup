package backend.core;

public interface ErrorRecipient {
    void standartError(String comment, Exception e);
}
