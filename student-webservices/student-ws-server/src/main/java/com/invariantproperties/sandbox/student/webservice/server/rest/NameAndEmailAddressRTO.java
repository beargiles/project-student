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

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import com.invariantproperties.project.student.util.StudentUtil;

/**
 * Name and address.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@XmlRootElement
public class NameAndEmailAddressRTO implements Validatable {

    // names must be alphabetic, an apostrophe, a dash or a space. (Anne-Marie,
    // O'Brien). This pattern should accept non-Latin characters.
    // digits and colon are added to aid testing. Unlikely but possible in real
    // names.
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\p{Digit}' :-]+$");

    // email address must be well-formed. This pattern should accept non-Latin
    // characters.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]+@([\\p{L}\\p{Digit}-]+\\.)?[\\p{L}]+");

    private String name;
    private String emailAddress;
    private String testUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTestUuid() {
        return testUuid;
    }

    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    /**
     * Validate values.
     */
    @Override
    public boolean validate() {
        if ((name == null) || !NAME_PATTERN.matcher(name).matches()) {
            return false;
        }

        if ((emailAddress == null) || !EMAIL_PATTERN.matcher(emailAddress).matches()) {
            return false;
        }

        if ((testUuid != null) && !StudentUtil.isPossibleUuid(testUuid)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        // FIXME: this is unsafe!
        return String.format("NameAndEmailAddress('%s', '%s', %s)", name, emailAddress, testUuid);
    }
}
