import org.junit.Assert;
import org.junit.Test;

import java.lang.ref.WeakReference;

/**
 * @author Nikita Gorbachevski
 */
public class VisibilityTest {

  private static class Holder {
    private boolean b = true;
  }

  private interface Testable {
    void test();

    Thread getThread();
  }

  private static class SharedVariableWithoutVolatile implements Testable {
    private Thread thread;
    private Holder holder = new Holder();

    @Override
    public void test() {
      thread = new Thread(new Runnable() {
        public void run() {
          int counter = 0;
          while (holder.b) {
            counter++;
          }
          System.out.println("Thread 1 finished. Counted up to " + counter);
        }
      });
      thread.start();

      new Thread(new Runnable() {
        public void run() {
          // Sleep for a bit so that thread 1 has a chance to start
          try {
            Thread.sleep(1000);
          } catch (InterruptedException ignored) {
          }
          System.out.println("Thread 2 finishing");
          holder.b = false;
        }
      }).start();
    }

    @Override
    public Thread getThread() {
      return thread;
    }
  }

  private static class SharedVariableWithVolatile implements Testable {
    private Thread thread;
    private volatile Holder holder = new Holder();

    @Override
    public void test() {
      thread = new Thread(new Runnable() {
        public void run() {
          int counter = 0;
          while (holder.b) {
            counter++;
          }
          System.out.println("Thread 1 finished. Counted up to " + counter);
        }
      });
      thread.start();

      new Thread(new Runnable() {
        public void run() {
          // Sleep for a bit so that thread 1 has a chance to start
          try {
            Thread.sleep(1000);
          } catch (InterruptedException ignored) {
          }
          System.out.println("Thread 2 finishing");
          holder.b = false;
        }
      }).start();
    }

    @Override
    public Thread getThread() {
      return thread;
    }
  }

  private static class SharedVariableThreadLocal implements Testable {
    private Thread thread;
    private Holder holder = new Holder();

    @Override
    public void test() {
      thread = new Thread(new Runnable() {
        public void run() {
          ThreadLocal<Holder> threadLocal = new ThreadLocal<>();
          threadLocal.set(holder);
          int counter = 0;
          while (threadLocal.get().b) {
            counter++;
          }
          System.out.println("Thread 1 finished. Counted up to " + counter);
        }
      });
      thread.start();

      new Thread(new Runnable() {
        public void run() {
          ThreadLocal<Holder> threadLocal = new ThreadLocal<>();
          threadLocal.set(holder);
          // Sleep for a bit so that thread 1 has a chance to start
          try {
            Thread.sleep(1000);
          } catch (InterruptedException ignored) {
          }
          System.out.println("Thread 2 finishing");
          threadLocal.get().b = false;
        }
      }).start();
    }

    @Override
    public Thread getThread() {
      return thread;
    }
  }

  private static class SharedVariableWeakReference implements Testable {
    private Thread thread;
    private Holder holder = new Holder();

    @Override
    public void test() {
      thread = new Thread(new Runnable() {
        public void run() {
          WeakReference<Holder> weakReference = new WeakReference<>(holder);
          int counter = 0;
          while (weakReference.get().b) {
            counter++;
          }
          System.out.println("Thread 1 finished. Counted up to " + counter);
        }
      });
      thread.start();

      new Thread(new Runnable() {
        public void run() {
          WeakReference<Holder> weakReference = new WeakReference<>(holder);
          // Sleep for a bit so that thread 1 has a chance to start
          try {
            Thread.sleep(1000);
          } catch (InterruptedException ignored) {
          }
          System.out.println("Thread 2 finishing");
          weakReference.get().b = false;
        }
      }).start();
    }

    @Override
    public Thread getThread() {
      return thread;
    }
  }

  private void testVisibility(Testable testable) throws Exception {
    testable.test();
    long start = System.currentTimeMillis();
    testable.getThread().join(2000);
    System.out.println("Waiting time " + (System.currentTimeMillis() - start));
    Assert.assertFalse(testable.getThread().isAlive());
  }

  @Test
  public void testSharedVariableWithoutVolatile() throws Exception {
    testVisibility(new SharedVariableWithoutVolatile());
  }

  @Test
  public void testSharedVariableWithVolatile() throws Exception {
    testVisibility(new SharedVariableWithVolatile());
  }

  @Test
  public void testSharedVariableThreadLocal() throws Exception {
    testVisibility(new SharedVariableThreadLocal());
  }

  @Test
  public void testSharedVariableWeakReference() throws Exception {
    testVisibility(new SharedVariableWeakReference());
  }
}
