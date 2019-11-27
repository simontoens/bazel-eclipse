package com.salesforce.bazel.eclipse.command.mock;

import java.io.IOException;
import java.util.List;

import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.salesforce.bazel.eclipse.command.BazelProcessBuilder;
import com.salesforce.bazel.eclipse.command.Command;

public class MockCommand implements Command {

    public MockCommand() {
        
    }
    
    
    public List<String> commandTokens;
    public List<String> outputLines;
    public List<String> errorLines;
    
    @Override
    public int run() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public ImmutableList<String> getSelectedErrorLines() {
        if (errorLines != null) {
            return ImmutableList.copyOf(errorLines);
        }
        return ImmutableList.of();
    }

    @Override
    public BazelProcessBuilder getProcessBuilder() {
        BazelProcessBuilder pb = Mockito.mock(BazelProcessBuilder.class);
        
        // you may need to add more mocking behaviors
        Mockito.when(pb.command()).thenReturn(commandTokens);
        
        return pb;
    }

    @Override
    public ImmutableList<String> getSelectedOutputLines() {
        if (outputLines != null) {
            return ImmutableList.copyOf(outputLines);
        }
        return ImmutableList.of();
    }

}
