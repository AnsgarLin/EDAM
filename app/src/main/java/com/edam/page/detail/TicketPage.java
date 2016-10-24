package com.edam.page.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.edam.util.Logger;

public class TicketPage extends TextView{

	public TicketPage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public TicketPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TicketPage(Context context) {
		super(context);
		init();
	}

	private void init() {
	}
	// ====================================================================================================
	public void setTicketURL(final String ticketURL) {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Logger.d(getClass(), "TicketURL: " + ticketURL);
				if (!ticketURL.equals("") && !ticketURL.equals("cafe") && !ticketURL.equals("TBD")) {
					int startIndex = 0;
					if (ticketURL.contains("free-get_")) {
						startIndex = "free-get_".length();
					} else if (ticketURL.contains("official_")) {
						startIndex = "official_".length();
					}
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ticketURL.substring(startIndex, ticketURL.length())));
					if (intent != null) {
						getContext().startActivity(intent);
					}
				}
			}
		});
	}
}
