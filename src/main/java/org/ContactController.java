package org;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/contact")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContactController {

    @Inject
    EntityManager entityManager;

    @GET()
    @Path("get-all")
    public List<Contact> getASllContacts() {
        return entityManager.createQuery("from Contact", Contact.class).getResultList();
    }

    @GET
    @Path("get/{id}")
    public Response getContactById(@PathParam("id") Long id) {
        Contact contact = entityManager.find(Contact.class, id);
        if (contact == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Contact not found").build();
        }
        return Response.ok(contact).build();
    }

    @POST
    @Path("create")
    @Transactional
    public Response createContact(Contact contact) {
        try {
            entityManager.persist(contact);
            entityManager.flush();
            return Response.status(Response.Status.CREATED).entity(contact).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving contact").build();
        }
    }

    @PUT
    @Path("update/{id}")
    @Transactional
    public Response updateContact(@PathParam("id") Long id, Contact updatedContact) {
        Contact contact = entityManager.find(Contact.class, id);
        if (contact == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Contact not found").build();
        }

        contact.name = updatedContact.name;
        contact.email = updatedContact.email;
        contact.dataProtection = updatedContact.dataProtection;

        entityManager.merge(contact);

        return Response.ok(contact).build();
    }

    @DELETE
    @Path("delete/{id}")
    @Transactional
    public Response deleteContact(@PathParam("id") Long id) {
        Contact contact = entityManager.find(Contact.class, id);
        if (contact == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Contact not found").build();
        }

        entityManager.remove(contact); 

        return Response.noContent().build();
    }

}
