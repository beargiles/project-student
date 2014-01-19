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

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.business.ClassroomManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/classroom")
public class ClassroomResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(ClassroomResource.class);
    private static final Classroom[] EMPTY_CLASSROOM_ARRAY = new Classroom[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private ClassroomFinderService finder;

    @Resource
    private ClassroomManagerService manager;

    @Resource
    private TestRunService testRunService;

    /**
     * Default constructor.
     */
    public ClassroomResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(ClassroomFinderService finder, ClassroomManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testRunService = testService;
    }

    /**
     * Get all Classrooms.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllClassrooms() {
        final List<Classroom> classrooms = finder.findAllClassrooms();

        final List<Classroom> results = new ArrayList<Classroom>(classrooms.size());
        for (Classroom classroom : classrooms) {
            results.add(scrubClassroom(classroom));
        }

        final Response response = Response.ok(results.toArray(EMPTY_CLASSROOM_ARRAY)).build();

        return response;
    }

    /**
     * Create a Classroom.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createClassroom(NameRTO req) {
        final String name = req.getName();

        Response response = null;

        Classroom classroom = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testRunService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                classroom = manager.createClassroomForTesting(name, testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            classroom = manager.createClassroom(name);
        }

        if (classroom == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(classroom.getUuid())).entity(scrubClassroom(classroom)).build();
        }

        return response;
    }

    /**
     * Get a specific Classroom.
     * 
     * @param uuid
     * @return
     */
    @Path("/{classroomId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getClassroom(@PathParam("classroomId") String id) {

        // 'object not found' handled by AOP
        final Classroom classroom = finder.findClassroomByUuid(id);
        final Response response = Response.ok(scrubClassroom(classroom)).build();

        return response;
    }

    /**
     * Update a Classroom.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{classroomId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateClassroom(@PathParam("classroomId") String id, NameRTO req) {
        final String name = req.getName();

        // 'object not found' handled by AOP
        final Classroom classroom = finder.findClassroomByUuid(id);
        final Classroom updatedClassroom = manager.updateClassroom(classroom, name);
        final Response response = Response.ok(scrubClassroom(updatedClassroom)).build();

        return response;
    }

    /**
     * Delete a Classroom.
     * 
     * @param id
     * @return
     */
    @Path("/{classroomId}")
    @DELETE
    public Response deleteClassroom(@PathParam("classroomId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteClassroom(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("classroom not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
