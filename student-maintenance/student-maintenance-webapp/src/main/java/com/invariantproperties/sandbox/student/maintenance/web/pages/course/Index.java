package com.invariantproperties.project.student.maintenance.web.pages.course;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.maintenance.web.pages.course.Editor.Mode;
import com.invariantproperties.project.student.maintenance.web.tables.CoursePagedDataSource;
import com.invariantproperties.project.student.util.StudentUtil;

/**
 * Maintenance page for courses.
 * 
 * See also: -
 * http://jumpstart.doublenegative.com.au/jumpstart/together/componentscrud
 * /courses
 */
public class Index {
    @Property
    @Inject
    @Symbol(SymbolConstants.TAPESTRY_VERSION)
    private String tapestryVersion;

    @InjectComponent
    private Zone zone;

    @Inject
    private AlertManager alertManager;

    @Inject
    private CourseFinderService courseFinderService;

    @Inject
    private CourseManagerService courseManagerService;

    @Property
    private Course course;

    // our sibling page
    @InjectPage
    private com.invariantproperties.project.student.maintenance.web.pages.course.Editor editorPage;

    /**
     * Get the datasource containing our data.
     * 
     * @return
     */
    public GridDataSource getCourses() {
        return new CoursePagedDataSource(courseFinderService);
    }

    /**
     * Handle a delete request. This could fail, e.g., if the course has already
     * been deleted.
     * 
     * @param courseUuid
     */
    void onActionFromDelete(String courseUuid) {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            alertManager.error("invalid UUID");
        } else {
            courseManagerService.deleteCourse(courseUuid, 0);
        }
    }

    /**
     * Bring up editor page in create mode.
     * 
     * @return
     */
    Object onActionFromCreate() {
        editorPage.setup(Mode.CREATE, null);
        return editorPage;
    }

    /**
     * Bring up editor page in create mode.
     * 
     * @return
     */
    Object onActionFromCreate1() {
        return onActionFromCreate();
    }

    /**
     * Bring up editor page in review mode.
     * 
     * @param courseUuid
     * @return
     */
    Object onActionFromView(String courseUuid) {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            alertManager.error("invalid UUID");
            return this;
        } else {
            editorPage.setup(Mode.REVIEW, courseUuid);
            return editorPage;
        }
    }

    /**
     * Bring up editor page in update mode.
     * 
     * @param courseUuid
     * @return
     */
    Object onActionFromUpdate(String courseUuid) {
        if (!StudentUtil.isPossibleUuid(courseUuid)) {
            alertManager.error("invalid UUID");
            return this;
        } else {
            editorPage.setup(Mode.UPDATE, courseUuid);
            return editorPage;
        }
    }
}
