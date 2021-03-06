package ie.smartcommuter.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class is used to manage database connections and queries.
 * 
 * @author Shane Doyle
 */
public class DatabaseManager {

	private SQLiteDatabase mDB;
	private DatabaseHelper mDatabaseHelper;
	public int maxFavourites, maxRecentlyViewed;

	public DatabaseManager(Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
		maxFavourites = 10;
		maxRecentlyViewed = 10;
	}

	/**
	 * This method is used to open a database connection.
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		mDB = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * This method is used to close a database connection.
	 */
	public void close() {
		mDB.close();
		mDatabaseHelper.close();
	}

	/**
	 * This method is used to get all stations from the database.
	 * 
	 * @return
	 */
	public List<Station> getAllStations() {
		List<Station> stations = new ArrayList<Station>();

		Cursor cursor = mDB.query("station_details", null, null, null, null,
				null, "station_name");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			Station station = cursorToStation(cursor);

			if (station != null) {
				stations.add(station);
			}

			cursor.moveToNext();
		}

		cursor.close();

		return stations;
	}

	/**
	 * This method is used to get all favourite stations from the database.
	 * 
	 * @return
	 */
	public List<Station> getFavouriteStations() {
		List<Station> stations = new ArrayList<Station>();

		Cursor cursor = mDB.query("favourite_station_details", null, null,
				null, null, null, "favourite_station_name");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			Station station = cursorToStation(cursor);

			if (station != null) {
				stations.add(station);
			}

			cursor.moveToNext();
		}

		cursor.close();

		return stations;
	}

	/**
	 * This method is used to get all recently viewed stations from the
	 * database.
	 * 
	 * @return
	 */
	public List<Station> getRecentlyViewedStations() {
		List<Station> stations = new ArrayList<Station>();

		try {
			Cursor cursor = mDB.query("recently_viewed_station_details", null,
					null, null, null, null, "recently_viewed_time DESC");

			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {

				Station station = cursorToStation(cursor);

				if (station != null) {
					stations.add(station);
				}

				cursor.moveToNext();
			}

			cursor.close();
		} catch (Exception e) {

		}

		return stations;
	}

	/**
	 * This method is used to get Stations that are near the phones location.
	 * 
	 * @param address
	 * @return
	 */
	public List<Station> getNearbyStations(Address address) {
		List<Station> stations = new ArrayList<Station>();

		String lat = Double.toString(GeoLocation.microDegreesToDegrees(address
				.getLatitude()));
		String lon = Double.toString(GeoLocation.microDegreesToDegrees(address
				.getLongitude()));

		String latOrderBy = "(" + lat + " - station_latitude) * (" + lat
				+ " - station_latitude)";
		String lonOrderBy = "(" + lon + " - station_longitude) * (" + lon
				+ " - station_longitude)";

		Cursor cursor = mDB.query("station_details", null, null, null, null,
				null, "(" + latOrderBy + " + " + lonOrderBy + ")", "20");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			Station station = cursorToStation(cursor);

			if (station != null) {
				stations.add(station);
			}

			cursor.moveToNext();
		}

		cursor.close();

		return stations;
	}

	/**
	 * This method is used to get all companies that are in the database.
	 * 
	 * @return
	 */
	public List<Company> getAllCompanies() {
		List<Company> companies = new ArrayList<Company>();

		Cursor cursor = mDB.query("company_details", null, null, null, null,
				null, "company_name");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			Company company = cursorToCompany(cursor);

			if (company != null) {
				companies.add(company);
			}

			cursor.moveToNext();
		}

		cursor.close();

		return companies;
	}

	/**
	 * This method is used to get a station from the database.
	 * 
	 * @param stationId
	 * @return
	 */
	public Station getStation(int stationId) {
		Cursor cursor = mDB.query("station_details", null, "station_id = "
				+ stationId, null, null, null, null);
		cursor.moveToFirst();
		Station station = cursorToStation(cursor);
		cursor.close();

		return station;
	}

	/**
	 * This method is used to get a company from the database.
	 * 
	 * @param companyName
	 * @return
	 */
	public Company getCompany(String companyName) {
		Cursor cursor = mDB.query("company_details", null, "company_name = "
				+ companyName, null, null, null, null);
		cursor.moveToFirst();
		Company company = cursorToCompany(cursor);
		cursor.close();

		return company;
	}

	/**
	 * This method is used to add a station to the favourite stations table in
	 * the database.
	 * 
	 * @param stationId
	 */
	public Boolean addToFavouriteStations(int stationId) {
		Cursor cursor = mDB.query("favourite_stations", null, null, null, null,
				null, null);

		Boolean result;

		if (cursor.getCount() >= maxFavourites) {
			result = false;
		} else {
			ContentValues values = new ContentValues();
			values.put("favourite_station_id", stationId);
			mDB.insert("favourite_stations", null, values);
			result = true;
		}

		cursor.close();

		return result;
	}

	/**
	 * This method is used to remove a station from the favourite stations table
	 * in the database.
	 * 
	 * @param stationId
	 */
	public void removeFromFavouriteStations(int stationId) {
		mDB.delete("favourite_stations", "favourite_station_id = " + stationId,
				null);
	}

	/**
	 * This method is used to remove all favourite stations from the favourite
	 * stations table in the database.
	 */
	public void removeAllFavouriteStations() {
		mDB.delete("favourite_stations", null, null);
	}

	/**
	 * This method is used to add a station to the recently viewed station table
	 * in the database.
	 * 
	 * @param stationId
	 */
	public void addToRecentlyViewedStations(int stationId) {
		ContentValues values = new ContentValues();
		values.put("recently_viewed_time", System.currentTimeMillis());
		Cursor cursor = mDB.query("recently_viewed_stations", null,
				"recently_viewed_station_id = " + stationId, null, null, null,
				null);

		if (cursor.getCount() == 0) {
			values.put("recently_viewed_station_id", stationId);
			mDB.insert("recently_viewed_stations", null, values);
		} else {
			mDB.update("recently_viewed_stations", values,
					"recently_viewed_station_id = " + stationId, null);
		}

		cursor.close();

		cursor = mDB.query("recently_viewed_stations", null, null, null, null,
				null, "recently_viewed_time");
		cursor.moveToFirst();

		int size = cursor.getCount();

		while (size > maxRecentlyViewed) {
			mDB.delete("recently_viewed_stations",
					"recently_viewed_station_id = " + cursor.getString(0), null);
			size--;
			cursor.moveToNext();
		}

		cursor.close();
	}

	/**
	 * This method is used to remove all Recently Viewed Stations from the
	 * database.
	 */
	public void removeAllRecentlyViewedStations() {
		mDB.delete("recently_viewed_stations", null, null);
	}

	/**
	 * This method is used to check if a station is already a favourite.
	 * 
	 * @param stationId
	 * @return
	 */
	public Boolean isFavourite(int stationId) {

		Cursor cursor = mDB.query("favourite_stations", null,
				"favourite_station_id = " + stationId, null, null, null, null);

		Boolean result;

		if (cursor.getCount() > 0) {
			result = true;
		} else {
			result = false;
		}

		cursor.close();

		return result;
	}

	public Integer getNumberOfFavouriteStations() {
		return null;

	}

	/**
	 * This class is used to get create a Station object from a cursor.
	 * 
	 * @param cursor
	 * @return
	 */
	private Station cursorToStation(Cursor cursor) {
		Station station = new Station();
		Address address = new Address();
		Company company = new Company();

		if (cursor.getCount() > 0) {
			station.setId(cursor.getInt(0));
			station.setName(cursor.getString(1));
			station.setApiCode(cursor.getString(2));

			company.setName(cursor.getString(3));
			company.setMode(cursor.getString(4));
			station.setCompany(company);

			address.setLocation(cursor.getString(5));
			address.setLatitude(cursor.getString(6));
			address.setLongitude(cursor.getString(7));
			station.setAddress(address);
		}

		return station;
	}

	/**
	 * This class is used to create a Company object from a cursor.
	 * 
	 * @param cursor
	 * @return
	 */
	private Company cursorToCompany(Cursor cursor) {
		Company company = new Company();
		ContactDetails details = new ContactDetails();

		if (cursor.getCount() > 0) {
			company.setName(cursor.getString(0));
			company.setMode(cursor.getString(1));

			details.setEmail(cursor.getString(2));
			details.setTelephone(cursor.getString(3));
			details.setWebsite(cursor.getString(4));
			details.setFacebook(cursor.getString(5));
			details.setTwitter(cursor.getString(6));

			company.setDetails(details);
		}

		return company;
	}

}
