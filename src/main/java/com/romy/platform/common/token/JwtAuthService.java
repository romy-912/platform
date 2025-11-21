package com.romy.platform.common.token;

import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.auth.service.AuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class JwtAuthService implements UserDetailsService {

    private final AuthLoginService authLoginService;

    @Override
    public AuthUserDvo loadUserByUsername(String usrId) throws UsernameNotFoundException {

        AuthUserDvo dvo = this.authLoginService.getUserInfoByToken(usrId);
        if (dvo == null) {
            throw new UsernameNotFoundException(usrId);
        }

        return dvo;
    }

}
