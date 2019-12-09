package com.salesforce.bazel.eclipse.config;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;

import com.salesforce.bazel.eclipse.model.BazelBuildFile;

/**
 * Object that encapsulates the logic and state regarding the active targets configured for an
 * Eclipse project.
 */
public class EclipseProjectBazelTargets {
    private IProject project;
    private String projectBazelLabel;
    
    /**
     * This is the list of targets listed as listed in the project preferences.
     * This may contain a single entry that is the wildcard target (:*), or it can be a list
     * of specific targets
     */
    private Set<String> configuredTargets = new TreeSet<>();
    
    /**
     * Convenience flag that indicates that the activatedTargets list contains one entry and it is
     * the wildcard entry
     */
    private boolean isActivatedWildcardTarget = false;
    
    /**
     * Contains the list of targets configured for building/testing. This will be the same as
     * configuredTargets if isActivatedWildcardTarget==false, or will be the actual list of all targets
     * found in the BUILD file if isActivatedWildcardTarget==true
     */
    private Set<String> actualTargets;
    
    
    public EclipseProjectBazelTargets(IProject project, String projectBazelLabel) {
         this.project = project;
         this.projectBazelLabel = projectBazelLabel;
    }
    
    // TODO weave these into the constructor
    
    public void activateWildcardTarget() {
        this.isActivatedWildcardTarget = true;
        this.configuredTargets.add(projectBazelLabel+":*");
    }

    public void activateSpecificTargets(Set<String> activatedTargets) {
        this.isActivatedWildcardTarget = false;
        this.configuredTargets = activatedTargets;
        this.actualTargets = activatedTargets;
    }

    // CONSUMER API
    
    public IProject getProject() {
        return this.project;
    }
    
    public Set<String> getConfiguredTargets() {
        return this.configuredTargets;
    }

    public Set<String> getActualTargets(BazelBuildFile bazelBuildFile) {
        if (this.isActivatedWildcardTarget) {
            this.actualTargets = bazelBuildFile.getAllTargetLabels();
        }
        
        return this.actualTargets;
    }

    public boolean isActivatedWildcardTarget() {
        return this.isActivatedWildcardTarget;
    }

    public boolean isAllTargetsDeactivated() {
        return this.configuredTargets.size() == 0;
    }

}
