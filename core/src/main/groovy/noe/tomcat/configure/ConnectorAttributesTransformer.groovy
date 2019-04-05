package noe.tomcat.configure

/**
 * Provides data for connectors specified in Tomcat server.xml.
 */
class ConnectorAttributesTransformer {

  private final ConnectorTomcatAbstract connector


  ConnectorAttributesTransformer(ConnectorTomcatAbstract connector) {
    this.connector = connector
  }

  /**
   * Provides attributes for non secure HTTP connector.
   * Returns map of key:value corresponding with attributes in Tomcat server.xml.
   */
  Map<String, Object> nonSecureHttpConnector() {
    return new CommonConnectorTransformer(connector).transform()
  }

  /**
   * Provides attributes for secure HTTP connector.
   * Returns map of key:value corresponding with attributes in Tomcat server.xml.
   */
  Map<String, Object> secureHttpConnector() {
    return new SecureHttpTransformer(connector).transform()
  }

  /**
   * Provides attributes for secure HTTP connector.
   * Returns map of key:value corresponding with attributes in Tomcat server.xml.
   */
  Map<String, Object> secureHttp2Connector() {
    return new SecureHttp2Transformer(connector).transform()
  }

  /**
   * Provides attributes for secure HTTP connector.
   * Returns map of key:value corresponding with attributes in Tomcat server.xml.
   */
  Map<String, Object> secureHttp2UpgradeProtocol() {
    return new SecureHttp2Transformer(connector).transformUpgradeProtocol()
  }

  /**
   * Provides attributes for AJP connector.
   * Returns map of key:value corresponding with attributes in Tomcat server.xml.
   */
  Map<String, Object> ajpConnector() {
    return new CommonConnectorTransformer(connector).transform()
  }

  /**
   * Provides map of attributes and valuse corresponding connectors elements in Tomcat server.xml
   * Use for all connectors but `SecureHttpConnectorTomcat`, for it use `SecureHttpTransformer`.
   */
  private static class CommonConnectorTransformer {
    ConnectorTomcatAbstract connector

    CommonConnectorTransformer(ConnectorTomcatAbstract connector) {
      this.connector = connector
    }

    Map<String, Object> transform() {
      Map<String, Object> res = [:]

      if (connector.getPort() != null && connector.getPort() > 0) {
        res.put('port', connector.getPort())
      }
      if (connector.getProtocol() != null && !connector.getProtocol().isEmpty()) {
        res.put('protocol', connector.getProtocol())
      }
      if (connector.getSecure() != null) {
        res.put('secure', connector.getSecure())
      }
      if (connector.getScheme() != null && !connector.getScheme().isEmpty()) {
        res.put('scheme', connector.getScheme())
      }

      if (connector.getMaxThreads() != null && connector.getMaxThreads() > 0) {
        res.put('maxThreads', connector.getMaxThreads())
      }
      if (connector.getAddress() != null && !connector.getAddress().isEmpty()) {
        res.put('address', connector.getAddress())
      }
      if (connector.getConnectionTimeout() != null && connector.getConnectionTimeout() > 0) {
        res.put('connectionTimeout', connector.getConnectionTimeout())
      }
      if (connector.getRedirectPort() != null && connector.getRedirectPort() > 0) {
        res.put('redirectPort', connector.getRedirectPort())
      }

      return res
    }
  }

  private static class SecureHttpTransformer {
    SecureHttpConnectorTomcatAbstract connector

    SecureHttpTransformer(SecureHttpConnectorTomcatAbstract connector) {
      this.connector = connector
    }

    Map<String, Object> transform() {
      Map<String, Object> res = new CommonConnectorTransformer(connector).transform()

      if (connector.getSslEnabled() != null) {
        res.put('SSLEnabled', connector.getSslEnabled())
      }

      // SSL BIO and NIO
      if (connector.getSslProtocol() != null && !connector.getSslProtocol().isEmpty()) {
        res.put('sslProtocol', connector.getSslProtocol())
      }
      if (connector.getKeystoreFile() != null && !connector.getKeystoreFile().isEmpty()) {
        res.put('keystoreFile', connector.getKeystoreFile())
      }
      if (connector.getKeystorePass() != null && !connector.getKeystorePass().isEmpty()) {
        res.put('keystorePass', connector.getKeystorePass())
      }
      if (connector.getClientAuth() != null) {
        res.put('clientAuth', connector.getClientAuth())
      }

      // SSL APR
      if (connector.getSslCertificateFile() != null && !connector.getSslCertificateFile().isEmpty()) {
        res.put('SSLCertificateFile', connector.getSslCertificateFile())
      }
      if (connector.getSslCertificateKeyFile() != null && !connector.getSslCertificateKeyFile().isEmpty()) {
        res.put('SSLCertificateKeyFile', connector.getSslCertificateKeyFile())
      }
      if (connector.getSslPassword() != null && !connector.getSslPassword().isEmpty()) {
        res.put('SSLPassword', connector.getSslPassword())
      }

      return res
    }
  }

  private static class SecureHttp2Transformer extends SecureHttpTransformer {
    SecureHttp2ConnectorTomcat connector1

    SecureHttp2Transformer(SecureHttp2ConnectorTomcat connector) {
      super(connector)
      this.connector1 = connector
    }

    Map<String, Object> transformUpgradeProtocol() {
      Map<String, Object> res = [:]

      if (connector.getProtocol() != null && !connector.getProtocol().isEmpty()) {
        res.put('className', connector.getUpgradeProtocol())
      }

      return res
    }
  }

}
