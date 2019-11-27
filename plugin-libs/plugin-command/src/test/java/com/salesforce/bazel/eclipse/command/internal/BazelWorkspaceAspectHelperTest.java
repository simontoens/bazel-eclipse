package com.salesforce.bazel.eclipse.command.internal;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.salesforce.bazel.eclipse.command.mock.MockWorkProgressMonitor;
import com.salesforce.bazel.eclipse.command.mock.TestBazelCommandEnvironmentFactory;
import com.salesforce.bazel.eclipse.model.AspectPackageInfo;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

/**
 * Tests various behaviors of the BazelWorkspaceAspectHelper collaborator.
 * <p>
 * These tests use a test harness to generate real json files on the file system, which are then
 * read in by the helper. See TestBazelCommandEnvironmentFactory for how that is done.
 */
public class BazelWorkspaceAspectHelperTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testAspectLoading() throws Exception {
        TestBazelCommandEnvironmentFactory env = createEnv();
        BazelWorkspaceAspectHelper aspectHelper = env.bazelWorkspaceCommandRunner.getBazelWorkspaceAspectHelper();
        
        // retrieve the aspects for the target
        List<String> targets = new ArrayList<>();
        targets.add("//projects/libs/javalib0:*");
        Map<String, AspectPackageInfo> aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        
        // now check that the caches are populated
        assertEquals(6, aspectHelper.aspectInfoCache_current.size());
        assertEquals(6, aspectHelper.aspectInfoCache_lastgood.size());
        
        // the purpose of the wildcard cache is to map wildcard targets (e.g. //projects/libs/javalib0:*)
        // to the list of actual targets (//projects/libs/javalib0:javalib0, //projects/libs/javalib0:javalib0-test)
        assertEquals(1, aspectHelper.aspectInfoCache_wildcards.size());
        assertEquals(6, aspectHelper.aspectInfoCache_wildcards.get("//projects/libs/javalib0:*").size());
    }

    @Test
    public void testAspectLoadingAndCaching() throws Exception {
        TestBazelCommandEnvironmentFactory env = createEnv();
        BazelWorkspaceAspectHelper aspectHelper = env.bazelWorkspaceCommandRunner.getBazelWorkspaceAspectHelper();
        
        // retrieve the aspects for the target
        List<String> targets = new ArrayList<>();
        targets.add("//projects/libs/javalib0:*");
        Map<String, AspectPackageInfo> aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        assertEquals(0, aspectHelper.numberCacheHits);
        
        // ask for the same target again
        aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        assertEquals(6, aspectHelper.numberCacheHits); // the entries all came from cache
    }
    
    @Test
    public void testAspectCacheFlush() throws Exception {
        TestBazelCommandEnvironmentFactory env = createEnv();
        BazelWorkspaceAspectHelper aspectHelper = env.bazelWorkspaceCommandRunner.getBazelWorkspaceAspectHelper();
        
        // retrieve the aspects for the target
        List<String> targets = new ArrayList<>();
        targets.add("//projects/libs/javalib0:*");
        Map<String, AspectPackageInfo> aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        assertEquals(0, aspectHelper.numberCacheHits);
        
        // ask for the same target again
        aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        assertEquals(6, aspectHelper.numberCacheHits); // the entries all came from cache

        // flush the cache (we do this when the user executes a 'clean' in Eclipse
        aspectHelper.flushAspectInfoCache();
        assertEquals(0, aspectHelper.aspectInfoCache_current.size());
        assertEquals(0, aspectHelper.aspectInfoCache_wildcards.size());
        assertEquals(6, aspectHelper.aspectInfoCache_lastgood.size()); // last good is an emergency fallback, not flushed
        
        // ask for the same target again
        aspectMap = aspectHelper.getAspectPackageInfos("test-project", targets, 
            new MockWorkProgressMonitor(), "testAspectLoading");
        // aspect infos returned for: guava, slf4j, hamcrest, junit, javalib0, javalib0-test
        assertEquals(6, aspectMap.size());
        assertEquals(6, aspectHelper.numberCacheHits); // the entries all came from cache
    }
    
    
    // INTERNAL
    
    private TestBazelCommandEnvironmentFactory createEnv() throws Exception {
        File testDir = tmpFolder.newFolder();
        File workspaceDir = new File(testDir, "bazel-workspace");
        workspaceDir.mkdirs();
        File outputbaseDir = new File(testDir, "outputbase");
        outputbaseDir.mkdirs();
        TestBazelWorkspaceFactory workspace = new TestBazelWorkspaceFactory(workspaceDir, outputbaseDir).javaPackages(1).build();
        TestBazelCommandEnvironmentFactory env = new TestBazelCommandEnvironmentFactory();
        env.createTestEnvironment(workspace, testDir);
        
        return env;
    }

}
