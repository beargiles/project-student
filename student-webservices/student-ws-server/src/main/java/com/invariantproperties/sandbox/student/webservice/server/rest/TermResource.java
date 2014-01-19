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
import com.invariantproperties.project.student.business.TermFinderService;
import com.invariantproperties.project.student.business.TermManagerService;
import com.invariantproperties.project.student.business.TestRunService;
import com.invariantproperties.project.student.domain.Term;
import com.invariantproperties.project.student.domain.TestRun;

@Service
@Path("/term")
public class TermResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(TermResource.class);
    private static final Term[] EMPTY_TERM_ARRAY = new Term[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private TermFinderService finder;

    @Resource
    private TermManagerService manager;

    @Resource
    private TestRunService testService;

    /**
     * Default constructor.
     */
    public TermResource() {

    }

    /**
     * Set values used in unit tests. (Required due to AOP)
     * 
     * @param finder
     * @param manager
     * @param testService
     */
    void setServices(TermFinderService finder, TermManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testService = testService;
    }

    /**
     * Get all Terms.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllTerms() {

        final List<Term> terms = finder.findAllTerms();

        final List<Term> results = new ArrayList<Term>(terms.size());
        for (Term term : terms) {
            results.add(scrubTerm(term));
        }

        final Response response = Response.ok(results.toArray(EMPTY_TERM_ARRAY)).build();

        return response;
    }

    /**
     * Create a Term.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createTerm(NameRTO req) {

        final String name = req.getName();

        Response response = null;
        Term term = null;

        if (req.getTestUuid() != null) {
            TestRun testRun = testService.findTestRunByUuid(req.getTestUuid());
            if (testRun != null) {
                term = manager.createTermForTesting(name, testRun);
            } else {
                response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
            }
        } else {
            term = manager.createTerm(name);
        }

        if (term == null) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            response = Response.created(URI.create(term.getUuid())).entity(scrubTerm(term)).build();
        }

        return response;
    }

    /**
     * Get a specific Term.
     * 
     * @param uuid
     * @return
     */
    @Path("/{termId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getTerm(@PathParam("termId") String id) {

        // 'object not found' handled by AOP
        Term term = finder.findTermByUuid(id);
        final Response response = Response.ok(scrubTerm(term)).build();

        return response;
    }

    /**
     * Update a Term.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{termId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateTerm(@PathParam("termId") String id, NameRTO req) {

        final String name = req.getName();

        // 'object not found' handled by AOP
        final Term term = finder.findTermByUuid(id);
        final Term updatedTerm = manager.updateTerm(term, name);
        final Response response = Response.ok(scrubTerm(updatedTerm)).build();

        return response;
    }

    /**
     * Delete a Term.
     * 
     * @param id
     * @return
     */
    @Path("/{termId}")
    @DELETE
    public Response deleteTerm(@PathParam("termId") String id, @PathParam("version") Integer version) {

        // we don't use AOP handler since it's okay for there to be no match
        try {
            manager.deleteTerm(id, version);
        } catch (ObjectNotFoundException exception) {
            LOG.debug("term not found: " + id);
        }

        final Response response = Response.noContent().build();

        return response;
    }
}
