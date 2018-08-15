package javmoo;

import java.util.ArrayList;
import java.util.HashMap;

public class ActressManager {

    private static HashMap<String, Actress> actressesByName;
    private static HashMap<Integer, Actress> actressesById;

    static {
        actressesByName = new HashMap<String, Actress>();
        actressesById = new HashMap<Integer, Actress>();
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
        }
        return actress;
    }

    private static void add(Actress actress)
    {
        actressesById.put(actress.getId(), actress);
        actressesByName.put(actress.getName(), actress);
    }
}
