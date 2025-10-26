package name.modid;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.text.Text;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;




public class EasyAFKClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("afk").executes(context -> {
                context.getSource().sendFeedback(Text.literal("Going AFK..."));

                new Thread(() -> {
                    MinecraftClient client2 = null;
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        String username = MinecraftClient.getInstance().getSession().getUsername();
                        String json = username + "," + CurrentIP.currentAddress+ ",1.21.8";


                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://easyafk.duckdns.org:80/process"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();


                        HttpResponse<String> response;
                        try {
                            response = client.send(request, BodyHandlers.ofString());
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("Response Code: " + response.statusCode());
                        System.out.println("Response Body: " + response.body());

                        client2 = MinecraftClient.getInstance();

                        if (client2.player != null) {
                            String url = response.body(); // must start with https:// or http://

                            // Create a clickable message
                            MutableText clickableLink = Text.literal(url)
                                    .setStyle(Style.EMPTY
                                            .withColor(Formatting.BLUE)
                                            .withUnderline(true)
                                            .withClickEvent(new ClickEvent() {
                                                @Override
                                                public Action getAction() {
                                                    return null;
                                                }
                                            }));

                            client2.player.sendMessage(clickableLink, false);
                            Util.getOperatingSystem().open(url);
                        }


                        HttpHeaders headers = response.headers();
                        Map<String, List<String>> map = headers.map();
                        map.forEach((k, v) -> System.out.println(k + ":" + v));


//                return 1;
                    } catch (Exception e) {
                        MinecraftClient client5 = MinecraftClient.getInstance();
                        if (client5.player != null) {
                            client5.player.sendMessage(
                                    Text.literal("Error Try Again? Check Console for full error message."),
                                    false
                            );
                            e.printStackTrace();
                        }
                    }
                }).start();
                return 1;
            }));

        });
	}
}