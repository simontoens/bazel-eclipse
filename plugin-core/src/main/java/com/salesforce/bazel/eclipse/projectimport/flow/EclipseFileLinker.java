/**
 * Copyright (c) 2020, Salesforce.com, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.bazel.eclipse.projectimport.flow;

import java.io.File;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

import com.salesforce.bazel.eclipse.BazelPluginActivator;
import com.salesforce.bazel.eclipse.runtime.api.ResourceHelper;
import com.salesforce.bazel.sdk.util.BazelPathHelper;

/**
 * Knows how to link files (used only during project import).
 */
class EclipseFileLinker {

    private final File bazelWorkspaceRootDirectory;

    EclipseFileLinker(File bazelWorkspaceRootDirectory) {
        this.bazelWorkspaceRootDirectory = Objects.requireNonNull(bazelWorkspaceRootDirectory);
    }

    boolean link(String packageFSPath, IProject eclipseProject, String fileName) {
        ResourceHelper resourceHelper = BazelPluginActivator.getResourceHelper();
        boolean retval = true;

        File f = new File(new File(bazelWorkspaceRootDirectory, packageFSPath), fileName);
        if (f.exists()) {
            IFile projectFile = resourceHelper.getProjectFile(eclipseProject, fileName);
            if (projectFile.exists()) {
                // What has happened is the user imported the Bazel workspace into Eclipse, but then deleted it from their Eclipse workspace at some point.
                // Now they are trying to import it again. Like the IntelliJ plugin we are refusing to do so, but perhaps we will
                // support this once we are convinced that we are safe to import over an old imported version of the Bazel workspace. TODO
                // For now, just bail, with a good message.
                String logMsg =
                        "You have imported this Bazel workspace into Eclipse previously, but then deleted it from your Eclipse workspace. "
                                + "This left files on the filesystem in your Eclipse workspace directory and the feature currently does not support overwriting an old imported Bazel workspace. "
                                + "\nTo import this Bazel workspace, use file system tools to delete the associated Bazel Eclipse project files from the "
                                + "Eclipse workspace directory, and then try to import again. \nFile: "
                                + projectFile.getFullPath();
                // TODO throwing this exception just writes a log message, we need a modal error popup for this error
                throw new IllegalStateException(logMsg);
            }
            try {
                resourceHelper.createFileLink(projectFile, Path.fromOSString(f.getCanonicalPath()), IResource.NONE,
                    null);
            } catch (Exception anyE) {
                // TODO throwing this exception just writes a log message, we need a modal error popup for this error
                BazelPluginActivator.error("Failure to link file [" + BazelPathHelper.getCanonicalPathStringSafely(f)
                        + "] for project [" + eclipseProject.getName() + "]");
                throw new IllegalStateException(anyE);
            }
        } else {
            BazelPluginActivator
                    .error("Tried to link a non-existant file [" + BazelPathHelper.getCanonicalPathStringSafely(f)
                            + "] for project [" + eclipseProject.getName() + "]");
            retval = false;
        }
        return retval;
    }
}
