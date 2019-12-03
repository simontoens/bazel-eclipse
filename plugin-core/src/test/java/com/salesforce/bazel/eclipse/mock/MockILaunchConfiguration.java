package com.salesforce.bazel.eclipse.mock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;

public class MockILaunchConfiguration implements ILaunchConfiguration {
    private static final String UOE_MSG = "MockILaunchConfiguration is pay as you go, you have hit a method that is not implemented."; 
    public Map<String, Object> attributes = new TreeMap<>();

    // IMPLEMENTED METHODS
    
    @Override
    public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
        return (Boolean) attributes.getOrDefault(attributeName, defaultValue);
    }

    @Override
    public int getAttribute(String attributeName, int defaultValue) throws CoreException {
        return (Integer) attributes.getOrDefault(attributeName, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAttribute(String attributeName, List<String> defaultValue) throws CoreException {
        return (List<String>) attributes.getOrDefault(attributeName, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getAttribute(String attributeName, Set<String> defaultValue) throws CoreException {
        return (Set<String>) attributes.getOrDefault(attributeName, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getAttribute(String attributeName, Map<String, String> defaultValue)
            throws CoreException {
        return (Map<String, String>) attributes.getOrDefault(attributeName, defaultValue);
    }

    @Override
    public String getAttribute(String attributeName, String defaultValue) throws CoreException {
        return (String) attributes.getOrDefault(attributeName, defaultValue);
    }

    @Override
    public Map<String, Object> getAttributes() throws CoreException {
        return attributes;
    }

    @Override
    public boolean hasAttribute(String attributeName) throws CoreException {
        return attributes.containsKey(attributeName);
    }
    
    
    // UNIMPLEMENTED METHODS
    // Please move implemented methods, in alphabetical order, above this line if you implement a method.

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean contentsEqual(ILaunchConfiguration configuration) {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public void delete() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public void delete(int flag) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public String getCategory() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public IFile getFile() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public IPath getLocation() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public IResource[] getMappedResources() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public String getMemento() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public Set<String> getModes() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunchDelegate getPreferredDelegate(Set<String> modes) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunchConfigurationType getType() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isLocal() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isMigrationCandidate() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isWorkingCopy() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build, boolean register) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public void migrate() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean supportsMode(String mode) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public ILaunchConfiguration getPrototype() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isAttributeModified(String attribute) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public boolean isPrototype() {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public Collection<ILaunchConfiguration> getPrototypeChildren() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public int getKind() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public Set<String> getPrototypeVisibleAttributes() throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

    @Override
    public void setPrototypeAttributeVisibility(String attribute, boolean visible) throws CoreException {
        throw new UnsupportedOperationException(UOE_MSG);
    }

}
