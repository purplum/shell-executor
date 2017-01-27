package com.thisismydesign.shellexecutor;

import com.thisismydesign.stringprocessor.StringProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * This class supports executing shell scripts.
 *
 * <p>It provides a clean interface, QoL features and solves several issues such as:
 * <ul>
 * <li>handling of both output streams (stdout and stderr)
 * <li>handling quote characters in commands
 * <li>providing commands as whole Strings instead of indefinite number of arguments
 * </ul>
 */
public class ShellExecutor {

    /**
     * Starts 'cmd' command and waits for it to finish.
     * Returns List of output lines (both stdout and stderr).
     *
     * <p>Will call cmd with 'sh' command so that it may contain quotes
     *
     * @param cmd bash command
     * @return output lines
     * @throws IOException if {@link ProcessBuilder#start} throws IOException
     * @throws InterruptedException if {@link Process#waitFor} throws InterruptedException
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

    /**
     * Starts 'cmd' command asynchronously.
     *
     * @param cmd bash command
     * @throws IOException if {@link ProcessBuilder#start} throws IOException
     */
    public static void call(String cmd) throws IOException {
        new ProcessBuilder("sh", "-c", cmd).start();
    }

    /**
     * Starts 'cmd' command and waits for it to finish.
     *
     * <p>After finished prints lines to stdout.
     *
     * @param cmd bash command
     * @throws IOException if {@link ProcessBuilder#start} throws IOException
     * @throws InterruptedException if {@link Process#waitFor} throws InterruptedException
     */
    public static void executeWithPrint(String cmd) throws IOException, InterruptedException {
        List<String> lines = execute(cmd);
        lines.forEach(System.out::println);
    }

    /**
     * Starts 'cmd' command and waits for it to finish.
     *
     * <p>Asserts successful execution based output and 'ok', 'nok' keywords provided.
     *
     * @param cmd bash command
     * @param ok keywords in output signaling correct execution
     * @param nok keywords in output signaling incorrect execution
     * @return whether execution was correct
     * @throws IOException if {@link ProcessBuilder#start} throws IOException
     * @throws InterruptedException if {@link Process#waitFor} throws InterruptedException
     * @throws IllegalStateException if no keyword is found
     */
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
