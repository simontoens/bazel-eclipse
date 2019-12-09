package com.salesforce.bazel.eclipse.config;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.google.common.collect.ImmutableList;
import com.salesforce.bazel.eclipse.BazelPluginActivator;

// TODO migrate this away from static methods
public class BazelProjectPreferences { 
    // TODO ideally these constants would be private, impl details
    /**
     * Absolute path of the Bazel workspace root 
     */
    public static final String BAZEL_WORKSPACE_ROOT_ABSPATH_PROPERTY = "bazel.workspace.root";
    
    /**
     * The label that identifies the Bazel package that represents this Eclipse project. This will
     * be the 'module' label when we start supporting multiple BUILD files in a single 'module'.
     * Example:  //projects/libs/foo
     * See https://github.com/salesforce/bazel-eclipse/issues/24
     */
    private static final String PROJECT_PACKAGE_LABEL = "bazel.package.label";
    
    /**
     * After import, the activated target is a single line, like: 
     *   bazel.activated.target0=//projects/libs/foo:*
     * which activates all targets by use of the wildcard. But users may wish to activate a subset
     * of the targets for builds, in which the prefs lines will look like:
     *   bazel.activated.target0=//projects/libs/foo:barlib
     *   bazel.activated.target1=//projects/libs/foo:bazlib
     */
    public static final String TARGET_PROPERTY_PREFIX = "bazel.activated.target";
    
    /**
     * Property that allows a user to set project specific build flags that get
     * passed to the Bazel executable.
     */
    static final String BUILDFLAG_PROPERTY_PREFIX = "bazel.build.flag";

    
    /**
     * The label that identifies the Bazel package that represents this Eclipse project. This will
     * be the 'module' label when we start supporting multiple BUILD files in a single 'module'.
     * Example:  //projects/libs/foo
     * See https://github.com/salesforce/bazel-eclipse/issues/24
     */
    public static String getBazelLabelForEclipseProject(IProject eclipseProject) {
        Preferences eclipseProjectBazelPrefs = BazelPluginActivator.getResourceHelper().getProjectBazelPreferences(eclipseProject);
        return eclipseProjectBazelPrefs.get(PROJECT_PACKAGE_LABEL, null);
    }

    
    /**
     * List the Bazel targets the user has chosen to activate for this Eclipse project. Each project configured 
     * for Bazel is configured to track certain targets and this function fetches this list from the project preferences.
     * After initial import, this will be just the wildcard target (:*) which means all targets are activated. This
     * is the safest choice as new targets that are added to the BUILD file will implicitly get picked up. But users
     * may choose to be explicit if one or more targets in a BUILD file is not needed for development.
     * <p>
     * By contract, this method will return only one target if the there is a wildcard target, even if the user does
     * funny things in their prefs file and sets multiple targets along with the wildcard target.
     */
    public static EclipseProjectBazelTargets getConfiguredBazelTargets(IProject eclipseProject) {
        Preferences eclipseProjectBazelPrefs = BazelPluginActivator.getResourceHelper().getProjectBazelPreferences(eclipseProject);
        String projectLabel = eclipseProjectBazelPrefs.get(PROJECT_PACKAGE_LABEL, null);

        EclipseProjectBazelTargets activatedTargets = new EclipseProjectBazelTargets(eclipseProject, projectLabel);
        
        Set<String> activeTargets = new TreeSet<>();
        for (String propertyName : getKeys(eclipseProjectBazelPrefs)) {
            if (propertyName.startsWith(TARGET_PROPERTY_PREFIX)) {
                String target = eclipseProjectBazelPrefs.get(propertyName, "");
                if (!target.isEmpty()) {
                    if (!target.startsWith(projectLabel)) {
                        // the user jammed in a label not associated with this project, ignore
                        //continue;
                    }
                    if (target.endsWith(":*")) {
                        // we have a wildcard target, so discard any existing targets we gathered (if the user messed up their prefs)
                        // and just go with that.
                        activatedTargets.activateWildcardTarget();
                        return activatedTargets;
                    }
                    activeTargets.add(target);
                }
            }
        }
        activatedTargets.activateSpecificTargets(activeTargets);
        
        return activatedTargets;
    }
    
    /**
     * List of Bazel build flags for this Eclipse project, taken from the project configuration
     */
    public static List<String> getBazelBuildFlagsForEclipseProject(IProject eclipseProject) {
        Preferences eclipseProjectBazelPrefs = BazelPluginActivator.getResourceHelper().getProjectBazelPreferences(eclipseProject);
        
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        for (String property : getKeys(eclipseProjectBazelPrefs)) {
            if (property.startsWith(BUILDFLAG_PROPERTY_PREFIX)) {
                listBuilder.add(eclipseProjectBazelPrefs.get(property, ""));
            }
        }
        return listBuilder.build();
    }
    
    
    public static void addSettingsToEclipseProject(IProject eclipseProject, String bazelWorkspaceRoot,
            String bazelProjectLabel, List<String> bazelTargets, List<String> bazelBuildFlags) throws BackingStoreException {

        Preferences eclipseProjectBazelPrefs = BazelPluginActivator.getResourceHelper().getProjectBazelPreferences(eclipseProject);

        eclipseProjectBazelPrefs.put(BAZEL_WORKSPACE_ROOT_ABSPATH_PROPERTY, bazelWorkspaceRoot);
        if (!bazelProjectLabel.startsWith("//")) {
            bazelProjectLabel = "//"+bazelProjectLabel;
        }
        eclipseProjectBazelPrefs.put(PROJECT_PACKAGE_LABEL, bazelProjectLabel);
        
        int i = 0;
        for (String bazelTarget : bazelTargets) {
            eclipseProjectBazelPrefs.put(TARGET_PROPERTY_PREFIX + i, bazelTarget);
            i++;
        }
        i = 0;
        for (String bazelBuildFlag : bazelBuildFlags) {
            eclipseProjectBazelPrefs.put(BUILDFLAG_PROPERTY_PREFIX + i, bazelBuildFlag);
            i++;
        }
        eclipseProjectBazelPrefs.flush();
    }
    

    // HELPERS
    
    private static String[] getKeys(Preferences prefs) {
        try {
            return prefs.keys();
        } catch (BackingStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
