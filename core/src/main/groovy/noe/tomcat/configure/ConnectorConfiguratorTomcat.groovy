package noe.tomcat.configure

import groovy.util.logging.Slf4j

/***
 * IMPORTANT: For usage within noe-core:tomcat.configure only
 *
 * Connectors configuration in Tomcat server.xml
 * Supported connectors: HTTP, (HTTPS), AJP
 *
 * It is user responsibility to set attributes of connectors semantically.
 *
 * It is expected that one or zero non-secure HTTP connector and one or zero secure HTTP connector
 * will be present in server.xml.
 * Limitation is because there is no possibility to specify connectors IDs.
 */
@Slf4j
class ConnectorConfiguratorTomcat {
  private final Node server

  public ConnectorConfiguratorTomcat(Node server) {
    this.server = server
  }

  /**
   * Configure not secure HTTP connector.
   * Returns updated server element, see `ConnectorConfiguratorTomcat#server`
   */
  public Node defineHttpConnector(NonSecureHttpConnectorTomcat connector) {
    int httpConnectorSize = loadHttpConnectorSize()

    if (httpConnectorSize > 1) {
      throw new IllegalStateException("Unexpected server.xml format - one secure connector expected at most")
    }
    if (httpConnectorSize == 1) {
      Node Connector = loadExistingHttpConnector()
      updateExistingConnector(Connector, new ConnectorAttributesTransformer(connector).nonSecureHttpConnector())
    } else {
      createNewConnector(new ConnectorAttributesTransformer(connector).nonSecureHttpConnector())
    }

    return server
  }

  /**
   * Configure secure HTTP connector
   * Returns updated server element, see `ConnectorConfiguratorTomcat#server`
   */
  public Node defineHttpsConnector(SecureHttpConnectorTomcat connector) {
    int httpsConnectorSize = loadHttpsConnectorSize()

    if (httpsConnectorSize > 1) {
      throw new IllegalStateException("Unexpected server.xml format - one secure connector expected at most")
    }
    if (httpsConnectorSize == 1) {
      Node Connector = loadExistingHttpsConnector()
      updateExistingConnector(Connector, new ConnectorAttributesTransformer(connector).secureHttpConnector())
    } else {
      createNewConnector(new ConnectorAttributesTransformer(connector).secureHttpConnector())
    }

    defineRedirectPorts(connector.getPort())

    return server
  }

  /**
   * Configure secure HTTP2 connector
   * Returns updated server element, see `ConnectorConfiguratorTomcat#server`
   */
  public Node defineHttp2Connector(SecureHttp2ConnectorTomcat connector) {
    int httpsConnectorSize = loadHttp2ConnectorSize()

    if (httpsConnectorSize > 1) {
      throw new IllegalStateException("Unexpected server.xml format - one secure connector expected at most")
    }
    if (httpsConnectorSize == 1) {
      Node Connector = loadExistingHttp2Connector()
      updateExistingConnector(Connector, new ConnectorAttributesTransformer(connector).secureHttp2Connector())
    } else {
      createNewConnector(new ConnectorAttributesTransformer(connector).secureHttp2Connector())
    }

//    Map<String, Object> res = new HashMap<String, Object>()
//    res.put('className', )
    upgradeProtocol(new ConnectorAttributesTransformer(connector).secureHttp2UpgradeProtocol())

    defineRedirectPorts(connector.getPort())

    return server
  }

  /**
   * Configure AJP connector
   * Returns updated server element, see `ConnectorConfiguratorTomcat#server`
   */
  public Node defineAjpConnector(AjpConnectorTomcat connector) {
    int ajpConnectorSize = loadAjpConnectorSize()

    if (ajpConnectorSize > 1) {
      throw new IllegalStateException("Unexpected server.xml format - one AJP connector expected at most")
    }
    if (ajpConnectorSize == 1) {
      Node Connector = loadExistingAjpConnector()
      updateExistingConnector(Connector, new ConnectorAttributesTransformer(connector).ajpConnector())
    } else {
      createNewConnector(new ConnectorAttributesTransformer(connector).ajpConnector())
    }

    return server
  }

