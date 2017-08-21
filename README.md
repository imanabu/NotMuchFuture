# NotMuchFuture

Very Simple CompletableFuture Example and Descriptions

# Why This Example

There are a lot of great examples out there when it comes to Java8 CompletableFuture
but they all seem to miss some basic stuff for those moving from other ways they have
done it. For me it was moving F.Promise to CompletableFuture in the Play Framework.

This example pretty much answers a lot of basic exception handling questions like;

* Why I cannot throw an exception within lambda?
* How can I really interrupt the chain of future operations if I cannot throw an
  exception?
* What do Exceptionally do, really?

I am sure there will be fancier and more correct way of doing things, but after reading this
document and playing with the provided sample code, you should be able to get-by
in moving from your current future implementation to the new CompletableFuture framework.

The supplied main.java contains the _playground_ code you can tweak to learn.

## Trick 1: Declare InterruptedException

The CompletableFuture containing function should have **throws InterruptedException** in function declaration.
Like;

    void go(final String v) throws InterruptedException {...}

## Trick 2: Fix The Unhandled Exception in a future lambda

When we are just cutting and pasting the code to move F.Promise to CompatibleFuture
we would run into situations where the compiler warns of **Unhandled Exception.**

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                    try {
                        if (v == "barf") {
                            throw new Exception("Barfing as `barf` is the parameter. ");
                        }
                        return "Did not barf : Given: " + v;
                    } catch (Exception ex) {
                        // If you re-throw the exception as RunTimException, you can throw an exception in 
                        // a lambda
                        throw new RuntimeException(ex);
                    }
                }
        );

This can be fixed by **re-throwing a caught exception with RunTimeException()**. This is the key 
number 1.

## Trick 3: Either Catch the ExecutionException or Use .exceptionally

If the function "barfs" with an exception, and if .exceptionally is not supplied,
it will trigger the ExecutionException and the completed future never returns a thing.

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
        
Here is an example of .exceptionally. Also note that cf.isCompletedExceptionally() 
can tell if it has encountered an issue in the chain. Note that in .exceptionally
part I _recover_ from the issue by returning a string. 

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
