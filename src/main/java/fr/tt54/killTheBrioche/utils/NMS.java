package fr.tt54.killTheBrioche.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Optional;

public class NMS {

    public static Location locateStructure(Player player, String structureKey) {
        ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
        Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, Identifier.tryParse(structureKey));

        Optional<HolderSet<Structure>> holderOpt = registry.get(key).map(HolderSet::direct);
        if (holderOpt.isEmpty()) {
            player.sendMessage("§cStructure introuvable dans le registre !");
            return null;
        }

        BlockPos origin = BlockPos.containing(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level, holderOpt.get(), origin, 100, false);

        if (pair == null) {
            player.sendMessage("§cAucune structure trouvée à proximité !");
            return null;
        }

        BlockPos found = pair.getFirst();
        return new Location(player.getWorld(), found.getX(), found.getY(), found.getZ());
    }
}

