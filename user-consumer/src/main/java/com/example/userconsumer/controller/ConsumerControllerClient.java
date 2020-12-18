package com.example.userconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

//@Controller
@RestController
public class ConsumerControllerClient {

    @Autowired
//    private DiscoveryClient discoveryClient;
    private LoadBalancerClient loadBalancerClient; // for load balancer

    @GetMapping("/eureka/client")
    public void getUser() throws RestClientException, IOException {

//        String baseUrl = "http://localhost:8080/user"; // 기존 서비스 호출 url

//        List<ServiceInstance> instances = discoveryClient.getInstances("user-producer");
//        ServiceInstance serviceInstance = instances.get(0);
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-producer"); // for load balancer
        String baseUrl = serviceInstance.getUri().toString() + "/user";
        System.out.println("baseUrl: " + baseUrl);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response=null;
        try{
            response=restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getHeaders(),String.class);
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        System.out.println(response.getBody());
    }

    private static HttpEntity<?> getHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}
