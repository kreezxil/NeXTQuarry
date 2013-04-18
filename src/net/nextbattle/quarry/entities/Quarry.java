package net.nextbattle.quarry.entities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.nextbattle.quarry.functions.StringFunctions;
import net.nextbattle.quarry.main.MainClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Quarry {

    public static ArrayList<Quarry> quarrylist = new ArrayList<>();
    private BlockFace dir;
    private int tier;
    private Block block;
    private String playername;
    private ArrayList<Block> QuarryBlocks;
    private ArrayList<Block> ArmBlocks;
    private int xwork = 0;
    private int ywork = 0;
    private int zwork = 0;
    private int xrealwork = -1;
    private int yrealwork = -1;
    private int zrealwork = -1;
    private boolean active = false;
    public Inventory fuel_inv;
    public Inventory upgr_inv;
    private int fuelcounter;
    private int nextTick = 0;
    private File file;
    private FileConfiguration fc;
    private String random_id;
    private boolean cantick = true;

    public static void LoadQuarry(File loadfile) {
        FileConfiguration fc_temp = YamlConfiguration.loadConfiguration(loadfile);
        List<?> list = fc_temp.getList("fuel_inv");
        Inventory fuel_inv_temp = Bukkit.createInventory(null, 27, "Quarry: Fuel Bay");
        if (list != null) {
            for (int i = 0; i < Math.min(list.size(), fuel_inv_temp.getSize()); i++) {
                fuel_inv_temp.setItem(i, (ItemStack) list.get(i));
            }
        }
        list = fc_temp.getList("upgr_inv");
        Inventory upgr_inv_temp = Bukkit.createInventory(null, 27, "Quarry: Upgrade Slots");
        if (list != null) {
            for (int i = 0; i < Math.min(list.size(), upgr_inv_temp.getSize()); i++) {
                upgr_inv_temp.setItem(i, (ItemStack) list.get(i));
            }
        }
        BlockFace dir_temp = BlockFace.NORTH;
        int dir_serialized = fc_temp.getInt("dir");
        if (dir_serialized == 0) {
            dir_temp = BlockFace.NORTH;
        }
        if (dir_serialized == 1) {
            dir_temp = BlockFace.EAST;
        }
        if (dir_serialized == 2) {
            dir_temp = BlockFace.SOUTH;
        }
        if (dir_serialized == 3) {
            dir_temp = BlockFace.WEST;
        }
        int tier_temp = fc_temp.getInt("tier");
        String player_temp = fc_temp.getString("playername");
        int xwork_temp = fc_temp.getInt("xwork");
        int ywork_temp = fc_temp.getInt("ywork");
        int zwork_temp = fc_temp.getInt("zwork");
        int xrealwork_temp = fc_temp.getInt("xrealwork");
        int yrealwork_temp = fc_temp.getInt("yrealwork");
        int zrealwork_temp = fc_temp.getInt("zrealwork");
        boolean active_temp = fc_temp.getBoolean("active");
        int nextTick_temp = fc_temp.getInt("nextTick");
        int fuelcounter_temp = fc_temp.getInt("fuelcounter");

        ArrayList<Block> ArmBlocks_temp = new ArrayList<>();
        list = fc_temp.getList("armblocks");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]) == null) {
                    loadfile.delete();
                    return;
                }
                ArmBlocks_temp.add(Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]).getBlockAt(new Location(Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]), Integer.parseInt(list.get(i).toString().split("\\$")[1]), Integer.parseInt(list.get(i).toString().split("\\$")[2]), Integer.parseInt(list.get(i).toString().split("\\$")[3]))));
            }
        }
        ArrayList<Block> QuarryBlocks_temp = new ArrayList<>();
        list = fc_temp.getList("armblocks");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]) == null) {
                    loadfile.delete();
                    return;
                }
                QuarryBlocks_temp.add(Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]).getBlockAt(new Location(Bukkit.getServer().getWorld(list.get(i).toString().split("\\$")[0]), Integer.parseInt(list.get(i).toString().split("\\$")[1]), Integer.parseInt(list.get(i).toString().split("\\$")[2]), Integer.parseInt(list.get(i).toString().split("\\$")[3]))));
            }
        }
        Block block_temp = null;
        String block_serialized = fc_temp.getString("block");
        block_temp = Bukkit.getServer().getWorld(block_serialized.split("\\$")[0]).getBlockAt(new Location(Bukkit.getServer().getWorld(block_serialized.split("\\$")[0]), Integer.parseInt(block_serialized.split("\\$")[1]), Integer.parseInt(block_serialized.split("\\$")[2]), Integer.parseInt(block_serialized.split("\\$")[3])));

        Quarry quarry = new Quarry(fuel_inv_temp, upgr_inv_temp, dir_temp, tier_temp, block_temp, player_temp, ArmBlocks_temp, QuarryBlocks_temp, xwork_temp, ywork_temp, zwork_temp, xrealwork_temp, yrealwork_temp, zrealwork_temp, active_temp, fuelcounter_temp, nextTick_temp, loadfile.getName().replace(".nxtb", ""));
    }

    public void save() throws IOException {
        //Inventories
        fc.set("fuel_inv", fuel_inv.getContents());
        fc.set("upgr_inv", upgr_inv.getContents());

        //Block Registers
        List<String> quarry_blocks_serialized = new ArrayList<>();
        for (Block b : QuarryBlocks) {
            quarry_blocks_serialized.add(b.getLocation().getWorld().getName() + "$" + b.getLocation().getBlockX() + "$" + b.getLocation().getBlockY() + "$" + b.getLocation().getBlockZ());
        }
        fc.set("quarryblocks", quarry_blocks_serialized);
        List<String> arm_blocks_serialized = new ArrayList<>();
        for (Block b : ArmBlocks) {
            arm_blocks_serialized.add(b.getLocation().getWorld().getName() + "$" + b.getLocation().getBlockX() + "$" + b.getLocation().getBlockY() + "$" + b.getLocation().getBlockZ());
        }
        fc.set("armblocks", arm_blocks_serialized);
        Block b = block;
        fc.set("block", b.getLocation().getWorld().getName() + "$" + b.getLocation().getBlockX() + "$" + b.getLocation().getBlockY() + "$" + b.getLocation().getBlockZ());

        //Position Work Variables
        fc.set("xwork", xwork);
        fc.set("ywork", ywork);
        fc.set("zwork", zwork);
        fc.set("xrealwork", xrealwork);
        fc.set("yrealwork", yrealwork);
        fc.set("zrealwork", zrealwork);

        //Miscellaneous Variables
        fc.set("tier", tier);
        fc.set("active", active);
        fc.set("fuelcounter", fuelcounter);
        fc.set("nextTick", nextTick);
        fc.set("random_id", random_id);
        fc.set("playername", playername);

        //BlockFace
        int dir_serialized = 0;
        if (dir.equals(BlockFace.NORTH) || dir.equals(BlockFace.NORTH_EAST)) {
            dir_serialized = 0;
        }
        if (dir.equals(BlockFace.EAST) || dir.equals(BlockFace.SOUTH_EAST)) {
            dir_serialized = 1;
        }
        if (dir.equals(BlockFace.SOUTH) || dir.equals(BlockFace.SOUTH_WEST)) {
            dir_serialized = 2;
        }
        if (dir.equals(BlockFace.WEST) || dir.equals(BlockFace.NORTH_WEST)) {
            dir_serialized = 3;
        }
        fc.set("dir", dir_serialized);

        //Save Config
        fc.save(file);
    }

    public Quarry(BlockFace dir, int tier, Block b, Player p) {
        this.dir = dir;
        this.tier = tier;
        this.block = b;
        this.playername = p.getName();
        QuarryBlocks = new ArrayList<>();
        ArmBlocks = new ArrayList<>();
        fuel_inv = Bukkit.createInventory(null, 27, "Quarry: Fuel Bay");
        upgr_inv = Bukkit.createInventory(null, 27, "Quarry: Upgrade Slots");
        fuelcounter = 0;
        quarrylist.add(this);
        newFile();
    }

    public Quarry(Inventory fuel_inv, Inventory upgr_inv, BlockFace dir, int tier, Block block, String playername, ArrayList<Block> ArmBlocks, ArrayList<Block> QuarryBlocks, int xwork, int ywork, int zwork, int xrealwork, int yrealwork, int zrealwork, boolean active, int fuelcounter, int nextTick, String random_id) {
        this.fuel_inv = fuel_inv;
        this.upgr_inv = upgr_inv;
        this.dir = dir;
        this.tier = tier;
        this.block = block;
        this.playername = playername;
        this.QuarryBlocks = QuarryBlocks;
        this.ArmBlocks = ArmBlocks;
        this.xwork = xwork;
        this.ywork = ywork;
        this.zwork = zwork;
        this.xrealwork = xrealwork;
        this.yrealwork = yrealwork;
        this.zrealwork = zrealwork;
        this.active = active;
        this.fuelcounter = fuelcounter;
        this.nextTick = nextTick;
        this.random_id = random_id;
        this.file = new File(MainClass.plugin.getDataFolder(), "/quarries/" + random_id + ".nxtb");
        this.fc = YamlConfiguration.loadConfiguration(file);
        quarrylist.add(this);
    }

    public String getRandomID() {
        return random_id;
    }

    public static boolean idExists(String id) {
        for (Quarry q : quarrylist) {
            if (id.equals(q.getRandomID())) {
                return true;
            }
        }
        return false;
    }

    public static void saveAll() {
        for (Quarry q : quarrylist) {
            try {
                q.save();
            } catch (IOException ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, null, ex);
            }
        }
    }

    public void newFile() {
        while (true) {
            String random_id_t = StringFunctions.generateRandomID();
            if (!idExists(random_id_t)) {
                random_id = random_id_t;
                break;
            }
        }

        file = new File(MainClass.plugin.getDataFolder(), "/quarries/" + random_id + ".nxtb");

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException ex) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, null, ex);
        }

        fc = YamlConfiguration.loadConfiguration(file);
        try {
            save();
        } catch (IOException ex) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public int getInterval() {
        int upgrades = 0;
        for (ItemStack is : upgr_inv.getContents()) {
            try {
                if (is != null) {
                    try {
                        if (is.getItemMeta().getDisplayName().equals(MainClass.citems.speed_upgrade.getItemMeta().getDisplayName())) {
                            upgrades += is.getAmount();
                        }
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (upgrades >= 3) {
            return 1;
        } else if (upgrades == 2) {
            return 4;
        } else if (upgrades == 1) {
            return 8;
        } else if (upgrades <= 0) {
            return 12;
        }
        return 12;
    }

    public static Quarry isActualQuarry(Block b) {
        try {
            for (Quarry q : quarrylist) {
                if (q.block.getLocation().equals(b.getLocation())) {
                    return q;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public int getFuelCounter() {
        return fuelcounter;
    }

    public void setFuelCounter(int value) {
        this.fuelcounter = value;
    }

    public void addToFuelCounter(int value) {
        this.fuelcounter += value;
    }

    public void removeFromFuelCounter(int value) {
        this.fuelcounter -= value;
    }

    public int getTier() {
        return tier;
    }

    public void addQuarryBlock(Block b) {
        QuarryBlocks.add(b);
    }

    public void removeQuarryBlock(Block b) {
        QuarryBlocks.remove(b);
    }

    public boolean isQuarryBlock(Block b) {
        if (QuarryBlocks.contains(b)) {
            return true;
        }
        return false;
    }

    public void doTick() {
        if (!block.getChunk().isLoaded() && !MainClass.config.getContinue_when_unloaded()) {
            return;
        }
        if (Bukkit.getServer().getPlayer(playername) != null) {
            if (!Bukkit.getServer().getPlayer(playername).isOnline() && !MainClass.config.getContinue_when_offline()) {
                return;
            }
        } else if (!MainClass.config.getContinue_when_offline()) {
            return;
        }
        if (tier == 0) {
            block.setType(Material.IRON_BLOCK);
        }
        if (tier == 1) {
            block.setType(Material.GOLD_BLOCK);
        }
        if (tier == 2) {
            block.setType(Material.OBSIDIAN);
        }
        if (!cantick) {
            return;
        }

        if (nextTick > 0) {
            nextTick -= 1;
            return;
        }
        nextTick = getInterval();

        //Fuel Checks
        if (fuelcounter < 1) {
            if (fuel_inv.contains(Material.COAL)) {
                if (fuel_inv.getItem(fuel_inv.first(Material.COAL)).getAmount() == 1) {
                    fuel_inv.setItem(fuel_inv.first(Material.COAL), null);
                } else {
                    fuel_inv.getItem(fuel_inv.first(Material.COAL)).setAmount(fuel_inv.getItem(fuel_inv.first(Material.COAL)).getAmount() - 1);
                }
                fuelcounter += 32;
            } else {
                fuelcounter = 0;
                return;
            }
        }
        fuelcounter -= 1;

        //Actions
        if (!buildFrame(true)) {
            if (!mineStep()) {
                drawarm();
            }
        }
    }

    public Block getBlockAtSpot(int xw, int yw, int zw) {
        World world = block.getWorld();
        Location loc = null;
        if (dir == BlockFace.WEST || dir == BlockFace.NORTH_WEST) {
            loc = new Location(world, block.getX() - xw - 1, block.getY() + (-1 - yw), block.getZ() + zw + 2);
        }
        if (dir == BlockFace.NORTH || dir == BlockFace.NORTH_EAST) {
            loc = new Location(world, block.getX() - 2 - xw, block.getY() + (-1 - yw), block.getZ() - zw - 1);
        }
        if (dir == BlockFace.SOUTH || dir == BlockFace.SOUTH_WEST) {
            loc = new Location(world, xw + block.getX() + 2, block.getY() + (-1 - yw), block.getZ() + zw + 1);
        }
        if (dir == BlockFace.EAST || dir == BlockFace.SOUTH_EAST) {
            loc = new Location(world, xw + block.getX() + 1, block.getY() + (-1 - yw), block.getZ() - zw - 2);
        }
        return world.getBlockAt(loc);
    }

    public boolean mineStep() {
        if (!active) {
            return false;
        }
        int yfinal = block.getY();
        int holesize = 0;
        if (tier == 0) {
            holesize = 16;
        }
        if (tier == 1) {
            holesize = 32;
        }
        if (tier == 2) {
            holesize = 48;
        }
        Location loc2 = block.getLocation();
        loc2.add(0, 1, 0);
        BlockState blockState = block.getWorld().getBlockAt(loc2).getState();
        if (blockState instanceof Chest && !MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork, zwork).getType())) {
            Chest chest = (Chest) blockState;
            for (ItemStack is : getBlockAtSpot(xwork, ywork, zwork).getDrops()) {
                // - Planned for v1.2.0 release.
                /*if (is.getType().equals(Material.CHEST) && upgr_inv.contains(MainClass.citems.chest_miner)) {
                    BlockState minecheststate = getBlockAtSpot(xwork, ywork, zwork).getState();
                    if (minecheststate instanceof Chest && !MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork, zwork).getType())) {
                        Chest minechest = (Chest) minecheststate;
                        for (ItemStack isc : minechest.getBlockInventory().getContents()) {
                            if (chest.getInventory().firstEmpty() != -1) {
                                chest.getInventory().addItem(isc);
                            } else {
                                return true;
                            }
                        }
                    }
                }*/
                if (fuel_inv.firstEmpty() != -1 && is.getType().equals(Material.COAL) && upgr_inv.contains(MainClass.citems.fuel_upgrade)) {
                    fuel_inv.addItem(is);
                } else {
                    if (chest.getInventory().firstEmpty() != -1) {
                        chest.getInventory().addItem(is);
                    } else {
                        return true;
                    }
                }
            }
        }
        if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork, zwork).getType())) {
            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                getBlockAtSpot(xwork, ywork, zwork).setType(Material.AIR);
            } else {
                return true;
            }

        }
        if (zwork == (holesize - 1)) {
            if (xwork == (holesize - 1)) {
                if (ywork == yfinal) {
                    return true;
                } else {
                    if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork + 1, zwork).getType())) {
                        ywork++;
                    } else {
                        return true;
                    }
                }
                if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(0, ywork, zwork).getType())) {
                    xwork = 0;
                } else {
                    return true;
                }
            } else {
                if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork + 1, ywork, zwork).getType())) {
                    xwork++;
                } else {
                    return true;
                }
            }
            if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork, 0).getType())) {
                zwork = 0;
            } else {
                return true;
            }
        } else {
            if (!MainClass.config.getCantBreak().contains(getBlockAtSpot(xwork, ywork, zwork + 1).getType())) {
                zwork++;
            } else {
                return true;
            }
        }
        drawarm();
        return true;
    }

    public void drawarm() {
        int xvar = xwork;
        int yvar = ywork;
        int zvar = zwork;
        if (buildFrame(false)) {
            return;
        }
        active = true;
        int holesize = 0;
        if (tier == 0) {
            holesize = 16;
        }
        if (tier == 1) {
            holesize = 32;
        }
        if (tier == 2) {
            holesize = 48;
        }
        //No change? No redraw!
        if (xwork == xrealwork && ywork == yrealwork && zwork == zrealwork) {
            return;
        }

        xrealwork = xwork;
        yrealwork = ywork;
        zrealwork = zwork;
        //remove old arm.
        try {
            for (Block b : ArmBlocks) {
                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                    b.setType(Material.AIR);
                } else {
                    return;
                }

            }
            for (Block b : ArmBlocks) {
                ArmBlocks.remove(b);
            }
        } catch (Exception e) {
        }
        //draw actual arm
        World world = block.getWorld();
        if (dir == BlockFace.WEST || dir == BlockFace.NORTH_WEST) {
            for (int x = 0; x < holesize; x++) {
                for (int z = 0; z < holesize; z++) {
                    for (int y = 0; y <= (yvar + 5); y++) {
                        Location loc = new Location(world, block.getX() - x - 1, block.getY() + (5 - y), block.getZ() + z + 2);
                        if ((x == xvar || z == zvar) && y == 0 && !(x == xvar && z == zvar)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == 0) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.IRON_BLOCK);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y != 0 && y != (yvar + 5) && y != (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 5)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.HOPPER);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }

                        if (x == xvar && z == zvar && y == (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.CAULDRON);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                    }
                }
            }
        }
        if (dir == BlockFace.NORTH || dir == BlockFace.NORTH_EAST) {
            for (int x = 0; x < holesize; x++) {
                for (int z = 0; z < holesize; z++) {
                    for (int y = 0; y <= (yvar + 5); y++) {
                        Location loc = new Location(world, block.getX() - 2 - x, block.getY() + (5 - y), block.getZ() - z - 1);
                        if ((x == xvar || z == zvar) && y == 0 && !(x == xvar && z == zvar)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == 0) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.IRON_BLOCK);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y != 0 && y != (yvar + 5) && y != (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 5)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.HOPPER);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.CAULDRON);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                    }
                }
            }
        }
        if (dir == BlockFace.EAST || dir == BlockFace.SOUTH_EAST) {
            for (int x = 0; x < holesize; x++) {
                for (int z = 0; z < holesize; z++) {
                    for (int y = 0; y <= (yvar + 5); y++) {
                        Location loc = new Location(world, x + block.getX() + 1, block.getY() + (5 - y), block.getZ() - z - 2);
                        if ((x == xvar || z == zvar) && y == 0 && !(x == xvar && z == zvar)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == 0) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.IRON_BLOCK);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y != 0 && y != (yvar + 5) && y != (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 5)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.HOPPER);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.CAULDRON);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                    }
                }
            }
        }
        if (dir == BlockFace.SOUTH || dir == BlockFace.SOUTH_WEST) {
            for (int x = 0; x < holesize; x++) {
                for (int z = 0; z < holesize; z++) {
                    for (int y = 0; y <= (yvar + 5); y++) {
                        Location loc = new Location(world, x + block.getX() + 2, block.getY() + (5 - y), block.getZ() + z + 1);
                        if ((x == xvar || z == zvar) && y == 0 && !(x == xvar && z == zvar)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == 0) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.IRON_BLOCK);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y != 0 && y != (yvar + 5) && y != (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 5)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.HOPPER);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                        if (x == xvar && z == zvar && y == (yvar + 4)) {
                            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                world.getBlockAt(loc).setType(Material.CAULDRON);
                                ArmBlocks.add(world.getBlockAt(loc));
                            } else {
                                return;
                            }

                        }
                    }
                }
            }
        }
    }

    public static boolean isInQuarriesBlock(Block b) {
        boolean contains = false;
        for (Quarry q : quarrylist) {
            if (q.containsBlock(b)) {
                contains = true;
            }
        }
        return contains;
    }

    public boolean containsBlock(Block b) {
        boolean contains = false;
        try {
            for (Block f : QuarryBlocks) {
                if (f.getLocation() == b.getLocation()) {
                    contains = true;
                }
            }
            for (Block f : ArmBlocks) {
                if (f.getLocation() == b.getLocation()) {
                    contains = true;
                }
            }
        } catch (Exception e) {
        }
        if (block.getLocation() == b.getLocation()) {
            contains = true;
        }
        return contains;
    }

    public boolean buildFrame(boolean edit) {
        if (dir == BlockFace.NORTH || dir == BlockFace.NORTH_EAST) {
            int holesize = 0;
            if (tier == 0) {
                holesize = 16;
            }
            if (tier == 1) {
                holesize = 32;
            }
            if (tier == 2) {
                holesize = 48;
            }
            World world = block.getWorld();
            for (int x = 0; x < (holesize + 2); x++) {
                for (int z = 0; z < (holesize + 2); z++) {
                    for (int y = 0; y < 6; y++) {
                        Location loc = new Location(world, block.getX() - 1 - x, block.getY() + y, block.getZ() - z);
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.AIR) && !world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && !world.getBlockAt(loc).getType().equals(Material.CAULDRON) && !world.getBlockAt(loc).getType().equals(Material.HOPPER) && !world.getBlockAt(loc).getType().equals(Material.IRON_BLOCK)) {
                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.AIR);
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                        int max = holesize + 1;
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && (((y == 0 || y == 5) && (x == 0 || z == 0 || z == max || x == max)) || ((x == 0 && z == 0) || (x == 0 && z == max) || (x == max && z == 0) || (x == max && z == max)))) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                    QuarryBlocks.add(world.getBlockAt(loc));
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (dir == BlockFace.EAST || dir == BlockFace.SOUTH_EAST) {
            int holesize = 0;
            if (tier == 0) {
                holesize = 16;
            }
            if (tier == 1) {
                holesize = 32;
            }
            if (tier == 2) {
                holesize = 48;
            }
            World world = block.getWorld();
            for (int x = 0; x < (holesize + 2); x++) {
                for (int z = 0; z < (holesize + 2); z++) {
                    for (int y = 0; y < 6; y++) {
                        Location loc = new Location(world, x + block.getX(), block.getY() + y, block.getZ() - z - 1);
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.AIR) && !world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && !world.getBlockAt(loc).getType().equals(Material.CAULDRON) && !world.getBlockAt(loc).getType().equals(Material.HOPPER) && !world.getBlockAt(loc).getType().equals(Material.IRON_BLOCK)) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.AIR);
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                        int max = holesize + 1;
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && (((y == 0 || y == 5) && (x == 0 || z == 0 || z == max || x == max)) || ((x == 0 && z == 0) || (x == 0 && z == max) || (x == max && z == 0) || (x == max && z == max)))) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                    QuarryBlocks.add(world.getBlockAt(loc));
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (dir == BlockFace.SOUTH || dir == BlockFace.SOUTH_WEST) {
            int holesize = 0;
            if (tier == 0) {
                holesize = 16;
            }
            if (tier == 1) {
                holesize = 32;
            }
            if (tier == 2) {
                holesize = 48;
            }
            World world = block.getWorld();
            for (int x = 0; x < (holesize + 2); x++) {
                for (int z = 0; z < (holesize + 2); z++) {
                    for (int y = 0; y < 6; y++) {
                        Location loc = new Location(world, x + block.getX() + 1, block.getY() + y, block.getZ() + z);
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.AIR) && !world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && !world.getBlockAt(loc).getType().equals(Material.CAULDRON) && !world.getBlockAt(loc).getType().equals(Material.HOPPER) && !world.getBlockAt(loc).getType().equals(Material.IRON_BLOCK)) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.AIR);
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                        int max = holesize + 1;
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && (((y == 0 || y == 5) && (x == 0 || z == 0 || z == max || x == max)) || ((x == 0 && z == 0) || (x == 0 && z == max) || (x == max && z == 0) || (x == max && z == max)))) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                    QuarryBlocks.add(world.getBlockAt(loc));
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (dir == BlockFace.WEST || dir == BlockFace.NORTH_WEST) {
            int holesize = 0;
            if (tier == 0) {
                holesize = 16;
            }
            if (tier == 1) {
                holesize = 32;
            }
            if (tier == 2) {
                holesize = 48;
            }
            World world = block.getWorld();
            for (int x = 0; x < (holesize + 2); x++) {
                for (int z = 0; z < (holesize + 2); z++) {
                    for (int y = 0; y < 6; y++) {
                        Location loc = new Location(world, block.getX() - x, block.getY() + y, block.getZ() + z + 1);
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.AIR) && !world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && !world.getBlockAt(loc).getType().equals(Material.CAULDRON) && !world.getBlockAt(loc).getType().equals(Material.HOPPER) && !world.getBlockAt(loc).getType().equals(Material.IRON_BLOCK)) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.AIR);
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                        int max = holesize + 1;
                        if (isInQuarriesBlock(world.getBlockAt(loc))) {
                            return true;
                        }
                        if (!world.getBlockAt(loc).getType().equals(Material.COBBLE_WALL) && (((y == 0 || y == 5) && (x == 0 || z == 0 || z == max || x == max)) || ((x == 0 && z == 0) || (x == 0 && z == max) || (x == max && z == 0) || (x == max && z == max)))) {

                            if (edit) {
                                if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                                    world.getBlockAt(loc).setType(Material.COBBLE_WALL);
                                    QuarryBlocks.add(world.getBlockAt(loc));
                                } else {
                                    return true;
                                }

                            }
                            return true;
                        }
                    }
                }
            }
        }
        Location loc2 = block.getLocation();
        loc2.add(0, 1, 0);
        if (!block.getWorld().getBlockAt(loc2).getType().equals(Material.CHEST)) {
            if (MainClass.ps.mayEditBlock(getBlockAtSpot(xwork, ywork, zwork), playername)) {
                block.getWorld().getBlockAt(loc2).setType(Material.CHEST);
                QuarryBlocks.add(block.getWorld().getBlockAt(loc2));
            } else {
                return true;
            }

            return true;
        }
        if (!QuarryBlocks.contains(block)) {
            QuarryBlocks.add(block);
        }
        return false;
    }

    public String getPlayerName() {
        return playername;
    }

    public Quarry delete() {
        cantick = false;
        quarrylist.remove(this);
        file.delete();
        return this;
    }
}