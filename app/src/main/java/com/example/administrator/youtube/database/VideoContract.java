package com.example.administrator.youtube.database;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 10/9/2017.
 */

public final class VideoContract {

    /*field table history*/
    public final class VideoHistory implements BaseColumns{

        public static final String TABLE_NAME = "history";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_VIDEO = "id_video";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static final String COLUMN_TITLE_CHANNEL ="title_channel";

        public static final String COLUMN_VIEW_COUNT = "view_count";

        public static final String COLUMN_DURATION = "duration";

        public static final String COLUMN_CHANNEL_ID = "channel_id";

        public static final String COLUMN_ESLAPSED_TIME = "eslapsed_time";

    }
    /*field table favourite*/
    public  final class VideoFavourite implements BaseColumns {

        public static final String TABLE_NAME = "favourite";


        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_VIDEO = "id_video";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static final String COLUMN_TITLE_CHANNEL ="title_channel";

        public static final String COLUMN_VIEW_COUNT = "view_count";

        public static final String COLUMN_DURATION = "duration";

        public static final String COLUMN_CHANNEL_ID = "channel_id";

        public static final String COLUMN_ESLAPSED_TIME = "eslapsed_time";
    }

    /*field table releted video */
    public  final class VideoReleted implements BaseColumns {

        public static final String TABLE_NAME = "releted";


        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_VIDEO = "id_video";

        public static final String COLUMN_ID_ROOT_VIDEO ="id_root_video";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static final String COLUMN_TITLE_CHANNEL ="title_channel";

        public static final String COLUMN_VIEW_COUNT = "view_count";

        public static final String COLUMN_DURATION = "duration";

        public static final String COLUMN_CHANNEL_ID = "channel_id";

        public static final String COLUMN_ESLAPSED_TIME = "eslapsed_time";
    }

    public  final class VideoDefault implements BaseColumns {

        public static final String TABLE_NAME = "default_video";


        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_VIDEO = "id_video";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static final String COLUMN_TITLE_CHANNEL ="title_channel";

        public static final String COLUMN_VIEW_COUNT = "view_count";

        public static final String COLUMN_DURATION = "duration";

        public static final String COLUMN_CHANNEL_ID = "channel_id";

        public static final String COLUMN_ESLAPSED_TIME = "eslapsed_time";
    }
}
