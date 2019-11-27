package com.salesforce.bazel.eclipse.command.mock;

import java.io.File;

import com.salesforce.bazel.eclipse.command.BazelCommandManager;
import com.salesforce.bazel.eclipse.command.BazelWorkspaceCommandRunner;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

/**
 * Factory for creating test environments for Bazel Command functional tests. Produces a real BazelWorkspaceCommandRunner with 
 * collaborators, with mock command execution underneath it all. Logically, this layer replaces the real Bazel executable with
 * command simulations.
 * 
 * @author plaird
 */
public class TestBazelCommandEnvironmentFactory {
    
    public BazelWorkspaceCommandRunner globalCommandRunner;
    public BazelWorkspaceCommandRunner bazelWorkspaceCommandRunner;
    
    public MockBazelAspectLocation bazelAspectLocation;
    public MockCommandConsole commandConsole;
    public MockCommandBuilder commandBuilder;
    public MockBazelExecutable bazelExecutable;

    /**
     * Basic testing environment for the command layer. It creates a simple Bazel workspace on the filesystem
     * with no Java/generic packages.
     */
    public void createTestEnvironment(File tempDir) {
        // the name of the directory that contains the bazel workspace is significant, as the Eclipse feature
        // will use it in the name of the Eclipse project
        File workspaceDir = new File(tempDir, "bazel-workspace");
        workspaceDir.mkdirs();
        File outputBase = new File(tempDir, "outputbase");
        outputBase.mkdirs();
        
        TestBazelWorkspaceFactory testWorkspace = new TestBazelWorkspaceFactory(workspaceDir, outputBase, "bazel_command_executor_test");
        createTestEnvironment(testWorkspace, tempDir);
    }
    
    /**
     * Creates a testing environment based on a test workspace passed in from the caller. If you are testing
     * commands in the context of actual Bazel packages (e.g. Java) this is the form to use.
     */
    public void createTestEnvironment(TestBazelWorkspaceFactory testWorkspace, File tempDir) {
        File execDir = new File(tempDir, "executable");
        execDir.mkdir();
        this.bazelExecutable = new MockBazelExecutable(execDir);
        
        this.bazelAspectLocation = new MockBazelAspectLocation(tempDir, "test-aspect-label");
        this.commandConsole = new MockCommandConsole();
        this.commandBuilder = new MockCommandBuilder(commandConsole, testWorkspace.dirWorkspaceRoot, testWorkspace.dirOutputBase, 
            testWorkspace.dirExecRoot, testWorkspace.dirBazelBin);
        
        BazelCommandManager bazelCommandManager = new BazelCommandManager(bazelAspectLocation, commandBuilder, commandConsole, 
            bazelExecutable.bazelExecutableFile);
        bazelCommandManager.setBazelExecutablePath(bazelExecutable.bazelExecutableFile.getAbsolutePath());
        
        this.globalCommandRunner = bazelCommandManager.getGlobalCommandRunner();
        this.bazelWorkspaceCommandRunner = bazelCommandManager.getWorkspaceCommandRunner(testWorkspace.dirWorkspaceRoot);
    }
    
}
