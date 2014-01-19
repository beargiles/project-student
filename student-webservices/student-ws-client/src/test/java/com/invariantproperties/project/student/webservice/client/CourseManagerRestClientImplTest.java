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

import static com.invariantproperties.project.student.matcher.CourseEquality.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.webservice.client.impl.CourseManagerRestClientImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for CourseManagerRestClientImpl. Remember that we want to test the
 * behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseManagerRestClientImplTest {
    private static final String UUID = "uuid";
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String SUMMARY = "summary";
    private static final String DESCRIPTION = "description";
    private static final Integer HOURS = 1;

    @Test
    public void testCreateCourse() {
        Course expected = new Course();
        expected.setCode(CODE);
        expected.setName(NAME);
        expected.setSummary(SUMMARY);
        expected.setDescription(DESCRIPTION);
        expected.setCreditHours(HOURS);
        CourseManagerRestClient client = new ManagerCourseMock(Response.Status.CREATED.getStatusCode(), expected);
        Course actual = client.createCourse(CODE, NAME, SUMMARY, DESCRIPTION, HOURS);
        assertThat(expected, equalTo(actual));
    }

    @Test(expected = RestClientFailureException.class)
    public void testCreateCourseError() {
        CourseManagerRestClient client = new ManagerCourseMock(500, null);
        client.createCourse(CODE, NAME, SUMMARY, DESCRIPTION, HOURS);
    }

    @Test
    public void testUpdateCourse() {
        Course expected = new Course();
        expected.setUuid(UUID);
        expected.setCode(CODE);
        expected.setName(NAME);
        expected.setSummary(SUMMARY);
        expected.setDescription(DESCRIPTION);
        expected.setCreditHours(HOURS);
        CourseManagerRestClient client = new ManagerCourseMock(200, expected);
        Course actual = client.updateCourse(expected.getUuid(), NAME, SUMMARY, DESCRIPTION, HOURS);
        assertThat(expected, equalTo(actual));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCourseMissing() {
        CourseManagerRestClient client = new ManagerCourseMock(404, null);
        client.updateCourse(UUID, NAME, SUMMARY, DESCRIPTION, HOURS);
    }

    @Test(expected = RestClientFailureException.class)
    public void testUpdateCourseError() {
        CourseManagerRestClient client = new ManagerCourseMock(500, null);
        client.updateCourse(UUID, NAME, SUMMARY, DESCRIPTION, HOURS);
    }

    @Test
    public void testDeleteCourse() {
        Course course = new Course();
        course.setUuid(UUID);
        CourseManagerRestClient client = new ManagerCourseMock(204, null);
        client.deleteCourse(course.getUuid(), 1);
    }

    @Test
    public void testDeleteCourseMissing() {
        CourseManagerRestClient client = new ManagerCourseMock(204, null);
        client.deleteCourse(UUID, 1);
    }

    @Test(expected = RestClientFailureException.class)
    public void testDeleteCourseError() {
        CourseManagerRestClient client = new ManagerCourseMock(500, null);
        client.deleteCourse(UUID, 1);
    }
}

/**
 * CourseManagerRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class ManagerCourseMock extends CourseManagerRestClientImpl {
    static final String RESOURCE = "test://rest/course/";
    private Client client;
    private WebResource webResource;
    private WebResource.Builder webResourceBuilder;
    private ClientResponse response;
    private final int status;
    private final Object results;

    ManagerCourseMock(int status, Object results) {
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
