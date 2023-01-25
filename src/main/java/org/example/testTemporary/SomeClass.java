package org.example.testTemporary;


import lombok.extern.slf4j.Slf4j;
import org.example.annotation.Autowired;
import org.example.annotation.Bean;

@Bean("TestClass")
@Slf4j
public class SomeClass {

    @Autowired
    private OtherClass otherClass;

    public SomeClass() {
    }

    public void printMessage() {
        String message = otherClass.sendMessage();
        log.info(message);
    }
}
