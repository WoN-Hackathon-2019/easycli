package at.apf.easycli.impl;

import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Meta;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.annotation.Usage;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsagePrinter {

    public String commandUsage(Method method) {
        Parameter[] params = method.getParameters();
        List<Parameter> flags = Stream.of(params)
                .filter(p -> p.isAnnotationPresent(Flag.class))
                .sorted((a, b) -> a.getAnnotation(Flag.class).value() - b.getAnnotation(Flag.class).value())
                .collect(Collectors.toList());
        List<Parameter> arguments = Stream.of(params)
                .filter(p -> !p.isAnnotationPresent(Flag.class) && !p.isAnnotationPresent(Meta.class))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("Usage: ");
        sb.append(method.getAnnotation(Command.class).value());

        if (flags.size() > 0) {
            sb.append(" [FLAG...]");
        }

        int optionalCounter = 0;
        for (Parameter p: arguments) {
            sb.append(" ");
            boolean optional = p.isAnnotationPresent(Optional.class) || p.isAnnotationPresent(DefaultValue.class);
            optionalCounter += optional ? 1 : 0;
            sb.append(optional ? "[" : "");
            sb.append("<");
            sb.append(p.getType().isArray() ? p.getType().getComponentType() : p.getType().getSimpleName());
            sb.append(">");
            sb.append(p.getType().isArray() ? "..." : "");
        }
        for (int i = 0; i < optionalCounter; i++) {
            sb.append("]");
        }

        sb.append("\n");
        sb.append(method.isAnnotationPresent(Usage.class) ? method.getAnnotation(Usage.class).value() + "\n" : "");

        if (flags.size() > 0) {
            sb.append("\n");
            sb.append("FLAGS:");
            sb.append("\n");
        }
        int longestAlternative = flags.stream()
                .map(p -> p.getAnnotation(Flag.class).alternative())
                .filter(a -> !a.isEmpty())
                .map(a -> a.length() + 4)
                .max((a, b) -> a - b)
                .orElse(0);

        for (Parameter p: flags) {
            sb.append("  -");
            Flag flagAnno = p.getAnnotation(Flag.class);
            sb.append(flagAnno.value());
            int curLen = 0;
            if (!flagAnno.alternative().isEmpty()) {
                sb.append(", --");
                sb.append(flagAnno.alternative());
                curLen += 4 + flagAnno.alternative().length();
            }
            while (curLen < longestAlternative) {
                sb.append(" ");
                curLen++;
            }
            sb.append("  ");
            sb.append(p.isAnnotationPresent(Usage.class) ? p.getAnnotation(Usage.class).value() : "");
            sb.append("\n");
        }

        return sb.toString();
    }

    public String listCommands(List<Method> methods) {
        StringBuilder sb = new StringBuilder();
        methods = methods.stream()
                .sorted((a, b) -> a.getAnnotation(Command.class).value().compareTo(
                b.getAnnotation(Command.class).value()))
                .collect(Collectors.toList());
        int longestCommand = methods.stream()
                .map(m -> m.getAnnotation(Command.class).value().length())
                .max((a, b) -> a - b)
                .orElse(0);
        for (Method m: methods) {
            String cmd = m.getAnnotation(Command.class).value();
            while (cmd.length() < longestCommand) {
                cmd += " ";
            }
            sb.append(cmd);
            sb.append("  ");
            sb.append(m.isAnnotationPresent(Usage.class) ? m.getAnnotation(Usage.class).value() : "");
            sb.append("\n");
        }
        return sb.toString();
    }
}
