package javmoo.actress;

import javmoo.Actress;

import java.util.HashMap;

public class Manager {

    private static HashMap<String, Actress> actressesByName;
    private static HashMap<Integer, Actress> actressesById;
    private static HashMap<Integer, Integer> actressesLeftLife;

    private static final int LIFE = 600; // 秒

    static {
        actressesByName = new HashMap<>();
        actressesById = new HashMap<>();
        actressesLeftLife = new HashMap<>();

        ManagerCleaner cleaner = new ManagerCleaner(Manager.actressesLeftLife);
        Thread t = new Thread(cleaner);
        t.start();
    }

    public static Actress getActress(int id)
    {
        Actress actress = actressesById.get(id);
        if (actress == null) {
            actress = new Actress();
            actress.load(id);
            if (actress.getId() > 0) {
                add(actress);
            }
        } else {
            refreshLeftLife(actress.getId());
        }
        return actress;
    }

    public static Actress getActress(String name)
    {
        Actress actress = actressesByName.get(name);
        if (actress == null) {
            Actress _actress = new Actress();
            _actress.load(name, "name");
            if (_actress.getId() > 0) {
                add(_actress);
                actress = _actress;
            }
        } else {
            refreshLeftLife(actress.getId());
        }
        return actress;
    }

    private static void add(Actress actress)
    {
        actressesById.put(actress.getId(), actress);
        actressesByName.put(actress.getName(), actress);
        actressesLeftLife.put(actress.getId(), LIFE);
    }

    static void remove(int actressId)
    {
        Actress actress = actressesById.remove(actressId);
        if (actress != null) {
            actressesByName.remove(actress.getName());
            actressesLeftLife.remove(actressId);
        }
    }

    private static void refreshLeftLife(int actressId)
    {
        actressesLeftLife.put(actressId, LIFE);
    }
}
