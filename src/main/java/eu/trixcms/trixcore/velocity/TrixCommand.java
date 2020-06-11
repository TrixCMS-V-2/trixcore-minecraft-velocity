package eu.trixcms.trixcore.velocity;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import eu.trixcms.trixcore.api.config.exception.InvalidConfigException;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.common.i18n.Translator;
import lombok.RequiredArgsConstructor;
import net.kyori.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class TrixCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TrixCore trixCore;
    private final Translator translator;

    @Override
    public void execute(@NonNull CommandSource sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            if (trixCore.getServerPort() == 0)
                logger.info(translator.of("CMD_PORT_ALREADY_RESET"));

            trixCore.getTrixServer().stop();

            try {
                trixCore.getTrixServer().setSecretKey("");
                trixCore.getTrixServer().setPort(-1);
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
            } catch (InvalidPortException ignored) {
            }

            logger.info(translator.of("CMD_PORT_SUCCESSFULLY_RESET"));
            sender.sendMessage(TextComponent.of(translator.of("CMD_PORT_SUCCESSFULLY_RESET")));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setup")) {
            int port = Integer.parseInt(args[1]);

            if (trixCore.getServerPort() != 0) {
                logger.error(translator.of("CMD_NEED_RESET_BEFORE"));
                return;
            }

            trixCore.getTrixServer().stop();

            try {
                trixCore.getTrixServer().setPort(port);
            } catch (InvalidPortException e) {
                logger.error(translator.of("PORT_HELP"));
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(TextComponent.of(translator.of("PORT_HELP")));
                sender.sendMessage(TextComponent.of(translator.of("ERROR") + e.getMessage()));
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
            }

            try {
                trixCore.getTrixServer().config(trixCore).start();
            } catch (InvalidPortException e) {
                logger.error(translator.of("PORT_HELP"));
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(TextComponent.of(translator.of("PORT_HELP")));
                sender.sendMessage(TextComponent.of(translator.of("ERROR") + e.getMessage()));
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(TextComponent.of(translator.of("ERROR") + e.getMessage()));
            } catch (InvalidConfigException e) {
                sender.sendMessage(TextComponent.of(translator.of("UNKNOWN_SAVER") + e.getMessage()));
                logger.error(translator.of("UNKNOWN_SAVER"), e);
            }

            logger.info(translator.of("CMD_PORT_SUCCESSFULLY_SETUP", port + ""));
            sender.sendMessage(TextComponent.of(translator.of("CMD_PORT_SUCCESSFULLY_SETUP", port + "")));
        }
    }
}
