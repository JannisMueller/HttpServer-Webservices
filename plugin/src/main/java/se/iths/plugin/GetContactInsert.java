package se.iths.plugin;

import se.iths.plugin.dao.ContactDao;
import se.iths.plugin.model.Contact;
import se.iths.routing.Route;
import se.iths.spi.IoHandler;

@Route(url = "/insertcontactviaget")
public class GetContactInsert extends GetContact implements IoHandler {

	/**
	 * Parses url-parameters of the request, extracts the firstname / lastname and inserts it to the database.
	 * Returns the inserted object as json.
	 */
	@Override
	public byte[] urlHandler(String requestPath, String requestBody, String requestMethod) {

		ContactDao contactDao = new ContactDao();

		String firstName = extractFirstName(requestPath);
		String lastName = extractLastName(requestPath);

		Contact contact = new Contact(firstName, lastName);
		contactDao.createContact(contact);

		return returnObjectAsJson(contact);
	}
}
