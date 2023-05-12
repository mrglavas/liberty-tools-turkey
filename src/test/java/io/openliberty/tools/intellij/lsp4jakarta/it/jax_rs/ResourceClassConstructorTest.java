/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package io.openliberty.tools.intellij.lsp4jakarta.it.jax_rs;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import io.openliberty.tools.intellij.lsp4jakarta.it.core.BaseJakartaTest;
import io.openliberty.tools.intellij.lsp4jakarta.it.core.JakartaForJavaAssert;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.IPsiUtils;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.internal.core.ls.PsiUtilsLSImpl;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class ResourceClassConstructorTest extends BaseJakartaTest {

    @Test
    public void MultipleConstructorsWithEqualParams() throws Exception {
        Module module = createMavenModule(new File("projects/jakarta-sample"));
        IPsiUtils utils = PsiUtilsLSImpl.getInstance(myProject);

        VirtualFile javaFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(ModuleUtilCore.getModuleDirPath(module)
                + "src/main/java/io/openliberty/sample/jakarta/jax_rs/RootResourceClassConstructorsEqualLen.java");
        String uri = VfsUtilCore.virtualToIoFile(javaFile).toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test expected diagnostics
        Diagnostic d1 = JakartaForJavaAssert.d(7, 8, 45,
                "Multiple constructors have the same number of parameters, it might be ambiguous which constructor is used.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "AmbiguousConstructors");

        Diagnostic d2 = JakartaForJavaAssert.d(11, 8, 45,
                "Multiple constructors have the same number of parameters, it might be ambiguous which constructor is used.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "AmbiguousConstructors");

        JakartaForJavaAssert.assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);

    }

    @Test
    public void MultipleConstructorsWithDifferentLength() throws Exception {
        Module module = createMavenModule(new File("projects/jakarta-sample"));
        IPsiUtils utils = PsiUtilsLSImpl.getInstance(myProject);

        VirtualFile javaFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(ModuleUtilCore.getModuleDirPath(module)
                + "src/main/java/io/openliberty/sample/jakarta/jax_rs/RootResourceClassConstructorsDiffLen.java");
        String uri = VfsUtilCore.virtualToIoFile(javaFile).toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test expected diagnostics
        Diagnostic d = JakartaForJavaAssert.d(7, 8, 44,
                "This constructor is unused, as root resource classes will only use the constructor with the most parameters.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "UnusedConstructor");

        JakartaForJavaAssert.assertJavaDiagnostics(diagnosticsParams, utils, d);
    }

    @Test
    public void NoPublicConstructor() throws Exception {
        Module module = createMavenModule(new File("projects/jakarta-sample"));
        IPsiUtils utils = PsiUtilsLSImpl.getInstance(myProject);

        VirtualFile javaFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(ModuleUtilCore.getModuleDirPath(module)
                + "src/main/java/io/openliberty/sample/jakarta/jax_rs/NoPublicConstructorClass.java");
        String uri = VfsUtilCore.virtualToIoFile(javaFile).toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test expected diagnostics
        Diagnostic d1 = JakartaForJavaAssert.d(7, 12, 36,
                "Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NoPublicConstructors");

        Diagnostic d2 = JakartaForJavaAssert.d(11, 14, 38,
                "Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NoPublicConstructors");

        JakartaForJavaAssert.assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);
    }

    
    @Test
    public void NoPublicConstructorProviderClass() throws Exception {
        Module module = createMavenModule(new File("projects/jakarta-sample"));
        IPsiUtils utils = PsiUtilsLSImpl.getInstance(myProject);

        VirtualFile javaFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(ModuleUtilCore.getModuleDirPath(module)
                + "src/main/java/io/openliberty/sample/jakarta/jax_rs/NoPublicConstructorProviderClass.java");
        String uri = VfsUtilCore.virtualToIoFile(javaFile).toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        Diagnostic d1 = JakartaForJavaAssert.d(19, 12, 44,
                "Provider classes are instantiated by the JAX-RS runtime and MUST have a public constructor",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NoPublicConstructors");

        Diagnostic d2 = JakartaForJavaAssert.d(23, 14, 46,
                "Provider classes are instantiated by the JAX-RS runtime and MUST have a public constructor",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NoPublicConstructors");

        JakartaForJavaAssert.assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);
    }
}
