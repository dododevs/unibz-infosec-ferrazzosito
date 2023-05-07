package it.unibz.infosec.examproject.user.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchUsers {

    private final UserRepository userRepository;

    @Autowired
    public SearchUsers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        Optional<User> searchedUser = userRepository.findById(id);

        if(searchedUser.isEmpty()){
            throw new IllegalArgumentException("User with id " + id + " is not present in the database");
        }
        return searchedUser.get();
    }

    public List<User> findAll(){
        List<User> list = userRepository.findAll();
        if(list.isEmpty()){
            throw new IllegalArgumentException("No users in database");
        }
        return list;
    }

}
