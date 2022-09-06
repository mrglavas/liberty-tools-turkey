/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.langserver.devtools.intellij.lsp4mp.lsp;

import com.intellij.lang.Language;

/**
 * Custom language for microprofile-config.properties files
 */
public class MicroProfileConfigLanguage extends Language {

    public static final MicroProfileConfigLanguage INSTANCE = new MicroProfileConfigLanguage();

    private MicroProfileConfigLanguage() {
        super("MicroProfileConfigProperties", "text/properties");
    }
}
