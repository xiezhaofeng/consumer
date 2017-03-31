package com.jers.account.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jers.account.consumer.UserConsumer;
import com.jers.account.entity.User;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@RestController
@RequestMapping(value="/consumer",produces="application/json; charset=utf-8")
public class UserController {

	 private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	 @Autowired
	    private DiscoveryClient discoveryClient;
		@Autowired
		private EurekaClient eurekaClient;
	@Autowired
    private UserConsumer userConsumer;
	
	@GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        User user = userConsumer.findById(id);
        LOGGER.info("获取到的用户id为 {}, User为 {}", id, user);
        return user;
    }
	@RequestMapping("/discovery")
    public String doDiscoveryService(){
        StringBuilder buf = new StringBuilder();
        List<ServiceInstance> serviceInstsces =  discoveryClient.getInstances("STORES");
        List<String> serviceIds = discoveryClient.getServices();
        if(!CollectionUtils.isEmpty(serviceIds)){
            for(String s : serviceIds){
                System.out.println("serviceId:" + s);
                List<ServiceInstance> serviceInstances =  discoveryClient.getInstances(s);
                if(!CollectionUtils.isEmpty(serviceInstances)){
                    for(ServiceInstance si:serviceInstances){
                        buf.append("["+si.getServiceId() +" host=" +si.getHost()+" port="+si.getPort()+" uri="+si.getUri()+"]");
                    }
                }else{
                    buf.append("no service.");
                }
            }
        }

        return buf.toString();
    }
}
