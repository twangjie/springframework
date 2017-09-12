package telnet.service;

import telnet.model.TelnetCommand;
import telnet.model.TelnetConnect;

/**
 * Created by 王杰 on 2017/7/4.
 */
public interface TelnetService {

    void executeTelnetCommand(String sessionId, TelnetCommand command);

    void executeTelnetTab(String sessionId, TelnetCommand command);

    void executeTelnetKeydown(String sessionId, TelnetCommand command);

    void executeTelnetConnect(String sessionId, TelnetConnect command);

    void executeTelnetDisconnect(String sessionId, TelnetConnect command);
}
