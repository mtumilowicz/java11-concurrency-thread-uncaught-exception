import org.junit.Test;

/**
 * Created by mtumilowicz on 2019-02-15.
 */
public class ThreadExceptionTest {
    
    private Runnable endsExceptionally = () -> {throw new RuntimeException("exception!");};
    
    @Test
    public void handled_specific() throws InterruptedException {
        var thread = new Thread(endsExceptionally);
        thread.setUncaughtExceptionHandler(SpecificUncaughtExceptionsHandler.INSTANCE);
        
        thread.start();
        thread.join();
    }
    
    @Test
    public void handled_global() throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(GlobalUncaughtExceptionsHandler.INSTANCE);
        
        var thread = new Thread(endsExceptionally);

        thread.start();
        thread.join();
    }
    
    @Test
    public void handled_group() throws InterruptedException {
        var t = new Thread(MyThreadGroup.INSTANCE, endsExceptionally);
        
        t.start();
        t.join();
    }

    @Test
    public void unhandled() throws InterruptedException {
        var thread = new Thread(endsExceptionally);

        thread.start();
        thread.join();
    }
}
