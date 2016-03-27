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
        name = "roadBikeApi",
        version = "v1",
        resource = "roadBike",
        namespace = @ApiNamespace(
                ownerDomain = "backend.noble.com",
                ownerName = "backend.noble.com",
                packagePath = ""
        )
)
public class RoadBikeEndpoint {

    private static final Logger logger = Logger.getLogger(RoadBikeEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(RoadBike.class);
    }

    /**
     * Returns the {@link RoadBike} with the corresponding ID.
     *
     * @param mId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code RoadBike} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "roadBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public RoadBike get(@Named("mId") Long mId) throws NotFoundException {
        logger.info("Getting RoadBike with ID: " + mId);
        RoadBike roadBike = ofy().load().type(RoadBike.class).id(mId).now();
        if (roadBike == null) {
            throw new NotFoundException("Could not find RoadBike with ID: " + mId);
        }
        return roadBike;
    }

    /**
     * Inserts a new {@code RoadBike}.
     */
    @ApiMethod(
            name = "insert",
            path = "roadBike",
            httpMethod = ApiMethod.HttpMethod.POST)
    public RoadBike insert(RoadBike roadBike) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that roadBike.mId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(roadBike).now();
        logger.info("Created RoadBike.");

        return ofy().load().entity(roadBike).now();
    }

    /**
     * Updates an existing {@code RoadBike}.
     *
     * @param mId      the ID of the entity to be updated
     * @param roadBike the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code RoadBike}
     */
    @ApiMethod(
            name = "update",
            path = "roadBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public RoadBike update(@Named("mId") Long mId, RoadBike roadBike) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mId);
        ofy().save().entity(roadBike).now();
        logger.info("Updated RoadBike: " + roadBike);
        return ofy().load().entity(roadBike).now();
    }

    /**
     * Deletes the specified {@code RoadBike}.
     *
     * @param mId the ID of the entity to delete
     * @throws NotFoundException if the {@code mId} does not correspond to an existing
     *                           {@code RoadBike}
     */
    @ApiMethod(
            name = "remove",
            path = "roadBike/{mId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mId") Long mId) throws NotFoundException {
        checkExists(mId);
        ofy().delete().type(RoadBike.class).id(mId).now();
        logger.info("Deleted RoadBike with ID: " + mId);
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
            path = "roadBike",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<RoadBike> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<RoadBike> query = ofy().load().type(RoadBike.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<RoadBike> queryIterator = query.iterator();
        List<RoadBike> roadBikeList = new ArrayList<RoadBike>(limit);
        while (queryIterator.hasNext()) {
            roadBikeList.add(queryIterator.next());
        }
        return CollectionResponse.<RoadBike>builder().setItems(roadBikeList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long mId) throws NotFoundException {
        try {
            ofy().load().type(RoadBike.class).id(mId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find RoadBike with ID: " + mId);
        }
    }
}