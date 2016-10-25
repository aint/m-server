package ua.softgroup.matrix.server.supervisor.queryexecutors;

import retrofit2.Response;

import java.io.IOException;

/**
 * Created by Vadim on 24.10.2016.
 */
interface QueryExecutor {

    Response executeQuery() throws IOException;
}
