package com.salesforce.bazel.eclipse.command.internal;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.salesforce.bazel.eclipse.abstractions.CommandConsoleFactory;
import com.salesforce.bazel.eclipse.command.mock.MockBazelExecutable;
import com.salesforce.bazel.eclipse.command.mock.MockCommandBuilder;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

public class BazelCommandExecutorTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testHappy() throws Exception {
      File tmpWorkspaceDir = tmpFolder.newFolder();
      File tmpOutputBase = tmpFolder.newFolder();
      File tmpExecutableDir = tmpFolder.newFolder();

      BazelCommandExecutor testExecutor = createExecutor(tmpWorkspaceDir, tmpOutputBase, tmpExecutableDir);
    }
    
    private BazelCommandExecutor createExecutor(File workspaceDir, File outputBase, File executableDir) {
        
        
        
        TestBazelWorkspaceFactory testWorkspace = new TestBazelWorkspaceFactory(workspaceDir, outputBase, "bazel_command_executor_test");
        CommandConsoleFactory consoleFactory = null;
        MockBazelExecutable mockBazelExecutable = new MockBazelExecutable(executableDir);

        MockCommandBuilder mockCommandBuilder = new MockCommandBuilder(consoleFactory, testWorkspace.dirWorkspaceRoot, testWorkspace.dirOutputBase, 
            testWorkspace.dirExecRoot, testWorkspace.dirBazelBin);
        
        BazelCommandExecutor commandExecutor = new BazelCommandExecutor(mockBazelExecutable.bazelExecutableFile, mockCommandBuilder);
        
        return commandExecutor;
    }
}
