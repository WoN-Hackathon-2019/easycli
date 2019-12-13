package at.apf.easycli;

public interface CliEngine {

    void register(Object obj);

    Object parse(String cmd) throws Exception;

}
