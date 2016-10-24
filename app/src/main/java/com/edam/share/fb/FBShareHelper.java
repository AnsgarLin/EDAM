package com.edam.share.fb;

import android.app.Activity;
import android.content.Context;

import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

public class FBShareHelper extends UiLifecycleHelper {
	public static FBShareHelper singleton;

	private FBShareHelper(Activity activity, StatusCallback callback) {
		super(activity, callback);
	}

	public static FBShareHelper getInstance(Activity activity) {
		if (singleton == null) {
			singleton = new FBShareHelper(activity, null);
		}
		return singleton;
	}

	public static boolean isFBInstall(Context context) {
		return FacebookDialog.canPresentShareDialog(context.getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG);

	}
}
