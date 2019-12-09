package com.salesforce.bazel.eclipse.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Abstraction over the internal details of a BUILD file.
 * Depending on how this object was built, it may not contain all of the information.
 */
public class BazelBuildFile {
    
    /**
     * The label that identifies the package associated with this BUILD file, e.g. //projects/libs/foo
     */
    String label;
    
    /**
     * Maps the String rule type (e.g. java_library) to the target labels (e.g. "//projects/libs/foo:foolib", 
     *   "//projects/libs/foo:barlib")
     */
    private Map<String, Set<String>> typeToTargetMap = new TreeMap<>();

    /**
     * Maps the String target label (e.g. //projects/libs/foo:foolib) to the rule type  (e.g. java_library)
     */
    private Map<String, String> targetToTypeMap = new TreeMap<>();

    private Set<String> allTargets = new TreeSet<>();
    
    
    public BazelBuildFile(String label) {
        this.label = label;
    }
    
    public void addTarget(String ruleType, String targetLabel) {
        this.targetToTypeMap.put(targetLabel, ruleType);
        this.allTargets.add(targetLabel);
        
        Set<String> targetsForRuleType = typeToTargetMap.get(ruleType);
        if (targetsForRuleType == null) {
            targetsForRuleType = new TreeSet<>();
            this.typeToTargetMap.put(ruleType, targetsForRuleType);
        }
        if (!targetsForRuleType.contains(targetLabel)) {
            targetsForRuleType.add(targetLabel);
        }
        
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public Set<String> getRuleTypes() {
        return this.typeToTargetMap.keySet();
    }
    
    public Set<String> getTargetsForRuleType(String ruleType) {
        return this.typeToTargetMap.get(ruleType);
    }
    
    public String getRuleTypeForTarget(String targetLabel) {
        return this.targetToTypeMap.get(targetLabel);
    }
    
    public Set<String> getAllTargetLabels() {
        return this.allTargets;
    }
}
