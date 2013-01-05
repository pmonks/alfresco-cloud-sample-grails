<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Network Information - ${params.id}</title>
</head>
<body>
  <g:if test="${authenticated}">
    <p>
      <br/>&nbsp;Information for Alfresco Cloud&trade; network <strong>${params.id}</strong>:
      <ul>
        <li>Enabled? ${networkInfo.isEnabled()}</li>
        <li>Home network? ${networkInfo.isHomeNetwork()}</li>
        <li>Paid network? ${networkInfo.isPaidNetwork()}</li>
        <li>Subscription level: ${networkInfo.subscriptionLevel}</li>
      </ul>
      <br/>&nbsp;Sites:
      <g:if test="${sites} != null">
      <ul>
        <g:each in="${sites}">
<!--          <li><g:link action="site" id="${it.id}">${it.id}</g:link></li> -->
          <li>${it.id}</li>
        </g:each>
      </ul>
      </g:if>
      <g:else>
        You don't have access to any sites in this network.
      </g:else>
    </p>
    <p>
      <br/>&nbsp;Click <g:link controller="oauth" action="resetSession">here</g:link> if you'd like to invalidate your access token and start again.
    </p>
  </g:if>
  <g:else>
    <p>
      <br/>&nbsp;Your login has expired, please <g:link action="index">return to the start</g:link> to re-authenticate.
    </p>
  </g:else>
</body>
</html>