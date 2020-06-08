/*-
 * #%L
 * qjc-groovy
 * %%
 * Copyright (C) 2020 Morten Haraldsen (ethlo)
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
