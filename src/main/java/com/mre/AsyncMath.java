package com.mre;

import reactor.core.publisher.Mono;

public interface AsyncMath {

    static Mono<Double> add(Mono<Double> x, Mono<Double> y) {
        return x.flatMap(u -> y.map(v -> u + v));
    }

    static Mono<Double> sub(Mono<Double> x, Mono<Double> y) {
        return x.flatMap(u -> y.map(v -> u - v));
    }

    static Mono<Double> mul(Mono<Double> x, Mono<Double> y) {
        return x.flatMap(u -> y.map(v -> u * v));
    }

    static Mono<Double> div(Mono<Double> x, Mono<Double> y) {
        return x.flatMap(u -> y.map(v -> u / v));
    }

    static Mono<Double> sqrt(Mono<Double> x) {
        return x.map(Math::sqrt);
    }

    // g(x,y) = sqrt(x^2 + y^2)
    // Vorsicht: Bad (!!!) Code
    static Mono<Double> norm1(Mono<Double> x, Mono<Double> y) {
        Mono<Double> xx = mul(x,x);
        Mono<Double> yy = mul(y,y);
        Mono<Double> sum = add(xx, yy);
        Mono<Double> result = sqrt(sum);
        return result;
    }

    // g(x,y) = sqrt(x^2 + y^2)
    static Mono<Double> norm2(Mono<Double> x, Mono<Double> y) {
        return Mono
                .zip(x.map(v->v*v),y.map(v->v*v))
                .map(v->v.getT1() + v.getT2())
                .map(Math::sqrt);
    }
}
