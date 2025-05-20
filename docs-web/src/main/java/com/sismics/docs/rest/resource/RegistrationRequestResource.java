package com.sismics.docs.rest.resource;

import com.sismics.docs.core.model.RegistrationRequest;
import com.sismics.docs.core.service.RegistrationRequestService;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/**
 * Registration request REST resource.
 */
@Path("registration")
public class RegistrationRequestResource extends BaseResource {

    private final RegistrationRequestService service = new RegistrationRequestService();

    /**
     * Guest submits registration request.
     *
     * @api {post} /registration/submit Guest submits request
     * @apiGroup Registration
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(
            @FormParam("name") String name,
            @FormParam("email") String email,
            @FormParam("password") String password,
            @FormParam("message") String message) {

        // 校验字段
        ValidationUtil.validateRequired(name, "name");
        ValidationUtil.validateRequired(email, "email");
        ValidationUtil.validateRequired(password, "password");

        service.submit(name, email, password, message);

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "submitted");

        return Response.ok(response.build()).build();
    }


    /**
     * Admin lists all pending requests.
     *
     * @api {get} /registration/admin/list List all requests
     * @apiGroup Registration
     */
    @GET
    @Path("admin/list")
    public Response list() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        List<RegistrationRequest> list = service.listPending();
        JsonArrayBuilder array = Json.createArrayBuilder();

        for (RegistrationRequest req : list) {
            array.add(Json.createObjectBuilder()
                    .add("id", req.getId().toString())
                    .add("name", req.getName())
                    .add("email", req.getEmail())
                    .add("message", req.getMessage() != null ? req.getMessage() : "")
                    .add("createdAt", req.getCreatedAt().toString()));
        }

        return Response.ok(array.build()).build();
    }

    /**
     * Admin accepts a request and creates user.
     *
     * @api {post} /registration/admin/accept/:id Accept request
     * @apiGroup Registration
     */
    @POST
    @Path("/admin/accept/{id}")
    public Response accept(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        service.accept(UUID.fromString(id));
        return Response.ok(Json.createObjectBuilder().add("status", "accepted").build()).build();
    }

    /**
     * Admin rejects a request.
     *
     * @api {post} /registration/admin/reject/:id Reject request
     * @apiGroup Registration
     */
    @POST
    @Path("admin/reject/{id}")
    public Response reject(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        service.reject(UUID.fromString(id));
        return Response.ok(Json.createObjectBuilder().add("status", "rejected").build()).build();
    }
}
