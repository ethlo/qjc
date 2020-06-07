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
    public void compileJavaSimplest() throws IOException, ClassNotFoundException
    {
        testJavaCompilation("class Test{}");
    }

    @Test(expected=CompilationException.class)
    public void compileJavaInvalid() throws IOException, ClassNotFoundException
    {
        testJavaCompilation("class Test{bar}");
    }

    private void testJavaCompilation(final String content) throws IOException, ClassNotFoundException
    {
        final JavaCompiler javaCompiler = new JavaCompiler(JavaCompilerTest.class.getClassLoader());
        final Path srcDir = Files.createTempDirectory("java-compile-test");
        final Path classesDir = srcDir.resolve("classes");
        Files.write(srcDir.resolve("Test.java"), content.getBytes(StandardCharsets.UTF_8));
        javaCompiler.compile(Collections.singleton(srcDir), classesDir);
        new URLClassLoader(new URL[]{IoUtil.toURL(classesDir)}).loadClass("Test");
    }
}