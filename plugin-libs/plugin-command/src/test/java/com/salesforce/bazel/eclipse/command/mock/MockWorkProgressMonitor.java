package com.salesforce.bazel.eclipse.command.mock;

import com.salesforce.bazel.eclipse.abstractions.WorkProgressMonitor;

public class MockWorkProgressMonitor implements WorkProgressMonitor {
    public String name = null;
    public int totalWork = 0;
    public boolean isCancelled = false;
    public String subtask = null;
    public int worked = 0;
    public boolean isDone = false;
    
    @Override
    public void beginTask(String name, int totalWork) {
        this.name = name;
        this.totalWork = totalWork;
    }

    @Override
    public void done() {
        this.isDone = true;
    }

    @Override
    public boolean isCanceled() {
        return this.isCancelled;
    }

    @Override
    public void setCanceled(boolean value) {
        this.isCancelled = value;
    }

    @Override
    public void subTask(String name) {
        this.subtask = name;
    }

    @Override
    public void worked(int work) {
        this.worked = work;
    }
}
