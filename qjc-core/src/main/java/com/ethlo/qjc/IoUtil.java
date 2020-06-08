package com.ethlo.qjc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class IoUtil
{
    public static URL toURL(final Path path)
    {
        try
        {
            return path.toUri().toURL();
        }
        catch (MalformedURLException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    public static Optional<String> toString(final Path file)
    {
        try
        {
            return Optional.of(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            return Optional.empty();
        }
    }
}
