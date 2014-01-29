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
 * specific language governing pestRunissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.webservice.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for TestRunManagerRestClientImpl. Remember that we want to test
 * the behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TestRunManagerRestClientImplTest {
    private static final String UUID = "uuid";

    @Test
    public void testCreateTestRun() {
        TestRun expected = new TestRun();
        expected.setUuid(UUID);
        TestRunManagerRestClient client = new ManagerTestRunMock(Response.Status.CREATED.getStatusCode(), expected);
        TestRun actual = client.createTestRun();
        assertNotNull(actual.getUuid());
        assertEquals(expected.getName(), actual.getName());
        // assertEquals(TestRunRestClientMock.RESOURCE + results.getUuid(),
        // actual.getSelf());
    }

    @Test(expected = RestClientFailureException.class)
    public void testCreateTestRunError() {
        TestRunManagerRestClient client = new ManagerTestRunMock(500, null);
        client.createTestRun();
    }

    @Test
    public void testDeleteTestRun() {
        TestRun testRun = new TestRun();
        testRun.setUuid(UUID);
        TestRunManagerRestClient client = new ManagerTestRunMock(204, null);
        client.deleteTestRun(testRun.getUuid(), 1);
    }

    @Test
    public void testDeleteTestRunMissing() {
        TestRunManagerRestClient client = new ManagerTestRunMock(204, null);
        client.deleteTestRun(UUID, 1);
    }

    @Test(expected = RestClientFailureException.class)
    public void testDeleteTestRunError() {
        TestRunManagerRestClient client = new ManagerTestRunMock(500, null);
        client.deleteTestRun(UUID, 1);
    }
}

/**
 * TestRunManagerRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class ManagerTestRunMock extends TestRunManagerRestClientImpl {
    static final String RESOURCE = "test://rest/testRun/";
    private Client client;
    private WebResource webResource;
    private WebResource.Builder webResourceBuilder;
    private ClientResponse response;
    private final int status;
    private final Object results;

    ManagerTestRunMock(int status, Object results) {
        super(RESOURCE);
        this.status = status;
        this.results = results;
    }

    /**
     * Override createClient() so it returns mocked object. These expectations
     * will handle basic CRUD operations, more advanced functionality will
     * require inspecting JSON payload of POST call.
     */
    @SuppressWarnings("unchecked")
    @Override
    Client createClient() {
        client = Mockito.mock(Client.class);
        webResource = Mockito.mock(WebResource.class);
        webResourceBuilder = Mockito.mock(WebResource.Builder.class);
        response = Mockito.mock(ClientResponse.class);
        when(client.resource(any(String.class))).thenReturn(webResource);
        when(webResource.accept(any(String.class))).thenReturn(webResourceBuilder);
        when(webResource.type(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.accept(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.type(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.get(eq(ClientResponse.class))).thenReturn(response);
        when(webResourceBuilder.post(eq(ClientResponse.class), any(String.class))).thenReturn(response);
        when(webResourceBuilder.put(eq(ClientResponse.class), any(String.class))).thenReturn(response);
        when(webResourceBuilder.delete(eq(ClientResponse.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(status);
        when(response.getEntity(any(Class.class))).thenReturn(results);
        return client;
    }
}
