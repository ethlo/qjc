import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.Test;

import com.ethlo.qjc.CompilationException;
import com.ethlo.qjc.Compiler;
import com.ethlo.qjc.groovy.GroovyCompiler;
import groovy.lang.GroovyClassLoader;

public class GroovyCompilerTest
{
    @Test
    public void compileSimplest() throws IOException, ClassNotFoundException
    {
        testCompilation("class Test{}");
    }

    @Test(expected = CompilationException.class)
    public void compileInvalid() throws IOException, ClassNotFoundException
    {
        testCompilation("class Test{{bar}");
    }

    private void testCompilation(final String content) throws IOException, ClassNotFoundException
    {
        final GroovyClassLoader classLoader = new GroovyClassLoader(GroovyCompilerTest.class.getClassLoader());
        final Compiler compiler = new GroovyCompiler(classLoader);
        final Path srcDir = Files.createTempDirectory("groovy-compile-test");
        final Path classesDir = srcDir.resolve("classes");
        Files.createDirectories(classesDir);
        Files.write(srcDir.resolve("Test.groovy"), content.getBytes(StandardCharsets.UTF_8));
        compiler.compile(Collections.singleton(srcDir), classesDir);
        classLoader.loadClass("Test");
    }
}