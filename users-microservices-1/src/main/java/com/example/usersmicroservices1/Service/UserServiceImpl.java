package com.example.usersmicroservices1.Service;

import com.example.usersmicroservices1.client.OrderServiceClient;
import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.jpa.UserEntity;
import com.example.usersmicroservices1.jpa.UserRepository;
import com.example.usersmicroservices1.vo.ResponseOrder;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    //추가로 주입받음
    Environment env;
    RestTemplate restTemplate;

    //@feign client사용한 service inteface
    OrderServiceClient orderServiceClient;

    //circuitbreaker 주입
    CircuitBreakerFactory circuitBreakerFactory;

    //userservicedetials 구현
    //username == email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null){
            throw new UsernameNotFoundException(username);
        }

        //security user 객체
        //모두 검색이 잘 되었다면 해당 유저를 반환하겠다
        //마지막은 권한값
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true, new ArrayList<>());
    }



    @Autowired //스프링이 기동되면서 등록할 수 있는 빈을 찾아서 메모리에 생성해주는 것이다.
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           OrderServiceClient orderServiceClient, //order-service 주입 -> feign client
                           RestTemplate restTemplate, //이것도 초기화가 되어있어야 주입이 되는데 주입이 안되면 에러남
                           CircuitBreakerFactory circuitBreakerFactory){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.orderServiceClient = orderServiceClient;
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
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

        /*
        Rest Template 코드는 더이상 사용되지 않을것 ->@feign client가 대체 주석처리
         */
//        //List<ResponseOrder> orders = new ArrayList<>();
//        //orderservice의 controller에서 getmapping으로 선언된, 오더 정보를 갖고옴
//        //user-service.yml파일의 값을 갖고옴
//        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
//        //주소값, 요청방식, 파라미터전달값, 어떤형식으로 전달 받을 것인지 -> 여기에 써있는 값들은
//        //order service의 controller의 getOrder부분의 값을 그대로 반환한 것이다.
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        //resposneorder타입으로 바꾸기
//        List<ResponseOrder> orderList = orderListResponse.getBody();


        /**
         * Fegin client 용 코드
         **/

        /*Feign Exception Handling*/
//        List<ResponseOrder> orderList = null;
//        try {
//            orderServiceClient.getOrders(userId);
//        } catch (FeignException ex){
//            log.error(ex.getMessage());
//        }


        /**
         * circuit breaker로 에러 처리하기 위해 주석 처리
         */
        /*feign error decoder로 예외처리*/
        //List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        //서킷 브레이커 생성
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        //서킷 브레이커 호출
        List<ResponseOrder> orderList = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());//문제 생겼을시 여기에 선언한 것으로 반환환
        userDto.setOrders(orderList);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }


    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity =  userRepository.findByEmail(email);

        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        return userDto;
    }
}
