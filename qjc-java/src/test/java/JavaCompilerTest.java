/*-
 * #%L
 * qjc-java
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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.ethlo.qjc.CompilationException;
import com.ethlo.qjc.Compiler;
import com.ethlo.qjc.IoUtil;
import com.ethlo.qjc.java.JavaCompiler;

public class JavaCompilerTest
{
    @Test
    public void compileSimplest() throws IOException, ClassNotFoundException
    {
        testCompilation(Collections.singletonMap("Test", "class Test{}"));
    }

    @Test(expected = CompilationException.class)
    public void compileInvalid() throws IOException, ClassNotFoundException
    {
        testCompilation(Collections.singletonMap("Test", "class Test{bar}"));
    }

    @Test
    public void compileMultiple() throws IOException, ClassNotFoundException
    {
        final Map<String, String> classDefs = new LinkedHashMap<>();
        classDefs.put("Test", "class Test{}");
        classDefs.put("Test2", "class Test2{private Test test = new Test();}");
        final Path classesDir = testCompilation(classDefs);
        final ClassLoader classLoader = new URLClassLoader(new URL[]{IoUtil.toURL(classesDir)});
        classLoader.loadClass("Test2");
    }

    private Path testCompilation(Map<String, String> classDefinitions) throws IOException, ClassNotFoundException
    {
        final Path srcDir = Files.createTempDirectory("java-compile-test");
        final Path classesDir = srcDir.resolve("classes");
        final ClassLoader classLoader = new URLClassLoader(new URL[]{IoUtil.toURL(classesDir)});
        final Compiler javaCompiler = new JavaCompiler(classLoader);
        Files.createDirectories(classesDir);
        for (final Map.Entry<String, String> e : classDefinitions.entrySet())
        {
            Files.write(srcDir.resolve(e.getKey() + ".java"), e.getValue().getBytes(StandardCharsets.UTF_8));
        }

        javaCompiler.compile(Collections.singleton(srcDir), classesDir);
        return classesDir;
    }
}
