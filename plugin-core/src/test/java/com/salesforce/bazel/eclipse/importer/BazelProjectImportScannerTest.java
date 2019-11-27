package com.salesforce.bazel.eclipse.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.salesforce.bazel.eclipse.model.BazelPackageInfo;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

public class BazelProjectImportScannerTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testHappyPath() throws Exception {
//        File tmpWorkspaceDir = tmpFolder.newFolder();
//        File tmpOutputBase = tmpFolder.newFolder();
        
        File tmpWorkspaceDir = new File("/tmp/bazeltest/ws");
        File tmpOutputBase = new File("/tmp/bazeltest/bin");
        
        
        new TestBazelWorkspaceFactory(tmpWorkspaceDir, tmpOutputBase).javaPackages(5).genrulePackages(2).build();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(5, rootWorkspacePackage.getChildPackageInfos().size());
    }

    // UNHAPPY PATHS
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDirectory() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        scanner.getProjects(tmpWorkspaceDir);
    }

    @Test
    public void testNoJavaProjects() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        File tmpOutputBase = tmpFolder.newFolder();
        new TestBazelWorkspaceFactory(tmpWorkspaceDir, tmpOutputBase).javaPackages(0).genrulePackages(2).build();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(0, rootWorkspacePackage.getChildPackageInfos().size());
    }
    
    @Test
    public void testNoProjects() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        File workspaceFile = new File(tmpWorkspaceDir, "WORKSPACE");
        workspaceFile.createNewFile();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(0, rootWorkspacePackage.getChildPackageInfos().size());
    }
}
