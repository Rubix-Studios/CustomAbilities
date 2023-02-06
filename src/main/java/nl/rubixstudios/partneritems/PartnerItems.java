package nl.rubixstudios.partneritems;

import lombok.Getter;
import nl.rubixstudios.partneritems.abilities.bleed.BleedController;
import nl.rubixstudios.partneritems.abilities.forcefield.ForceFieldController;
import nl.rubixstudios.partneritems.abilities.grapple.GrappleHarpoonController;
import nl.rubixstudios.partneritems.abilities.paralyze.ParalyzeHoeController;
import nl.rubixstudios.partneritems.abilities.reversebow.ReverseBowController;
import nl.rubixstudios.partneritems.abilities.teleportportal.TeleportPortalController;
import nl.rubixstudios.partneritems.abilities.teleportportal.command.PortalCommand;
import nl.rubixstudios.partneritems.abilities.tntbomber.TntBomberController;
import nl.rubixstudios.partneritems.command.PartnerItemCommand;
import nl.rubixstudios.partneritems.command.PartnerItemsCommand;
import nl.rubixstudios.partneritems.data.Config;
import nl.rubixstudios.partneritems.data.ConfigFile;
import nl.rubixstudios.partneritems.timer.TimeController;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.check.CheckUtil;
import nl.rubixstudios.partneritems.util.particles.particletrailbuilder.ParticleTrailManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class PartnerItems extends JavaPlugin {

    @Getter private static PartnerItems instance;

    private ConfigFile configFile;

    private ParticleTrailManager particleTrailManager;

    private TimeController timeController;
    private TntBomberController tntBomberController;
    private BleedController bleedController;
    private GrappleHarpoonController grappleHarpoonController;
    private TeleportPortalController teleportPortalController;
    private ReverseBowController reverseBowController;
    private ParalyzeHoeController paralyzeHoeController;
    private ForceFieldController forceFieldController;

    @Override
    public void onEnable() {
        instance = this;

        this.log("&7===&f=============================================&f===");
        this.log("- &fName&7: &7&lPartnerItems");
        this.log("- &fVersion&7: &7" + this.getDescription().getVersion());
        this.log("- &fAuthor&7: &7" + this.getDescription().getAuthors());
        this.log("");

        try {
            this.configFile = new ConfigFile("config.yml");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new Config();

        if (!CheckUtil.isLicenseValid(Config.LICENSE_KEY)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Lazarus")) {
            this.log("&eChecking Lazarus:");
            this.log("   &c&lLazarus is not installed on this server!");
            this.log("");
            this.log("- &cDisabling plugin...");
            this.log("&7===&f=============================================&7===");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            this.log("&eChecking Lazarus:");
            this.log("   &aSuccesfully detected &e&lLazarus&a!");
            this.log("");
        }

        this.particleTrailManager = new ParticleTrailManager();
        this.timeController = new TimeController();

        this.enableAbilities();

        this.getCommand("partneritem").setExecutor(new PartnerItemCommand());
        this.getCommand("partneritems").setExecutor(new PartnerItemsCommand());
        this.getCommand("portal").setExecutor(new PortalCommand());

        this.log("");
        this.log("- &aSuccesfully enabled &e&lPartnerItems &aplugin.");
        this.log("&7===&f=============================================&7===");
    }

    private void enableAbilities() {
        this.bleedController = new BleedController();
        this.tntBomberController = new TntBomberController();
        this.grappleHarpoonController = new GrappleHarpoonController();
        this.teleportPortalController = new TeleportPortalController();
        this.reverseBowController = new ReverseBowController();
        this.paralyzeHoeController = new ParalyzeHoeController();
        this.forceFieldController = new ForceFieldController();
    }

    @Override
    public void onDisable() {
        this.log("&7===&f=============================================&7===");
        this.log("- &cDisabling &f&lPartnerItems " + this.getDescription().getVersion());
        this.log("");

        if (this.particleTrailManager != null) this.particleTrailManager.disable();

        if (this.timeController != null) this.timeController.disable();
        if (this.tntBomberController != null) this.tntBomberController.disable();
        if (this.bleedController != null) this.bleedController.disable();
        if (this.grappleHarpoonController != null) this.grappleHarpoonController.disable();
        if (this.teleportPortalController != null) this.teleportPortalController.disable();
        if (this.reverseBowController != null) this.reverseBowController.disable();
        if (this.paralyzeHoeController != null) this.paralyzeHoeController.disable();
        if (this.forceFieldController != null) this.forceFieldController.disable();


        this.log("- &7Succesfully disabled all partner items.");

        Bukkit.getServicesManager().unregisterAll(this);
        this.log("");
        this.log("&7===&f=============================================&7===");

    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ColorUtil.translate(message));
    }
}
