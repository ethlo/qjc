package com.ethlo.jc;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.Test;

import com.ethlo.jc.java.JavaCompiler;

public class JavaCompilerTest
{
    @Test
    public void compileSimplest() throws IOException, ClassNotFoundException
    {
        testCompilation("class Test{}");
    }

    @Test(expected=CompilationException.class)
    public void compileInvalid() throws IOException, ClassNotFoundException
    {
        testCompilation("class Test{bar}");
    }

    private void testCompilation(final String content) throws IOException, ClassNotFoundException
    {
        final Path srcDir = Files.createTempDirectory("java-compile-test");
        final Path classesDir = srcDir.resolve("classes");
        final ClassLoader classLoader = new URLClassLoader(new URL[]{IoUtil.toURL(classesDir)});
        final Compiler javaCompiler = new JavaCompiler(classLoader);
        Files.createDirectories(classesDir);
        Files.write(srcDir.resolve("Test.java"), content.getBytes(StandardCharsets.UTF_8));
        javaCompiler.compile(Collections.singleton(srcDir), classesDir);
        final ClassLoader classLoader2 = new URLClassLoader(new URL[]{IoUtil.toURL(classesDir)});
        classLoader2.loadClass("Test");
    }
}