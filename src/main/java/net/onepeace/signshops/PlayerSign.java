package net.onepeace.signshops;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class PlayerSign {
    private final Player player;
    private final Block block;
    public PlayerSign(Player player, Block block) {
        this.player = player;
        this.block = block;
    }
    public Player getPlayer(){
        return player;
    }
    public Block getBlock() {
        return block;
    }
    @Override
    public boolean equals(Object object) {
        if(object == null) return false;
        if(getClass() != object.getClass()) return false;
        final PlayerSign other = (PlayerSign) object;

        if ((this.player.getUniqueId() == null) ? (other.getPlayer().getUniqueId() != null) : !this.player.getUniqueId().equals(other.getPlayer().getUniqueId())) {
            return false;
        }

        if (this.block.getLocation() != other.getBlock().getLocation()) {
            return false;
        }

        return true;
    }
    @Override
    public int hashCode() {
        return Objects.hash(player.getUniqueId(), block.getLocation());
    }


}
