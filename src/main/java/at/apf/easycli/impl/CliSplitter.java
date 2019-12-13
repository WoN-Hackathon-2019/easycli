package at.apf.easycli.impl;

import at.apf.easycli.exception.MalformedCommandException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CliSplitter {

    public List<String> split(String cmd) {
        List<String> parts = new LinkedList<>();
        int lastPos = 0;
        boolean inString = false;

        for (int i = 0; i < cmd.length(); i++) {
            if (cmd.charAt(i) == '"' && (i == 0 || cmd.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString && cmd.charAt(i) == ' ') {
                parts.add(cmd.substring(lastPos, i));
                lastPos = i + 1;
            }
        }

        if (inString) {
            throw new MalformedCommandException("Malformed string. \" is missing");
        }

        parts.add(cmd.substring(lastPos, cmd.length()));

        parts = parts.stream()
                .map(s -> s.startsWith("\"") ? s.substring(1) : s)
                .map(s -> s.endsWith("\"") ? s.substring(0, s.length() - 1) : s)
                .map(s -> s.replace("\\\"", "\""))
                .collect(Collectors.toList());

        return parts;
    }
}
