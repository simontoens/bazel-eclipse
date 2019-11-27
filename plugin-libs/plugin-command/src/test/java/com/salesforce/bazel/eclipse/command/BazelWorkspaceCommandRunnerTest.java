package com.salesforce.bazel.eclipse.command;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.salesforce.bazel.eclipse.command.mock.MockWorkProgressMonitor;
import com.salesforce.bazel.eclipse.command.mock.TestBazelCommandEnvironmentFactory;
import com.salesforce.bazel.eclipse.model.AspectPackageInfo;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

public class BazelWorkspaceCommandRunnerTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testGlobalRunner() throws Exception {
        TestBazelCommandEnvironmentFactory env = new TestBazelCommandEnvironmentFactory();
        env.createTestEnvironment(tmpFolder.newFolder());

        // verify that the command runner has the Bazel exec path
        assertEquals(env.bazelExecutable.getAbsolutePath(), BazelWorkspaceCommandRunner.getBazelExecutablePath());

        // run our version check, will throw if version is not approved
        BazelWorkspaceCommandRunner globalRunner = env.globalCommandRunner;
        globalRunner.runBazelVersionCheck();
    }

    @Test
    public void testWorkspaceRunner() throws Exception {
        File testDir = tmpFolder.newFolder();
        File workspaceDir = new File(testDir, "bazel-workspace");
        workspaceDir.mkdirs();
        File outputbaseDir = new File(testDir, "outputbase");
        outputbaseDir.mkdirs();
        TestBazelWorkspaceFactory workspace = new TestBazelWorkspaceFactory(workspaceDir, outputbaseDir).javaPackages(3).build();
        TestBazelCommandEnvironmentFactory env = new TestBazelCommandEnvironmentFactory();
        env.createTestEnvironment(workspace, testDir);

        // verify that the command runner has the Bazel exec path
        assertEquals(env.bazelExecutable.getAbsolutePath(), BazelWorkspaceCommandRunner.getBazelExecutablePath());

        BazelWorkspaceCommandRunner workspaceRunner = env.bazelWorkspaceCommandRunner;
        
        // test getting aspects from the file system
        Set<String> targets = new TreeSet<>();
        targets.add("//projects/libs/javalib0:*");
        Map<String, AspectPackageInfo> aspectMap = workspaceRunner.getAspectPackageInfos("javalib0", targets, new MockWorkProgressMonitor(),
            "testWorkspaceRunner");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        
        // run a clean, should not throw an exception
        workspaceRunner.runBazelClean(new MockWorkProgressMonitor());
    }
}
