package it.satispay.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import it.satispay.demo.service.ISatispayCallsService;

/**
 * The Class MainController.
 */
@RestController
public class MainController {

  /** The satispay calls service. */
  @Autowired
  private ISatispayCallsService satispayCallsService;

  /**
   * Call with GET method.
   *
   * @return the string
   */
  @GetMapping("/get")
  public String get() {
    return satispayCallsService.httpGetCall();
  }

  /**
   * Call with POST method.
   *
   * @return the string
   */
  @GetMapping("/post")
  public String post() {
    return satispayCallsService.httpPostCall();
  }

  /**
   * Call with PUT method.
   *
   * @return the string
   */
  @GetMapping("/put")
  public String put() {
    return satispayCallsService.httpPutCall();
  }

  /**
   * Call with DELETE method.
   *
   * @return the string
   */
  @GetMapping("/delete")
  public String delete() {
    return satispayCallsService.httpDeleteCall();
  }
}
