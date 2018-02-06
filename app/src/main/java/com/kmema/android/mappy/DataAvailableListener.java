package com.kmema.android.mappy;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kmema on 2/5/2018.
 */

public interface DataAvailableListener {
    interface onDataAvailable{
        public void displayDataInList(List<HashMap<String, String>> listData);
    }
}
