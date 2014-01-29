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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.invariantproperties.project.student.business.FinderService;
import com.invariantproperties.project.student.domain.PersistentObject;
import com.invariantproperties.project.student.domain.TestRun;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Abstract implementation of RestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class AbstractFinderRestClientImpl<T extends PersistentObject> implements FinderRestClient<T>, FinderService<T> {
    private final String resource;
    private final Class<T> objectClass;
    private final Class<T[]> objectArrayClass;

    /**
     * Constructor.
     * 
     * @param resource
     */
    public AbstractFinderRestClientImpl(final String resource, final Class<T> objectClass,
            final Class<T[]> objectArrayClass) {
        this.resource = resource;
        this.objectClass = objectClass;
        this.objectArrayClass = objectArrayClass;
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
     * Count number of objects
     */
    @Override
    public long count() {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource);
            final ClientResponse response = webResource.queryParam("countOnly", "true")
                    .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                // final Long entity = response.getEntity(Long.class);
                Long entity = 0L;
                return entity;
            } else {
                throw new RestClientFailureException(resource, objectClass, null, response);
            }
        } finally {
            client.destroy();
        }
    }

    /**
     * Count number of objects with specified test uuid
     */
    @Override
    public long countByTestRun(TestRun testRun) {
        if (testRun == null || testRun.getUuid() == null) {
            return count();
        }

        final Client client = createClient();
        final MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("countOnly", "true");
        params.add("testUuid", testRun.getUuid());

        try {
            final WebResource webResource = client.resource(resource);
            final ClientResponse response = webResource.queryParams(params).accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final Long entity = response.getEntity(Long.class);
                return entity;
            } else {
                throw new RestClientFailureException(resource, objectClass, null, response);
            }
        } finally {
            client.destroy();
        }
    }

    /**
     * List all objects. This is a risky method since there's no attempt at
     * pagination.
     */
    public T[] getAllObjects(final T[] emptyListClass) {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource);
            final ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final T[] entities = response.getEntity(objectArrayClass);
                return entities;
            } else {
                throw new RestClientFailureException(resource, objectClass, "<none>", response);
            }
        } finally {
            client.destroy();
        }
    }

    /**
     * Get a specific object.
     */
    public T getObject(String uuid) {
        final Client client = createClient();

        try {
            final WebResource webResource = client.resource(resource + uuid);
            final ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

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
}
