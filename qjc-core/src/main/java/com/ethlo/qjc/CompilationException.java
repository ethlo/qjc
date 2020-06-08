package com.ethlo.qjc;

public class CompilationException extends RuntimeException
{
    public CompilationException(final String msg)
    {
        super(msg);
    }

    public CompilationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
