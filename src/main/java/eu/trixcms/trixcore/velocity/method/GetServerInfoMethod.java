package eu.trixcms.trixcore.velocity.method;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import eu.trixcms.trixcore.api.container.ProxyInfoContainer;
import eu.trixcms.trixcore.api.container.ProxyServerContainer;
import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import eu.trixcms.trixcore.velocity.TrixCore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@MethodName(method = Methods.GET_SERVER_INFO)
public class GetServerInfoMethod implements IMethod {

    @Override
    public IResponse exec(String[] args) {
        InetAddress ip = null;

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String completeIP = ((ip == null) ? "localhost" : ip.getHostAddress());

        return new SuccessResponse(
                new ProxyInfoContainer(
                        completeIP,
                        TrixCore.getInstance().getProxyServer().getVersion().getVersion(),
                        TrixCore.getInstance().getProxyServer().getAllServers().stream()
                                .map(server -> new ProxyServerContainer(server.getServerInfo().getName(), getServerDescription(server), server.getPlayersConnected().size()))
                                .collect(Collectors.toList())
                )
        );
    }

    private String getServerDescription(RegisteredServer server) {
        try {
            return server.ping().get().getDescription().toString();
        } catch (InterruptedException | ExecutionException e) {
            return "unknown";
        }
    }
}