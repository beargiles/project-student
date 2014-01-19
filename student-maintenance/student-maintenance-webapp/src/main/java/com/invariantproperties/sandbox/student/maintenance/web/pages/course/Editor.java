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
package com.invariantproperties.project.student.maintenance.web.pages.course;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.maintenance.util.ExceptionUtil;
import com.invariantproperties.project.student.maintenance.web.components.CustomForm;
import com.invariantproperties.project.student.util.StudentUtil;
import com.invariantproperties.project.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.project.student.webservice.client.RestClientFailureException;

/**
 * This component will trigger the following events on its container (which in
 * this example is the page):
 * {@link Editor.web.components.examples.component.crud.Editor#CANCEL_CREATE} ,
 * {@link Editor.web.components.examples.component.crud.Editor#SUCCESSFUL_CREATE}
 * (Long courseUuid),
 * {@link Editor.web.components.examples.component.crud.Editor#FAILED_CREATE} ,
 * {@link Editor.web.components.examples.component.crud.Editor#TO_UPDATE} (Long
 * courseUuid),
 * {@link Editor.web.components.examples.component.crud.Editor#SUCCESSFUL_UPDATE}
 * (Long courseUuid),
 * {@link Editor.web.components.examples.component.crud.Editor#FAILED_UPDATE}
 * (Long courseUuid),
 * {@link Editor.web.components.examples.component.crud.Editor#SUCCESSFUL_DELETE}
 * (Long courseUuid),
 * {@link Editor.web.components.examples.component.crud.Editor#FAILED_DELETE}
 * (Long courseUuid).
 */
// @Events is applied to a component solely to document what events it may
// trigger. It is not checked at runtime.
@Events({ Editor.CANCEL_CREATE, Editor.SUCCESSFUL_CREATE, Editor.FAILED_CREATE, Editor.TO_UPDATE, Editor.CANCEL_UPDATE,
        Editor.SUCCESSFUL_UPDATE, Editor.FAILED_UPDATE, Editor.SUCCESFUL_DELETE, Editor.FAILED_DELETE })
public class Editor {
    public static final String CANCEL_CREATE = "cancelCreate";
    public static final String SUCCESSFUL_CREATE = "successfulCreate";
    public static final String FAILED_CREATE = "failedCreate";
    public static final String TO_UPDATE = "toUpdate";
    public static final String CANCEL_UPDATE = "cancelUpdate";
    public static final String SUCCESSFUL_UPDATE = "successfulUpdate";
    public static final String FAILED_UPDATE = "failedUpdate";
    public static final String SUCCESFUL_DELETE = "successfulDelete";
    public static final String FAILED_DELETE = "failedDelete";

    public enum Mode {
        CREATE, REVIEW, UPDATE;
    }

    private static final Logger LOG = Logger.getLogger(Editor.class);

    // Parameters

    @ActivationRequestParameter
    @Property
    private Mode mode;

    @ActivationRequestParameter
    @Property
    private String courseUuid;

    // Screen fields

    @Property
    private Course course;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String deleteMessage;

    // Work fields

    // This carries version through the redirect that follows a server-side
    // validation failure.
    @Persist(PersistenceConstants.FLASH)
    private Integer versionFlash;

    // Generally useful bits and pieces

    @Inject
    private CourseFinderService courseFinderService;

    @Inject
    private CourseManagerService courseManagerService;

    @Component
    private CustomForm createForm;

    @Component
    private CustomForm reviewForm;

    @Component
    private CustomForm updateForm;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private Messages messages;

    @InjectPage
    private com.invariantproperties.project.student.maintenance.web.pages.course.Index indexPage;

    // The code

