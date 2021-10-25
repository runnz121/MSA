package com.example.usersmicroservices1.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    //jpa의 경우 findBy라고 쓰면 그 뒤에오는건 column은 연동되어있는 DB에서 하나의 값을 찾아주게된다
    //즉 userid컬럼에서 해당 userid를 찾는다 select userid
    UserEntity findByUserId(String userId);

    UserEntity findByEmail(String username);
}
