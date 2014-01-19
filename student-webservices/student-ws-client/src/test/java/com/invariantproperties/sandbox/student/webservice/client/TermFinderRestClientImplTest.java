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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.webservice.client.impl.TermFinderRestClientImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for TermFinderRestClientImpl. Remember that we want to test the
 * behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermFinderRestClientImplTest {
    private static final String UUID = "uuid";

    @Test
    public void testGetAllTermsEmpty() {
        TermFinderRestClient client = new FinderTermMock(200, new Term[0]);
        List<Term> results = client.findAllTerms();
        assertEquals(0, results.size());
    }

    @Test
    public void testGetAllTermsNonEmpty() {
        Term term = new Term();
        term.setUuid(UUID);
        TermFinderRestClient client = new FinderTermMock(200, new Term[] { term });
        List<Term> results = client.findAllTerms();
        assertEquals(1, results.size());
    }

    @Test(expected = RestClientFailureException.class)
    public void testGetAllTermsError() {
        TermFinderRestClient client = new FinderTermMock(500, null);
        client.findAllTerms();
    }

    @Test
    public void testGetTerm() {
        Term expected = new Term();
        expected.setUuid(UUID);
        TermFinderRestClient client = new FinderTermMock(200, expected);
        Term actual = client.findTermByUuid(expected.getUuid());
        assertEquals(expected.getUuid(), actual.getUuid());
        // assertEquals(TermRestClientMock.RESOURCE + term.getUuid(),
        // actual.getSelf());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetTermMissing() {
        TermFinderRestClient client = new FinderTermMock(404, null);
        client.findTermByUuid(UUID);
    }

    @Test(expected = RestClientFailureException.class)
    public void testGetTermError() {
        TermFinderRestClient client = new FinderTermMock(500, null);
        client.findTermByUuid(UUID);
    }
}

/**
 * TermFinderRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class FinderTermMock extends TermFinderRestClientImpl {
    static final String RESOURCE = "test://rest/term/";
    private Client client;
    private WebResource webResource;
    private WebResource.Builder webResourceBuilder;
    private ClientResponse response;
    private final int status;
    private final Object results;

    FinderTermMock(int status, Object results) {
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
