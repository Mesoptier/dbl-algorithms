import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// From StackOverflow:
public abstract class ObjectPool<T> {

  private final Queue<T> objects;

  public ObjectPool() {
    this.objects = new ConcurrentLinkedQueue<T>();
  }

  public ObjectPool(Collection<? extends T> objects) {
    this.objects = new ConcurrentLinkedQueue<T>(objects);
  }

  public abstract T createExpensiveObject();

  public T borrow() {
    T t;
    if ((t = objects.poll()) == null) {
      t = createExpensiveObject();
    }
    return t;
  }

  public void giveBack(T object) {
    this.objects.offer(object); // no point to wait for free space, just return
  }

}
