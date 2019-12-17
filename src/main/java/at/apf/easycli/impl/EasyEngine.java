package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Meta;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.annotation.Usage;
import at.apf.easycli.exception.CommandNotFoundException;
import at.apf.easycli.exception.MalformedCommandException;
import at.apf.easycli.exception.MalformedMethodException;
import at.apf.easycli.util.Tuple;
import at.apf.easycli.util.TypeParser;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * Implementation to register command-containing objects and then parse command strings to invoke the implemented
 * commands.
 */
public class EasyEngine implements CliEngine {

    private Map<String, Tuple<Method, Object>> commands = new HashMap<>();
    private TypeParser tp = new TypeParser();
    private CliSplitter splitter = new CliSplitter();
    private UsagePrinter usagePrinter = new UsagePrinter();

    @Override
    public void register(Object obj) {
        for (Method m: obj.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                // Constraints for arguments:
                //   - Array at the end
                //   - Only one array
                //   - Optionals at the end
                Parameter[] parameters = m.getParameters();
                boolean hasOptionals = false;
                boolean hasArray = false;
                for (int i = 0; i < parameters.length; i++) {
                    Parameter par = parameters[i];

                    if (par.isAnnotationPresent(Meta.class)) {
                        continue;
                    }

                    if (!tp.isValidType(par.getType())) {
                        throw new MalformedMethodException("Only simple types and arrays are allowed");
                    }

                    if (par.isAnnotationPresent(Flag.class)) {
                        if (!par.getType().equals(boolean.class)) {
                            throw new MalformedMethodException("Flag parameter must be boolean");
                        }
                        continue;
                    }

                    if (hasArray) {
                        throw new MalformedMethodException("Only one Array at the end of the parameter definition is allowed.");
                    }
                    hasArray = par.getType().isArray();

                    boolean isOptional = par.isAnnotationPresent(Optional.class) || par.isAnnotationPresent(DefaultValue.class);
                    if (hasOptionals && !isOptional) {
                        throw new MalformedMethodException("Non-optional parameter at position " + i
                                + ".Only optional parameters are allowed after an optional parameter");
                    }
                    hasOptionals = isOptional;
                }
                String command = m.getAnnotation(Command.class).value();
                if (command.isEmpty()) {
                    throw new MalformedMethodException("Command value can not be empty");
                }
                if (commands.containsKey(command)) {
                    throw new KeyAlreadyExistsException("Command '" + command + "' already exists");
                }
                commands.put(command, new Tuple<>(m, obj));
            }
        }
    }

    @Override
    public Object parse(String cmd) throws Exception {
        return parse(cmd, new Object[0]);
    }

    @Override
    public Object parse(String cmd, Object... metadata) throws Exception {
        List<String> parts = splitter.split(cmd);
        List<String> flags = parts.stream() // add --flag
                .skip(1)
                .filter(a -> a.startsWith("--"))
                .map(a -> a.substring(2))
                .collect(Collectors.toList());
        flags.addAll(parts.stream() // add -abc
                .skip(1)
                .filter(a -> a.startsWith("-") && !a.startsWith("--"))
                .filter(a -> !(a.charAt(1) >= '0' && a.charAt(1) <= '9'))
                .map(a -> a.substring(1))
                .flatMapToInt(CharSequence::chars)
                .mapToObj(i -> Character.toString((char) i))
                .collect(Collectors.toList()));
        List<String> arguments = parts.stream()
                .skip(1)
                .filter(a -> !a.startsWith("-") || (a.charAt(1) >= '0' && a.charAt(1) <= '9'))
                .collect(Collectors.toList());
        String command = parts.get(0);

        if (!commands.containsKey(command)) {
            throw new CommandNotFoundException("Command '" + command + "' not registered");
        }

        Method method = commands.get(command).getKey();
        Parameter[] params = method.getParameters();
        Object[] paramValues = new Object[params.length];

        int cmdIndex = 0;
        int metaIndex = 0;
        for (int i = 0; i < params.length; i++) {
            if (handleMetaArgument(metadata, metaIndex, params[i], paramValues, i)) {
                metaIndex++;
                continue;
            }
            if (!handleFlag(flags, params[i], paramValues, i)) {
                cmdIndex = handleArgument(arguments, cmdIndex, params[i], paramValues, i);
            }
        }
        int countArgumentParameters = (int) Stream.of(method.getParameters())
                .filter(p -> !p.isAnnotationPresent(Meta.class) && !p.isAnnotationPresent(Flag.class))
                .count();

        if ((cmdIndex > 0 && arguments.size() > countArgumentParameters) || (countArgumentParameters == 0 && arguments.size() > 0)) {
            throw new MalformedCommandException("Too many arguments passed for command '" + command + "'");
        }


        method.setAccessible(true);
        return method.invoke(commands.get(command).getValue(), paramValues);
    }

    @Override
    public String listCommands() {
        return usagePrinter.listCommands(commands.values().stream().map(t -> t.getKey()).collect(Collectors.toList()));
    }

    @Override
    public String usage(String cmd) {
        List<String> parts = splitter.split(cmd);
        String command = parts.get(0);

        if (!commands.containsKey(command)) {
            throw new CommandNotFoundException("Command '" + command + "' not registered");
        }

        Method method = commands.get(command).getKey();
        return usagePrinter.commandUsage(method);
    }

    /***
     * if possible inserts the meta object of the metadata array at position metaIndex into the
     * paramValues-array at position argumentPosition.
     * @param metadata array of all metadata objects for the command.
     * @param metaIndex position which metadata object of the metadata array is used.
     * @param par parameter definition which will be filled.
     * @param paramValues the array where the metadata object value gets inserted.
     * @param argumentPosition the position where the metadata object should be inserted in the paramValues-array.
     * @return true if par was a Meta parameter, otherwise false
     */
    private boolean handleMetaArgument(Object[] metadata, int metaIndex, Parameter par, Object[] paramValues, int argumentPosition) {
        if (!par.isAnnotationPresent(Meta.class)) {
            return false;
        }

        if (metaIndex >= metadata.length) {
            if (par.isAnnotationPresent(Optional.class)) {
                paramValues[argumentPosition] = tp.defaultValue(par);
            } else if (par.isAnnotationPresent(DefaultValue.class)) {
                String defaultValue = par.getAnnotation(DefaultValue.class).value();
                paramValues[argumentPosition] = tp.parseType(par.getType(), defaultValue);
            } else {
                throw new MalformedCommandException("Metadata argument '" + par.getName() + "' is missing.");
            }
        } else {
            paramValues[argumentPosition] = metadata[metaIndex];
        }

        return true;
    }

    /***
     * if possible inserts the argument of the arguments-list at position cmdIndex into the paramValues-array at
     * position argumentPosition. It parses the argument to the needed type defined in par.
     * @param arguments list of all arguments of the command line.
     * @param cmdIndex position which argument of the arguments list is used.
     * @param par parameter definition which will be filled.
     * @param paramValues the array where the arguments parsed value gets inserted.
     * @param argumentPosition the position where the parsed argument should be inserted in the paramValues-array.
     * @return the next cmdIndex or -1 if it is finished.
     */
    private int handleArgument(List<String> arguments, int cmdIndex, Parameter par, Object[] paramValues, int argumentPosition) {

        if (arguments.size() <= cmdIndex) {
            // Not set
            if (par.isAnnotationPresent(Optional.class)) {
                paramValues[argumentPosition] = tp.defaultValue(par);
            } else if (par.isAnnotationPresent(DefaultValue.class)) {
                String defaultValue = par.getAnnotation(DefaultValue.class).value();
                paramValues[argumentPosition] = tp.parseType(par.getType(), defaultValue);
            } else {
                throw new MalformedCommandException("Argument '" + par.getName() + "' is missing.");
            }
            return cmdIndex + 1;
        }

        if (par.getType().isArray()) {
            // Handle array
            Class arrayType = par.getType().getComponentType();
            if (arrayType.equals(char.class)) {
                char[] arr = new char[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toChar(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else if (arrayType.equals(int.class)) {
                int[] arr = new int[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toInt(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else if (arrayType.equals(long.class)) {
                long[] arr = new long[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toLong(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else if (arrayType.equals(float.class)) {
                float[] arr = new float[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toFloat(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else if (arrayType.equals(double.class)) {
                double[] arr = new double[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toDouble(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else if (arrayType.equals(boolean.class)) {
                boolean[] arr = new boolean[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.toBool(arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            } else {
                Object[] arr = (Object[]) Array.newInstance(arrayType, arguments.size() - cmdIndex);
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = tp.parseType(arrayType, arguments.get(cmdIndex));
                    cmdIndex++;
                    j++;
                }
                paramValues[argumentPosition] = arr;
            }
            return -1;
        }

        paramValues[argumentPosition] = tp.parseType(par.getType(), arguments.get(cmdIndex));

        return cmdIndex + 1;
    }

    /***
     * Checks if par is a flag parameter. If yes it searches if the flag is set in the flags list and sets the
     * paramValues[argumentPosition] true. if it is not set, it the paramValues position will be set to false.
     * @param flags flags-list where all parsed flags are contained.
     * @param par parameter definition which will be filled.
     * @param paramValues the array where the arguments parsed value gets inserted.
     * @param argumentPosition the position where the parsed argument should be inserted in the paramValues-array.
     * @return true if par was a flag parameter, otherwise false.
     */
    private boolean handleFlag(List<String> flags, Parameter par, Object[] paramValues, int argumentPosition) {
        if (!par.isAnnotationPresent(Flag.class)) {
            return false;
        }

        Flag flagAnno = par.getAnnotation(Flag.class);
        paramValues[argumentPosition] = flags.contains(flagAnno.alternative()) || flags.contains(flagAnno.value() + "");
        return true;
    }

}
