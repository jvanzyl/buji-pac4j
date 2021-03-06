/*
 * Licensed to the bujiio organization of the Shiro project under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.buji.pac4j.filter;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.engine.ShiroCallbackLogic;

/**
 * <p>This filter finishes the login process for an indirect client, based on the {@link #callbackLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods:</p>
 * <ul>
 *     <li><code>{@link #setConfig(Config)}</code> (security configuration)</li>
 *     <li><code>{@link #setDefaultUrl(String)}</code> (default url after login if none was requested)</li>
 *     <li><code>{@link #setSaveInSession(Boolean)}</code> (whether the profile should be saved into the session)</li>
 *     <li><code>{@link #setMultiProfile(Boolean)}</code> (whether multiple profiles should be kept)</li>
 *     <li><code>{@link #setDefaultClient(String)}</code> (the default client if none is provided on the URL)</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CallbackFilter implements Filter {

    private CallbackLogic<Object, JEEContext> callbackLogic;

    private Config config;

    private String defaultUrl;

    private Boolean saveInSession;

    private Boolean multiProfile;

    private String defaultClient;

    private HttpActionAdapter<Object, JEEContext> httpActionAdapter;

    public CallbackFilter() {
        callbackLogic = new ShiroCallbackLogic<>();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        assertNotNull("callbackLogic", callbackLogic);
        assertNotNull("config", config);

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final SessionStore<JEEContext> sessionStore = config.getSessionStore();
        final JEEContext context = new JEEContext(request, response, sessionStore != null ? sessionStore : ShiroSessionStore.INSTANCE);

        callbackLogic.perform(context, config, JEEHttpActionAdapter.findBestAdapter(httpActionAdapter, config),
                this.defaultUrl, this.saveInSession, this.multiProfile, false, this.defaultClient);
    }

    @Override
    public void destroy() {}

    public CallbackLogic<Object, JEEContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic<Object, JEEContext> callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public Boolean getSaveInSession() {
        return saveInSession;
    }

    public void setSaveInSession(final Boolean saveInSession) {
        this.saveInSession = saveInSession;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(final String defaultClient) {
        this.defaultClient = defaultClient;
    }

    public HttpActionAdapter<Object, JEEContext> getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(final HttpActionAdapter<Object, JEEContext> httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }
}
