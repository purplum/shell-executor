# ShellExecutor

### Utility that supports executing shell scripts from Java.

It provides a clean interface, QoL features and solves several issues such as:
- handling of both output streams (stout and sterr)
- handling quote characters in commands
- providing commands as whole Strings instead of indefinite number of arguments

### API

#### public static List<String> execute(String cmd)
Starts 'cmd' command and waits for it to finish. Returns List of output lines (both stdout and stderr).

#### public static void call(String cmd)
Starts 'cmd' command.

#### public static void executeWithPrint(String cmd)
Starts 'cmd' command and waits for it to finish. After finished prints lines to stdout.

#### public static boolean executeWithAssert(String cmd, String[] ok, String[] nok)
Starts 'cmd' command and waits for it to finish. Asserts successful execution based output and 'ok', 'nok' keywords provided. Throws IllegalStateException if no keyword is found.
