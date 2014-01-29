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
package com.invariantproperties.project.student.webservice.server.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/testRun")
public class TestRunResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(TestRunResource.class);
    private static final TestRun[] EMPTY_TEST_RUN_ARRAY = new TestRun[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private TestRunService service;

    /**
     * Default constructor.
     */
    public TestRunResource() {

    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    TestRunResource(TestRunService service) {
        this.service = service;
    }

    /**
     * Get all TestRuns.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllTestRuns() {
        final List<TestRun> testRuns = service.findAllTestRuns();

        final List<TestRun> results = new ArrayList<TestRun>(testRuns.size());
        for (TestRun testRun : testRuns) {
            results.add(scrubTestRun(testRun));
        }

        final Response response = Response.ok(results.toArray(EMPTY_TEST_RUN_ARRAY)).build();

        return response;
    }

    /**
     * Create a TestRun.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createTestRun(TestRunNameRTO req) {
        LOG.debug("TestRunResource: createTestRun()");

        Response response = null;

        TestRun testRun = null;
        if (req.getName() == null || req.getName().isEmpty()) {
            testRun = service.createTestRun();
        } else {
            testRun = service.createTestRun(req.getName());
        }
        if (testRun == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(testRun.getUuid())).entity(scrubTestRun(testRun)).build();
        }

        return response;
    }

    /**
     * Get a specific TestRun.
     * 
     * @param uuid
     * @return
     */
    @Path("/{testRunId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getTestRun(@PathParam("testRunId") String id) {

        // 'object not found' handled by AOP
        TestRun testRun = service.findTestRunByUuid(id);
        final Response response = Response.ok(scrubTestRun(testRun)).build();

        return response;
    }

    /**
     * Delete a TestRun.
     * 
     * @param id
     * @return
     */
    @Path("/{testRunId}")
    @DELETE
    public Response deleteTestRun(@PathParam("testRunId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            service.deleteTestRun(id);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("testrun not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
