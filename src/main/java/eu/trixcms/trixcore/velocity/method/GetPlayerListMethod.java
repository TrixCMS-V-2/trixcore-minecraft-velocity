package eu.trixcms.trixcore.velocity.method;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.container.PlayerContainer;
import eu.trixcms.trixcore.api.container.PlayersContainer;
import eu.trixcms.trixcore.common.response.JsonResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import eu.trixcms.trixcore.velocity.TrixCore;

import java.util.stream.Collectors;

@MethodName(method = Methods.GET_PLAYER_LIST)
public class GetPlayerListMethod implements IMethod {

    @Override
    public JsonResponse exec(String[] args) {
        return new SuccessResponse(new PlayersContainer(
                TrixCore.getInstance().getProxyServer().getAllPlayers().stream()
                        .map(player -> new PlayerContainer(player.getUsername(), player.getUniqueId()))
                        .collect(Collectors.toList()))
        );
    }
}
