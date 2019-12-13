package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.exception.CommandNotFoundException;
import at.apf.easycli.exception.MalformedCommandException;
import at.apf.easycli.exception.MalformedMethodException;
import at.apf.easycli.util.Tuple;
import at.apf.easycli.util.TypeParsre;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyEngine implements CliEngine {

    private Map<String, Tuple<Method, Object>> commands = new HashMap<>();
    private TypeParsre tp = new TypeParsre();
    private CliSplitter splitter = new CliSplitter();

    @Override
    public void add(Object obj) {
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
                if (command == null || command.isEmpty()) {
                    throw new MalformedMethodException("Command value can not be empty");
                }
                commands.put(command, new Tuple<>(m, obj));
            }
        }
    }

    public Object parse(String cmd) throws Exception {
        List<String> parts = splitter.split(cmd);
        List<String> flags = getFlags(parts);
        List<String> arguments = getArguments(parts);
        String command = parts.get(0);

        if (!commands.containsKey(command)) {
            throw new CommandNotFoundException("Command '" + command + "' not registered");
        }

        Method method = commands.get(command).getKey();
        Parameter[] params = method.getParameters();
        Object[] paramValues = new Object[params.length];

        int cmdIndex = 0;
        for (int i = 0; i < params.length; i++) {
            if (!handleFlag(flags, params[i], paramValues, i)) {
                cmdIndex = handleArgument(arguments, cmdIndex, params[i], paramValues, i);
                if (cmdIndex < 0) {
                    break;
                }
            }
        }

        if (cmdIndex >= arguments.size()) {
            throw new MalformedCommandException("Too many arguments passed for command '" + command + "'");
        }

        try {
            method.setAccessible(true);
            return method.invoke(commands.get(command).getValue(), paramValues);
        } catch (Exception e) {
            throw e;
        }
    }

    private int handleArgument(List<String> arguments, int cmdIndex, Parameter par, Object[] paramValues, int argumentPosition) {

        if (arguments.size() >= cmdIndex) {
            // Not set
            if (par.isAnnotationPresent(Optional.class)) {
                if (par.getType().equals(String.class) || par.getType().isArray()) {
                    paramValues[argumentPosition] = null;
                } else if (par.getType().equals(boolean.class)) {
                    paramValues[argumentPosition] = false;
                } else {
                    paramValues[argumentPosition] = 0;
                }
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
            if (arrayType.equals(int.class)) {
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
            } else if (arrayType.equals(String.class)) {
                String[] arr = new String[arguments.size() - cmdIndex];
                int j = 0;
                while (cmdIndex < arguments.size()) {
                    arr[j] = arguments.get(cmdIndex);
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

    private boolean handleFlag(List<String> flags, Parameter par, Object[] paramValues, int argumentPosition) {
        if (!par.isAnnotationPresent(Flag.class)) {
            return false;
        }

        Flag flagAnno = par.getAnnotation(Flag.class);
        paramValues[argumentPosition] = flags.contains(flagAnno.alternative()) || flags.contains(flagAnno.value() + "");
        return true;
    }

    private List<String> getArguments(List<String> cmdParts) {
        List<String> args = new ArrayList<>();

        for (int i = 1; i < cmdParts.size(); i++) {
            String p = cmdParts.get(i);
            if (!p.startsWith("-")) {
                args.add(p);
            }
        }

        return args;
    }


    private List<String> getFlags(List<String> cmdParts) {
        List<String> flags = new ArrayList<>();

        for (String p: cmdParts) {
            if (p.startsWith("--")) {
                flags.add(p.substring(2));
            } else if (p.startsWith("-")) {
                for (int i = 1; i < p.length(); i++) {
                    flags.add(p.charAt(i) + "");
                }
            }
        }

        return flags;
    }

}
