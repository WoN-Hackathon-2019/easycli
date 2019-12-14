package at.apf.easycli.util;

import java.lang.reflect.Parameter;
import java.util.EnumSet;

public class TypeParser {

    public boolean isValidType(Class type) {
        return type.equals(int.class) || type.equals(long.class) || type.equals(float.class)
                || type.equals(double.class) || type.equals(boolean.class) || type.equals(String.class)
                || type.equals(char.class) || type.isEnum()
                || (type.isArray() && isValidType(type.getComponentType()) && !type.getComponentType().isArray());
    }

    /***
     * Retunrs the default value for an optional {@link Parameter}.
     * @param par the parameter to find out the default value.
     * @return the default value for the par's type.
     */
    public Object defaultValue(Parameter par) {
        if (par.getType().equals(boolean.class)) {
            return false;
        } else if (par.getType().equals(int.class) || par.getType().equals(long.class)
                || par.getType().equals(char.class)) {
            return 0;
        } else if (par.getType().equals(float.class) || par.getType().equals(double.class)) {
            return 0.0;
        } else {
            return null;
        }
    }

    public Object parseType(Class type, String str) {
        if (type.equals(char.class)) {
            return toChar(str);
        } else if (type.equals(int.class)) {
            return toInt(str);
        } else if (type.equals(float.class)) {
            return toFloat(str);
        } else  if (type.equals(double.class)) {
            return toDouble(str);
        } else if (type.equals(long.class)) {
            return toLong(str);
        } else if (type.equals(String.class)) {
            return str;
        } else if (type.equals(boolean.class)) {
            return toBool(str);
        } else if (type.isEnum()) {
            return toEnum(type, str);
        } else {
            return null;
        }
    }

    public char toChar(String str) {
        return str.toCharArray()[0];
    }

    public int toInt(String str) {
        return Integer.parseInt(str);
    }

    public long toLong(String str) {
        return Long.parseLong(str);
    }

    public float toFloat(String str) {
        return Float.parseFloat(str);
    }

    public double toDouble(String str) {
        return Double.parseDouble(str);
    }

    public boolean toBool(String str) {
        return Boolean.parseBoolean(str);
    }

    public <E extends Enum<E>> E toEnum(Class<E> enumType, String str) {
        for (E enumEntity: EnumSet.allOf(enumType)) {
            if (enumEntity.name().equalsIgnoreCase(str)) {
                return enumEntity;
            }
        }
        throw new IllegalArgumentException("'" + str + "' is not an entity in the enum '" + enumType.getName() + "'");
    }
}
