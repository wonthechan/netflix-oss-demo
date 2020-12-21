package com.example.userconsumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
public class ConsumerControllerClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/eureka/client/{path}")
    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
    }, threadPoolProperties = @HystrixProperty(name = "coreSize", value = "100"))
    public void getUser(@PathVariable String path) throws RestClientException, IOException {

        List<ServiceInstance> instances = discoveryClient.getInstances("zuul-gateway");
        ServiceInstance serviceInstance = instances.get(0);

        String baseUrl = serviceInstance.getUri().toString() + "/api/v1/prod/" + path;
        System.out.println("baseUrl: " + baseUrl);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response=null;
        try{
            response=restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getHeaders(),String.class);
        }catch (Exception ex)
        {
            System.out.println(ex);
            throw new RuntimeException("producer emitted error while responding");
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println(response.getBody());
            return;
        }

        throw new RuntimeException("producer emitted error while responding");
    }

    private static HttpEntity<?> getHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }

    // fallback method: 기존 메소드와 반환타입 및 파라미터를 동일하게 맞춘다.
    // 서킷 브레이커가 열리지 않더라도 호출될 수 있음
    private void fallback(String path) {
        System.out.println("executed fallback method!");
    }
}
