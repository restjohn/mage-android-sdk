package mil.nga.giat.mage.sdk.push;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import mil.nga.giat.mage.sdk.connectivity.ConnectivityUtility;
import mil.nga.giat.mage.sdk.datastore.observation.Attachment;
import mil.nga.giat.mage.sdk.datastore.observation.AttachmentHelper;
import mil.nga.giat.mage.sdk.exceptions.ObservationException;
import mil.nga.giat.mage.sdk.login.LoginTaskFactory;
import mil.nga.giat.mage.sdk.http.resource.ObservationResource;

public class AttachmentPushIntentService extends IntentService {
	
	private static final String LOG_NAME = AttachmentPushIntentService.class.getName();
	
	public static final String ATTACHMENT_PUSHED = "mil.nga.giat.mage.sdk.service.ATTACHMENT_PUSHED";
	
	public static final String ATTACHMENT_ID = "attachmentId";

	public AttachmentPushIntentService() {
		super(LOG_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!ConnectivityUtility.isOnline(getApplicationContext()) || LoginTaskFactory.getInstance(getApplicationContext()).isLocalLogin()) {
			Log.d(LOG_NAME, "Not connected.  Will try later.");
			return;
		}
		Long attachmentId = intent.getLongExtra(ATTACHMENT_ID, -1);
		Log.i(LOG_NAME, "Handling attachment: " + attachmentId);
		try {
			AttachmentHelper attachmentHelper = AttachmentHelper.getInstance(getApplicationContext());

			Attachment attachment = attachmentHelper.read(attachmentId);

			if (attachment.getUrl() != null) {
				Log.i(LOG_NAME, "Already pushed attachment " + attachmentId + ", skipping.");
				return;
			}

			Log.d(LOG_NAME, "Staging attachment with id: " + attachment.getId());
			attachmentHelper.stageForUpload(attachment);
			Log.d(LOG_NAME, "Pushing attachment with id: " + attachment.getId());

			ObservationResource observationResource = new ObservationResource(getApplicationContext());
			attachment = observationResource.createAttachment(attachment);
			if(attachment != null) {
				Log.d(LOG_NAME, "Pushed attachment with remote_id: " + attachment.getRemoteId());
			}
			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ATTACHMENT_PUSHED);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(ATTACHMENT_ID, attachmentId);
			sendBroadcast(broadcastIntent);
			
		} catch (ObservationException oe) {
			Log.e(LOG_NAME, "Error obtaining attachment: " + attachmentId, oe);
		} catch (Exception e) {
			Log.e(LOG_NAME, "Error pushing attachment: " + attachmentId, e);
		}
	}
}
