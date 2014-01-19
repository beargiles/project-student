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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.invariantproperties.project.student.business.ManagerService;
import com.invariantproperties.project.student.domain.PersistentObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Abstract implementation of RestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class AbstractManagerRestClientImpl<T extends PersistentObject> implements ManagerRestClient<T>,
        ManagerService<T> {
    private final String resource;
    private final Class<T> objectClass;

    /**
     * Constructor.
     * 
     * @param resource
     */
    public AbstractManagerRestClientImpl(final String resource, final Class<T> objectClass) {
        this.resource = resource;
        this.objectClass = objectClass;
    }

    /**
     * Helper method for testing.
     * 
     * @return
     */
    Client createClient() {
        return Client.create();
    }

    /**
     * Create an object with the specified values.
     */
    public T createObject(final String json) {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource);
            final ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                final T entity = response.getEntity(objectClass);
                // entity.setSelf(resource + entity.getUuid());
                return entity;
            } else {
                throw new RestClientFailureException(resource, objectClass, "(" + json + ")", response);
            }
        } finally {
            client.destroy();
        }
    }

    /**
     * Update an object with the specified json.
     */
    public T updateObject(final String json, final String uuid) {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource + uuid);
            final ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final T entity = response.getEntity(objectClass);
                // entity.setSelf(resource + entity.getUuid());
                return entity;
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ObjectNotFoundException(resource, objectClass, uuid);
            } else {
                throw new RestClientFailureException(resource, objectClass, uuid, response);
            }
        } finally {
            client.destroy();
        }
    }

    /**
     * Delete specified object.
     */
    public void deleteObject(String uuid, Integer version) {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource + uuid);
            final ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                // do nothing
            } else if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                // do nothing
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                // do nothing - delete is idempotent
            } else {
                throw new RestClientFailureException(resource, objectClass, uuid, response);
            }
        } finally {
            client.destroy();
        }
    }
}
