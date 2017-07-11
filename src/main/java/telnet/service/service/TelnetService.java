package telnet.service.service;

import telnet.service.model.TelnetCommand;
import telnet.service.model.TelnetConnect;
import telnet.service.model.TelnetResponse;

/**
 * Created by 王杰 on 2017/7/4.
 */
public interface TelnetService {

    TelnetResponse executeTelnetCommand(String sessionId, TelnetCommand command);

    TelnetResponse executeTelnetKeydown(String sessionId, TelnetCommand command);

    TelnetResponse executeTelnetConnect(String sessionId, TelnetConnect command);

    TelnetResponse executeTelnetDisconnect(String sessionId, TelnetConnect command);
}
