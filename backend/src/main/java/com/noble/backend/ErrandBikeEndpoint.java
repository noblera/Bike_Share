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
        name = "errandBikeApi",
        version = "v1",
        resource = "errandBike",
        namespace = @ApiNamespace(
                ownerDomain = "backend.noble.com",
                ownerName = "backend.noble.com",
                packagePath = ""
        )
)
public class ErrandBikeEndpoint {

    private static final Logger logger = Logger.getLogger(ErrandBikeEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(ErrandBike.class);
    }

    /**
     * Returns the {@link ErrandBike} with the corresponding ID.
     *
     * @param mId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code ErrandBike} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "errandBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public ErrandBike get(@Named("mId") Long mId) throws NotFoundException {
        logger.info("Getting ErrandBike with ID: " + mId);
        ErrandBike errandBike = ofy().load().type(ErrandBike.class).id(mId).now();
        if (errandBike == null) {
            throw new NotFoundException("Could not find ErrandBike with ID: " + mId);
        }
        return errandBike;
    }

    /**
     * Inserts a new {@code ErrandBike}.
     */
    @ApiMethod(
            name = "insert",
            path = "errandBike",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ErrandBike insert(ErrandBike errandBike) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that errandBike.mId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(errandBike).now();
        logger.info("Created ErrandBike.");

        return ofy().load().entity(errandBike).now();
    }

    /**
     * Updates an existing {@code ErrandBike}.
     *
     * @param mId        the ID of the entity to be updated
     * @param errandBike the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code ErrandBike}
     */
    @ApiMethod(
            name = "update",
            path = "errandBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public ErrandBike update(@Named("mId") Long mId, ErrandBike errandBike) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mId);
        ofy().save().entity(errandBike).now();
        logger.info("Updated ErrandBike: " + errandBike);
        return ofy().load().entity(errandBike).now();
    }

    /**
     * Deletes the specified {@code ErrandBike}.
     *
     * @param mId the ID of the entity to delete
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code ErrandBike}
     */
    @ApiMethod(
            name = "remove",
            path = "errandBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mId") Long mId) throws NotFoundException {
        checkExists(mId);
        ofy().delete().type(ErrandBike.class).id(mId).now();
        logger.info("Deleted ErrandBike with ID: " + mId);
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
            path = "errandBike",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<ErrandBike> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<ErrandBike> query = ofy().load().type(ErrandBike.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<ErrandBike> queryIterator = query.iterator();
        List<ErrandBike> errandBikeList = new ArrayList<ErrandBike>(limit);
        while (queryIterator.hasNext()) {
            errandBikeList.add(queryIterator.next());
        }
        return CollectionResponse.<ErrandBike>builder().setItems(errandBikeList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long mId) throws NotFoundException {
        try {
            ofy().load().type(ErrandBike.class).id(mId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ErrandBike with ID: " + mId);
        }
    }
}