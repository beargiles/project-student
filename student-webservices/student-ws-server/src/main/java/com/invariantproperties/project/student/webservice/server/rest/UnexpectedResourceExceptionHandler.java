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

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.invariantproperties.project.student.business.ObjectNotFoundException;

/**
 * AOP handler for unexpected exceptions in resources.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Aspect
@Component
public class UnexpectedResourceExceptionHandler {

    /**
     * Check for an unhandled exception from a REST resource. If we catch one
     * AND the method returns a Response we can return a Server Internal Error
     * (500) error code instead of blowing up. We need to check though since
     * some methods don't return a Response.
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("target(com.invariantproperties.project.student.webservice.server.rest.AbstractResource)")
    public Object checkForUnhandledException(ProceedingJoinPoint pjp) throws Throwable {
        Object results = null;
        Logger log = Logger.getLogger(pjp.getSignature().getClass());

        try {
            results = pjp.proceed(pjp.getArgs());
        } catch (ObjectNotFoundException e) {
            // this is safe to log since we know that we've passed filtering.
            String args = Arrays.toString(pjp.getArgs());
            results = Response.status(Status.NOT_FOUND).entity("object not found: " + args).build();
            if (log.isDebugEnabled()) {
                log.debug("object not found: " + args);
            }
        } catch (Exception e) {
            // find the method we called. We can't cache this since the method
            // may be overloaded
            Method method = findMethod(pjp);
            if ((method != null) && Response.class.isAssignableFrom(method.getReturnType())) {
                // if the method returns a response we can return a 500 message.
                if (!(e instanceof UnitTestException)) {
                    if (log.isInfoEnabled()) {
                        log.info(
                                String.format("%s(): unhandled exception: %s", pjp.getSignature().getName(),
                                        e.getMessage()), e);
                    }
                } else if (log.isTraceEnabled()) {
                    log.info("unit test exception: " + e.getMessage());
                }
                results = Response.status(Status.INTERNAL_SERVER_ERROR).build();
            } else {
                // DO NOT LOG THE EXCEPTION. That just clutters the log - let
                // the final handler log it.
                throw e;
            }
        }

        return results;
    }

    /**
     * Find method called via reflection.
     * 
     * @param pjp
     * @return
     */
    Method findMethod(ProceedingJoinPoint pjp) {
        Class<?>[] argtypes = new Class[pjp.getArgs().length];
        for (int i = 0; i < argtypes.length; i++) {
            argtypes[i] = pjp.getArgs()[i].getClass();
        }

        Method method = null;

        try {
            // @SuppressWarnings("unchecked")
            method = pjp.getSignature().getDeclaringType().getMethod(pjp.getSignature().getName(), argtypes);
        } catch (Exception e) {
            Logger.getLogger(UnexpectedResourceExceptionHandler.class).info(
                    String.format("could not find method for %s.%s", pjp.getSignature().getDeclaringType().getName(),
                            pjp.getSignature().getName()));
        }

        return method;
    }
}
