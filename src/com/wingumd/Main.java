package com.wingumd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {
        NorMuchFuture f = new NorMuchFuture();
        try {
            f.go("no barf");
            f.go("barf");
            f.goExceptionally("no barf");
            f.goExceptionally("barf");
        } catch (Exception ex) {
            System.out.println("screwed up");
        }
    }
}

class NorMuchFuture {

    void go(final String v) throws InterruptedException {

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                    try {
                        if (v.equals("barf")) {
                            throw new Exception("Barfing as `barf` is the parameter. ");
                        }
                        return "Did not barf: Given: " + v;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );

        String res = "never done";
        try {
            res = cf
                    .thenApply(v2 -> {
                        System.out.print(v2);
                        return v2;
                    })
                    .get();
        } catch (ExecutionException ex) {
            System.out.printf("CF execution exception %s", ex.toString());
        }

        if (cf.isCompletedExceptionally()) {
            System.out.print("\nIt was CompletedExceptionally");
        }

        System.out.printf("\nThe Final result is: %s", res);
    }

    void goExceptionally(final String v) throws InterruptedException {

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                    try {
                        if (v.equals("barf")) {
                            throw new Exception("Barfing exceptionally as `barf` is the parameter. ");
                        }
                        return "Did not barf exceptionally: Given: " + v;
                    } catch (Exception ex) {
                        // If you re-throw the exception as RunTimException, you can throw an exception in
                        // a lambda
                        throw new RuntimeException(ex);
                    }
                }
        );

        String res = "never done";
        try {
            res = cf
                    .thenApply(v2 -> {
                        System.out.print(v2);
                        return v2;
                    })
                    .exceptionally(ex -> {
                        System.out.print("Exceptionally " + ex.toString());
                        return "Exceptionally handled.";
                    })
                    .get();
        } catch (ExecutionException ex) {
            System.out.printf("CF execution exception %s", ex.toString());
        }

        if (cf.isCompletedExceptionally()) {
            System.out.print("\nIt was CompletedExceptionally");
        }

        System.out.printf("\nThe Final result is: %s", res);
    }

}
