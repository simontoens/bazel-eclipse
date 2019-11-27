package com.salesforce.bazel.eclipse.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.salesforce.bazel.eclipse.config.BazelEclipseProjectFactory;
import com.salesforce.bazel.eclipse.importer.BazelProjectImportScanner;
import com.salesforce.bazel.eclipse.model.BazelPackageInfo;
import com.salesforce.bazel.eclipse.runtime.EclipseWorkProgressMonitor;
import com.salesforce.bazel.eclipse.runtime.JavaCoreHelper;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

/**
 * Factory for creating test environments for Eclipse functional tests. Produces a Mock Eclipse workspace from templates.
 * 
 * @author plaird
 */
public class EclipseFunctionalTestEnvironmentFactory {

    /**
     * Creates an environment with a Bazel workspace with Java packages on disk, but nothing has been imported yet into Eclipse. 
     * Includes:
     * <p><ul>
     * <li>A Bazel workspace on disk
     * <li>Bazel command runner prepared to run 'bazel info workspace' which is done during Bazel Import
     * <li>Everything else in base initialized state
     * </ul>
     */
    public static MockEclipse createMockEnvironment_PriorToImport_JavaPackages(File testTempDir, int numberOfJavaPackages) throws Exception {
        // build out a Bazel workspace with specified number of Java packages, and a couple of genrules packages just to test that they get ignored
        File wsDir = new File(testTempDir, MockEclipse.BAZEL_WORKSPACE_NAME);
        wsDir.mkdirs();
        File outputbaseDir = new File(testTempDir, "outputbase");
        outputbaseDir.mkdirs();
        TestBazelWorkspaceFactory bazelWorkspaceCreator = new TestBazelWorkspaceFactory(wsDir, outputbaseDir).
                javaPackages(numberOfJavaPackages).genrulePackages(2);
        bazelWorkspaceCreator.build();

        // create the mock Eclipse runtime
        MockEclipse mockEclipse = new MockEclipse(bazelWorkspaceCreator, testTempDir);

        return mockEclipse;
    }
    
    /**
     * Creates an environment with a Bazel workspace with Java packages on disk, and the Java packages have been imported
     * as Eclipse projects. 
     * Includes:
     * <p><ul>
     * <li>A Bazel workspace on disk
     * <li>Each Bazel Java package is an imported Eclipse Java project with Bazel nature
     * </ul>
     */
    public static MockEclipse createMockEnvironment_Imported_All_JavaPackages(File testTempDir, int numberOfJavaPackages, boolean computeClasspaths) throws Exception {
        // create base configuration, which includes the real bazel workspace on disk
        MockEclipse mockEclipse = createMockEnvironment_PriorToImport_JavaPackages(testTempDir, numberOfJavaPackages);

        // scan the bazel workspace filesystem to build the list of Java projects
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo workspaceRootProject = scanner.getProjects(mockEclipse.getBazelWorkspaceRoot());
        
        // choose the list of Bazel packages to import, in this case we assume the user selected all Java packages
        List<BazelPackageInfo> bazelPackagesToImport = new ArrayList<>();
        bazelPackagesToImport.add(workspaceRootProject);
        addBazelPackageInfosToSelectedList(workspaceRootProject, bazelPackagesToImport);
                
        // run the import process (this is actually done in BazelImportWizard.performFinish() when a user is running the show)
        List<IProject> importedProjectsList = BazelEclipseProjectFactory.importWorkspace(workspaceRootProject, bazelPackagesToImport, new EclipseWorkProgressMonitor(), null);
        mockEclipse.setImportedProjectsList(importedProjectsList);
        
        // do you want to simulate Eclipse calling getClasspath on the classpath container for each project?
        if (computeClasspaths) {
            for (IProject project : importedProjectsList) {
                JavaCoreHelper javaHelper = mockEclipse.getJavaCoreHelper();
                javaHelper.getResolvedClasspath(javaHelper.getJavaProjectForProject(project), false);
            }
        }
        
        return mockEclipse;
    }
    
    private static void addBazelPackageInfosToSelectedList(BazelPackageInfo currentNode, List<BazelPackageInfo> bazelPackagesToImport) {
        Collection<BazelPackageInfo> children = currentNode.getChildPackageInfos();
        for (BazelPackageInfo child : children) {
            // eventually this method should accept filter criteria, but for now we are just importing all packages
            bazelPackagesToImport.add(child);
        }
    }
}
