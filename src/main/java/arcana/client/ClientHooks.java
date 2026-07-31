package arcana.client;

import arcana.client.gui.ResearchBrowserScreen;
import net.minecraft.client.Minecraft;

public final class ClientHooks {
    private ClientHooks() {
    }

    public static void openThaumonomicon() {
        Minecraft.getInstance().setScreen(new ResearchBrowserScreen());
    }
}
