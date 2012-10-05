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

import org.springframework.social.connect.*
import org.springframework.social.oauth2.*
import org.springframework.social.alfresco.connect.*
import org.springframework.social.alfresco.api.*


class OauthController {
    // Inject link generator
    LinkGenerator grailsLinkGenerator

    private ConnectionFactory connectionFactory;

    public OauthController()
    {
      connectionFactory = new AlfrescoConnectionFactory(grailsApplication.config.alfresco.oauth.apikey,
                                                        grailsApplication.config.alfresco.oauth.secret);
    }

    def index() {
      if (session["accessGrant"]) {
        // Authenticated, display networks
        def alfrescoApi = alfrescoApi(session["accessGrant"])
        def networks    = alfrescoApi.networks.entries

        [
          authenticated : true,
          networks      : networks
        ]
      }
      else {
        // We're not yet authenticated so do the dance
        def parameters        = new OAuth2Parameters();
        parameters.setRedirectUri(grailsLinkGenerator.link(action : "callback", absolute : true));
        parameters.setScope(Alfresco.DEFAULT_SCOPE);

        String authUrl = connectionFactory.getOAuthOperations().buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, parameters);
        [
          authenticated    : false,
          authorisationURL : authUrl
        ]
      }
    }

    def callback() {
      def authCode = params.code

      if (!authCode) {
        throw new Exception("authCode not provided to callback")
      }
      else {
        def accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(authCode,
                                                                                   grailsLinkGenerator.link(action : "callback", absolute : true),
                                                                                   null);
        session["accessGrant"] = accessGrant
      }
    }

    def resetSession() {
      session.invalidate()
      redirect(action : "index")
    }

    def network() {
      def networkId   = params.id
      def alfrescoApi = alfrescoApi(session["accessGrant"])

      if (alfrescoApi)
      {
        def networkInfo = alfrescoApi.getNetwork(networkId)
        def sites       = alfrescoApi.getSites(networkId).entries

        [
          authenticated : true,
          networkInfo   : networkInfo,
          sites         : sites
        ]
      }
      else
      {
        resetSession()
      }
    }


    private def alfrescoApi(AccessGrant accessGrant)
    {
      accessGrant ? connectionFactory.createConnection(accessGrant).getApi() : null
    }

}
