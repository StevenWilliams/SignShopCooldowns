package net.onepeace.signshops.tasks;

import net.onepeace.signshops.listeners.ShopUse;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class Save extends BukkitRunnable {
    @Override
    public void run() {
        try {
            FileOutputStream fos = new FileOutputStream("cooldowns.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ShopUse.cooldown);
            oos.close();
            fos.close();

            fos = new FileOutputStream("itemLimits.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ShopUse.itemLimit);
            oos.close();
            fos.close();

            fos = new FileOutputStream("moneyLimit.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ShopUse.moneyLimit);
            oos.close();
            fos.close();

        } catch(IOException ex) {
            ex.printStackTrace();
        }


    }
}
