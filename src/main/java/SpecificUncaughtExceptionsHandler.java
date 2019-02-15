/**
 * Created by mtumilowicz on 2019-02-15.
 */
final class SpecificUncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Caught specific: " + e
                + " in: " + t.getName());
    }
}
