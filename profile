#!/bin/zsh

javac Main.java

time java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints\
    -agentpath:/opt/homebrew/lib/libasyncProfiler.dylib=start,event=cpu,file=flamegraph.html\
    Main 256 128
