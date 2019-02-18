package it.satispay.demo.service;

import org.springframework.http.HttpMethod;

/**
 * The Interface ISatispayService.
 */
public interface ISatispayService {

  /**
   * Gets the signature.
   *
   * @param httpMethod the http method
   * @param date the date
   * @return the signature
   */
  String getSignature(HttpMethod httpMethod, String date);

  /**
   * Gets the digest.
   *
   * @param httpMethod the http method
   * @return the digest
   */
  String getDigest(HttpMethod httpMethod);


}
