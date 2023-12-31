/*******************************************************************************
 * Copyright (c) 2020, 2022 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.tools.intellij.util;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.sun.istack.Nullable;
import org.jetbrains.plugins.terminal.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LibertyProjectUtil {
    private static Logger log = Logger.getInstance(LibertyProjectUtil.class);;

    @Nullable
    public static Project getProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context);
    }

    /**
     * Returns a list of valid Gradle build files in the project
     * @param project
     * @return ArrayList of BuildFiles
     */
    public static ArrayList<BuildFile> getGradleBuildFiles(Project project) throws IOException, SAXException, ParserConfigurationException {
        return getBuildFiles(project, Constants.LIBERTY_GRADLE_PROJECT);
    }

    /**
     * Returns a list of valid Maven build files in the project
     * @param project
     * @return ArrayList of BuildFiles
     */
    public static ArrayList<BuildFile> getMavenBuildFiles(Project project) throws IOException, SAXException, ParserConfigurationException {
        return getBuildFiles(project, Constants.LIBERTY_MAVEN_PROJECT);
    }

    /**
     * Get the terminal widget for the current project
     * @param project
     * @param projectName
     * @param createWidget true if a new widget should be created
     * @return ShellTerminalWidget object
     */
    public static ShellTerminalWidget getTerminalWidget(Project project, String projectName, boolean createWidget) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow terminalWindow = toolWindowManager.getToolWindow("Terminal");

        // look for existing terminal tab
        ShellTerminalWidget widget = getTerminalWidget(terminalWindow, projectName);
        if (widget != null) {
            return widget;
        } else if (createWidget) {
            // create a new terminal tab
            TerminalView terminalView = TerminalView.getInstance(project);
            LocalTerminalDirectRunner terminalRun = new LocalTerminalDirectRunner(project);
            TerminalTabState tabState = new TerminalTabState();
            tabState.myTabName = projectName;
            tabState.myWorkingDirectory = project.getBasePath();
            terminalView.createNewSession(terminalRun, tabState);
            return getTerminalWidget(terminalWindow, projectName);
        }
        return null;
    }

    // returns valid build files for the current project
    private static ArrayList<BuildFile> getBuildFiles(Project project, String buildFileType) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<BuildFile> buildFiles = new ArrayList<BuildFile>();

        if (buildFileType.equals(Constants.LIBERTY_MAVEN_PROJECT)) {
            PsiFile[] mavenFiles = FilenameIndex.getFilesByName(project, "pom.xml", GlobalSearchScope.projectScope(project));
            for (int i = 0; i < mavenFiles.length; i++) {
                BuildFile buildFile = LibertyMavenUtil.validPom(mavenFiles[i]);
                if (buildFile.isValidBuildFile()) {
                    buildFile.setBuildFile(mavenFiles[i]);
                    buildFiles.add(buildFile);
                }
            }
        } else if (buildFileType.equals(Constants.LIBERTY_GRADLE_PROJECT)) {
            PsiFile[] gradleFiles = FilenameIndex.getFilesByName(project, "build.gradle", GlobalSearchScope.projectScope(project));
            for (int i = 0; i < gradleFiles.length; i++) {
                try {
                    BuildFile buildFile = LibertyGradleUtil.validBuildGradle(gradleFiles[i]);
                    if (buildFile.isValidBuildFile()) {
                        buildFile.setBuildFile(gradleFiles[i]);
                        buildFiles.add(buildFile);
                    }
                } catch (Exception e) {
                    log.error("Error parsing build.gradle", e.getMessage());
                }
            }
        }
        return buildFiles;
    }

    private static ShellTerminalWidget getTerminalWidget(ToolWindow terminalWindow, String projectName) {
        Content[] terminalContents = terminalWindow.getContentManager().getContents();
        for (int i = 0; i < terminalContents.length; i++) {
            if (terminalContents[i].getTabName().equals(projectName)) {
                JBTerminalWidget widget = TerminalView.getWidgetByContent(terminalContents[i]);
                ShellTerminalWidget shellWidget = (ShellTerminalWidget) Objects.requireNonNull(widget);
                return shellWidget;
            }
        }
        return null;
    }

}
