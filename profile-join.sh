java -jar ./target/benchmarks.jar -jvmArgs "-Xms4g -Xmx4g -XX:MaxInlineLevel=15 -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints" -foe true -rf csv -rff results.csv -prof "async:output=flamegraph,text;simple=true;dir=/tmp/Profile;width=1920;libPath=/home/mfusco/software/async-profiler-1.8.1-linux-x64/build/libasyncProfiler.so;jstackdepth=50;interval=1000000" org.benchmark.join.MyBenchmark
