package at.apf.easycli.util;

public class TypeParsre {

    public boolean isValidType(Class type) {
        return type.equals(int.class) || type.equals(long.class) || type.equals(float.class)
                || type.equals(double.class) || type.equals(boolean.class) || type.equals(String.class)
                || (type.isArray() && isValidType(type.getComponentType()));
    }

    public Object parseType(Class type, String str) {
        if (type.equals(int.class)) {
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
        } else {
            return null;
        }
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
}
