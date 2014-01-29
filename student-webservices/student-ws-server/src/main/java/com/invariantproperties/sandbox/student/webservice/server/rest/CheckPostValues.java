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

import java.util.Arrays;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.invariantproperties.project.student.util.StudentUtil;

/**
 * AOP handler to check resource payload values. We also use this class to log
 * method entry, vs. a separate class, since we know that the method arguments
 * have been validated. We might not know that with a separate class.
 * 
 * We don't check for unhandled exceptions since that's handled by a separate
 * class. (One class, one task.)
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Aspect
@Component
public class CheckPostValues {

    /**
     * Check post values on create method.
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource) && args(rto,..)")
    public Object checkParametersCreate(ProceedingJoinPoint pjp, Validatable rto) throws Throwable {
        final Logger log = Logger.getLogger(pjp.getSignature().getDeclaringType());
        final String name = pjp.getSignature().getName();
        Object results = null;

        if (rto.validate()) {
            // this should be safe since parameters have been validated.
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s(%s): entry", name, Arrays.toString(pjp.getArgs())));
            }
            results = pjp.proceed(pjp.getArgs());
        } else {
            // FIXME: this is unsafe
            if (log.isInfoEnabled()) {
                log.info(String.format("%s(%s): bad arguments", name, Arrays.toString(pjp.getArgs())));
            }
            // TODO: tell caller what the problems were
            results = Response.status(Status.BAD_REQUEST).build();
        }

        return results;
    }

    /**
     * Check post values on update method.
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource) && args(uuid,rto,..)")
    public Object checkParametersUpdate(ProceedingJoinPoint pjp, String uuid, Validatable rto) throws Throwable {
        final Logger log = Logger.getLogger(pjp.getSignature().getDeclaringType());
        final String name = pjp.getSignature().getName();
        Object results = null;

        if (!StudentUtil.isPossibleUuid(uuid)) {
            // this is a possible attack.
            if (log.isInfoEnabled()) {
                log.info(String.format("%s(): uuid", name));
            }
            results = Response.status(Status.BAD_REQUEST).build();
        } else if (rto.validate()) {
            // this should be safe since parameters have been validated.
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s(%s): entry", name, Arrays.toString(pjp.getArgs())));
            }
            results = pjp.proceed(pjp.getArgs());
        } else {
            // FIXME: this is unsafe
            if (log.isInfoEnabled()) {
                log.info(String.format("%s(%s): bad arguments", name, Arrays.toString(pjp.getArgs())));
            }
            // TODO: tell caller what the problems were
            results = Response.status(Status.BAD_REQUEST).build();
        }

        return results;
    }

    /**
     * Check post values on delete method. This is actually a no-op but it
     * allows us to log method entry.
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource) && args(uuid,version) && execution(* *.delete*(..))")
    public Object checkParametersDelete(ProceedingJoinPoint pjp, String uuid, Integer version) throws Throwable {
        final Logger log = Logger.getLogger(pjp.getSignature().getDeclaringType());
        final String name = pjp.getSignature().getName();
        Object results = null;

        if (!StudentUtil.isPossibleUuid(uuid)) {
            // this is a possible attack.
            if (log.isInfoEnabled()) {
                log.info(String.format("%s(): uuid", name));
            }
            results = Response.status(Status.BAD_REQUEST).build();
        } else {
            // this should be safe since parameters have been validated.
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s(%s): entry", name, Arrays.toString(pjp.getArgs())));
            }
            results = pjp.proceed(pjp.getArgs());
        }

        return results;
    }

    /**
     * Check post values on find methods. This is actually a no-op but it allows
     * us to log method entry.
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource) && execution(* *.find*(..))")
    public Object checkParametersFind(ProceedingJoinPoint pjp) throws Throwable {
        final Logger log = Logger.getLogger(pjp.getSignature().getDeclaringType());

        if (log.isDebugEnabled()) {
            log.debug(String.format("%s(%s): entry", pjp.getSignature().getName(), Arrays.toString(pjp.getArgs())));
        }
        final Object results = pjp.proceed(pjp.getArgs());

        return results;
    }
}
