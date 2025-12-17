package network.twink.antiantiafk;

import org.bukkit.Location;

public class BlockPos {

    private int x;
    private int y;
    private int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(Location location) {
        this.x = location.getBlockX();
        this.x = location.getBlockY();
        this.x = location.getBlockZ();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location loc) {
            return loc.getBlockX() == getX() &&
                    loc.getBlockY() == getY() &&
                    loc.getBlockZ() == getZ();
        } else if (obj instanceof BlockPos pos) {
            return pos.getX() == this.getX() &&
                    pos.getY() == this.getY() &&
                    pos.getZ() == this.getZ();
        }
        return super.equals(obj);
    }
}
