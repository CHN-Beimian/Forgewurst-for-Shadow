package net.wurstclient.forge;

import java.awt.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.compatibility.WHackList;
import net.wurstclient.forge.hacks.*;
import net.wurstclient.forge.settings.Setting;
import net.wurstclient.forge.utils.JsonUtils;

public final class HackList extends WHackList {
	public static HackList hacklist;
	//
	
	public final InformationHUD informationHUD=register(new InformationHUD());
	public final TeamsHack teamsHack=register(new TeamsHack());
	public final TeleportHitHack teleportHitHack=register(new TeleportHitHack());
	public final FastSleepingHack fastSleepingHack=register(new FastSleepingHack());
	public final PluginGetterHack pluginGetterHack=register(new PluginGetterHack());
	public final PVPHack pvpHack=register(new PVPHack());
	public final TestHack testHack=register(new TestHack());
	public final AntiBotHack antiBotHack=register(new AntiBotHack());
	public final AimBotHack aimBotHack=register(new AimBotHack());
	private final AutoSayHack autoSayHack=register(new AutoSayHack());
	private final KickPlayerHack kickPlayerHack=register(new KickPlayerHack());
	private final BedFuckerHack bedFuckerHack=register(new BedFuckerHack());
	private final BackHack backHack=register(new BackHack());
	private final BhopHack bhopHack=register(new BhopHack());
	private final LimitJumpHack limitJumpHack=register(new LimitJumpHack());
	public final InfiniteAuraHack infiniteAuraHack=register(new InfiniteAuraHack());
	public final FastUseHack fastUseHack=register(new FastUseHack());
	public final RegenHack regenHack=register(new RegenHack());
	public final VelocityHack velocityHack =register(new VelocityHack());
	public final ServerHack serverHack=register(new ServerHack());
	public final ChestStealHack chestStealHack=register(new ChestStealHack());
	public final NoticeHack noticeHack=register(new NoticeHack());
	public final NoHurtHack noHurtHack =register(new NoHurtHack());
	public final UpHack upHack=register(new UpHack());
	public final TeleportHack teleportHack=register(new TeleportHack());
	public final SkinDerpHack skinDerpHack=register(new SkinDerpHack());
	public final BowAimBotHack bowAimBotHack =register(new BowAimBotHack());
	public final AutoClickerHack autoClickerHack =register(new AutoClickerHack());
	public final StepHack stepHack =register(new StepHack());
	public final PropagandaHack propagandaHack=register(new PropagandaHack());
	public final SpeedHack speedHack =register(new SpeedHack());
	public final GuiMoveHack guiMoveHack =register(new GuiMoveHack());
	public final AutoLogHack autoLogHack =register(new AutoLogHack());
	public final AutoTextHack autoTextHack =register(new AutoTextHack());
	
	public final ButtonHack buttonHack =register(new ButtonHack());
	public final CustomChat customChat =register(new CustomChat());
	public final MultiAura massKilAuraHack =register(new MultiAura());
	public final AutoRespawnHack autoRespawnHack =register(new AutoRespawnHack());
	public final AutoSoupHack autoSoup =register(new AutoSoupHack());
	public final NoClipHack noClipHack = register(new NoClipHack());
	public final TpAuraHack tpAura = register(new TpAuraHack());
	public final AutoTotemHack autoTotemHack = register(new AutoTotemHack());
	public final ScaffoldHack scaffoldHack = register(new ScaffoldHack());
	public final SuperFlyHack superFly = register(new SuperFlyHack());
	public final TracersHack tracersHack = register(new TracersHack());
	public final ArmorHUDHack armorHUDHack = register(new ArmorHUDHack());
	public final CancelPacketHack cancelPacketHack = register(new CancelPacketHack());
	public final CriticalHack criticalHack = register(new CriticalHack());
	public final Derp derp = register(new Derp());
	public final TwerkHack twerkHack = register(new TwerkHack());
	public final FlashHack flashHack = register(new FlashHack());
	public final AntiWeather antiWeather = register(new AntiWeather());
	public final AntiAFK antiAFK = register(new AntiAFK());
	public final AutoCurseHack autoCurseHack = register(new AutoCurseHack());
	public final AirJumpHack airJumpHack = register(new AirJumpHack());
	public final AntiSpamHack antiSpamHack = register(new AntiSpamHack());
	public final AutoArmorHack autoArmorHack = register(new AutoArmorHack());
	public final AutoFarmHack autoFarmHack = register(new AutoFarmHack());
	public final AutoFishHack autoFishHack = register(new AutoFishHack());
	public final AutoSprintHack autoSprintHack = register(new AutoSprintHack());
	public final AutoSwimHack autoSwimHack = register(new AutoSwimHack());
	public final AutoToolHack autoToolHack = register(new AutoToolHack());
	public final AutoWalkHack autoWalkHack = register(new AutoWalkHack());
	public final BlinkHack blinkHack = register(new BlinkHack());
	public final BunnyHopHack bunnyHopHack = register(new BunnyHopHack());
	public final ChestEspHack chestEspHack = register(new ChestEspHack());
	public final ClickGuiHack clickGuiHack = register(new ClickGuiHack());
	public final FastBreakHack fastBreakHack = register(new FastBreakHack());
	public final FastLadderHack fastLadderHack = register(new FastLadderHack());
	public final FastPlaceHack fastPlaceHack = register(new FastPlaceHack());
	public final FlightHack flightHack = register(new FlightHack());
	public final FreecamHack freecamHack = register(new FreecamHack());
	public final FullbrightHack fullbrightHack = register(new FullbrightHack());
	public final GlideHack glideHack = register(new GlideHack());
	public final ItemEspHack itemEspHack = register(new ItemEspHack());
	public final JesusHack jesusHack = register(new JesusHack());
	public final KillauraHack killauraHack = register(new KillauraHack());
	public final MobEspHack mobEspHack = register(new MobEspHack());
	public final MobSpawnEspHack mobSpawnEspHack = register(new MobSpawnEspHack());
	public final NoFallHack noFallHack = register(new NoFallHack());
	public final NoHurtcamHack noHurtcamHack = register(new NoHurtcamHack());
	public final NoWebHack noWebHack = register(new NoWebHack());
	public final NukerHack nukerHack = register(new NukerHack());
	public final PlayerEspHack playerEspHack = register(new PlayerEspHack());
	public final RadarHack radarHack = register(new RadarHack());
	public final RainbowUiHack rainbowUiHack = register(new RainbowUiHack());
	public final SneakHack sneakHack = register(new SneakHack());
	public final SpiderHack spiderHack = register(new SpiderHack());
	public final TimerHack timerHack = register(new TimerHack());
	public final TunnellerHack tunnellerHack = register(new TunnellerHack());
	public final XRayHack xRayHack = register(new XRayHack());

