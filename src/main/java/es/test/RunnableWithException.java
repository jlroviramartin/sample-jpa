package es.test;

public interface RunnableWithException<TException extends Exception> {
    void run() throws TException;
}
