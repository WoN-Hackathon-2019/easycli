package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.exception.MalformedMethodException;
import org.junit.Test;

import javax.management.openmbean.KeyAlreadyExistsException;

public class EasyEngineAddTest {

    private CliEngine engine = new EasyEngine();

    @Test
    public void addWithoutParams_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla() {

            }
        });
    }

    @Test
    public void addTwoCommands_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla() {

            }
            @Command("/tra")
            void tra() {

            }
        });
    }

    @Test
    public void addSingleParam_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(String asdf) {

            }
        });
    }

    @Test
    public void addFiveParams_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(String asdf, String bla, String tra, String lkjlkj, String kdkdk) {

            }
        });
    }

    @Test
    public void addSingleOptionalParam_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Optional String nanan) {

            }
        });
    }

    @Test
    public void addSingleDefaultValueParam_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@DefaultValue("jojo") String var1) {

            }
        });
    }

    @Test
    public void addThreeOptionals_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Optional int a, @Optional int b, @Optional int c) {

            }
        });
    }

    @Test
    public void addSingleArray_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(String[] arr) {

            }
        });
    }

    @Test
    public void addWithArrayAtTheEnd_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(String var1, String[] arr) {

            }
        });
    }

    @Test
    public void addWithFlagAfterOptional_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Optional String var1, @Flag('c') boolean c) {

            }
        });
    }

    @Test
    public void addWithFlagAfterArray_shouldWork() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(String[] var1, @Flag('c') boolean c) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void addTwoArrays_shouldThrowMalformedMethodException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(int[] arr1, int[] arr2) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void addParamAfterArray_shouldThrowMalformedMethodException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(int[] arr, int var1) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void addNonOptionalAfterOptional_shouldThrowMalformedMethodException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Optional int a, int b) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void addNoBooleanForFlag_shouldThrowMalformedMethodException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Flag('a') int a) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void addComplexType_shouldThrowMalformedMethodException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(Object obj) {

            }
        });
    }

    @Test(expected = KeyAlreadyExistsException.class)
    public void addCommandTwice_shouldThrowKeyAlreadyExistsException() {
        engine.add(new Object(){
            @Command("/bla")
            void bla(int a) {

            }
            @Command("/bla")
            void bla() {

            }
        });
    }
}
