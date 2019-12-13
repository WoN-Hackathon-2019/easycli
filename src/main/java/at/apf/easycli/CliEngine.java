package at.apf.easycli;

public interface CliEngine {

    void add(Object obj);

    Object parse(String cmd) throws Exception;

}
