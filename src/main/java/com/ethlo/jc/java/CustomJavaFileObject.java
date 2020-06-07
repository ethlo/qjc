package com.ethlo.jc.java;

/*-
 * #%L
 * jc
 * %%
 * Copyright (C) 2018 - 2019 Morten Haraldsen (ethlo)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

class CustomJavaFileObject implements JavaFileObject
{
    private final String binaryName;
    private final URI uri;
    private final String name;

    public CustomJavaFileObject(String binaryName, URI uri)
    {
        this.uri = uri;
        this.binaryName = binaryName;
        name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
    }

    @Override
    public URI toUri()
    {
        return uri;
    }

    @Override
    public InputStream openInputStream() throws IOException
    {
        return uri.toURL().openStream(); // easy way to handle any URI!
    }

    @Override
    public OutputStream openOutputStream() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified()
    {
        return 0;
    }

    @Override
    public boolean delete()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind()
    {
        return Kind.CLASS;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind)
    {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName())
                || getName().endsWith("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel()
    {
        throw new UnsupportedOperationException();
    }

    public String binaryName()
    {
        return binaryName;
    }
}
