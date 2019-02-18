package it.satispay.demo.service.impl;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.jmeter.services.FileServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import it.satispay.demo.service.ISatispayService;

/**
 * The Class SatispayService.
 */
@Service
public class SatispayService implements ISatispayService {

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger();

  /** The Constant P8_BEGIN_MARKER. */
  public static final String P8_BEGIN_MARKER = "-----BEGIN PRIVATE KEY";

  /** The Constant P8_END_MARKER. */
  public static final String P8_END_MARKER = "-----END PRIVATE KEY";

  /** The server. */
  private final FileServer server = FileServer.getFileServer();

  /*
   * (non-Javadoc)
   * 
   * @see it.satispay.demo.service.ISatispayService#getSignature(java.lang.String, java.lang.String)
   */
  @Override
  public String getSignature(HttpMethod httpMethod, String date) {

    final Signature signature =
        new Signature("signature-test-66289", "rsa-sha256", null, "(request-target)", "date", "digest");

    PrivateKey privateKey = null;

    try {
      privateKey = getPrivateKey("key/client-rsa-private-key.pem");
    } catch (final IOException e) {
      logger.error("Error reading Primary key: {}", ExceptionUtils.getMessage(e));
      logger.debug(ExceptionUtils.getStackTrace(e));
    }

    final Signer signer = new Signer(privateKey, signature);

    final String method = httpMethod.name();

    final String uri = "/wally-services/protocol/tests/signature";

    final Map<String, String> headers = new HashMap<>();
    headers.put("Date", date);
    headers.put("digest", getDigest(httpMethod));
    Signature signed = null;

    try {
      signed = signer.sign(method, uri, headers);
    } catch (final IOException e) {
      logger.error("Error creating signature: {}", ExceptionUtils.getMessage(e));
      logger.debug(ExceptionUtils.getStackTrace(e));
      return "";
    }

    logger.info("Signature: {}", signed.toString());
    return signed.toString();
  }

  /**
   * Get a Private Key from the file.
   *
   * @param filename the filename
   * @return Private key
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private PrivateKey getPrivateKey(String filename) throws IOException {

    server.reserveFile(filename, "UTF-8", filename);
    final PrivateKey key = read(filename);
    server.closeFile(filename);

    return key;
  }

  /**
   * Read the PEM file and return the key.
   *
   * @param filename the filename
   * @return the private key
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private PrivateKey read(String filename) throws IOException {
    String line;
    KeyFactory factory;
    try {
      factory = KeyFactory.getInstance("RSA");
    } catch (final NoSuchAlgorithmException e) {
      throw new IOException("JCE error: " + e.getMessage());
    }

    while ((line = server.readLine(filename)) != null) {

      if (line.indexOf(P8_BEGIN_MARKER) != -1) {
        final byte[] keyBytes = readKeyMaterial(P8_END_MARKER, filename);
        final EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        try {
          return factory.generatePrivate(keySpec);
        } catch (final InvalidKeySpecException e) {
          throw new IOException("Invalid PKCS#8 PEM file: " + e.getMessage());
        }
      }

    }

    throw new IOException("Invalid PEM file: no begin marker");
  }

  /**
   * Read the PEM file and convert it into binary DER stream.
   *
   * @param endMarker the end marker
   * @param filename the filename
   * @return the byte[]
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private byte[] readKeyMaterial(String endMarker, String filename) throws IOException {
    String line = null;
    final StringBuilder buf = new StringBuilder();

    while ((line = server.readLine(filename)) != null) {
      if (line.indexOf(endMarker) != -1) {

        return new Base64().decode(buf.toString().getBytes());
      }

      buf.append(line.trim());
    }

    throw new IOException("Invalid PEM file: No end marker");
  }

  @Override
  public String getDigest(HttpMethod httpMethod) {
    String msg = "";
    if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
      msg = "{}";
    }
    byte[] digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256").digest(msg.getBytes());
    } catch (final NoSuchAlgorithmException e) {
      logger.error("Error creating diget: {}", ExceptionUtils.getMessage(e));
      logger.debug(ExceptionUtils.getStackTrace(e));
    }

    return "SHA-256=" + new String(Base64.encodeBase64(digest));

  }
}
