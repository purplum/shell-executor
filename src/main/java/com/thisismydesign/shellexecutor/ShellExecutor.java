package com.thisismydesign.shellexecutor;

import com.thisismydesign.stringprocessor.StringProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ShellExecutor {

    /**
     * Will call cmd with 'sh' command - it is required if cmd contains a quote
     */
    public static List<String> execute(String cmd) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", cmd);

        processBuilder.redirectErrorStream(true);
        Process p = processBuilder.start();

        Scanner scanner = new Scanner(p.getInputStream());
        List<String> output = new ArrayList<>();
        while (scanner.hasNextLine()) {
            output.add(scanner.nextLine());
        }
        scanner.close();

        p.waitFor();

        return output;
    }

    public static void call(String cmd) throws IOException {
        new ProcessBuilder("sh", "-c", cmd).start();
    }

    public static void executeWithPrint(String cmd) throws IOException, InterruptedException {
        List<String> lines = execute(cmd);
        lines.forEach(System.out::println);
    }

    public static boolean executeWithAssert(String cmd, String[] ok, String[] nok) throws IOException,
            InterruptedException {
        List<String> lines = execute(cmd);
        String[] keyWords = Stream.concat(Arrays.stream(ok), Arrays.stream(nok)).toArray(String[]::new);
        String found = StringProcessor.findAny(keyWords, lines);
        if (found == null) {
            throw new IllegalStateException(String.format("Expected results (%s) of command not found in output: %s",
                    Arrays.toString(keyWords), Arrays.toString(lines.toArray())));
        } else return StringProcessor.containsAny(ok, found);
    }
}
