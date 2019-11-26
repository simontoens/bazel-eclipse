package com.salesforce.bazel.eclipse.command.internal;

import java.io.File;

/**
 * Prompt to use to precede each log line from the command. 
 */
public enum ConsoleType {
    NO_CONSOLE, SYSTEM, WORKSPACE;
    
    public String getConsoleName(File directory) {
        switch (this) {
        case SYSTEM:
            return "Bazel [system]";
        case WORKSPACE:
            return "Bazel [" + directory.toString() + "]";
        case NO_CONSOLE:
        default:
            return null;
        }
    }
}
