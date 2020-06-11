package eu.trixcms.trixcore.velocity.method;

import com.velocitypowered.api.proxy.Player;
import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.ArgsPrecondition;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.common.response.JsonResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import eu.trixcms.trixcore.velocity.TrixCore;

@MethodName(method = Methods.IS_CONNECTED)
public class IsConnectedMethod implements IMethod {

    @Override
    @ArgsPrecondition(amount = 1)
    public JsonResponse exec(String[] args) {
        return new SuccessResponse(TrixCore.getInstance().getProxyServer().getAllPlayers().stream()
                .map(Player::getUsername)
                .anyMatch(name -> name.equals(args[0]))
        );
    }
}
