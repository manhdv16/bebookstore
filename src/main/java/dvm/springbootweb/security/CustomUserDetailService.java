package dvm.springbootweb.security;

import dvm.springbootweb.entity.User;
import dvm.springbootweb.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailService
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    private static final Logger LOGGER = LogManager.getLogger(CustomUserDetailService.class);

    /**
     * Load User By Username
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if(user == null){
            LOGGER.error("user not found with "+username);
            throw new UsernameNotFoundException("user not found with "+username);
        }else{
            return CustomUserDetail.mapUserToUserDetail(userRepository.findByUserName(username));
        }
    }
}
