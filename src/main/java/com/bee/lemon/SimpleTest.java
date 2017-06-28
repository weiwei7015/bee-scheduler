package com.bee.lemon;


import java.util.regex.Pattern;

public class SimpleTest {

    public static void main(String[] args) {
        System.out.println(Pattern.matches(".* --datafile=\\S+ .*", " --server.port=8080 --datafile=F:/tmp/hsql/scheduler "));
    }
}
