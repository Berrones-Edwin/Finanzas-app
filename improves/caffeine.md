  // 1. For a single user
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    // 2. For the entire list of users
    @Cacheable(value = "all-users", key = "'all'")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    // 1. Cacheamos la lista completa con la clave estática 'all'
    @Cacheable(value = "all-users", key = "'all'")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Cacheamos a cada usuario individual por su ID
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    // 3. Cuando creamos un usuario...
    @CacheEvict(value = "all-users", key = "'all'") // ¡Borramos la lista vieja!
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 4. Cuando actualizamos un usuario...
    // Actualizamos el caché individual del usuario (#user.id) 
    // Y BORRAMOS la lista completa porque los datos de un elemento cambiaron.
    @CachePut(value = "users", key = "#user.id")
    @CacheEvict(value = "all-users", key = "'all'")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // 5. Cuando eliminamos un usuario...
    // Borramos tanto al usuario individual (#id) como la lista completa.
    @CacheEvict(value = "users", key = "#id")
    @CacheEvict(value = "all-users", key = "'all'")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // ... tus otros métodos de consulta (getAllUsers, getUserById)

    @Caching(
        // 1. Modificamos los datos guardados del usuario individual
        put = {
            @CachePut(value = "users", key = "#user.id")
        },
        // 2. Limpiamos todas las listas porque este usuario cambió de datos
        evict = {
            @CacheEvict(value = "all-users", allEntries = true)
        }
    )
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}


@Caching(
    evict = {
        @CacheEvict(value = "users", key = "#id"),                // Borra al usuario de la memoria
        @CacheEvict(value = "all-users", allEntries = true)        // Destruye todas las listas
    }
)
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

