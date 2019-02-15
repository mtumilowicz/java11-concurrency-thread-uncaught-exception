/**
 * Created by mtumilowicz on 2019-02-15.
 */
final class SpecificUncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {

    static final SpecificUncaughtExceptionsHandler INSTANCE = new SpecificUncaughtExceptionsHandler();

    private SpecificUncaughtExceptionsHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Caught specific: " + e
                + " in: " + t.getName());
    }
}
