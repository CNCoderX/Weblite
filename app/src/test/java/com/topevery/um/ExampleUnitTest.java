package com.topevery.um;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        File file = new File("D:\\android\\temp\\NDK\\app\\src\\main\\cpp\\lame");
        File[] child = file.listFiles();
        for (File f : child) {
            System.out.println("src/main/cpp/lame/" + f.getName());
        }
    }
}