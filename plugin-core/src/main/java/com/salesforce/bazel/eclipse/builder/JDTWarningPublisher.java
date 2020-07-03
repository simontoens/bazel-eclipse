package com.salesforce.bazel.eclipse.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.salesforce.bazel.eclipse.model.BazelProblem;

/**
 * This class publishes JDT based code warnings to the Problems View.
 *
 * @author stoens
 * @since Summer 2020
 *
 */
class JDTWarningPublisher implements IElementChangedListener {

    private final ConcurrentHashMap<IProject, List<BazelProblem>> projectToWarnings;
    private final BazelProblemMarkerManager markerManager;

    public JDTWarningPublisher() {
        this.projectToWarnings = new ConcurrentHashMap<>();
        this.markerManager = new BazelProblemMarkerManager(this.getClass().getName());
    }

    @Override
    public void elementChanged(ElementChangedEvent event) {
        if (event.getType() == ElementChangedEvent.POST_RECONCILE) {
            IJavaElementDelta delta = event.getDelta();
            CompilationUnit ast = delta.getCompilationUnitAST();
            if (ast != null) {
                List<BazelProblem> warnings = getWarnings(ast);
                IJavaElement element = delta.getElement();
                IProject project = element.getJavaProject().getProject();
                projectToWarnings.put(project, warnings);
            }
        }
    }

    /**
     * Publish captured warnings to the Problems View, for the specified projects.
     */
    void publish(Collection<IProject> projects, IProgressMonitor monitor) {
        for (IProject project : projects) {
            List<BazelProblem> warnings = projectToWarnings.remove(project);
            if (warnings != null) {
                markerManager.clearAndPublish(warnings, project, monitor);
            }
        }
    }

    private static List<BazelProblem> getWarnings(CompilationUnit ast) {
        IProblem[] problems = ast.getProblems();
        List<BazelProblem> warnings = new ArrayList<>();
        for (IProblem problem : problems) {
            if (!problem.isWarning()) {
                continue;
            }
            String path = new String(problem.getOriginatingFileName());
            // remove leading project name
            if (path.startsWith(File.separator)) {
                path = path.substring(1);
            }
            int i = path.indexOf(File.separator);
            if (i != -1) {
                path = path.substring(i+1);
            }
            warnings.add(BazelProblem.createWarning(path, problem.getSourceLineNumber(), problem.getMessage()));

        }
        return warnings;
    }
}
