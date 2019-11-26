package com.salesforce.bazel.eclipse.command.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.salesforce.bazel.eclipse.abstractions.WorkProgressMonitor;
import com.salesforce.bazel.eclipse.command.BazelCommandLineToolConfigurationException;

/**
 * Helper that knows how to run bazel query commands.
 */
public class BazelQueryHelper {

    /**
     * Underlying command invoker which takes built Command objects and executes them.
     */
    private final BazelCommandExecutor bazelCommandExecutor;
    
    public BazelQueryHelper(BazelCommandExecutor bazelCommandExecutor) {
        this.bazelCommandExecutor = bazelCommandExecutor;
    }

    /**
     * Returns the list of targets found in the BUILD files for the given sub-directories. Uses Bazel Query to build the
     * list.
     *
     * @param progressMonitor
     *            can be null
     * @throws BazelCommandLineToolConfigurationException
     */
    public synchronized List<String> listBazelTargetsInBuildFiles(File bazelWorkspaceRootDirectory, WorkProgressMonitor progressMonitor,
            File... directories) throws IOException, InterruptedException, BazelCommandLineToolConfigurationException {
        ImmutableList.Builder<String> argBuilder = ImmutableList.builder();
        argBuilder.add("query");
        for (File f : directories) {
            String directoryPath = f.toURI().relativize(bazelWorkspaceRootDirectory.toURI()).getPath();
            argBuilder.add(directoryPath+"/...");
        }
        return bazelCommandExecutor.runBazelAndGetOutputLines(bazelWorkspaceRootDirectory, progressMonitor, 
            argBuilder.build(), (t) -> t);
    }

    
    /**
     * Gives a list of target completions for the given beginning string. The result is the list of possible completion
     * for a target pattern starting with string.
     * <p>
     * <b>WARNING:</b> this method was written for the original Bazel plugin for a search feature, but was not actually
     * used as far as we can tell. It may or may not work as advertised.
     *
     * @param userSearchString
     *            the partial target string entered by the user
     *
     * @throws BazelCommandLineToolConfigurationException
     */
    public List<String> getMatchingTargets(File bazelWorkspaceRootDirectory, String userSearchString, WorkProgressMonitor progressMonitor)
            throws IOException, InterruptedException, BazelCommandLineToolConfigurationException {
        if (userSearchString.equals("/") || userSearchString.isEmpty()) {
            return ImmutableList.of("//");
        } else if (userSearchString.contains(":")) {
            // complete targets using `bazel query`
            int idx = userSearchString.indexOf(':');
            final String packageName = userSearchString.substring(0, idx);
            final String targetPrefix = userSearchString.substring(idx + 1);
            List<String> args = ImmutableList.<String> builder().add("query", packageName + ":*").build();
            Function<String, String> selector = line -> {
                int i = line.indexOf(':');
                String s = line.substring(i + 1);
                return !s.isEmpty() && s.startsWith(targetPrefix) ? (packageName + ":" + s) : null;
            };

            List<String> outputLines = this.bazelCommandExecutor.runBazelAndGetOuputLines(ConsoleType.WORKSPACE,
                bazelWorkspaceRootDirectory, progressMonitor, args, selector);

            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.addAll(outputLines);

            if ("all".startsWith(targetPrefix)) {
                builder.add(packageName + ":all");
            }
            if ("*".startsWith(targetPrefix)) {
                builder.add(packageName + ":*");
            }
            return builder.build();
        } else {
            // complete packages
            int lastSlash = userSearchString.lastIndexOf('/');
            final String prefix = lastSlash > 0 ? userSearchString.substring(0, lastSlash + 1) : "";
            final String suffix = lastSlash > 0 ? userSearchString.substring(lastSlash + 1) : userSearchString;
            final String directory = (prefix.isEmpty() || prefix.equals("//")) ? ""
                    : prefix.substring(userSearchString.startsWith("//") ? 2 : 0, prefix.length() - 1);
            File file = directory.isEmpty() ? bazelWorkspaceRootDirectory : new File(bazelWorkspaceRootDirectory, directory);
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            File[] files = file.listFiles((f) -> {
                // Only give directories whose name starts with suffix...
                return f.getName().startsWith(suffix) && f.isDirectory()
                // ...that does not start with '.'...
                        && !f.getName().startsWith(".")
                // ...and is not a Bazel convenience link
                        && (!file.equals(bazelWorkspaceRootDirectory) || !f.getName().startsWith("bazel-"));
            });
            if (files != null) {
                for (File d : files) {
                    builder.add(prefix + d.getName() + "/");
                    if (new File(d, "BUILD").exists()) {
                        builder.add(prefix + d.getName() + ":");
                    }
                }
            }
            if ("...".startsWith(suffix)) {
                builder.add(prefix + "...");
            }
            return builder.build();
        }
    }

}
