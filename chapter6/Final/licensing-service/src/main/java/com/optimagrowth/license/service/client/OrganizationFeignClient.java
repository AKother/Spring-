package com.optimagrowth.license.service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.optimagrowth.license.model.Organization;

//向Feign标识服务
@FeignClient("organization-service")
public interface OrganizationFeignClient {
    //定义端点的路径和动作
    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organization/{organizationId}",
            consumes="application/json")
    //定义传入端点的参数
    Organization getOrganization(@PathVariable("organizationId") String organizationId);
}
