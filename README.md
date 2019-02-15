# java11-concurrency-thread-uncaught-exception

_Reference_: [advanced-exception-handling](https://medium.com/@yosimizrachi/advanced-exception-handling-thread-uncaughtexceptionhandler-c72e013da092)

# preface
## Thread.UncaughtExceptionHandler
* interface for handlers invoked when a `Thread` abruptly 
terminates due to an uncaught exception.
* `@FunctionalInterface` with method
    ```
    void uncaughtException(Thread t, Throwable e);
    ```

## thread group
* is represented by `ThreadGroup` class
* `thread.getThreadGroup()`
* a thread is always a member of a thread group
* by default, the thread group is inherited from creator thread
* the JVM creates a thread group called `main` and a thread in this group called `main`, which is
  responsible for running the `main()` at startup
* thread groups are arranged in a tree-like structure
* thread group main does not have a parent
* thread group is `Thread.UncaughtExceptionHandler`

## unhandled exceptions
When thread terminates due to an uncaught exception:
1. if `thread.getUncaughtExceptionHandler()` is not null,
its method `uncaughtException(Thread t, Throwable e)` is called
1. otherwise, thread group method `uncaughtException(Thread t, Throwable e)`
is called - if **uncaughtException** is not `@Override`, `uncaughtException`
is called recursively in the consecutive parents (note that `main` parent is `null`)
1. otherwise, if parent is null and `Thread.getDefaultUncaughtExceptionHandler()` is not null,
its method `uncaughtException(Thread t, Throwable e)` is called

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