package fr.tt54.killTheBrioche.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class NMS {

    public static Location locateAncientCity(Player player) {
        // Récupérer le monde NMS
        ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();

        // Récupérer le registre des structures
        Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        // Clé de la structure "minecraft:ancient_city"
        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE,
                Identifier.tryParse("minecraft:ancient_city"));

        // Récupérer le HolderSet correspondant
        var holderOpt = registry.get(key).map(holder -> net.minecraft.core.HolderSet.direct(holder));
        if (holderOpt.isEmpty()) {
            player.sendMessage("§cStructure introuvable dans le registre !");
            return null;
        }

        // Position du joueur
        BlockPos origin = BlockPos.containing(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        // Chercher la structure la plus proche
        Pair<BlockPos, net.minecraft.core.Holder<Structure>> pair =
                level.getChunkSource().getGenerator().findNearestMapStructure(level, holderOpt.get(), origin, 100, false);

        if (pair == null) {
            player.sendMessage("§cAucune Ancient City trouvée à proximité !");
            return null;
        }

        BlockPos found = pair.getFirst();
        Location loc = new Location(player.getWorld(), found.getX(), found.getY(), found.getZ());

        player.sendMessage("§aAncient City trouvée en " + found.getX() + ", " + found.getY() + ", " + found.getZ());
        return loc;
    }
}

