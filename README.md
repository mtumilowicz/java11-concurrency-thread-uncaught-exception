[![Build Status](https://travis-ci.com/mtumilowicz/java11-concurrency-thread-uncaught-exception.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-concurrency-thread-uncaught-exception)

# java11-concurrency-thread-uncaught-exception

_Reference_: [advanced-exception-handling](https://medium.com/@yosimizrachi/advanced-exception-handling-thread-uncaughtexceptionhandler-c72e013da092)  
_Reference_: https://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601

# preface
## Thread.UncaughtExceptionHandler
* interface for handlers invoked when a `Thread` abruptly 
terminates due to an uncaught exception.
```
@FunctionalInterface
public interface UncaughtExceptionHandler {
    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    void uncaughtException(Thread t, Throwable e);
}
```
* per-thread basis
    * `thread.setUncaughtExceptionHandler(...)`
    * `thread.getUncaughtExceptionHandler()`
* default
    * `Thread.setDefaultUncaughtExceptionHandler(...)`
    * `Thread.getDefaultUncaughtExceptionHandler()`
* from `Thread` class
    ```
    // null unless explicitly set
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
    
    // null unless explicitly set
    private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    ```

## thread group
* is represented by `ThreadGroup` class
* `thread.getThreadGroup()`
* a thread is always a member of a thread group
* by default, the thread group is inherited from creator thread
* the JVM creates a thread group called `main` and a thread in this group called `main`, which is
  responsible for running the `main()` at startup
* thread groups are arranged in a tree-like structure
* thread group `main` does not have a parent
* `ThreadGroup implements Thread.UncaughtExceptionHandler`

## unhandled exceptions
When thread terminates due to an uncaught exception:
1. (**first per-thread basis**): if `thread.getUncaughtExceptionHandler()` is not null,
its method `uncaughtException(Thread t, Throwable e)` is called
1. (**second per-`ThreadGroup` basis**): otherwise, thread group method `uncaughtException(Thread t, Throwable e)`
is called - if `uncaughtException` method is not `@Override`, `uncaughtException`
is called recursively in the consecutive parents (note that `main` parent is `null`)
1. (**bubbles up to the top-level**): top-level thread group handler delegates to the default system handler (if one
   exists; the default is none) and otherwise prints the stack trace to the console

# project description
We will provide simple examples of how to handle uncaught exceptions
thrown in threads.

1. handler for a specific thread
    ```
    final class SpecificUncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Caught specific: " + e
                    + " in: " + t.getName());
        }
    }
    ```
    and test:
    ```
    var thread = new Thread(endsExceptionally);
    thread.setUncaughtExceptionHandler(new SpecificUncaughtExceptionsHandler());
    
    thread.start();
    thread.join();
    ```
    will print:
    ```
    Caught specific: java.lang.RuntimeException: exception! in: Thread-0
    ```
1. global handler
    ```
    final class GlobalUncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Caught global: " + e
                    + " in: " + t.getName());
        }
    }
    ```
    and test:
    ```
    Thread.setDefaultUncaughtExceptionHandler(new GlobalUncaughtExceptionsHandler());
    
    var thread = new Thread(endsExceptionally);
    
    thread.start();
    thread.join();
    ```
    will print:
    ```
    Caught global: java.lang.RuntimeException: exception! in: Thread-0
    ```
1. group handler
    ```
    final class MyThreadGroup extends ThreadGroup {
    
        MyThreadGroup() {
            super("My Thread Group");
        }
    
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Caught in My Thread Group: " + e
                    + " in: " + t.getName());
        }
    }
    ```
    and tests:
    ```
    var t = new Thread(new MyThreadGroup(), endsExceptionally);
    
    t.start();
    t.join();
    ```
    will print:
    ```
    Caught in My Thread Group: java.lang.RuntimeException: exception! in: Thread-0
    ```
1. unhandled at all
    ```
    var thread = new Thread(endsExceptionally);
    
    thread.start();
    thread.join();
    ```
    will print:
    ```
    Exception in thread "Thread-0" java.lang.RuntimeException: exception!
    	at ThreadExceptionTest.lambda$new$0(ThreadExceptionTest.java:8)
    	at java.base/java.lang.Thread.run(Thread.java:834)
    ```