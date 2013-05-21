/*
 * Copyright 2012 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.samples.pubsub;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;

import java.io.IOException;

/**
 * Simple PubSub resource that demonstrate many functionality supported by
 * Atmosphere JQuery Plugin and AtmosphereHandler extension.  You can compare that implementation
 * with the MeteorPubSub and the JQueryPubsub sample
 * <p/>
 * This sample support out of the box WebSocket, Long-Polling, SSE and Streaming. You can also use the
 * @ManagedService annotation for more out of the box supported feature.
 *
 * @author Jeanfrancois Arcand
 */
@Singleton
@AtmosphereHandlerService(path = "/", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        TrackMessageSizeInterceptor.class,
        BroadcastOnPostAtmosphereInterceptor.class,
        SuspendTrackerInterceptor.class})
public class AtmosphereHandlerPubSub implements AtmosphereHandler {

    public void onRequest(AtmosphereResource r) throws IOException {
        r.setBroadcaster(lookupBroadcaster(r.getRequest().getPathInfo()));
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        if (event.isSuspended()) {
            String message = event.getMessage() == null ? null : event.getMessage().toString();
            if (message != null && message.indexOf("message") != -1) {
                event.getResource().write(message.substring("message=".length()));
            } else {
                event.getResource().write("=error=");
            }
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Retrieve the {@link Broadcaster} based on the request's path info.
     *
     * @param pathInfo
     * @return the {@link Broadcaster} based on the request's path info.
     */
    Broadcaster lookupBroadcaster(String pathInfo) {
        String[] decodedPath = pathInfo.split("/");
        Broadcaster b = BroadcasterFactory.getDefault().lookup(decodedPath[decodedPath.length - 1], true);
        return b;
    }

}
