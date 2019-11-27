package com.salesforce.bazel.eclipse.mock;

import org.eclipse.jdt.core.IClasspathAttribute;

public class MockIClasspathAttribute implements IClasspathAttribute {
    public String name;
    public String value;
    
    public MockIClasspathAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
