package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.dao.AuditLogDao;
import com.sismics.docs.core.dao.criteria.AuditLogCriteria;
import com.sismics.docs.core.dao.dto.UserActivityGanttDto;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ForbiddenClientException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Path("admin-dashboard")
public class AdminDashboardResource extends BaseResource {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 判断用户是否为管理员
     */
    private void authenticateAdmin() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        // 这里用BaseResource的checkBaseFunction判断是否有ADMIN权限
        checkBaseFunction(BaseFunction.ADMIN);
    }

    @GET
    @Path("user-activities-gantt")
    public Response getUserActivitiesGantt(@QueryParam("userId") String userId,
                                           @QueryParam("start") String startStr,
                                           @QueryParam("end") String endStr) {
        authenticateAdmin();

        AuditLogCriteria criteria = new AuditLogCriteria();
        criteria.setUserId(userId);
        try {
            if (startStr != null && !startStr.isEmpty()) {
                Date startDate = DATE_FORMAT.parse(startStr);
                criteria.setStartDate(startDate);
            }
            if (endStr != null && !endStr.isEmpty()) {
                Date endDate = DATE_FORMAT.parse(endStr);
                criteria.setEndDate(endDate);
            }
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format, expected yyyy-MM-dd")
                    .build();
        }

        criteria.setTypes(Arrays.asList(AuditLogType.CREATE, AuditLogType.UPDATE, AuditLogType.DELETE));

        AuditLogDao auditLogDao = new AuditLogDao();
        List<UserActivityGanttDto> activities = auditLogDao.findUserActivitiesForGantt(criteria);

        JsonArrayBuilder array = Json.createArrayBuilder();
        for (UserActivityGanttDto dto : activities) {
            array.add(Json.createObjectBuilder()
                    .add("taskId", dto.getTaskId())
                    .add("taskName", dto.getTaskName() != null ? dto.getTaskName() : "")
                    .add("userId", dto.getUserId() != null ? dto.getUserId() : "")
                    .add("username", dto.getUsername() != null ? dto.getUsername() : "")
                    .add("type", dto.getType() != null ? dto.getType().name() : "")
                    .add("timestamp", dto.getTimestamp()));
        }

        return Response.ok(array.build()).build();
    }
}
