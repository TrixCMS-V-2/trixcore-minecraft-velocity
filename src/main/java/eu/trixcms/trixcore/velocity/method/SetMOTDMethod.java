package eu.trixcms.trixcore.velocity.method;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.common.response.ErrorResponse;
import eu.trixcms.trixcore.common.response.JsonResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import eu.trixcms.trixcore.velocity.TrixCore;

import java.io.IOException;

@MethodName(method = Methods.SET_MOTD)
public class SetMOTDMethod implements IMethod {

    @Override
    public JsonResponse exec(String[] args) {
        String motd = args[0];
        try {
            TrixCore.getInstance().saveMotd(motd);
            return new SuccessResponse(TrixCore.getInstance().getTranslator().of("HTTP_MOTD_EDITED_SUCCESSFULLY"));
        } catch (IOException e) {
            return new ErrorResponse(500, TrixCore.getInstance().getTranslator().of("HTTP_MOTD_ERROR"));
        }
    }
}
