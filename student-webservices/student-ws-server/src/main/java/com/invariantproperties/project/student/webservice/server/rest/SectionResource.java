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
import com.invariantproperties.project.student.business.SectionFinderService;
import com.invariantproperties.project.student.business.SectionManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/section")
public class SectionResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(SectionResource.class);
    private static final Section[] EMPTY_SECTION_ARRAY = new Section[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private SectionFinderService finder;

    @Resource
    private SectionManagerService manager;

    @Resource
    private TestRunService testService;

    /**
     * Default constructor.
     */
    public SectionResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(SectionFinderService finder, SectionManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testService = testService;
    }

    /**
     * Get all Sections.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllSections() {

        List<Section> sections = finder.findAllSections();

        List<Section> results = new ArrayList<Section>(sections.size());
        for (Section section : sections) {
            results.add(scrubSection(section));
        }

        final Response response = Response.ok(results.toArray(EMPTY_SECTION_ARRAY)).build();

        return response;
    }

    /**
     * Create a Section.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createSection(NameRTO req) {

        final String name = req.getName();

        Response response = null;

        Section section = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                section = manager.createSectionForTesting(name, testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            section = manager.createSection(name);
        }
        if (section == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(section.getUuid())).entity(scrubSection(section)).build();
        }

        return response;
    }

    /**
     * Get a specific Section.
     * 
     * @param uuid
     * @return
     */
    @Path("/{sectionId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getSection(@PathParam("sectionId") String id) {

        // 'object not found' handled by AOP
        Section section = finder.findSectionByUuid(id);
        final Response response = Response.ok(scrubSection(section)).build();

        return response;
    }

    /**
     * Update a Section.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{sectionId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateSection(@PathParam("sectionId") String id, NameRTO req) {

        final String name = req.getName();

        // 'object not found' handled by AOP
        final Section section = finder.findSectionByUuid(id);
        final Section updatedSection = manager.updateSection(section, name);
        final Response response = Response.ok(scrubSection(updatedSection)).build();

        return response;
    }

    /**
     * Delete a Section.
     * 
     * @param id
     * @return
     */
    @Path("/{sectionId}")
    @DELETE
    public Response deleteSection(@PathParam("sectionId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteSection(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("section not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
