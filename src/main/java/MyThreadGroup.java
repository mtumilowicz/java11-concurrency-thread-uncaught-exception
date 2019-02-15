/**
 * Created by mtumilowicz on 2019-02-15.
 */
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
