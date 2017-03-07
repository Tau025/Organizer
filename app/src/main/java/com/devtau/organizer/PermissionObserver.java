package com.devtau.organizer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import com.devtau.organizer.util.Logger;
import java.util.ArrayList;
import java.util.List;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionObserver {

	private static final String LOG_TAG = "PermissionObserver";
	private Context context;

	public PermissionObserver(Context context){
		this.context = context;
	}


	public boolean isPermissionDynamic() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	public boolean isReadStoragePermissionGranted(){
		return isPermissionGranted(READ_EXTERNAL_STORAGE);
	}

	public boolean isReadContactPermissionGranted(){
		return isPermissionGranted(READ_CONTACTS);
	}

	private boolean isPermissionGranted(String permission) {
		try {
			int selfPermission = ContextCompat.checkSelfPermission(context, permission);
			if (selfPermission == PackageManager.PERMISSION_GRANTED) {
				Logger.d(LOG_TAG, "Permission: " + permission + " is granted");
				return true;
			} else {
				Logger.d(LOG_TAG, "Permission: " + permission + " is denied");
				return false;
			}
		} catch (Exception e) {
			Logger.e(LOG_TAG, "Failed to check permission");
			return false;
		}
	}
	public String[] createContactPermission(){
		return new String[]{READ_CONTACTS};
	}


	public String[] createPermissionForStorage(){
		final List<String> permissionList = new ArrayList<>();
		addPermission(permissionList, WRITE_EXTERNAL_STORAGE);
		addPermission(permissionList, READ_EXTERNAL_STORAGE);
		return permissionList.toArray(new String[0]);
	}

	private void addPermission(List<String> permissionList, String permission){
		Logger.d(LOG_TAG, "Permission " + permission + " value is: " + ContextCompat.checkSelfPermission(context, permission));
		if (!isPermissionGranted(permission)){
			permissionList.add(permission);
		}
	}
}
