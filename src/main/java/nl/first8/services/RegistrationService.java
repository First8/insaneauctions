package nl.first8.services;

import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.logging.Logger;

@ApplicationScoped
public class RegistrationService {

	private static final Logger log = Logger.getLogger(RegistrationService.class.getName());

    @Inject
    private Client client;

    @Inject
    private HttpSession session;

    public RegistrationResultDto lookupMemberById(String nickname) {
        Date date = new Date();
        log.info("Nickname [" + nickname + "]");
        if(StringUtils.isNotEmpty(nickname)) {
            if( client.isLoggedIn()) {
                log.info("User already logged in as '" + client.getNickname() + "' so not logging in again as '" + nickname + "'");
                return new RegistrationResultDto(true, client.getDateLoggedIn());
            } else {
                client.setLoggedIn(true);
                client.setDateLoggedIn(date);
                client.setNickname(nickname);
                return new RegistrationResultDto(false, date);
            }
        }
        return new RegistrationResultDto(false,null);
    }

    public RegistrationDto currentLogin() {
        if (client.isLoggedIn()) {
            log.info("User already logged in as '" + client.getNickname() + "'");
            return new RegistrationDto(client.getNickname(), client.getDateLoggedIn());
        } else {
            log.info("No user is logged in");
            return new RegistrationDto();
        }
    }

    public RegistrationDto logout() {
        session.invalidate();
        return new RegistrationDto();
    }
}