	private final Path enabledHacksFile;
	private final Path settingsFile;
	private boolean disableSaving;

	public HackList(Path enabledHacksFile, Path settingsFile) {
		this.enabledHacksFile = enabledHacksFile;
		this.settingsFile = settingsFile;
	}

	public void loadEnabledHacks() {
		JsonArray json;
		try (BufferedReader reader = Files.newBufferedReader(enabledHacksFile)) {
			json = JsonUtils.jsonParser.parse(reader).getAsJsonArray();

		} catch (NoSuchFileException e) {
			saveEnabledHacks();
			return;

		} catch (Exception e) {
			System.out.println("Failed to load " + enabledHacksFile.getFileName());
			e.printStackTrace();

			saveEnabledHacks();
			return;
		}

		disableSaving = true;
		for (JsonElement e : json) {
			if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString())
				continue;

			Hack hack = get(e.getAsString());
			if (hack == null || !hack.isStateSaved())
				continue;

			hack.setEnabled(true);
		}
		disableSaving = false;

		saveEnabledHacks();
	}

	public void saveEnabledHacks() {
		if (disableSaving)
			return;

		JsonArray enabledHacks = new JsonArray();
		for (Hack hack : getRegistry())
			if (hack.isEnabled() && hack.isStateSaved())
				enabledHacks.add(new JsonPrimitive(hack.getName()));

		try (BufferedWriter writer = Files.newBufferedWriter(enabledHacksFile)) {
			JsonUtils.prettyGson.toJson(enabledHacks, writer);

		} catch (IOException e) {
			System.out.println("Failed to save " + enabledHacksFile.getFileName());
			e.printStackTrace();
		}
	}

	public void loadSettings() {
		JsonObject json;
		try (BufferedReader reader = Files.newBufferedReader(settingsFile)) {
			json = JsonUtils.jsonParser.parse(reader).getAsJsonObject();

		} catch (NoSuchFileException e) {
			saveSettings();
			return;

		} catch (Exception e) {
			System.out.println("Failed to load " + settingsFile.getFileName());
			e.printStackTrace();

			saveSettings();
			return;
		}

		disableSaving = true;
		for (Entry<String, JsonElement> e : json.entrySet()) {
			if (!e.getValue().isJsonObject())
				continue;

			Hack hack = get(e.getKey());
			if (hack == null)
				continue;

			Map<String, Setting> settings = hack.getSettings();
			for (Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet()) {
				String key = e2.getKey().toLowerCase();
				if (!settings.containsKey(key))
					continue;

				settings.get(key).fromJson(e2.getValue());
			}
		}
		disableSaving = false;

		saveSettings();
	}

	public void saveSettings() {
		if (disableSaving)
			return;

		JsonObject json = new JsonObject();
		for (Hack hack : getRegistry()) {
			if (hack.getSettings().isEmpty())
				continue;

			JsonObject settings = new JsonObject();
			for (Setting setting : hack.getSettings().values())
				settings.add(setting.getName(), setting.toJson());

			json.add(hack.getName(), settings);
		}

		try (BufferedWriter writer = Files.newBufferedWriter(settingsFile)) {
			JsonUtils.prettyGson.toJson(json, writer);

		} catch (IOException e) {
			System.out.println("Failed to save " + settingsFile.getFileName());
			e.printStackTrace();
		}
	}
	public static HackList instance() {
		return hacklist;
	}
	
}