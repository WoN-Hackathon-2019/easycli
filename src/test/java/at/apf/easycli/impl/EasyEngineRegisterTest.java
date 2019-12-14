package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Meta;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.exception.MalformedMethodException;
import org.junit.Test;

import javax.management.openmbean.KeyAlreadyExistsException;

public class EasyEngineRegisterTest {

    private CliEngine engine = new EasyEngine();

    @Test
    public void registerWithoutParams_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla() {

            }
        });
    }

    @Test
    public void registerTwoCommands_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla() {

            }
            @Command("/tra")
            void tra() {

            }
        });
    }

    @Test
    public void registerSingleParam_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String asdf) {

            }
        });
    }

    @Test
    public void registerFiveParams_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String asdf, String bla, String tra, String lkjlkj, String kdkdk) {

            }
        });
    }

    @Test
    public void registerSingleOptionalParam_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Optional String nanan) {

            }
        });
    }

    @Test
    public void registerSingleDefaultValueParam_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@DefaultValue("jojo") String var1) {

            }
        });
    }

    @Test
    public void registerThreeOptionals_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Optional int a, @Optional int b, @Optional int c) {

            }
        });
    }

    @Test
    public void registerSingleArray_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String[] arr) {

            }
        });
    }

    @Test
    public void registerWithArrayAtTheEnd_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String var1, String[] arr) {

            }
        });
    }

    @Test
    public void registerWithFlagAfterOptional_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Optional String var1, @Flag('c') boolean c) {

            }
        });
    }

    @Test
    public void registerWithFlagAfterArray_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String[] var1, @Flag('c') boolean c) {

            }
        });
    }

    @Test
    public void registerWithMetaAfterArray_shouldWork() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(String[] var1, @Meta Object obj) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void registerTwoArrays_shouldThrowMalformedMethodException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(int[] arr1, int[] arr2) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void registerParamAfterArray_shouldThrowMalformedMethodException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(int[] arr, int var1) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void registerNonOptionalAfterOptional_shouldThrowMalformedMethodException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Optional int a, int b) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void registerNoBooleanForFlag_shouldThrowMalformedMethodException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag('a') int a) {

            }
        });
    }

    @Test(expected = MalformedMethodException.class)
    public void registerComplexType_shouldThrowMalformedMethodException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(Object obj) {

            }
        });
    }

    @Test(expected = KeyAlreadyExistsException.class)
    public void registerCommandTwice_shouldThrowKeyAlreadyExistsException() {
        engine.register(new Object(){
            @Command("/bla")
            void bla(int a) {

            }
            @Command("/bla")
            void bla() {

            }
        });
    }
}
