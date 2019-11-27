package com.salesforce.bazel.eclipse.command.mock;

import java.io.File;

/**
 * Creates a mock executable binary on the file system, for tests that expect to have an 
 * actual Bazel executable.
 */
public class MockBazelExecutable {
    public File bazelExecutableFile;
    
    /**
     * Creates the fake executable file.
     * 
     * @param executableDir directory in which the binary file will be placed, must exist, must be writable
     */
    public MockBazelExecutable(File executableDir) {
        this.bazelExecutableFile = new File(executableDir, "bazel");
        try {
            this.bazelExecutableFile.createNewFile();
            this.bazelExecutableFile.setExecutable(true);
        } catch (Exception anyE) {
            throw new RuntimeException(anyE);
        }
    }
    
    public String getAbsolutePath() {
        return bazelExecutableFile.getAbsolutePath();
    }
}
