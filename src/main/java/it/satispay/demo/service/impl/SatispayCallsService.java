package it.satispay.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import it.satispay.demo.service.ISatispayCallsService;
import it.satispay.demo.service.ISatispayService;

/**
 * The Class SatispayCallsService.
 */
@Service
public class SatispayCallsService implements ISatispayCallsService {

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger();

  /** The Constant SATISPAY_URL. */
  private static final String SATISPAY_URL =
      "https://staging.authservices.satispay.com/wally-services/protocol/tests/signature";

  /** The satispay service. */
  @Autowired
  private ISatispayService satispayService;

  /*
   * (non-Javadoc)
   * 
   * @see it.satispay.demo.service.ISatispayCallsService#httpGetCall()
   */
  @Override
  public String httpGetCall() {
    final HttpMethod httpMethod = HttpMethod.GET;
    return exchange(httpMethod);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.satispay.demo.service.ISatispayCallsService#httpPostCall()
   */
  @Override
  public String httpPostCall() {
    final HttpMethod httpMethod = HttpMethod.POST;
    return exchangeWithBody(httpMethod);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.satispay.demo.service.ISatispayCallsService#httpPutCall()
   */
  @Override
  public String httpPutCall() {
    final HttpMethod httpMethod = HttpMethod.PUT;
    return exchangeWithBody(httpMethod);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.satispay.demo.service.ISatispayCallsService#httpDeleteCall()
   */
  @Override
  public String httpDeleteCall() {
    final HttpMethod httpMethod = HttpMethod.DELETE;
    return exchange(httpMethod);
  }

  /**
   * Exchange.
   *
   * @param httpMethod the http method
   * @return the string
   */
  private String exchange(final HttpMethod httpMethod) {
    // Create a new HttpEntity
    final HttpEntity<String> entity = new HttpEntity<>(getHeaders(httpMethod));

    // Execute the method writing your HttpEntity to the request
    logger.debug("Rest call {} {} ", httpMethod.name(), entity.getHeaders());
    final RestTemplate restTemplate = new RestTemplate();
    return restTemplate.exchange(SATISPAY_URL, httpMethod, entity, String.class).getBody();
  }

  /**
   * Exchange with body.
   *
   * @param httpMethod the http method
   * @return the string
   */
  private String exchangeWithBody(final HttpMethod httpMethod) {
    final Map<String, Object> bodyParam = new HashMap<>();
    // Create a new HttpEntity
    final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyParam, getHeaders(httpMethod));

    // Execute the method writing your HttpEntity to the request
    logger.debug("Rest call {} {} ", httpMethod.name(), entity.getHeaders());
    final RestTemplate restTemplate = new RestTemplate();
    return restTemplate.exchange(SATISPAY_URL, httpMethod, entity, String.class).getBody();
  }

  /**
   * Gets the headers.
   *
   * @param httpMethod the http method
   * @return the headers
   */
  private HttpHeaders getHeaders(HttpMethod httpMethod) {

    final String date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());

    // Set the Signature headers
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", satispayService.getSignature(httpMethod, date));
    headers.set("Date", date);
    headers.set("Digest", satispayService.getDigest(httpMethod));
    return headers;
  }
}
