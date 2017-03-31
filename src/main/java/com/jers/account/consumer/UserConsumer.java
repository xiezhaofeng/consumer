package com.jers.account.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jers.account.entity.User;

@Service
public class UserConsumer {
	@Autowired
    private RestTemplate restTemplate;

    public User findById(Long id) {
        return restTemplate.getForObject("http://MICROSERVICE-PROVIDER-USER/user/" + id, User.class);
    }
}
