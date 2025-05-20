package com.sismics.docs.core.service;

import com.sismics.docs.core.dao.RegistrationRequestDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.RegistrationRequest;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.constant.Constants;

import java.sql.Timestamp;
import java.util.*;

/**
 * Service for handling guest registration requests.
 */
public class RegistrationRequestService {

    private final RegistrationRequestDao requestDao = new RegistrationRequestDao();
    private final UserDao userDao = new UserDao();

    /**
     * Guest submits a registration request.
     */
    public void submit(String name, String email, String password, String message) {
        RegistrationRequest request = new RegistrationRequest();
        request.setId(UUID.randomUUID());
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setMessage(message);
        request.setStatus("pending");
        request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        requestDao.insert(request);
    }

    /**
     * Admin views all pending registration requests.
     */
    public List<RegistrationRequest> listPending() {
        return requestDao.findAllPending();
    }

    /**
     * Admin accepts a request and creates a user.
     */
    public void accept(UUID id) {
        RegistrationRequest req = requestDao.findById(id);
        if (req == null || !"pending".equals(req.getStatus())) {
            throw new IllegalStateException("Request not found or already processed.");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(req.getName());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());
        user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setStorageQuota(100 * 1024 * 1024L); // 100MB
        user.setOnboarding(true);

        try {
            // Provide a meaningful creator ID, like "system" or admin ID
            userDao.create(user, "system");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }

        requestDao.updateStatus(id, "accepted");
    }

    /**
     * Admin rejects a registration request.
     */
    public void reject(UUID id) {
        requestDao.updateStatus(id, "rejected");
    }
}
