package eu.trixcms.trixcore.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import eu.trixcms.trixcore.api.command.ICommandExecutor;
import eu.trixcms.trixcore.api.config.IConfig;
import eu.trixcms.trixcore.api.config.exception.InvalidConfigException;
import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.api.method.exception.DuplicateMethodNameException;
import eu.trixcms.trixcore.api.method.exception.InvalidMethodDefinitionException;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.api.util.ServerTypeEnum;
import eu.trixcms.trixcore.common.CommandManager;
import eu.trixcms.trixcore.common.SchedulerManager;
import eu.trixcms.trixcore.common.TrixServer;
import eu.trixcms.trixcore.common.i18n.JsonMessageSource;
import eu.trixcms.trixcore.common.i18n.Translator;
import eu.trixcms.trixcore.velocity.config.Config;
import eu.trixcms.trixcore.velocity.method.*;
import lombok.Getter;
import net.kyori.text.TextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(id = "trixcore", name = "TrixCore", url = "https://trixcms.eu", authors = {"antoineok", "iambluedev"})
public class TrixCore implements IConfig, ICommandExecutor<CommandContainer> {

    private static final Logger logger = LoggerFactory.getLogger(TrixCore.class);

    @Getter private static TrixCore instance;
    @Getter private TrixServer trixServer;
    @Getter private Translator translator;
    @Getter private SchedulerManager schedulerManager;
    @Getter private CommandManager commandManager;

    @Getter
    private final ProxyServer proxyServer;

    private Config config;
    private File configFile;

    @Inject
    public TrixCore(ProxyServer proxyServer, @DataDirectory Path dirPath) {
        instance = this;
        this.proxyServer = proxyServer;
        configFile = new File(dirPath.toFile(), "config.json");
        translator = new Translator(JsonMessageSource.class, Lang.values());

        config = Config.load(configFile);

        schedulerManager = new SchedulerManager(translator);
        commandManager = new CommandManager(this,
                translator,
                schedulerManager,
                new File(dirPath.toFile(), "commands.json"));
        trixServer = new TrixServer();

        try {
            trixServer
                    .translator(translator)
                    .scheduler(schedulerManager)
                    .commandManager(commandManager)
                    .serverType(ServerTypeEnum.VELOCITY)
                    .registerMethods(
                            new GetPlayerListMethod(),
                            new GetServerInfoMethod(),
                            new IsConnectedMethod(),
                            new RemoveScheduledCommandsMethod(),
                            new RunCommandMethod(),
                            new RunScheduledCommandMethod(),
                            new SetMOTDMethod()
                    );
        } catch (DuplicateMethodNameException | InvalidMethodDefinitionException e) {
            logger.error(translator.of("ERROR"), e);
        }

        try {
            trixServer.config(this);

            logger.info(translator.of("STARTING_SERVER"));
            trixServer.start();
        } catch (InvalidPortException e) {
            logger.error(translator.of("PORT_HELP"));
            logger.error(translator.of("ERROR"), e);
        } catch (IOException e) {
            logger.error(translator.of("ERROR"), e);
        } catch (InvalidConfigException e) {
            logger.error(translator.of("UNKNOWN_SAVER"), e);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) throws IOException {
        logger.info(translator.of("STOPPING_SERVER"));
        trixServer.stop();
        config.save(configFile);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxyServer.getCommandManager().register(new TrixCommand(this, translator), "trixcore");
    }

    @Subscribe
    public void onPing(ProxyPingEvent e) {
        if (config.getCustomMotd() != null && !config.getCustomMotd().isEmpty()) {
            final ServerPing.Builder builder = e.getPing().asBuilder();
            builder.description(TextComponent.of(config.getCustomMotd()));
            e.setPing(builder.build());
        }
    }

    @Override
    public boolean executeCommand(CommandContainer commandContainer) {
        return proxyServer.getCommandManager().execute(proxyServer.getConsoleCommandSource(), commandContainer.getCmd());
    }

    @Override
    public String getSecretKey() {
        String key = config.getSecretKey();
        return (key == null || key.isEmpty()) ? "" : key;
    }

    @Override
    public Integer getServerPort() {
        Integer port = config.getPort();
        return port == null ? 0 : port;
    }

    @Override
    public void saveSecretKey(String key) throws IOException {
        logger.info(translator.of("SAVER_SAVING_SECRET_KEY"));
        config.setSecretKey(key);
        config.save(configFile);
    }

    @Override
    public void saveServerPort(Integer port) throws IOException {
        logger.info(translator.of("SAVER_SAVING_SERVER_PORT"));
        config.setPort(port);
        config.save(configFile);
    }

    public void saveMotd(String motd) throws IOException {
        config.setCustomMotd(motd);
        config.save(configFile);
    }
}