    public void setup(Mode mode, String courseUuid) {
        this.mode = mode;
        // this test includes valid 'null' case.
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            this.courseUuid = null;
        } else {
            this.courseUuid = courseUuid;
        }
    }

    // setupRender() is called by Tapestry right before it starts rendering the
    // component.

    void setupRender() {

        if (mode == Mode.REVIEW) {
            // this test includes valid 'null' case.
            if (!StudentUtil.isPossibleUuid(courseUuid)) {
                courseUuid = null;
                course = null;
                // Handle null course in the template.
            } else {
                if (course == null) {
                    try {
                        course = courseFinderService.findCourseByUuid(courseUuid);
                    } catch (ObjectNotFoundException e) {
                        // Handle null course in the template.
                        LOG.trace("course not found: " + courseUuid);
                    }
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////
    // CREATE
    // /////////////////////////////////////////////////////////////////////

    // Handle event "cancelCreate"

    Object onCancelCreate() {
        return indexPage;
        // Return false, which means we haven't handled the event so bubble it
        // up.
        // This method is here solely as documentation, because without this
        // method the event would bubble up anyway.
        // return false;
    }

    // Component "createForm" bubbles up the PREPARE event when it is rendered
    // or submitted

    void onPrepareFromCreateForm() {
        // Instantiate a Course for the form data to overlay.
        course = new Course();
    }

    // Component "createForm" bubbles up the VALIDATE event when it is submitted

    void onValidateFromCreateForm() {

        if (createForm.getHasErrors()) {
            // We get here only if a server-side validator detected an error.
            return;
        }

        try {
            course = courseManagerService.createCourse(course.getCode(), course.getName(), course.getSummary(),
                    course.getDescription(), 1);
        } catch (RestClientFailureException e) {
            createForm.recordError("Internal error on server.");
            createForm.recordError(e.getMessage());
            // TODO: replace with exception service
            LOG.debug("internal error on server during validation", e);
        } catch (Exception e) {
            createForm.recordError(ExceptionUtil.getRootCauseMessage(e));
            // TODO: replace with exception service
            LOG.info("unhandled exception during validation", e);
        }
    }

    // Component "createForm" bubbles up SUCCESS or FAILURE when it is
    // submitted, depending on whether VALIDATE
    // records an error

    boolean onSuccessFromCreateForm() {
        // We want to tell our containing page explicitly what course we've
        // created, so we trigger new event
        // "successfulCreate" with a parameter. It will bubble up because we
        // don't have a handler method for it.
        componentResources.triggerEvent(SUCCESSFUL_CREATE, new Object[] { course.getUuid() }, null);
        // We don't want "success" to bubble up, so we return true to say we've
        // handled it.
        mode = Mode.REVIEW;
        courseUuid = course.getUuid();
        return true;
    }

    boolean onFailureFromCreateForm() {
        // Rather than letting "failure" bubble up which doesn't say what you
        // were trying to do, we trigger new event
        // "failedCreate". It will bubble up because we don't have a handler
        // method for it.
        componentResources.triggerEvent(FAILED_CREATE, null, null);
        // We don't want "failure" to bubble up, so we return true to say we've
        // handled it.
        return true;
    }

    // /////////////////////////////////////////////////////////////////////
    // REVIEW
    // /////////////////////////////////////////////////////////////////////

    void onPrepareFromReviewForm() {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            LOG.info("Invalid CourseUUID");
            courseUuid = null;
            course = null;
        } else {
            try {
                course = courseFinderService.findCourseByUuid(courseUuid);
            } catch (ObjectNotFoundException e) {
                // Handle null course in the template.
                LOG.trace("course not found: " + courseUuid);
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////
    // UPDATE
    // /////////////////////////////////////////////////////////////////////

    // Handle event "cancelUpdate"

    Object onCancelUpdate(String courseUuid) {
        return indexPage;
    }

    // Component "updateForm" bubbles up the PREPARE_FOR_RENDER event during
    // form render

    void onPrepareForRenderFromUpdateForm() {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            LOG.info("Invalid CourseUUID");
            courseUuid = null;
            course = null;
        } else {
            try {
                course = courseFinderService.findCourseByUuid(courseUuid);
            } catch (ObjectNotFoundException e) {
                // Handle null course in the template.
                LOG.trace("course not found: " + courseUuid);
            }
        }

        // If the form has errors then we're redisplaying after a redirect.
        // Form will restore your input values but it's up to us to restore
        // Hidden values.

        if (updateForm.getHasErrors()) {
            if (course != null) {
                course.setVersion(versionFlash);
            }
        }
    }

    // Component "updateForm" bubbles up the PREPARE_FOR_SUBMIT event during for
    // submission

    void onPrepareForSubmitFromUpdateForm() {
        // Get objects for the form fields to overlay.
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            LOG.info("Invalid CourseUUID");
            courseUuid = null;
            course = new Course();
        } else {
            try {
                course = courseFinderService.findCourseByUuid(courseUuid);
            } catch (ObjectNotFoundException e) {
                course = new Course();
                updateForm.recordError("Course has been deleted by another process.");
                LOG.trace("course not found: " + courseUuid);
            }
        }
    }

    // Component "updateForm" bubbles up the VALIDATE event when it is submitted

    void onValidateFromUpdateForm() {

        if (updateForm.getHasErrors()) {
            // We get here only if a server-side validator detected an error.
            return;
        }

        try {
            courseManagerService
                    .updateCourse(course, course.getName(), course.getSummary(), course.getDescription(), 1);
        } catch (RestClientFailureException e) {
            updateForm.recordError("Internal error on server.");
            updateForm.recordError(e.getMessage());
            // TODO: replace with exception service
            LOG.debug("internal error on server during validation", e);
        } catch (Exception e) {
            // Display the cause. In a real system we would try harder to get a
            // user-friendly message.
            updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
            // TODO: replace with exception service
            LOG.info("unhandled exception during validation", e);
        }
    }

    // Component "updateForm" bubbles up SUCCESS or FAILURE when it is
    // submitted, depending on whether VALIDATE
    // records an error

    boolean onSuccessFromUpdateForm() {
        // We want to tell our containing page explicitly what course we've
        // updated, so we trigger new event
        // "successfulUpdate" with a parameter. It will bubble up because we
        // don't have a handler method for it.
        componentResources.triggerEvent(SUCCESSFUL_UPDATE, new Object[] { courseUuid }, null);

        // We don't want "success" to bubble up, so we return true to say we've
        // handled it.
        mode = Mode.REVIEW;
        return true;
    }

    boolean onFailureFromUpdateForm() {
        versionFlash = course.getVersion();

        // Rather than letting "failure" bubble up which doesn't say what you
        // were trying to do, we trigger new event
        // "failedUpdate". It will bubble up because we don't have a handler
        // method for it.
        componentResources.triggerEvent(FAILED_UPDATE, new Object[] { courseUuid }, null);
        // We don't want "failure" to bubble up, so we return true to say we've
        // handled it.
        return true;
    }

    // /////////////////////////////////////////////////////////////////////
    // DELETE
    // /////////////////////////////////////////////////////////////////////

    // Handle event "delete"

    Object onDelete(String courseUuid) {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            LOG.info("Invalid CourseUUID");
            courseUuid = null;
            course = null;
            return this;
        }

        this.courseUuid = courseUuid;
        int courseVersion = 0;

        try {
            courseManagerService.deleteCourse(courseUuid, courseVersion);
        } catch (ObjectNotFoundException e) {
            // object has already been deleted.
            LOG.trace("course not found: " + courseUuid);
        } catch (RestClientFailureException e) {
            createForm.recordError("Internal error on server.");
            createForm.recordError(e.getMessage());

            // Display the cause. In a real system we would try harder to get a
            // user-friendly message.
            deleteMessage = ExceptionUtil.getRootCauseMessage(e);

            // Trigger new event "failedDelete" which will bubble up.
            componentResources.triggerEvent(FAILED_DELETE, new Object[] { courseUuid }, null);
            // We don't want "delete" to bubble up, so we return true to say
            // we've handled it.
            // TODO: replace with exception service
            LOG.debug("internal error on server during validation", e);
            return true;
        } catch (Exception e) {
            // Display the cause. In a real system we would try harder to get a
            // user-friendly message.
            deleteMessage = ExceptionUtil.getRootCauseMessage(e);

            // Trigger new event "failedDelete" which will bubble up.
            componentResources.triggerEvent(FAILED_DELETE, new Object[] { courseUuid }, null);
            // We don't want "delete" to bubble up, so we return true to say
            // we've handled it.
            // TODO: replace with exception service
            LOG.info("unhandled exception during deletion", e);
            return true;
        }

        // Trigger new event "successfulDelete" which will bubble up.
        componentResources.triggerEvent(SUCCESFUL_DELETE, new Object[] { courseUuid }, null);
        // We don't want "delete" to bubble up, so we return true to say we've
        // handled it.
        return indexPage;
    }

    // /////////////////////////////////////////////////////////////////////
    // PAGE NAVIGATION
    // /////////////////////////////////////////////////////////////////////

    // Handle event "toUpdate"

    boolean onToUpdate(String courseUuid) {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            LOG.info("Invalid CourseUUID");
            return true;
        } else {
            mode = Mode.UPDATE;
            return false;
        }
    }

    // Handle event "toIndex"

    Object onToIndex() {
        return indexPage;
    }

    // /////////////////////////////////////////////////////////////////////
    // OTHER
    // /////////////////////////////////////////////////////////////////////

    // Getters

    public boolean isModeCreate() {
        return mode == Mode.CREATE;
    }

    public boolean isModeReview() {
        return mode == Mode.REVIEW;
    }

    public boolean isModeUpdate() {
        return mode == Mode.UPDATE;
    }

    public String getDatePattern() {
        return "dd/MM/yyyy";
    }

    public Format getDateFormat() {
        return new SimpleDateFormat(getDatePattern());
    }
}
