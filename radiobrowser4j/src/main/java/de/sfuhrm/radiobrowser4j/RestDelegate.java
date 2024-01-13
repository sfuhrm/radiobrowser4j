package de.sfuhrm.radiobrowser4j;

import java.util.List;
import java.util.Map;

/** Interface towards the implementation of the REST
 * client.
 * @author Stephan Fuhrmann
 * */
interface RestDelegate {
    /** Sends a GET request to the remote server.
     * @param path the path on the web server.
     * @param resultClass the result class to retrieve.
     * @param <T> the expected return type.
     * @return an instance of the result class.
     * */
    <T> T get(String path, Class<T> resultClass);

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    List<Station> postWithListOfStation(String path,
                    Map<String, String> requestParams);

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    List<Map<String, String>> postWithListOfMapOfString(
            String path,
            Map<String, String> requestParams);

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @param resultClass the expected resulting class wrapped in a
     *                    generic type.
     * @param <T> the expected return type.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    <T> T post(String path,
               Map<String, String> requestParams,
               Class<T> resultClass);
}
