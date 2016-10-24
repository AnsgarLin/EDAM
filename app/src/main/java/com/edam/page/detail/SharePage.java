package com.edam.page.detail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.edam.main.R;
import com.edam.share.fb.FBShareHelper;
import com.edam.util.Logger;
import com.edam.util.Util;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class SharePage extends TextView{

	public SharePage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public SharePage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SharePage(Context context) {
		super(context);
		init();
	}

	private void init() {
	}
	// ====================================================================================================
	public void setShareURL(final String shareURL) {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getText().equals(Util.SHARE_PLATFORM[0])) {
					// From FB
					publish(shareURL);
				}
				else if (getText().equals(Util.SHARE_PLATFORM[1])) {
					// From line
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://line.naver.jp/R/msg/text/?" + URLEncoder.encode(shareURL, "UTF-8")));
						if (intent != null) {
							getContext().startActivity(intent);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void publish(String shareURL) {
		String[] fb = shareURL.split("\n");

		if (FBShareHelper.isFBInstall(getContext())) {
			// Publish the post using the Share Dialog
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder((Activity) getContext()).setLink(fb[1]).setName(fb[0]).setApplicationName("EDAM")
					.build();
			FBShareHelper.getInstance((Activity) getContext()).trackPendingDialogCall(shareDialog.present());
		} else {
			 publishFeedDialog(fb[0], fb[1]);
		}
	}

	private void publishFeedDialog(final String title, final String shareURL) {
		Bundle params = new Bundle();
		params.putString("name", title);
//		params.putString("caption", "Build great social apps and get more installs.");
		params.putString("link", shareURL);
//		params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
			WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getContext(), Session.getActiveSession(), params)).setOnCompleteListener(
					new OnCompleteListener() {

						@Override
						public void onComplete(Bundle values, FacebookException error) {
							if (error == null) {
								// When the story is posted, echo the success
								// and the post Id.
								final String postId = values.getString("post_id");
								if (postId != null) {
									Logger.d(getClass(), "Posted story, id: " + postId);
								} else {
									// User clicked the Cancel button
									Logger.d(getClass(), "Publish cancelled");
								}
							} else if (error instanceof FacebookOperationCanceledException) {
								// User clicked the "x" button
								Logger.d(getClass(), "Publish cancelled");
							} else {
								// Generic, ex: network error
								Logger.d(getClass(), getResources().getString(R.string.facebook_publish_network_error));
							}
						}

					}).build();
			feedDialog.show();
		} else {
			Session.openActiveSession((Activity) getContext(), true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (session.isOpened()) {
						Logger.d(getClass(), "Publish: " + title + "successfully");
						publishFeedDialog(title, shareURL);
					} else {
						Logger.d(getClass(), "FB session not opened ... ");
					}
				}
			});
		}
	}

}
