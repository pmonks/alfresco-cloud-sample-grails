/*
 * Copyright 2012 Alfresco Software Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is part of an unsupported extension to Alfresco.
 *
 * This file contains all of the controller logic for the Grails sample app.
 */

 package publicapitest

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.apache.http.*
import org.apache.http.client.*
import org.apache.http.protocol.*
import org.apache.http.params.*
import org.apache.http.client.params.*
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import net.sf.json.JSONException

class OauthController {
    // Inject link generator
    LinkGenerator grailsLinkGenerator

    def index() {
       if (session["accessToken"]) {
           // Call list networks API (https://api.alfresco.com/)
           def accessToken = session["accessToken"]
           def http = new HTTPBuilder("https://api.alfresco.com/")
           http.setHeaders( [ "Authorization" : "Bearer " + accessToken ])  // Header based token passing (preferred)

           http.request(GET, JSON) {
//              headers.Accept = 'application/json'
//              uri.query = [ access_token : accessToken ]    // Query string based token passing (NOT preferred)
              response.success = { response, json ->
                log.debug "####SUCCESS:\n" + responseToString(response) + "\n\tBody:\n" + String.valueOf(json)
                [ authenticated : true,
                  success : true,
                  networks : json ]
              }
              response.failure = { response, reader ->
                log.debug "####FAILURE:\n" + responseToString(response) + "\n\tBody:\n" + reader.text
                [ authenticated : true,
                  success : false,
                  errorMessage : "HTTP Status Code ${response.getStatus()} returned by Alfresco" ]
              }
           }
       }
       else {
           [
               authenticated             : false,
               alfrescoOAuthAuthorizeURI : "https://api.alfresco.com/auth/oauth/versions/2/authorize",
               clientId                  : grailsApplication.config.alfresco.oauth.apikey,
               scope                     : "public_api",
               responseType              : "code",
               callbackURL               : grailsLinkGenerator.link(action : "callback", absolute : true)
           ]
       }
    }

    def callback() {
        def authCode = params.code

        if (!authCode) {
            throw new Exception("authCode not provided to callback")
        }
        else {
            // Exchange the auth code for an access token (we have a short period of time to do this)
            def apiKey = grailsApplication.config.alfresco.oauth.apikey
            def secret = grailsApplication.config.alfresco.oauth.secret
            def http   = new HTTPBuilder("https://api.alfresco.com/")

            log.debug "About to POST https://api.alfresco.com/auth/oauth/versions/2/token\n" +
                      "\tcode : " + authCode + "\n" +
                      "\tclient_id : " + apiKey + "\n" +
                      "\tclient_secret : " + secret + "\n" +
                      "\tredirect_uri : " + grailsLinkGenerator.link(action : "callback", absolute : true) + "\n" +
                      "\tgrant_type : authorization_code"

            http.request(POST, JSON) {
              uri.path = "/auth/oauth/versions/2/token"
              requestContentType = ContentType.URLENC
              body = [ code          : authCode,
                       client_id     : apiKey,
                       client_secret : secret,
                       redirect_uri  : grailsLinkGenerator.link(action : "callback", absolute : true),
                       grant_type    : "authorization_code" ]
              response.success = { response, json ->
                session["accessToken"] = json["access_token"]   // Note: shoving the token in the session is fine for a demo app, but _NOT_ ok in a real app!
                session["refreshToken"] = json["refresh_token"]
                log.debug "####SUCCESS:\n" + responseToString(response) + "\n" + String.valueOf(json)
              }
              response.failure = { response -> log.debug "####FAILURE:\n" + responseToString(response) }
            }
        }
    }

    def resetSession() {
      session.invalidate()
      redirect(action: "index")
    }


    def network() {
      def networkId   = params.id
      def accessToken = session["accessToken"]

      if (accessToken)
      {
        callAlfrescoCloud(networkId,
                          accessToken,
                          { response, json   -> [ authenticated : true, success : true, networkInfo : json ]},
                          { response, reader -> [ authenticated : true, success : false, errorMessage : "HTTP Status Code ${response.getStatus()} returned by Alfresco" ] },
                          { exception        -> [ authenticated : true, success : false, errorMessage : (exception == null ? "[no exception provided]" : "${exception.getClass().toString()}: ${exception.getMessage()}") ] })
      }
      else
      {
        resetSession()
      }
    }


    private def callAlfrescoCloud(String apiPath, String accessToken, Closure onSuccess, Closure onFailure, Closure onException) {
      def url  = "https://api.alfresco.com/" + (apiPath == null ? "" : apiPath)
      def http = new HTTPBuilder(url)
      http.setHeaders( [ "Authorization" : "Bearer " + accessToken ])  // Header based token passing (preferred)

      log.debug "About to GET ${url} with access token ${accessToken}"

      try {
        http.request(GET, JSON) {
//          uri.query = [ access_token : accessToken ]    // Query string based token passing (NOT preferred)
          response.success = { response, json ->
            log.debug "####SUCCESS:\n" + responseToString(response) + "\n\tBody:\n" + String.valueOf(json)
            onSuccess(response, json)
          }
          response.failure = { response, reader ->
            log.debug "####FAILURE:\n" + responseToString(response) + "\n\tBody:\n" + reader.text
            onFailure(response, reader)
          }
        }
      }
      catch(Exception e) {
        onException(e)
      }

    }


    /**
     * Debugging method for obtaining the state of a response as a String.
     *
     * @param response The response to retrieve the response state from <i>(may be null)</i>.
     * @return The response state as a human-readable string value <i>(will not be null)</i>.
     */
    private String responseToString(final HttpResponseDecorator response)
    {
        StringBuffer result = new StringBuffer(128);

        if (response != null)
        {
            result.append("\n\tStatus: ");
            result.append(response.getStatus());
            result.append(" ");
            result.append(response.getStatusLine());

            result.append("\n\tHeaders: ");

            for (final Header header : response.getAllHeaders())
            {
                result.append("\n\t\t");
                result.append(header.getName());
                result.append(" : ");
                result.append(header.getValue());
            }

            if (response.getData() != null)
            {
                result.append("\n\tBody:");
                result.append("\n");
                result.append(String.valueOf(response.getData()));
            }
        }
        else
        {
            result.append("(null)");
        }

        return(result.toString());
    }

}
