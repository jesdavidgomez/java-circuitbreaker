package com.circuitbraker.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiMock {
    public int mockPost(int num) {
        if(num%2 == 0) {
            throw new RuntimeException("Error calling post API");
        }
        return num;
    }

    public int mockPostSlow(int num) {
        try {
            Thread.sleep(num*1000);
            return num;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
