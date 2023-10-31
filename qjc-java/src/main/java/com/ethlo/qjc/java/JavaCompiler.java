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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
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

    private static List<String> buildCompilerOptions(final Collection<Path> sourcePath, final Path classesDirectory)
    {
        final Map<String, String> compilerOpts = new LinkedHashMap<>();

        compilerOpts.put("d", classesDirectory.toAbsolutePath().toString());

        compilerOpts.put("sourcepath", StringUtils.collectionToDelimitedString(sourcePath, File.separator));

        final List<String> opts = new ArrayList<>(compilerOpts.size() * 2);
        for (Map.Entry<String, String> compilerOption : compilerOpts.entrySet())
        {
            opts.add("-" + compilerOption.getKey());
            String value = compilerOption.getValue();
            if (!StringUtils.isEmpty(value))
            {
                opts.add(value);
            }
        }
        return opts;
    }

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
            final List<File> sourceFiles = CompilerUtil.findSourceFiles(JAVA_EXTENSION, sourcePaths.toArray(new Path[0])).stream().map(Path::toFile).collect(Collectors.toList());
            if (sourceFiles.isEmpty())
            {
                logger.debug("No Java files found in {}", StringUtils.collectionToCommaDelimitedString(sourcePaths));
                return;
            }
            logger.info("Found {} java files", sourceFiles.size());

            logger.debug("Compiling: {}", StringUtils.collectionToCommaDelimitedString(sourceFiles));

            final Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjectsFromFiles(sourceFiles);

            List<String> compilerOptions = buildCompilerOptions(sourcePaths, classesDirectory);
            logger.debug("Compiler options: {}", StringUtils.collectionToCommaDelimitedString(compilerOptions));

            final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            final javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, standardFileManager, diagnostics, compilerOptions, null, compilationUnits);
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
