package com.example.usersmicroservices1.Service;

import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.jpa.UserEntity;
import com.example.usersmicroservices1.jpa.UserRepository;
import com.example.usersmicroservices1.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;


    @Autowired //스프링이 기동되면서 등록할 수 있는 빈을 찾아서 메모리에 생성해주는 것이다.
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){ //이것도 초기화가 되어있어야 주입이 되는데 주입이 안되면 에러남
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        //model mapper를 통해 request요청을 dto로 변환 , entity로 변환할 수 있다.

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //모델 메퍼가  변환시, 얼마나 정확하게 매칭되게끔 할지 정하는 전략환경설정을 지정
        UserEntity userEntity = mapper.map(userDto, UserEntity.class); //mapper의 map메소드를 통해 userdto를 userentity.class로 변환시킬 수 있다.
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null){
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }
}
