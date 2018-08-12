package javmoo;

public class Task {

    private int count;
    private int current;

    Task(int count)
    {
        this.count = count;
        this.current = 0;
    }

    public int getNext() {
        if (current < count) {
            return ++current;
        }
        return -1;
    }
}
