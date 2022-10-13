package dtri.com.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;

import dtri.com.db.entity.ERP_PM_Entity;

public class PMTempBean {

	// 物件最新狀態Temp
	// ID, 物件
	private LinkedHashMap<String, ERP_PM_Entity> mapPmEntity;

	// 鎖定物件
	// lockPmID: MOC_ID, 使用者名稱
	// lockPmTime: MOC_ID, 使用時間(限制5分鐘?)
	// mocTagId: MOC_ID, 使用者名稱
	private Map<String, String> lockPmID;
	private Map<String, Long> lockPmTime;
	private Map<String, String> mocTagID; // 顯示-有修改標記
	// 更新欄位
	private String update_cell;
	private String update_value;
	private int moc_priority;
	private String moc_week;
	private String mpr_date;
	private JSONArray excel_json;

	// 推播對象
	private String userName;
	// 推播訊息
	private String message;

	public PMTempBean() {
		lockPmID = new HashMap<String, String>();
		lockPmTime = new HashMap<String, Long>();
		mocTagID = new HashMap<String, String>();
	}

	public LinkedHashMap<String, ERP_PM_Entity> getMapPmEntity() {
		return mapPmEntity;
	}

	public void setMapPmEntity(LinkedHashMap<String, ERP_PM_Entity> mapPmEntity) {
		if (this.mapPmEntity != null) {
			this.mapPmEntity.clear();
		}
		this.mapPmEntity = mapPmEntity;
	}

	public Map<String, String> getLockPmID() {
		return lockPmID;
	}

	public void setLockPmID(Map<String, String> lockPmID) {
		this.lockPmID = lockPmID;
	}

	public Map<String, Long> getLockPmTime() {
		return lockPmTime;
	}

	public void setLockPmTime(Map<String, Long> lockPmTime) {
		this.lockPmTime = lockPmTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUpdate_cell() {
		return update_cell;
	}

	public void setUpdate_cell(String update_cell) {
		this.update_cell = update_cell;
	}

	public String getUpdate_value() {
		return update_value;
	}

	public void setUpdate_value(String update_value) {
		this.update_value = update_value;
	}

	public int getMoc_priority() {
		return moc_priority;
	}

	public void setMoc_priority(int moc_priority) {
		this.moc_priority = moc_priority;
	}

	public String getMpr_date() {
		return mpr_date;
	}

	public void setMpr_date(String mpr_date) {
		this.mpr_date = mpr_date;
	}

	public JSONArray getExcel_json() {
		return excel_json;
	}

	public void setExcel_json(JSONArray excel_json) {
		this.excel_json = excel_json;
	}

	public String getMoc_week() {
		return moc_week;
	}

	public void setMoc_week(String moc_week) {
		this.moc_week = moc_week;
	}

	public Map<String, String> getMocTagId() {
		return mocTagID;
	}

	public void setMocTagId(Map<String, String> mocTagID) {
		this.mocTagID = mocTagID;
	}

}
