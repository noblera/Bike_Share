package com.noble.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "genericBikeApi",
        version = "v1",
        resource = "genericBike",
        namespace = @ApiNamespace(
                ownerDomain = "backend.noble.com",
                ownerName = "backend.noble.com",
                packagePath = ""
        )
)
public class GenericBikeEndpoint {

    private static final Logger logger = Logger.getLogger(GenericBikeEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(GenericBike.class);
    }

    /**
     * Returns the {@link GenericBike} with the corresponding ID.
     *
     * @param mId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code GenericBike} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "genericBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GenericBike get(@Named("mId") Long mId) throws NotFoundException {
        logger.info("Getting GenericBike with ID: " + mId);
        GenericBike genericBike = ofy().load().type(GenericBike.class).id(mId).now();
        if (genericBike == null) {
            throw new NotFoundException("Could not find GenericBike with ID: " + mId);
        }
        return genericBike;
    }

    /**
     * Inserts a new {@code GenericBike}.
     */
    @ApiMethod(
            name = "insert",
            path = "genericBike",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GenericBike insert(GenericBike genericBike) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that genericBike.mId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(genericBike).now();
        logger.info("Created GenericBike.");

        return ofy().load().entity(genericBike).now();
    }

    /**
     * Updates an existing {@code GenericBike}.
     *
     * @param mId         the ID of the entity to be updated
     * @param genericBike the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code GenericBike}
     */
    @ApiMethod(
            name = "update",
            path = "genericBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public GenericBike update(@Named("mId") Long mId, GenericBike genericBike) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mId);
        ofy().save().entity(genericBike).now();
        logger.info("Updated GenericBike: " + genericBike);
        return ofy().load().entity(genericBike).now();
    }

    /**
     * Deletes the specified {@code GenericBike}.
     *
     * @param mId the ID of the entity to delete
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code GenericBike}
     */
    @ApiMethod(
            name = "remove",
            path = "genericBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mId") Long mId) throws NotFoundException {
        checkExists(mId);
        ofy().delete().type(GenericBike.class).id(mId).now();
        logger.info("Deleted GenericBike with ID: " + mId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "genericBike",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GenericBike> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<GenericBike> query = ofy().load().type(GenericBike.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<GenericBike> queryIterator = query.iterator();
        List<GenericBike> genericBikeList = new ArrayList<GenericBike>(limit);
        while (queryIterator.hasNext()) {
            genericBikeList.add(queryIterator.next());
        }
        return CollectionResponse.<GenericBike>builder().setItems(genericBikeList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long mId) throws NotFoundException {
        try {
            ofy().load().type(GenericBike.class).id(mId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find GenericBike with ID: " + mId);
        }
    }
}