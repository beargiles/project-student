/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.webservice.client;

import com.invariantproperties.project.student.domain.PersistentObject;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Exception thrown when REST client received an unexpected status code.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class RestClientFailureException extends RestClientException {
    private static final long serialVersionUID = 1L;

    private final String resource;
    private final Class<? extends PersistentObject> objectClass;
    private final String uuid;
    private final int statusCode;

    /**
     * Constructor
     * 
     * @param resource
     * @param objectClass
     * @param uuid
     * @param response
     */
    public RestClientFailureException(final String resource, final Class<? extends PersistentObject> objectClass,
            final String uuid, final ClientResponse response) {
        super("rest client received error (" + response.getStatus() + "): " + resource + "[" + uuid + "]");
        this.resource = resource;
        this.objectClass = objectClass;
        this.uuid = uuid;
        this.statusCode = response.getStatus();
    }

    public String getResource() {
        return resource;
    }

    public Class<? extends PersistentObject> getObjectClass() {
        return objectClass;
    }

    /**
     * Get UUID, "<none>" (during listAllX()) or "(name)" (during createX())
     * 
     * @return
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get standard HTTP status code.
     * 
     * @return
     */
    public int getStatusCode() {
        return statusCode;
    }
}
