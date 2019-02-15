/**
 * Created by mtumilowicz on 2019-02-15.
 */
final class GlobalUncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {

    static final GlobalUncaughtExceptionsHandler INSTANCE = new GlobalUncaughtExceptionsHandler();

    private GlobalUncaughtExceptionsHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Caught global: " + e
                + " in: " + t.getName());
    }
}

