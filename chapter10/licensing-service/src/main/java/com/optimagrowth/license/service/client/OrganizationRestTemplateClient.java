package com.optimagrowth.license.service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContext;

@Component
public class OrganizationRestTemplateClient {
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	OrganizationRedisRepository redisRepository;

	private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

	public Organization getOrganization(String organizationId){
		logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        // 1. 先从缓存中找
        Organization organization = checkRedisCache(organizationId);

        // 1.1 如果缓存中存在，直接返回
        if (organization != null){
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        // 1.2 缓存不存在，调用缓存
		ResponseEntity<Organization> restExchange =
				restTemplate.exchange(
						"http://gateway:8072/organization/v1/organization/{organizationId}",
						HttpMethod.GET,
						null, Organization.class, organizationId);
		
		/*Save the record from cache*/
        organization = restExchange.getBody();
        // 1.3 存入缓存
        if (organization != null) {
            cacheOrganizationObject(organization);
        }

		return restExchange.getBody();
	}

	private Organization checkRedisCache(String organizationId) {
		try {
			return redisRepository.findById(organizationId).orElse(null);
		}catch (Exception ex){
            // 捕获redis异常 但不让服务失败
			logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex);
			return null;
		}
	}
	
	private void cacheOrganizationObject(Organization organization) {
        try {
        	redisRepository.save(organization);
        }catch (Exception ex){
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), ex);
        }
    }
}
