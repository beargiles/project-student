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

import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.StudentFinderService;
import com.invariantproperties.project.student.business.StudentManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/student")
public class StudentResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(StudentResource.class);
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private StudentFinderService finder;

    @Resource
    private StudentManagerService manager;

    @Resource
    private TestRunService testService;

    /**
     * Default constructor.
     */
    public StudentResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(StudentFinderService finder, StudentManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testService = testService;
    }

    /**
     * Get all Students.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllStudents() {

        final List<Student> students = finder.findAllStudents();

        final List<Student> results = new ArrayList<Student>(students.size());
        for (Student student : students) {
            results.add(scrubStudent(student));
        }

        final Response response = Response.ok(results.toArray(EMPTY_STUDENT_ARRAY)).build();

        return response;
    }

    /**
     * Create a Student.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createStudent(NameAndEmailAddressRTO req) {

        final String name = req.getName();

        final String email = req.getEmailAddress();

        Response response = null;
        Student student = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                student = manager.createStudentForTesting(name, email, testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            student = manager.createStudent(name, email);
        }

        if (student == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(student.getUuid())).entity(scrubStudent(student)).build();
        }

        return response;
    }

    /**
     * Get a specific Student.
     * 
     * @param uuid
     * @return
     */
    @Path("/{studentId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getStudent(@PathParam("studentId") String id) {

        // 'object not found' handled by AOP
        Student student = finder.findStudentByUuid(id);
        final Response response = Response.ok(scrubStudent(student)).build();

        return response;
    }

    /**
     * Update a Student.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{studentId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateStudent(@PathParam("studentId") String id, NameAndEmailAddressRTO req) {

        final String name = req.getName();
        final String email = req.getEmailAddress();

        // 'object not found' handled by AOP
        final Student student = finder.findStudentByUuid(id);
        final Student updatedStudent = manager.updateStudent(student, name, email);
        final Response response = Response.ok(scrubStudent(updatedStudent)).build();

        return response;
    }

    /**
     * Delete a Student.
     * 
     * @param id
     * @return
     */
    @Path("/{studentId}")
    @DELETE
    public Response deleteStudent(@PathParam("studentId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteStudent(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("student not found" + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
