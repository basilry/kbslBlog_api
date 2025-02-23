package com.kbslblog_api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Slf4j
@Component
public class WebClientConfig {

    /**
     * Creates and configures a {@link WebClient} bean for making reactive HTTP requests.
     *
     * <p>This method builds a customized {@link WebClient} with an underlying {@link HttpClient} that:
     * <ul>
     *   <li>Uses a connection provider ("ApiConnections") with a 30-second maximum idle time and background eviction.</li>
     *   <li>Sets a connection timeout of 10 seconds and a response timeout of 10 seconds.</li>
     *   <li>Adds read and write timeout handlers, each set to 10 seconds.</li>
     *   <li>Enables wiretap for HTTP traffic debugging.</li>
     * </ul>
     * The resulting {@link WebClient} is further configured with default headers for JSON content.
     * </p>
     *
     * @return a fully configured {@link WebClient} instance
     */
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create(
                        ConnectionProvider.builder("ApiConnections")
                                .maxIdleTime(Duration.ofSeconds(30))
                                .evictInBackground(Duration.ofSeconds(30))
                                .build()
                )
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 10)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10))
                                .addHandlerLast(new WriteTimeoutHandler(10))
                );

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

        return WebClient.builder()
                .clientConnector(connector)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }
}