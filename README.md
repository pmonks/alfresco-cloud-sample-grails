alfresco-cloud-sample-grails
============================

A sample Grails application that runs against the Alfresco Cloud.  The primary purpose of this app is to demonstrate the OAuth2 flow end-to-end.

Author
------
Peter Monks (pmonks@gmail.com)

License
-------
Apache 2.0 - see COPYING for full details.

Disclaimer
----------
This is the first Grails app I've ever written, so it's probably a steaming pile of crap from a Grails best practices perspective.  I don't care.  My primary goal was to stand up a webapp quickly (which Grails succeeded at - yay!) - learning Grails was a secondary goal.

Description
-----------
A sample Grails app that demonstrates the use of OAuth2 to connect to the Alfresco Cloud's Public APIs.  It calls read-only APIs only (the "list networks" API, specifically), so is safe for use against your Alfresco Cloud account.

Note: it does _not_ fully implement all of the OAuth2 flows - in particular the refresh flows (for both access tokens and refresh tokens) aren't implemented yet.  YMMV!

Pre-requisites
--------------
1. A 1.6 (or better) JVM installed
2. Groovy v2.0 (or better) installed - see http://groovy.codehaus.org/
3. Grails v2.1 (or better) installed - see http://grails.org/
4. A developer account setup on the Alfresco Developer Portal: https://developer.alfresco.com/

Note: there may be a Grails plugin or two required as well (I was mucking about with a few of those, and you'll see artifacts of that scattered through the source code / configuration files).  I don't believe the current version of the code is actually using any of those plugins, but I may be wrong.

Installation
------------
1. Unzip this ZIP file somewhere convenient

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
   Callback URL: http://localhost:8080/publicapitest/oauth/callback

Configuration
-------------
In your favourite text editor, make the following edits:
1. Copy ./grails-app/conf/publicapitest-config.properties.sample to ./grails-app/conf/publicapitest-config.properties
2. In ./grails-app/conf/publicapitest-config.properties:
   Set the value of the "alfresco.oauth.apikey" property to the API key of the new app in the developer portal
   Set the value of the "alfresco.oauth.secret" property to the secret of the new app in the developer portal

Starting the app
----------------
bash-3.2$ grails
<grails status messages removed for clarity>
grails> clean
<grails status messages removed for clarity>
grails> run-app
<grails status messages removed for clarity - note: you may receive one deprecation warning at this step>
grails>

Using the app
-------------
1. Navigate to http://localhost:8080/publicapitest/oauth in your browser
2. Click the "here" link
3. In the popup window, login with your Alfresco Cloud credentials (note: _not_ your Developer Portal credentials - this is an end user login, not a developer login)
4. Click "allow"
5. If the popup closes and you see a list of your Alfresco Cloud networks, the OAuth flow completed successfully

Stopping the app
----------------
grails> exit
<grails status messages removed for clarity>
grails>

Exiting grails
--------------
grails> exit
<grails status messages removed for clarity>
bash-3.2$

Some code details
-----------------
Despite all the crap that Grails generated, the custom code in this app is pretty simple.  There is a single controller containing all the logic:

    ./grails-app/controllers/publicapitest/OauthController.groovy

And two views that define the main page and the callback (which is briefly shown in the popup window, after the user authorises the app):

    ./grails-app/views/oauth/index.gsp
    ./grails-app/views/oauth/callback.gsp

Feel free to have a poke around - Peter is a complete Grails n00b (this is literally his first time using it), so this app is probably a steaming pile of horseshit from a "Grails best practices" perspective.

TODOs
-----
* Add support for the OAuth refresh flow - currently the access token will timeout after one hour, and you'll need to reset the access token and re-run the OAuth flow to continue.
* When a network is clicked, show the details about that network, along with the list of sites within it that the user has access to.  Would be nice to allow the user to drill down into the document library (using CMIS) and show the folder/file hierarchy as well.

