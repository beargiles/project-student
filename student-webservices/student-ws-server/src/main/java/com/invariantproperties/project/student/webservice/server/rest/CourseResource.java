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

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.ObjectNotFoundException;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/course")
public class CourseResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(CourseResource.class);
    private static final Course[] EMPTY_COURSE_ARRAY = new Course[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private CourseFinderService finder;

    @Resource
    private CourseManagerService manager;

    @Resource
    private TestRunService testRunService;

    /**
     * Default constructor.
     */
    public CourseResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(CourseFinderService finder, CourseManagerService manager, TestRunService testRunService) {
        this.finder = finder;
        this.manager = manager;
        this.testRunService = testRunService;
    }

    /**
     * Get all Courses.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllCourses() {
        final List<Course> courses = finder.findAllCourses();

        final List<Course> results = new ArrayList<Course>(courses.size());
        for (Course course : courses) {
            results.add(scrubCourse(course));
        }

        final Response response = Response.ok(results.toArray(EMPTY_COURSE_ARRAY)).build();

        return response;
    }

    /**
     * Create a Course.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createCourse(CourseInfoRTO req) {
        final String code = req.getCode();
        final String name = req.getName();

        Response response = null;
        Course course = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testRunService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                course = manager.createCourseForTesting(code, name, req.getSummary(), req.getDescription(),
                        req.getCreditHours(), testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            course = manager.createCourse(code, name, req.getSummary(), req.getDescription(), req.getCreditHours());
        }
        if (course == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(course.getUuid())).entity(scrubCourse(course)).build();
        }

        return response;
    }

    /**
     * Get a specific Course.
     * 
     * @param uuid
     * @return
     */
    @Path("/{courseId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getCourse(@PathParam("courseId") String id) {

        // 'object not found' handled by AOP
        Course course = finder.findCourseByUuid(id);
        final Response response = Response.ok(scrubCourse(course)).build();

        return response;
    }

    /**
     * Update a Course.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{courseId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateCourse(@PathParam("courseId") String id, CourseInfoRTO req) {

        final String name = req.getName();

        // 'object not found' handled by AOP
        final Course course = finder.findCourseByUuid(id);
        final Course updatedCourse = manager.updateCourse(course, name, req.getSummary(), req.getDescription(),
                req.getCreditHours());
        final Response response = Response.ok(scrubCourse(updatedCourse)).build();

        return response;
    }

    /**
     * Delete a Course.
     * 
     * @param id
     * @return
     */
    @Path("/{courseId}")
    @DELETE
    public Response deleteCourse(@PathParam("courseId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteCourse(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("course not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
