package repo.xirong.java.demo.mapper;

import org.springframework.stereotype.Component;
import repo.xirong.java.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


//@Repository
@Component
public interface TestMapper {

    ArrayList<User> getAllUsers ();
}
