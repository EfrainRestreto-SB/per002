package org.acme.webapi.controllers;

import org.acme.transversal.utils.DbTableChecker;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Endpoint temporal para verificar la existencia de tablas
 */
@Path("/admin/db")
public class DbAdminController {
    
    @Inject
    DbTableChecker dbTableChecker;
    
    @GET
    @Path("/check-audit-table")
    @Produces(MediaType.TEXT_PLAIN)
    public String checkAuditTable() {
        return dbTableChecker.checkAuditLogsTable();
    }
}
