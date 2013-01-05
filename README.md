alfresco-cloud-sample-grails
============================

A sample Grails application that runs against the Alfresco Cloud.  The primary purpose of this app is to demonstrate the OAuth2 flow end-to-end, using the spring-social-alfresco library.

Author
------
Peter Monks (pmonks@gmail.com)

License
-------
Apache 2.0 - see COPYING for full details.

Disclaimer
----------
This is the first Grails app I've ever written, so it's probably a steaming pile from a Grails best practices perspective.  This doesn't concern me - my primary goal was to stand up a webapp quickly (which Grails succeeded at - yay!) - learning Grails was a secondary goal.

Description
-----------
A sample Grails app that demonstrates the use of OAuth2 to connect to the Alfresco Cloud's Public APIs.  It calls read-only APIs only (the "list networks" and "list sites" APIs, specifically), so is safe for use against your Alfresco Cloud account.

Note: it does _not_ fully implement all of the OAuth2 flows - in particular the access token refresh flow isn't implemented yet.  YMMV!

Pre-requisites
--------------
1. A 1.6 (or better) JVM installed
2. Groovy v2.0 (or better) installed - see http://groovy.codehaus.org/
3. Grails v2.2 (or better) installed - see http://grails.org/
4. A developer account setup on the Alfresco Developer Portal: https://developer.alfresco.com/

Note: there may be a Grails plugin or two required as well (I was mucking about with a few of those, and you'll see artifacts of that scattered through the source code / configuration files).  I don't believe the current version of the code is actually using any of those plugins, but I may be wrong.

Registering a new app
---------------------
In the Alfresco Developer Portal, register a new application with the following details:

1. Application Information Tab:
   Application Name: alfresco-cloud-sample-grails
   Platform: Java
   Description: I <3 mullets!
2. API Management Tab:
   Current APIs: Alfresco Public API
3. Auth Tab:
   Callback URL: http://localhost:8080/alfresco-cloud-sample-grails/oauth/callback

Configuration
-------------
In your favourite text editor, make the following edits:

1. Copy ./alfresco-cloud-sample-grails-config.properties.sample to ./alfresco-cloud-sample-grails-config.properties
2. In ./alfresco-cloud-sample-grails-config.properties:
   Set the value of the "alfresco.oauth.apikey" property to the API key of the new app in the developer portal
   Set the value of the "alfresco.oauth.secret" property to the secret of the new app in the developer portal

Starting the app
----------------
Change into the appropriate directory (direct or spring-social-alfresco)

    bash-3.2$ grails
    [grails status messages removed for clarity]
    grails> clean
    [grails status messages removed for clarity]
    grails> run-app
    [grails status messages removed for clarity - note: you may receive one deprecation warning at this step]
    grails>

Using the app
-------------
1. Navigate to http://localhost:8080/alfresco-cloud-sample-grails/oauth/ in your browser
2. Click the "here" link
3. In the popup window, login with your Alfresco Cloud credentials (note: _not_ your Developer Portal credentials - this is an end user login, not a developer login)
4. Click "allow"
5. If the popup closes and you see a list of your Alfresco Cloud networks, the OAuth flow completed successfully

Stopping the app
----------------
    grails> stop-app
    [grails status messages removed for clarity]
    grails>

Exiting grails
--------------
    grails> exit
    [grails status messages removed for clarity]
    bash-3.2$

Some code details
-----------------
Despite all the stuff that Grails generated, the custom code in this app is pretty simple.  There is a single controller containing all the logic:

    ./grails-app/controllers/publicapitest/OauthController.groovy

And two views that define the main page and the callback (which is briefly shown in the popup window, after the user authorises the app):

    ./grails-app/views/oauth/index.gsp
    ./grails-app/views/oauth/callback.gsp

Feel free to suggest improvements!

TODOs
-----
* Add support for the OAuth refresh flow - currently the access token will timeout after one hour, and you'll need to reset the access token and re-run the OAuth flow to continue.
* Properly support paging (currently only the first page is displayed).
* Continue linking to and displaying the site hierarchy, document library folder hierarchy, and files within the folders.  Note: this will also demonstrate how to use the CMIS APIs within a client app.

