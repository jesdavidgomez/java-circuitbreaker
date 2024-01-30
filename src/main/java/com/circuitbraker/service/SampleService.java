package com.circuitbraker.service;

import com.circuitbraker.mock.ApiMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SampleService {
    private final ApiMock mock;
    private final CircuitBreaker circuitBreaker;

    public SampleService(ApiMock mock, CircuitBreaker circuitBreaker) {
        this.mock = mock;
        this.circuitBreaker = circuitBreaker;
    }

    public int runApiPostCall() {
        this.circuitBreaker.getEventPublisher().onSuccess(e -> {
            System.out.println(e.getEventType());
        }).onError(e -> {
            System.out.println(e.getEventType());
            System.out.println(e.getThrowable().getMessage());
        }).onCallNotPermitted(e -> {
            System.out.println(e.getEventType());
            System.out.println("Is open");
        }).onStateTransition((e) -> {
            System.out.println(e.getEventType());
            System.out.println(e.getStateTransition());
        });

        return Decorators
                .ofSupplier(() -> this.mock.mockPost(4)) // pass a Supplier
                .withCircuitBreaker(this.circuitBreaker)  // set CB instance to use
                .withFallback(Arrays.asList(RuntimeException.class), this::fallBackRunApiPostCall) // fallback for exceptions
                .get(); // wait for response and get result or fallback

    }

    public int fallBackRunApiPostCall(Throwable err) {
        return 1000;
    }
}
