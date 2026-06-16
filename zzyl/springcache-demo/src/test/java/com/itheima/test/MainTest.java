package com.itheima.test;

import java.util.Objects;

/**
 * @author sjqn
 * @date 2023/10/31
 */
public class MainTest {

    public static void main(String[] args) {
        int result = Objects.hash("李大","21");
        System.out.println(result);
        //26124678--26124678
        //26124679--26124679
    }
}
