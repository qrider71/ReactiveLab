package com.mre;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.mre.AsyncMath.*;

public class App {

    public static Mono<Double> f1() {
        return Mono.fromSupplier(() -> {
            System.out.println("calling f1");
            return 2.0d;
        }).delayElement(Duration.ofSeconds(5));
    }

    public static Mono<Double> f2() {
        return Mono.fromSupplier(() -> {
            System.out.println("calling f2");
            throw new RuntimeException("f2 failed");
        });
    }

    public static void testAsync() throws Exception {

        System.out.println("Async Test");

        // Die beiden Aufrufe von f1 passieren parallel wie bei der Laufzeit zu sehen ist
        norm2(f1(), f1())
                .elapsed()
                .subscribe(t -> System.out.printf("Duration %d : value %f%n", t.getT1(),t.getT2()));

        norm2(f1(), f2())
                .retryWhen(
                        Retry
                                .fixedDelay(3, Duration.ofSeconds(3))
                                .doBeforeRetry(x -> System.out.println("Retrying ..."))
                )
                .doOnError(Exception.class, e -> System.out.println("Received error"))
                .onErrorReturn(0.0)
                .subscribe(v-> System.out.printf("Received value %f%n", v));

        Thread.sleep(20000);
    }

    public static void testSync() throws Exception {

        System.out.println("Sync Test");

        // Die beiden Aufrufe von f1 passieren parallel wie bei der Laufzeit zu sehen ist
        var res = norm2(f1(), f1()).elapsed().block();
        System.out.printf("Duration %d : value %f%n", res.getT1(),res.getT2());
    }

    public static void main(String[] args) throws Exception {

        testSync();

        System.out.println();

        testAsync();


    }
}
