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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import com.invariantproperties.project.student.util.StudentUtil;

/**
 * Check parameters to REST service calls. This is an alternative to the web.xml
 * filter.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
// @Aspect
// @Component
public class RestParameterChecker {
    private static final Logger LOG = Logger.getLogger(RestParameterChecker.class);

    /**
     * Check that the UUID is well-formed in CRUD REST service calls.
     * 
     * @param pjp
     * @param uuid
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource) && args(uuid,..) && (execution(* *.create*(..)) || execution(* *.delete*(..)) || execution(* *.update*(..)) || execution(* *.get*(..)))")
    public Object checkUuid(ProceedingJoinPoint pjp, String uuid) throws Throwable {
        final Object[] args = pjp.getArgs();
        Object results = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("checking for bad UUID: %s(%s)...", pjp.getSignature().getName(), uuid));
        }

        if (!StudentUtil.isPossibleUuid(uuid)) {
            results = Response.status(Status.BAD_REQUEST).build();
            LOG.info(pjp.getSignature().getName() + ": attempt to use malformed UUID");
        } else {
            results = pjp.proceed(args);
        }

        return results;
    }
}
