package az.unibank.commons.util.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthDataBean authDataBean;

    public long getCurrentUser() {
        return authDataBean.getUser().getId();
    }



}
