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

import com.invariantproperties.project.student.business.InstructorFinderService;
import com.invariantproperties.project.student.business.InstructorManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/instructor")
public class InstructorResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(InstructorResource.class);
    private static final Instructor[] EMPTY_INSTRUCTOR_ARRAY = new Instructor[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private InstructorFinderService finder;

    @Resource
    private InstructorManagerService manager;

    @Resource
    private TestRunService testService;

    /**
     * Default constructor.
     */
    public InstructorResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(InstructorFinderService finder, InstructorManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testService = testService;
    }

    /**
     * Get all Instructors.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllInstructors() {

        final List<Instructor> instructors = finder.findAllInstructors();

        final List<Instructor> results = new ArrayList<Instructor>(instructors.size());
        for (Instructor instructor : instructors) {
            results.add(scrubInstructor(instructor));
        }

        final Response response = Response.ok(results.toArray(EMPTY_INSTRUCTOR_ARRAY)).build();

        return response;
    }

    /**
     * Create a Instructor.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createInstructor(NameAndEmailAddressRTO req) {

        final String name = req.getName();
        final String email = req.getEmailAddress();

        Response response = null;

        Instructor instructor = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                instructor = manager.createInstructorForTesting(name, email, testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            instructor = manager.createInstructor(name, email);
        }

        if (instructor == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(instructor.getUuid())).entity(scrubInstructor(instructor)).build();
        }

        return response;
    }

    /**
     * Get a specific Instructor.
     * 
     * @param uuid
     * @return
     */
    @Path("/{instructorId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getInstructor(@PathParam("instructorId") String id) {

        // 'object not found' handled by AOP
        Instructor instructor = finder.findInstructorByUuid(id);
        final Response response = Response.ok(scrubInstructor(instructor)).build();

        return response;
    }

    /**
     * Update a Instructor.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{instructorId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateInstructor(@PathParam("instructorId") String id, NameAndEmailAddressRTO req) {

        final String name = req.getName();
        final String email = req.getEmailAddress();

        // 'object not found' handled by AOP
        final Instructor instructor = finder.findInstructorByUuid(id);
        final Instructor updatedInstructor = manager.updateInstructor(instructor, name, email);
        final Response response = Response.ok(scrubInstructor(updatedInstructor)).build();

        return response;
    }

    /**
     * Delete a Instructor.
     * 
     * @param id
     * @return
     */
    @Path("/{instructorId}")
    @DELETE
    public Response deleteInstructor(@PathParam("instructorId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteInstructor(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("instructor not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
