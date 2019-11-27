package com.salesforce.bazel.eclipse.command.mock;

import java.io.File;

import com.salesforce.bazel.eclipse.abstractions.BazelAspectLocation;

public class MockBazelAspectLocation implements BazelAspectLocation{

    public File aspectDir; 
    public String aspectLabel;
    
    public MockBazelAspectLocation(File aspectDir, String aspectLabel) {
        this.aspectDir = aspectDir;
        this.aspectLabel = aspectLabel;
    }
    
    @Override
    public File getAspectDirectory() {
        return aspectDir;
    }

    @Override
    public String getAspectLabel() {
        return aspectLabel;
    }

}
