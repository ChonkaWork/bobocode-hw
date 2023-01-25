package org.example.testTemporary;

import org.example.annotation.Bean;

@Bean
public class OtherClass {
    public OtherClass() {
    }

    public String sendMessage() {
        return "This is a message";
    }
}