  private void defineRedirectPorts(Integer port) {
    defineHttpConnector(new NonSecureHttpConnectorTomcat().setRedirectPort(port))
    defineAjpConnector(new AjpConnectorTomcat().setRedirectPort(port))
  }

  private Node loadExistingHttpConnector() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttpProtocol(connector) && !isSecured(connector) }
    if (connectors.size() == 1) {
      return connectors.first()
    } else if (connectors.size() < 1) {
      return null
    } else {
      throw new IllegalStateException("Unexpected server.xml format - one http connector expected at most")
    }
  }

  private Node loadExistingHttpsConnector() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttpProtocol(connector) && isSecured(connector) }
    if (connectors.size() == 1) {
      return connectors.first()
    } else if (connectors.size() < 1) {
      return null
    } else {
      throw new IllegalStateException("Unexpected server.xml format - one http connector expected")
    }
  }

  private Node loadExistingHttp2Connector() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttp2Protocol(connector) && isSecured(connector) }
    if (connectors.size() == 1) {
      return connectors.first()
    } else if (connectors.size() < 1) {
      return null
    } else {
      throw new IllegalStateException("Unexpected server.xml format - one http connector expected")
    }
  }

  private Node loadExistingAjpConnector() {
    def connectors = server.Service.Connector.findAll { connector -> hasAjpProtocol(connector) }
    if (connectors.size() == 1) {
      return connectors.first()
    } else if (connectors.size() < 1) {
      return null
    } else {
      throw new IllegalStateException("Unexpected server.xml format - one http connector expected at most")
    }
  }

  private boolean isSecured(Node connector) {
    return connector.@secure.toString().toLowerCase().trim() == "true"
  }

  private void createNewConnector(Map<String, Object> attributes) {
    server.Service.each { service ->
      service.appendNode ("Connector", attributes)
    }
  }

  private void updateExistingConnector(Node Connector, Map<String, Object> attributes) {
    attributes.each { attribute ->
      Connector.@"${attribute.key}" = attribute.value
    }
  }

  private void upgradeProtocol(Map<String, Object> attributes) {
//    server.Service.each { service ->
//      service.Connector.appendNode ("UpgradeProtocol", attributes)
//    }

    server.Service.'*'.find { node ->
      if (node.name() == "Connector") {
        node.appendNode ("UpgradeProtocol", attributes)
      }
    }
  }

  /**
   * Returns count of non secure HTTP connectors
   */
  public int loadHttpConnectorSize() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttpProtocol(connector) && !isSecured(connector) }
    return connectors.size()
  }

  /**
   * Returns count of secure HTTP connectors
   */
  public int loadHttpsConnectorSize() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttpProtocol(connector) && isSecured(connector) }
    return connectors.size()
  }

  /**
   * Returns count of secure HTTP2 connectors
   */
  public int loadHttp2ConnectorSize() {
    def connectors = server.Service.Connector.findAll { connector -> hasHttp2Protocol(connector) && isSecured(connector) }
    return connectors.size()
  }

  /**
   * Returns count of AJP secure HTTP connectors
   */
  public int loadAjpConnectorSize() {
    def connectors = server.Service.Connector.findAll { connector -> hasAjpProtocol(connector) }
    return connectors.size()
  }

  private boolean hasHttpProtocol(Node connector) {
    return connector.@protocol == null || connector.@protocol.toString() in ConnectorTomcatUtils.retrieveAllHttpProtocols()
  }

  private boolean hasAjpProtocol(Node connector) {
    return connector.@protocol != null && connector.@protocol.toString() in ConnectorTomcatUtils.retrieveAllAjpProtocols()
  }

  private boolean hasHttp2Protocol(Node connector) {
//    boolean hasHttp2Protocol = false
    log.info('******** OUT OF CHILD OF CONNECTOR')
    connector.children().each { child ->
      log.info('CHILD OF CONNECTOR: ' + child.toString())
      if (child.@protocol != null && child.@protocol.toString() in ConnectorTomcatUtils.retrieveAllHttp2Protocols()) {
        return true
      }
    }
    return false
//    return connector.@protocol != null && connector.@protocol.toString() in ConnectorTomcatUtils.retrieveAllHttp2Protocols()
  }

}
