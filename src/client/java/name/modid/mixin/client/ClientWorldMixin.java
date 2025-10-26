package name.modid.mixin.client;

import name.modid.CurrentIP;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.net.SocketAddress;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    // Update current address when connecting to a new world
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(
            ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey registryRef, RegistryEntry dimensionType, int loadDistance, int simulationDistance, WorldRenderer worldRenderer, boolean debugWorld, long seed, int seaLevel, CallbackInfo ci
    ) {
        SocketAddress newAddress = networkHandler.getConnection().getAddress();

        CurrentIP.LOGGER.debug("Setting EyeIP address to: " + newAddress);

        CurrentIP.currentAddress = newAddress;
    }
}