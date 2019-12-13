package at.apf.easycli.impl.util;

public class MutableContainer<T> {

    private T value;

    public MutableContainer(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
