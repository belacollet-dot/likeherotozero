package de.likeherotozero.service;

import de.likeherotozero.entity.Role;
import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.repository.ScientistUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final ScientistUserRepository userRepository;

    public UserService(ScientistUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<ScientistUser> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public List<ScientistUser> getAllScientists() {
        return userRepository.findByRoleOrderByNameAsc(Role.SCIENTIST);
    }

    public ScientistUser createScientist(String name, String email, String password) {
        return userRepository.save(new ScientistUser(name, email, password, Role.SCIENTIST));
    }

    public void updateScientist(Long id, String name, String email) {
        ScientistUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scientist not found: " + id));
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }

    public void updateScientists(List<Long> ids, List<String> names, List<String> emails) {
        for (int i = 0; i < ids.size(); i++) {
            updateScientist(ids.get(i), names.get(i), emails.get(i));
        }
    }

    public void deleteScientist(Long id) {
        userRepository.deleteById(id);
    }
}
