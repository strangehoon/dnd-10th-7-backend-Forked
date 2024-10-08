package com.sendback.global.config.RateLimit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@Slf4j
public class RateLimitFilter implements Filter {

    private ProxyManager<String> proxyManager;
    private final int CAPACITY = 60;
    private final int GREEDY_TOKEN_REFILL_COUNT = 10;
    private final int GREEDY_TOKEN_REFILL_MINUTES = 1;

    @Autowired
    public RateLimitFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String key = httpRequest.getRemoteAddr(); // IP 기반, 필요시 커스텀
        Supplier<BucketConfiguration> bucketConfigurationSupplier = getConfigSupplier();
        Bucket bucket = proxyManager.builder().build(key, bucketConfigurationSupplier); // 기존 버킷이 존재하면 조회, 없으면 생성
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpServletResponse httpServletResponse = makeRateLimitResponse(servletResponse, probe);
        }
    }

    public Supplier<BucketConfiguration> getConfigSupplier() {
        Refill greedyTokenRefill = Refill.greedy(GREEDY_TOKEN_REFILL_COUNT, Duration.ofMinutes(GREEDY_TOKEN_REFILL_MINUTES));
        Bandwidth greedyBandwidth = Bandwidth.classic(CAPACITY, greedyTokenRefill);

        return () -> BucketConfiguration.builder()
                .addLimit(greedyBandwidth)
                .build();
    }
    // 에러 핸들링
    private HttpServletResponse makeRateLimitResponse(ServletResponse servletResponse, ConsumptionProbe probe) throws IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setContentType("text/plain");
        httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "" +
                TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        httpResponse.setStatus(429);
        httpResponse.getWriter().append("Too many requests");

        return httpResponse;
    }
}