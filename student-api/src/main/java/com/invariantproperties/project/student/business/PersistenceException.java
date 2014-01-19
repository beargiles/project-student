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
package com.invariantproperties.project.student.business;

/**
 * Exception thrown when there's an unexpected exception from the persistence
 * layer. This could happen, for example, if the database connection has been
 * lost.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class PersistenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String uuid;
    private final Integer id;

    /**
     * Summary of what was being done when the exception happened. The parameter
     * is the (internationalized?) exception message to use.
     * 
     * @author bgiles
     */
    public enum Type {
        UNABLE_TO_COUNT("unable to count %s by testrun "), UNABLE_TO_FIND_BY_ID("unable to find %s by id"), UNABLE_TO_FIND_BY_UUID(
                "unable to find %s by uuid"), UNABLE_TO_FIND_BY_EMAIL_ADDRESS("unable to find %s by email address"), UNABLE_TO_FIND_BY_CODE(
                "unable to find %s by code"), UNABLE_TO_LIST("unable to get list of %s by testrun"), UNABLE_TO_CREATE(
                "unable to create %s"), UNABLE_TO_CREATE_FOR_TESTING("unable to create %s for testing"), UNABLE_TO_UPDATE(
                "unable to update %s"), UNABLE_TO_DELETE("unable to delete %s");

        private final String msg;

        private Type(String msg) {
            this.msg = msg;
        }

        public String format(String poc) {
            return String.format(msg, poc);
        }
    }

    private final Type type;

    public PersistenceException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.uuid = null;
        this.id = null;
    }

    public PersistenceException(Type type, String message, Throwable cause, String uuid) {
        super(message, cause);
        this.type = type;
        this.uuid = uuid;
        this.id = null;
    }

    public PersistenceException(Type type, String message, Throwable cause, Integer id) {
        super(message, cause);
        this.type = type;
        this.uuid = null;
        this.id = id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Type getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getId() {
        return id;
    }
}
