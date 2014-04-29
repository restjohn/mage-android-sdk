package mil.nga.giat.mage.sdk.push;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import mil.nga.giat.mage.sdk.ConnectivityAwareIntentService;
import mil.nga.giat.mage.sdk.R;
import mil.nga.giat.mage.sdk.connectivity.ConnectivityUtility;
import mil.nga.giat.mage.sdk.datastore.location.Location;
import mil.nga.giat.mage.sdk.datastore.location.LocationHelper;
import mil.nga.giat.mage.sdk.http.post.MageServerPostRequests;
import mil.nga.giat.mage.sdk.preferences.PreferenceHelper;
import android.content.Intent;
import android.util.Log;

public class LocationPushIntentService extends ConnectivityAwareIntentService {

	private static final String LOG_NAME = LocationPushIntentService.class.getName();

	// in milliseconds
	private long pushFrequency;
	
	protected AtomicBoolean pushSemaphore = new AtomicBoolean(false);

	public LocationPushIntentService() {
		super(LOG_NAME);
	}

	protected final long getLocationPushFrequency() {
		return PreferenceHelper.getInstance(getApplicationContext()).getValue(R.string.locationPushFrequencyKey, Long.class, R.string.locationPushFrequencyDefaultValue);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		pushFrequency = getLocationPushFrequency();
		while (true) {
			if (isConnected) {
				pushFrequency = getLocationPushFrequency();
				LocationHelper locationHelper = LocationHelper.getInstance(getApplicationContext());
				// FIXME: causing a null pointer exception, because of the last_modified stuff.
				List<Location> locations = locationHelper.getCurrentUserLocations(10L);
				for (Location location : locations) {
					MageServerPostRequests.postLocation(location, getApplicationContext());
				}
			} else {
				Log.d(LOG_NAME, "The device is currently disconnected. Can't push locations.");
				pushFrequency = Math.min(pushFrequency * 2, 30 * 60 * 1000);
			}
			long lastFetchTime = new Date().getTime();
			long currentTime = new Date().getTime();

			try {
				while (lastFetchTime + pushFrequency > (currentTime = new Date().getTime())) {
					synchronized (pushSemaphore) {
						Log.d(LOG_NAME, "Location push sleeping for " + (lastFetchTime + pushFrequency - currentTime) + "ms.");
						pushSemaphore.wait(lastFetchTime + pushFrequency - currentTime);
						if (pushSemaphore.get() == true) {
							break;
						}
					}
				}
				synchronized (pushSemaphore) {
					pushSemaphore.set(false);
				}
			} catch (InterruptedException ie) {
				Log.e(LOG_NAME, "Interupted.  Unable to sleep " + pushFrequency, ie);
			} finally {
				isConnected = ConnectivityUtility.isOnline(getApplicationContext());
			}
		}
	}
	
	@Override
	public void onAnyConnected() {
		super.onAnyConnected();
		synchronized (pushSemaphore) {
			pushSemaphore.set(true);
			pushSemaphore.notifyAll();
		}
	}
}