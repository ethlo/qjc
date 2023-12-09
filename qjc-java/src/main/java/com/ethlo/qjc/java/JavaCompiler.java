package com.ethlo.qjc.java;/*-
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
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethlo.qjc.CompilationException;
import com.ethlo.qjc.Compiler;
import com.ethlo.qjc.CompilerUtil;
import com.ethlo.qjc.StringUtils;

public class JavaCompiler implements Compiler
{
    private static final String JAVA_EXTENSION = "java";

    private static final Logger logger = LoggerFactory.getLogger(JavaCompiler.class);

    @Override
    public void compile(final Set<Path> sourcePaths, Path classesDirectory)
    {
        final javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
        {
            throw new IllegalStateException("You need to run build with JDK or have tools.jar on the classpath");
        }

        try (final StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), StandardCharsets.UTF_8))
        {
            final List<Path> sourceFiles = CompilerUtil.findSourceFiles(JAVA_EXTENSION, sourcePaths);
            if (sourceFiles.isEmpty())
            {
                logger.debug("No Java files found in {}", StringUtils.collectionToCommaDelimitedString(sourcePaths));
                return;
            }
            logger.info("Found {} java files", sourceFiles.size());

            logger.debug("Compiling: {}", StringUtils.collectionToCommaDelimitedString(sourceFiles));

            final Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjects(sourceFiles.toArray(new Path[0]));

            standardFileManager.setLocationFromPaths(StandardLocation.SOURCE_PATH, sourcePaths);
            standardFileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(classesDirectory));

            final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            final javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, standardFileManager, diagnostics, List.of(), null, compilationUnits);
            final Boolean retVal = task.call();
            final StringBuilder s = new StringBuilder();
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics())
            {
                s.append("\n").append(diagnostic);
            }

            if (!retVal)
            {
                throw new CompilationException(s.toString());
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
