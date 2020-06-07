package com.ethlo.jc.groovy;

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

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethlo.jc.Compiler;
import com.ethlo.jc.CompilerUtil;
import com.ethlo.jc.IoUtil;
import com.ethlo.jc.StringUtils;
import groovy.lang.GroovyClassLoader;

public class GroovyCompiler implements Compiler
{
    private static final String GROOVY_EXTENSION = "groovy";

    private static final Logger logger = LoggerFactory.getLogger(GroovyCompiler.class);

    private final GroovyClassLoader classLoader;

    public GroovyCompiler(final GroovyClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    @Override
    public void compile(final Set<Path> sourcePaths, final Path classesDirectory)
    {
        logger.debug("Groovy source paths: {}", StringUtils.collectionToCommaDelimitedString(sourcePaths));
        final CompilationUnit compileUnit = new CompilationUnit(classLoader);
        final List<Path> sourceFiles = CompilerUtil.findSourceFiles(GROOVY_EXTENSION, sourcePaths.toArray(new Path[0]));
        if (sourceFiles.isEmpty())
        {
            logger.debug("No Groovy files found in {}", StringUtils.collectionToCommaDelimitedString(sourcePaths));
            return;
        }
        logger.info("Found {} groovy files", sourceFiles.size());

        for (Path sourceFile : sourceFiles)
        {
            logger.debug("Found source {}", sourceFile);
            compileUnit.addSource(sourceFile.toAbsolutePath().toString(), IoUtil.toString(sourceFile).orElseThrow(() -> new UncheckedIOException(new FileNotFoundException(sourceFile.toString()))));
        }

        final CompilerConfiguration ccfg = new CompilerConfiguration();
        ccfg.setTargetDirectory(classesDirectory.toFile());
        compileUnit.setConfiguration(ccfg);
        compileUnit.compile();
        classLoader.addURL(IoUtil.toURL(classesDirectory.toAbsolutePath()));
    }
}
