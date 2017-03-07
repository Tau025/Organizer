package com.devtau.organizer.util;

public enum PermissionCodes {

	MainMultiplyPermissions(250),
	PhotoPermission(251),
	StoragePermission(252),
	LocationPermission(253),
	ContactPermission(254),
	CameraForScanPermission(255),
	RegistrationImportantPermission(256);


	private int code;

	PermissionCodes(int permissionCode) {
		this.code = permissionCode;
	}

	public int getCode() {
		return code;
	}

	public static PermissionCodes fromType(int type){
		for (PermissionCodes status : values()){
			if (status.code == type){
				return status;
			}
		}
		return null;
	}
}
