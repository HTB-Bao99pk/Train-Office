package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.DiscountPolicy;

import java.util.List;
import java.util.Optional;

public interface DiscountPolicyService {

    List<DiscountPolicy> getAllPolicies();

    List<DiscountPolicy> getActivePolicies();

    Optional<DiscountPolicy> getPolicyById(Long id);

    Optional<DiscountPolicy> getActivePolicyByCode(String policyCode);

    DiscountPolicy savePolicy(DiscountPolicy policy);

    void deletePolicy(Long id);

    boolean matchesAge(DiscountPolicy policy, int age);
}