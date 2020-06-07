package com.ethlo.jc;

import org.codehaus.groovy.control.CompilationFailedException;

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
