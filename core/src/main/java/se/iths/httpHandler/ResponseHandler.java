package se.iths.httpHandler;

import se.iths.model.HttpRequest;
import se.iths.model.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseHandler {

	/**
	 * Constructs our httpResponse and send it out to the client via methods.
	 * @param content server from our plugins (file or database query)
	 */
	public void handleResponse(byte[] content, Socket socket, HttpRequest httpRequest) throws Exception {
		var output = new PrintStream(socket.getOutputStream());

		HttpResponse httpResponse = createHttpResponse(content, httpRequest);

		sendHttpResponse(httpResponse, output);
	}

	/**
	 * Populates our httpResponse object.
	 * @param content from our IoHandlers. If it is 0 bytes we return a 404 httpResponse object.
	 * @param httpRequest with requested information.
	 * @return populated httpResponse.
	 */
	private HttpResponse createHttpResponse(byte[] content, HttpRequest httpRequest) throws IOException {

		if (content.length > 0) {
			return new HttpResponse(httpRequest.getRequestMethod(), "200 OK", findContentType(httpRequest.getRequestPath()), content.length, content);
		} else {
			File file404 = new File("core/src/main/resources/404.html");
			content = Files.readAllBytes(Path.of(file404.getAbsolutePath()));
			return new HttpResponse(httpRequest.getRequestMethod(), "404", findContentType(httpRequest.getRequestPath()), content.length, content);
		}
	}

	/**
	 * Sends the response to client.
	 * @param httpResponse populated with all data needed for response.
	 * @param output used for sending data to client, closed after sending data.
	 */
	private void sendHttpResponse(HttpResponse httpResponse, PrintStream output) throws IOException {
		output.println(httpResponse.getStatus());
		output.println("Content-Length: " + httpResponse.getContentLength());
		output.println(httpResponse.getContentType());
		output.println("");
		if (!httpResponse.getMethod().equals("HEAD")) {
			output.write(httpResponse.getContent());
		}
		output.flush();
		output.close();
	}

	/**
	 * If the url points to a database action it gets set to json.
	 * Otherwise we check mimetype of the file.
	 * @param requestPath to the file or database path
	 * @return mimetype/content-type of the file / path
	 */
	private String findContentType(String requestPath) throws IOException {

		String mimeType = "";

		if (requestPath.contains("/getcontact") || requestPath.contains("/postcontact") || requestPath.contains("/insertcontactviaget")) {
			mimeType = "application/json";
		} else {
			Path path = Path.of(requestPath);
			mimeType = Files.probeContentType(path);
		}

		return mimeType;
	}
}