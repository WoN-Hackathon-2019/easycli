package at.apf.easycli;

/***
 * Able to register Objects that contain @{@link at.apf.easycli.exception.CommandNotFoundException} annotated methods
 * and afterwards parse command strings to invoke those methods with all its arguments.
 */
public interface CliEngine {

    /***
     * Registers the obj in the engine and evaluates if all @{@link at.apf.easycli.annotation.Command} annotated methods
     * are well-formed.
     * @param obj Object to register.
     *
     * <pre>
     *   cliEngine.register(new Object() {
     *       &#64;Command("/add")
     *       int add(int a, int b) {
     *           return a + b;
     *       }
     *   });
     * </pre>
     */
    void register(Object obj);

    /***
     * Parses the command string, and searchs for a registered command implementation and invokes it with all its
     * arguments.
     * @param cmd command to parse.
     * @return the result of the invoked command method.
     * @throws Exception if the command can't be found, or there is a parsing error.
     *
     * <pre>
     *     int result = cliEngine.parse("/add 5 4");
     * </pre>
     */
    Object parse(String cmd) throws Exception;

    /***
     * Parses the command string, and searchs for a registered command implementation and invokes it with all its
     * arguments and the given metadata objects.
     * @param cmd command to parse.
     * @param metadata metadata to pass into the implemented command method.
     * @return the result of the invoked command method.
     * @throws Exception if the command can't be found, or there is a parsing error.
     *
     * <pre>
     *     int result = cliEngine.parse("/add 5 4", obj1, obj2);
     * </pre>
     */
    Object parse(String cmd, Object... metadata) throws Exception;

    /***
     * Lists all registered commands with their main usage description.
     * @return String with each registered command in a single line.
     */
    String listCommands();

    /***
     * Returns the usage text for the given command.
     * @param cmd Command that needs to start with a registered @{@link at.apf.easycli.annotation.Command}.
     * @return The usage text for the given command.
     */
    String usage(String cmd);

}
