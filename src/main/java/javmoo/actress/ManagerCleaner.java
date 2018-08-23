package javmoo.actress;

import java.util.HashMap;
import java.util.Set;

public class ManagerCleaner implements Runnable {

    private HashMap<Integer, Integer> actressesLeftLife;

    ManagerCleaner(HashMap<Integer, Integer> actressesLeftLife)
    {
        this.actressesLeftLife = actressesLeftLife;
    }

    @Override
    public void run() {
        Set<Integer> keys = this.actressesLeftLife.keySet();
        System.out.println("[ActressManager] Left " + keys.size() + " entities.");
        for(Integer actressId: keys) {
            int leftLife = this.actressesLeftLife.get(actressId);
            leftLife--;
            if (leftLife < 0) {
                Manager.remove(actressId);
            } else {
                this.actressesLeftLife.put(actressId, leftLife);
            }
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
