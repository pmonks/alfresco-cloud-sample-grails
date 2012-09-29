<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>List Networks</title>
  <script type="text/javascript">
    function popupOAuth()
    {
        var popupWidth  = 320
        var popupHeight = 480
        var left        = (screen.width / 2) - (popupWidth / 2);
        var top         = (screen.height / 2) - (popupHeight / 2);
        var targetWin   = window.open("${authorisationURL}",
                                      "Alfresco Cloud™ Authorization",
                                      "toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,dependent=yes,width=" + popupWidth + ",height=" + popupHeight + ",top=" + top + ",left=" + left);
    }
  </script>
</head>
<body>
  <g:if test="${authenticated}">
    <p>
      Your Alfresco Cloud&trade; networks:
      <ul>
        <g:each in="${networks}">
          <li><g:link action="network" id="${it.id}">${it.id}</g:link></li>
        </g:each>
      </ul>
    </p>
    <p>
      <br/>&nbsp;Click <g:link controller="oauth" action="resetSession">here</g:link> if you'd like to invalidate your access token and start again.
    </p>
  </g:if>
  <g:else>
    <p>
      <br/>&nbsp;Please click <a href="#" onclick="popupOAuth()">here</a> to authorise this app to use your Alfresco Cloud&trade; account.
    </p>
  </g:else>
</body>
</html>