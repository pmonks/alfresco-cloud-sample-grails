<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Network Information - ${params.id}</title>
</head>
<body>
  <g:if test="${authenticated}">
    <p>
      <g:if test="${success}">
        <br/>&nbsp;Information for Alfresco Cloud network ${params.id}:
        <ul>
          <li>Enabled? ${networkInfo.isEnabled}</li>
          <li>Home network? ${networkInfo.homeNetwork}</li>
          <li>Paid network? ${networkInfo.paidNetwork}</li>
          <li>Subscription level: ${networkInfo.subscriptionLevel}</li>
        </ul>
        <br/>&nbsp;Sites:
        <ul>
          <li>####TODO</li>
        </ul>
      </g:if>
      <g:else>
        <br/>&nbsp;Ruh roh raggy - something went horribly wrong when we tried to retrieve information for Alfresco Cloud&trade; network ${params.id}.  The error was: ${errorMessage}.
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