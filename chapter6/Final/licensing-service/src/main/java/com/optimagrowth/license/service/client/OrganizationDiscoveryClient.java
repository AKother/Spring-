package com.optimagrowth.license.service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;

@Component
public class OrganizationDiscoveryClient {

    //将Discovery Client注入这个类
    @Autowired
    private DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        // 此处实例化RestTemplate，将不会调用LoadBalancer
        RestTemplate restTemplate = new RestTemplate();

        //获取组织服务的所有实例的列表
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if (instances.size()==0) return null;
        //检索服务端点
        String serviceUri = String.format("%s/v1/organization/%s",instances.get(0).getUri().toString(), organizationId);

        //使用标准的Spring RestTemplate类去调用服务
        ResponseEntity< Organization > restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null,
                        Organization.class,
                        organizationId);
        return restExchange.getBody();
    }
}
