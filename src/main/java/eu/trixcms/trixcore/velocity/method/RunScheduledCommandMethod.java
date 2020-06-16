package eu.trixcms.trixcore.velocity.method;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.ArgsPrecondition;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.common.response.ErrorResponse;
import eu.trixcms.trixcore.common.response.JsonResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import eu.trixcms.trixcore.velocity.TrixCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TimerTask;

@MethodName(method = Methods.RUN_SCHEDULED_COMMAND)
public class RunScheduledCommandMethod implements IMethod {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @ArgsPrecondition(amount = 2)
    public JsonResponse exec(String[] args) {
        String command = args[0];
        int time = Integer.parseInt(args[1]);
        CommandContainer cmd = new CommandContainer(time, command);

        TrixCore.getInstance().getTrixServer().scheduler().schedule(new TimerTask() {
            @Override
            public void run() {
                TrixCore.getInstance().executeCommand(cmd);
            }
        }, time * 1000);

        try {
            TrixCore.getInstance().getCommandManager().add(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse(501, TrixCore.getInstance().getTranslator().of("HTTP_TASK_SAVE_COMMAND_ERROR"));
        }

        return new SuccessResponse(TrixCore.getInstance().getTranslator().of("HTTP_TASK_SAVE_COMMAND_SUCCESS", "/" + cmd.getCmd(), cmd.getTime() + ""));
    }
}
