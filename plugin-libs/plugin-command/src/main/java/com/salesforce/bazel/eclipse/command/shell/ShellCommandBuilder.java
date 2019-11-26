package com.salesforce.bazel.eclipse.command.shell;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.salesforce.bazel.eclipse.abstractions.CommandConsole;
import com.salesforce.bazel.eclipse.abstractions.CommandConsoleFactory;
import com.salesforce.bazel.eclipse.command.CommandBuilder;

/**
 * Implementation of CommandBuilder that builds real command line commands (as opposed to
 * mock commands used in testing). This is the implementation used by the plugin when running
 * in Eclipse.
 * <p>
 * It creates instances of type ShellCommand.
 */
public class ShellCommandBuilder extends CommandBuilder {

    public ShellCommandBuilder(final CommandConsoleFactory consoleFactory) {
        super(consoleFactory);
    }

    /**
     * Build a Command object.
     */
    public ShellCommand build_impl() throws IOException {
        Preconditions.checkNotNull(directory);
        ImmutableList<String> args = this.args.build();
        CommandConsole console = consoleName == null ? null : consoleFactory.get(consoleName,
            "Running " + String.join(" ", args) + " from " + directory.toString());
        
        ShellCommand command = new ShellCommand(console, directory, args, stdoutSelector, stderrSelector, stdout, stderr,
            progressMonitor, timeoutMS);
        
        return command;
    }

}
