package com.xplor.local.syncing.download;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import com.xplor.helper.DataBaseHelper;

public class ProjectVideoHelper extends DataBaseHelper {

	public ProjectVideoHelper(Context context) {
		super(context);
	}

	// Modify syncing base d on isuploaded.
	public List<ProjectVideo> getAllVideoToUpload() {

		openDataBase();

		String querySearchData = "SELECT id, upload_filename FROM news_feeds WHERE is_data_uploaded = 0 " +
				"AND status = 1 AND upload_filename != '' AND upload_file_type = '2'";
		List<ProjectVideo> _list = new ArrayList<ProjectVideo>();
		Cursor corsur = mDataBase.rawQuery(querySearchData, null);

		if (corsur.moveToFirst()) {
			do {
				ProjectVideo obj = new ProjectVideo();
				obj.videoId = corsur.getLong(0);
				String fName = corsur.getString(1);
				obj.fileName = (fName == null) ? "" : fName;
				_list.add(obj);
			 } while (corsur.moveToNext());
		}
		if (corsur != null)
			corsur.close();
		mDataBase.close();
		return _list;

	}
}
