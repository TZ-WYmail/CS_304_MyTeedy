package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.RegistrationRequest;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * DAO for RegistrationRequest.
 */
public class RegistrationRequestDao {

    /**
     * Insert a new registration request.
     */
    public void insert(RegistrationRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        request.setId(UUID.randomUUID());
        request.setCreatedAt(new java.sql.Timestamp(new Date().getTime()));
        em.persist(request);
    }

    /**
     * Get all pending requests.
     */
    public List<RegistrationRequest> findAllPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select r from RegistrationRequest r where r.status = 'pending' order by r.createdAt asc");
        return q.getResultList();
    }

    /**
     * Find by ID.
     */
    public RegistrationRequest findById(UUID id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(RegistrationRequest.class, id);
    }

    /**
     * Update request status.
     */
    public void updateStatus(UUID id, String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        RegistrationRequest req = em.find(RegistrationRequest.class, id);
        if (req != null) {
            req.setStatus(status);
        }
    }
}
