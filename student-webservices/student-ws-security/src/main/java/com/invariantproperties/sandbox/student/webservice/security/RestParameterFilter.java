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
package com.invariantproperties.project.student.webservice.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.invariantproperties.project.student.util.StudentUtil;

/**
 * Filter requests based on valid REST parameters.
 * 
 * The first level only considers the path info - does the URL contain a valid
 * noun (e.g., 'classroom') and optional UUID?
 * 
 * A second level can add inspection of the payload. E.g., are dates
 * well-formed? Do names only contain letters (including non-Latin ones),
 * spaces, apostrophes or dashes?
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class RestParameterFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(RestParameterFilter.class);
    private static final Set<String> validNouns = new HashSet<>();

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig cfg) throws ServletException {

        // learn valid nouns
        final String nouns = cfg.getInitParameter("valid-nouns");
        if (nouns != null) {
            for (String noun : nouns.split(",")) {
                validNouns.add(noun.trim());
            }
        }
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest hreq = (HttpServletRequest) req;
        HttpServletResponse hresp = (HttpServletResponse) resp;

        // verify the noun + uuid
        if (!checkPathInfo(hreq, hresp)) {
            return;
        }

        // do additional tests, e.g., inspect payload

        chain.doFilter(req, resp);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * Check the pathInfo. We know that all paths should have the form
     * /{noun}/{uuid}/...
     * 
     * @param req
     * @return
     */
    public boolean checkPathInfo(HttpServletRequest req, HttpServletResponse resp) {
        // this pattern only handles noun and UUID, no additional parameters.
        Pattern pattern = Pattern.compile("^/([\\p{Alpha}]+)(/?([\\p{XDigit}-]+)?)?");
        Matcher matcher = pattern.matcher(req.getPathInfo());
        matcher.find();

        // verify this is a valid noun.
        if ((matcher.groupCount() >= 1) && !validNouns.contains(matcher.group(1))) {
            // LOG.info("unrecognized noun");
            LOG.info("unrecognized noun: '" + matcher.group(1) + "'");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        // verify this is a valid verb.
        if ((matcher.groupCount() >= 4) && !StudentUtil.isPossibleUuid(matcher.group(4))) {
            LOG.info("invalid UUID");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }
}
