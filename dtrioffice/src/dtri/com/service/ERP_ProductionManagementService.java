package dtri.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.bean.JsonObjBean;
import dtri.com.bean.JsonTemplateBean;
import dtri.com.bean.PMTempBean;
import dtri.com.controller.ProductionManagementController;
import dtri.com.db.entity.ERP_PM_Entity;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.db.mssql.dao.ERP_ProductionManagementDao;
import dtri.com.db.pgsql.dao.ProductionManagementDao;
import dtri.com.db.pgsql.dao.ProductionRecordsDAO;
import dtri.com.tools.Fm_Time_Model;
import dtri.com.tools.JsonDataModel;

@Transactional
@Service
public class ERP_ProductionManagementService {
	@Autowired
	private ERP_ProductionManagementDao erp_pm_Dao;
	@Autowired
	private ProductionManagementDao pm_Dao;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ProductionManagementController pmController;
	@Autowired
	private ProductionRecordsDAO daoRecordsDAO;
	// 共用
	private static PMTempBean pmTempBean = new PMTempBean();

	/**
	 * @param entity 查詢使用者資料
	 * @return 查詢後清單
	 * 
	 **/
	public List<ERP_PM_Entity> searchProductionManagement(ERP_PM_Entity entity) {
		List<ERP_PM_Entity> entities = pm_Dao.searchERP_PM_List(entity);
		return entities;
	}

	/** 只同步 [特定 製令單moc_id] ERP資料庫 & Temp 資料 **/
	private Boolean doSameDataProductionManagement(String moc_id) {
		// 更新資料庫
		Boolean check = true;
		try {
			LinkedHashMap<String, ERP_PM_Entity> all_temp_new = new LinkedHashMap<String, ERP_PM_Entity>();
			all_temp_new = pmTempBean.getMapPmEntity();
			ERP_PM_Entity pm_new = all_temp_new.get(moc_id);
			pm_Dao.updateOneFromERP(pm_new);
			check = false;
		} catch (Exception e) {
			check = false;
		}
		return check;
	}

	/** 每60秒同步 ERP資料庫 & Temp 資料 **/
	private Boolean doSameDataProductionManagement() {
		Boolean check = true;
		try {
			// Step1. 取得(ERP) 資料 (僅侷限 A521+A511)
			List<ERP_PM_Entity> erp_entities = erp_pm_Dao.getERP_PM_List();
			Map<String, JSONArray> tag_all_id = pmTempBean.getMocTagAllID();
			Map<String, String> tag_all_time = pmTempBean.getMocTagAllTime();

			int update_check = 0;
			ERP_PM_Entity pmTemp = new ERP_PM_Entity();
			LinkedHashMap<String, ERP_PM_Entity> all_temp = new LinkedHashMap<String, ERP_PM_Entity>();

			// Step2.填入資料(如果沒資料去 資料庫看看先)
			all_temp = pmTempBean.getMapPmEntity();
			if (all_temp == null) {
				// 取得(延展系統) 資料(僅 有效=1)
				ERP_PM_Entity search_data = new ERP_PM_Entity();
				search_data.setUseful(1);// 有效=1 無效=2
				List<ERP_PM_Entity> entities = pm_Dao.searchERP_PM_List(search_data);
				all_temp = new LinkedHashMap<String, ERP_PM_Entity>();
				for (ERP_PM_Entity erp_PM_Entity : entities) {
					all_temp.put(erp_PM_Entity.getMoc_id(), erp_PM_Entity);
				}
			}
			// Step3. 暫時標記3
			pm_Dao.updateToUseful3From1();
			// Step4. 更新資料矯正
			JSONArray tag_all_arr = new JSONArray();
			String tag_all_str = "";
			int now_year = Fm_Time_Model.getYear(new Date());
			int now_week = Fm_Time_Model.getWeek(new Date());
			int old_week = 0;
			int old_year = 0;
			int erp_week = 0;
			int erp_year = 0;
			boolean same_check = false;
			for (ERP_PM_Entity one : erp_entities) {
				one.setMoc_id(one.getMoc_id().replaceAll(" ", ""));
				one.setMoc_ta009(Fm_Time_Model.to_yyMMdd(one.getMoc_ta009())); // 預計開工
				one.setMoc_ta010(Fm_Time_Model.to_yyMMdd(one.getMoc_ta010())); // 預計完工
				same_check = false;

				// Step5.是否要同 pmTempBean 一起更新
				if (all_temp != null && all_temp.containsKey(one.getMoc_id())) {// 工單號
					// [是]-更新舊資料
					// 生管-製令單
					ProductionRecordsEntity pre_one = daoRecordsDAO.beforeCheckAddOne(one.getMoc_id());
					// 取得暫存
					pmTemp = null;
					pmTemp = all_temp.get(one.getMoc_id());

					// Step5-2. 如果有變化ERP 資料(預計生產數 預計開工 預計完工 產品品號 生產備註 )
					tag_all_arr = new JSONArray();
					tag_all_str = "";
					if (tag_all_id.containsKey(one.getMoc_id())) {
						tag_all_arr = tag_all_id.get(one.getMoc_id());
						tag_all_str = tag_all_arr.toString();
					}
					// 預計開工
					if (!pmTemp.getMoc_ta009().equals(one.getMoc_ta009()) && tag_all_str.indexOf("s_1") < 0) {
						tag_all_arr.put("s_1");
						same_check = true;
					}
					// 預計完工
					if (!pmTemp.getMoc_ta010().equals(one.getMoc_ta010()) && tag_all_str.indexOf("s_2") < 0) {
						tag_all_arr.put("s_2");
						same_check = true;
					}
					// 產品品號
					if (!pmTemp.getMoc_ta006().equals(one.getMoc_ta006()) && tag_all_str.indexOf("s_4") < 0) {
						tag_all_arr.put("s_4");
						same_check = true;
					}
					// 預計產量
					if ((!("" + pmTemp.getMoc_ta015()).equals(one.getMoc_ta015() + "")) && tag_all_str.indexOf("s_7") < 0) {
						tag_all_arr.put("s_7");
						same_check = true;
					}
					// 製令備註
					if (!pmTemp.getMoc_ta029().equals(one.getMoc_ta029()) && tag_all_str.indexOf("s_10") < 0) {
						tag_all_arr.put("s_10");
						same_check = true;
					}

					// Step5-3.資料更新
					String pps = "準備中";
					String ppt = "";
					if (pre_one != null) {
						ppt = pre_one.getProduct_type();
						switch ("" + pre_one.getProduct_progress()) {
						case "2":
							pps = "注意事(已開單)";
							break;
						case "3":
							pps = "流程卡(已開單)";
							break;
						}
					}
					// ERP 有修改?
					if (same_check) {
						tag_all_id.put(one.getMoc_id(), tag_all_arr);
						tag_all_time.put(one.getMoc_id(), Fm_Time_Model.to_yyMMdd(new Date()));
						one.setSys_modify_date(new Date());
						one.setSys_modify_user("System");
					} else {
						one.setSys_modify_date(pmTemp.getSys_modify_date());
						one.setSys_modify_user(pmTemp.getSys_modify_user());
					}
					one.setSys_create_date(pmTemp.getSys_create_date());
					one.setSys_create_user(pmTemp.getSys_create_user());

					one.setBom_kind(ppt);// (查)BOM 類型

					one.setMoc_note(pmTemp.getMoc_note());
					one.setMoc_status(pps);// (查)開單狀態
					one.setMoc_priority(pmTemp.getMoc_priority());
					// 物控-物料
					one.setMpr_note(pmTemp.getMpr_note());
					one.setMpr_date(pmTemp.getMpr_date());// 最後交齊日
					// 倉庫-撿料
					one.setIvn_note(pmTemp.getIvn_note());
					one.setIvn_items(pmTemp.getIvn_items());
					one.setIvn_status(pmTemp.getIvn_status());// 1=齊料 2=未齊 3=未備
					// 產線
					one.setMes_note(pmTemp.getMes_note());
					old_week = Integer.parseInt(pmTemp.getMoc_week().split("-W")[1]);
					old_year = Integer.parseInt(pmTemp.getMoc_week().split("-W")[0]);
					// 如果(同一年含去年)且(週期小於本周)
					if (old_year <= now_year && old_week < now_week) {
						one.setMoc_week(now_year + "-W" + String.format("%02d", now_week));
					} else if (!pmTemp.getMoc_ta009().equals(one.getMoc_ta009())) {// 週期有修改
						erp_week = Fm_Time_Model.getWeek(Fm_Time_Model.toDate(one.getMoc_ta009()));
						erp_year = Integer.parseInt(one.getMoc_ta009().split("-")[0]);
						one.setMoc_week(erp_year + "-W" + String.format("%02d", erp_week));
					} else {
						one.setMoc_week(pmTemp.getMoc_week());
					}
					// 1.未生產,2.已發料,3.生產中,Y.已完工,y.指定完工
					switch (one.getMoc_ta011()) {
					case "1":
						one.setMoc_ta011("未生產");
						break;
					case "2":
						one.setMoc_ta011("已發料");
						break;
					case "3":
						one.setMoc_ta011("生產中");
						break;
					case "Y":
						one.setMoc_ta011("已完工");
						break;
					case "y":
						one.setMoc_ta011("指定完工");
						break;
					default:
						break;
					}
					// 系統
					one.setNote("");
					one.setUseful(1);

				} else {

					// [否]-新增資料
					one.setBom_kind("");// (查)BOM 類型
					// 生管-製令單
					JSONArray new_notes = new JSONArray();
					if (one.getMoc_ta054() != null && !one.getMoc_ta054().equals("")) {
						JSONObject new_note = new JSONObject();
						new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
						new_note.put("user", one.getMoc_cuser());
						new_note.put("ms", one.getMoc_ta054());
						new_notes.put(new_note);
					}
					one.setMoc_note(new_notes.toString());
					one.setMoc_status("");// (查)開單狀態
					one.setMoc_priority(100);
					// 物控-物料
					one.setMpr_note("[]");
					one.setMpr_date("");// 最後交齊日
					// 倉庫-撿料
					one.setIvn_note("[]");
					one.setIvn_items(0);
					one.setIvn_status(3);// 1=齊料 2=未齊 3=未備
					// 產線
					one.setMes_note("[]");
					// 系統
					one.setNote("");
					one.setUseful(1);
					one.setSys_create_date(new Date());
					one.setSys_create_user("System");
					one.setSys_modify_date(new Date());
					one.setSys_modify_user("System");

					erp_week = Fm_Time_Model.getWeek(Fm_Time_Model.toDate(one.getMoc_ta009()));
					erp_year = Integer.parseInt(one.getMoc_ta009().split("-")[0]);
					// 如果(同一年含去年)且(週期小於本周)
					if (erp_year <= now_year && erp_week < now_week) {
						erp_week = now_week;
						erp_year = now_year;
					}
					one.setMoc_week(erp_year + "-W" + String.format("%02d", erp_week));
					// 1.未生產,2.已發料,3.生產中,Y.已完工,y.指定完工
					switch (one.getMoc_ta011()) {
					case "1":
						one.setMoc_ta011("未生產");
						break;
					case "2":
						one.setMoc_ta011("已發料");
						break;
					case "3":
						one.setMoc_ta011("生產中");
						break;
					case "Y":
						one.setMoc_ta011("已完工");
						break;
					case "y":
						one.setMoc_ta011("指定完工");
						break;
					default:
						break;
					}
				}
				try {
					byte bytes[] = one.toString().getBytes("UTF-8");
					Checksum checksum = new CRC32();
					checksum.update(bytes, 0, bytes.length);
					one.setSys_check_sum(String.valueOf(checksum.getValue()));
				} catch (Exception e) {
					one.setSys_check_sum("");
					e.printStackTrace();
				}

				update_check = pm_Dao.updateOneFromERP(one);
				// 沒資料則新增
				if (update_check == 0) {
					pm_Dao.addedOne(one);
					// Step. 如果有變化ERP 資料(預計生產數 預計開工 預計完工 產品品號 生產備註 )
					tag_all_arr = new JSONArray();
					tag_all_arr.put("s_0").put("s_1").put("s_2").put("s_3").put("s_4").put("s_5").put("s_6").put("s_7");
					tag_all_id.put(one.getMoc_id(), tag_all_arr);
					tag_all_time.put(one.getMoc_id(), Fm_Time_Model.to_yyMMdd(new Date()));
				}
			}
			ERP_PM_Entity search_data = new ERP_PM_Entity();
			search_data.setUseful(3);// 有效=1 無效=2
			// Step6.取得不再活耀 製令單
			List<ERP_PM_Entity> erp_entities_end = pm_Dao.searchERP_PM_List(search_data);
			Map<String, String> mocTagId = pmTempBean.getMocTagId();
			// 標記成已 結束->更新最後一次
			for (ERP_PM_Entity one : erp_entities_end) {
				ERP_PM_Entity erp_end = erp_pm_Dao.getERP_PM_End_List(one);
				one.setMoc_ta015(erp_end.getMoc_ta015());
				one.setMoc_ta017(erp_end.getMoc_ta017());
				one.setMoc_ta009(erp_end.getMoc_ta009());
				one.setMoc_ta010(erp_end.getMoc_ta010());
				one.setMoc_ta029(erp_end.getMoc_ta029());
				one.setCop_tc012(erp_end.getCop_tc012());
				one.setOrder_id(erp_end.getOrder_id());
				switch (erp_end.getMoc_ta011()) {
				case "1":
					one.setMoc_ta011("未生產");
					break;
				case "2":
					one.setMoc_ta011("已發料");
					break;
				case "3":
					one.setMoc_ta011("生產中");
					break;
				case "Y":
					one.setMoc_ta011("已完工");
					break;
				case "y":
					one.setMoc_ta011("指定完工");
					break;
				default:
					break;
				}
				update_check = pm_Dao.updateOneFromERP(one);
				// 生管標記-關閉
				if (mocTagId.containsKey(one.getMoc_id())) {
					mocTagId.remove(one.getMoc_id());
					pmTempBean.setMocTagId(mocTagId);
				}
			}

			// Step7. 暫時標記3 標記成已 結束
			pm_Dao.updateToUseful2From3();

			// 取得(延展系統) 資料(僅 有效=1)
			search_data = new ERP_PM_Entity();
			search_data.setUseful(1);// 有效=1 無效=2
			List<ERP_PM_Entity> entities = pm_Dao.searchERP_PM_List(search_data);
			// Step7. 更新Temp
			LinkedHashMap<String, ERP_PM_Entity> all_temp_new = new LinkedHashMap<String, ERP_PM_Entity>();
			for (ERP_PM_Entity erp_PM_Entity : entities) {
				all_temp_new.put(erp_PM_Entity.getMoc_id(), erp_PM_Entity);
			}
			pmTempBean.setMapPmEntity(all_temp_new);
			pmTempBean.setMocTagAllID(tag_all_id);
			pmTempBean.setMocTagAllTime(tag_all_time);
			// Step8. 如果時間到 清除資料
			String hms = Fm_Time_Model.to_Hms(new Date());
			if (hms.equals("12:30:00") || hms.equals("12:31:00") || hms.equals("12:32:00")) {
				pmTempBean.setMocTagAllID(new HashMap<String, JSONArray>());
				pmTempBean.setMocTagAllTime(new HashMap<String, String>());
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return check;
	}

	/** 處理資訊 **/
	public synchronized Boolean doDataProductionManagement(String action, PMTempBean pmNewTempBean) {
		Boolean check = false;
		// 對象: all = 全體廣播(用於客戶端更新),
		// 對象: 指定人 = 特定廣播(用於 訊息成功失敗),
		// 對象: server = 後端資料重整,
		Map<String, String> mocTagId = pmTempBean.getMocTagId();
		String sendWho = pmNewTempBean.getUserName();
		JSONObject data = new JSONObject();
		JSONArray moc_data = new JSONArray();
		JSONArray moc_id_lock = new JSONArray();
		JSONArray moc_id_tag = new JSONArray();
		JSONArray moc_id_tag_all = new JSONArray();
		String message = "";
		switch (sendWho) {
		case "all":
			Map.Entry<String, String> new_item = pmNewTempBean.getLockPmID().entrySet().iterator().next();
			String new_id = new_item.getKey();
			String new_name = new_item.getValue();
			Long new_time = pmNewTempBean.getLockPmTime().get(new_id);

			// =============動作?=============
			switch (action) {
			case "only_lock":
				// 鎖定時 已經有人用?
				if (pmTempBean.getLockPmID().containsKey(new_id)) {
					String old_id = new_id;
					Long old_time = pmTempBean.getLockPmTime().get(new_id);
					String old_name = pmTempBean.getLockPmID().get(new_id);
					if (old_name.equals(new_name) || new_time - old_time > 300000L) {
						// 不同一人+超時?
						pmTempBean.getLockPmID().put(old_id, new_name);
						pmTempBean.getLockPmTime().put(old_id, new_time);
						message = "OK";
						check = true;
					} else {
						// 不能解鎖 別人正在使用
						message = "Fail";
						check = false;
					}
				} else {
					// 沒人用 登記鎖定
					pmTempBean.getLockPmID().put(new_id, new_name);
					pmTempBean.getLockPmTime().put(new_id, new_time);
					message = "OK";
					check = true;
				}
				break;
			case "only_unlock":
				// 解鎖時 已經有人用?
				if (pmTempBean.getLockPmID().containsKey(new_id)) {
					String old_name = pmTempBean.getLockPmID().get(new_id);
					// 同一人?
					if (old_name.equals(new_name)) {
						pmTempBean.getLockPmID().remove(new_id);
						pmTempBean.getLockPmTime().remove(new_id);
					}
				}
				check = true;
				message = "OK";
				break;
			case "moc_tag":
				// 生管-標記已讀
				mocTagId = pmTempBean.getMocTagId();
				// 必須是同一人+同一筆工單
				if (mocTagId.containsKey(new_id) && mocTagId.get(new_id).equals(new_name)) {
					mocTagId.remove(new_id);
					pmTempBean.setMocTagId(mocTagId);
				}
				// 解鎖時 已經有人用?
				if (pmTempBean.getLockPmID().containsKey(new_id)) {
					String old_name = pmTempBean.getLockPmID().get(new_id);
					// 同一人?
					if (old_name.equals(new_name)) {
						pmTempBean.getLockPmID().remove(new_id);
						pmTempBean.getLockPmTime().remove(new_id);
					}
				}
				break;
			case "only_unlock_save":
				// 標記修改
				JSONArray tagAllID = new JSONArray();
				if (pmTempBean.getMocTagAllID().containsKey(new_id)) {
					tagAllID = pmTempBean.getMocTagAllID().get(new_id);
				}
				// 指定更新
				// {"date":"2022-01-02 10:20:59", "user":"admin", "ms":"XXXX"}
				// 生管備註更新
				LinkedHashMap<String, ERP_PM_Entity> upd_pms = pmTempBean.getMapPmEntity();
				ERP_PM_Entity upd_pm = upd_pms.get(new_id);
				if (pmNewTempBean.getUpdate_cell().equals("moc_note")) {
					// 多筆資料
					if (pmNewTempBean.getExcel_json().length() > 0) {
						pmNewTempBean.getExcel_json().forEach(jys -> {
							// 如果[pmTempBean]沒有製令單->不更新
							JSONObject one = (JSONObject) jys;
							if (pmTempBean.getMapPmEntity().containsKey(one.getString("moc_id"))) {
								JSONObject new_note = new JSONObject();
								ERP_PM_Entity upd_pm_one = pmTempBean.getMapPmEntity().get(one.getString("moc_id"));
								JSONArray new_notes = new JSONArray(upd_pm_one.getMoc_note());
								// 標記修改
								JSONArray tagAllIDs = new JSONArray();
								if (pmTempBean.getMocTagAllID().containsKey(one.getString("moc_id"))) {
									tagAllIDs = pmTempBean.getMocTagAllID().get(one.getString("moc_id"));
								}
								// 資訊(ms)
								if (one.has("ms") && !one.getString("ms").equals("")) {
									new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
									new_note.put("user", one.getString("user"));
									new_note.put("ms", one.getString("ms"));
									new_notes.put(new_note);
									upd_pm_one.setSys_modify_date(new Date());
									upd_pm_one.setSys_modify_user(new_name);
									upd_pm_one.setMoc_note(new_notes.toString());
									// 標記修改
									if (tagAllIDs.toString().indexOf("s_15") < 0) {
										tagAllIDs.put("s_15");
									}
								}
								// 優先權(priority)
								if (one.has("priority") && !one.getString("priority").equals("")) {
									upd_pm_one.setMoc_priority(Integer.parseInt(one.getString("priority")));
									upd_pm_one.setSys_modify_date(new Date());
									upd_pm_one.setSys_modify_user(new_name);
									// 標記修改
									if (tagAllIDs.toString().indexOf("s_0") < 0) {
										tagAllIDs.put("s_0");
									}
								}
								// 周排(week)
								if (one.has("week") && !one.getString("week").equals("")) {
									upd_pm_one.setMoc_week(one.getString("week"));
									upd_pm_one.setSys_modify_date(new Date());
									upd_pm_one.setSys_modify_user(new_name);
								}
								pmTempBean.getMapPmEntity().put(one.getString("moc_id"), upd_pm_one);
								// 標記修改
								pmTempBean.getMocTagAllTime().put(one.getString("moc_id"), Fm_Time_Model.to_yyMMdd(new Date()));
								pmTempBean.getMocTagAllID().put(one.getString("moc_id"), tagAllIDs);
							}
						});
					} else {
						// 單一資料
						if (!pmNewTempBean.getUpdate_value().equals("")) {
							JSONObject new_note = new JSONObject();
							JSONArray new_notes = new JSONArray(upd_pm.getMoc_note());
							new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
							new_note.put("user", new_name);
							new_note.put("ms", pmNewTempBean.getUpdate_value());
							new_notes.put(new_note);
							upd_pm.setMoc_note(new_notes.toString());
							upd_pm.setSys_modify_date(new Date());
							upd_pm.setSys_modify_user(new_name);
							// 標記已讀
							mocTagId = pmTempBean.getMocTagId();
							if (mocTagId.containsKey(new_id) && mocTagId.get(new_id).equals(new_name)) {
								mocTagId.remove(new_id);
								pmTempBean.setMocTagId(mocTagId);
							}
							// 標記修改
							if (tagAllID.toString().indexOf("s_15") < 0) {
								tagAllID.put("s_15");
							}
						}
						// 標記修改-優先權
						if (tagAllID.toString().indexOf("s_0") < 0 && upd_pm.getMoc_priority() != pmNewTempBean.getMoc_priority()) {
							tagAllID.put("s_0");
						}
						upd_pm.setMoc_priority(pmNewTempBean.getMoc_priority());// 優先權
						upd_pm.setMoc_week(pmNewTempBean.getMoc_week());// 週期
						pmTempBean.getMapPmEntity().put(new_id, upd_pm);
						// 標記修改
						pmTempBean.getMocTagAllID().put(new_id, tagAllID);
						pmTempBean.getMocTagAllTime().put(new_id, Fm_Time_Model.to_yyMMdd(new Date()));
					}
				}
				// 物控
				else if (pmNewTempBean.getUpdate_cell().equals("mpr_note")) {
					// 多筆資料
					if (pmNewTempBean.getExcel_json().length() > 0) {
						pmNewTempBean.getExcel_json().forEach(jys -> {
							// 如果[pmTempBean]沒有製令單->不更新
							JSONObject one = (JSONObject) jys;
							if (pmTempBean.getMapPmEntity().containsKey(one.getString("moc_id"))) {
								JSONObject new_note = new JSONObject();
								ERP_PM_Entity upd_pm_one = pmTempBean.getMapPmEntity().get(one.getString("moc_id"));
								JSONArray new_notes = new JSONArray(upd_pm_one.getMpr_note());
								// 標記修改
								JSONArray tagAllIDs = new JSONArray();
								if (pmTempBean.getMocTagAllID().containsKey(one.getString("moc_id"))) {
									tagAllIDs = pmTempBean.getMocTagAllID().get(one.getString("moc_id"));
								}
								// 資訊(ms)
								if (!one.getString("ms").equals("")) {
									new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
									new_note.put("user", one.getString("user"));
									new_note.put("ms", one.getString("ms"));
									new_notes.put(new_note);
									upd_pm_one.setMpr_note(new_notes.toString());
									upd_pm_one.setSys_modify_date(new Date());
									upd_pm_one.setSys_modify_user(new_name);
									// 標記有修正
									Map<String, String> mocTagIds = pmTempBean.getMocTagId();
									mocTagIds.put(one.getString("moc_id"), pmTempBean.getMapPmEntity().get(one.getString("moc_id")).getMoc_cuser());
									pmTempBean.setMocTagId(mocTagIds);
									// 標記修改
									if (tagAllIDs.toString().indexOf("s_17") < 0) {
										tagAllIDs.put("s_17");
									}
								}
								// 齊料日(mpr_date)
								if (!one.getString("mpr_date").equals("")) {
									upd_pm_one.setMpr_date(one.getString("mpr_date"));
									upd_pm_one.setSys_modify_date(new Date());
									upd_pm_one.setSys_modify_user(new_name);
									// 標記修改
									if (tagAllIDs.toString().indexOf("s_16") < 0) {
										tagAllIDs.put("s_16");
									}
								}
								pmTempBean.getMapPmEntity().put(one.getString("moc_id"), upd_pm_one);
								// 標記修改
								pmTempBean.getMocTagAllTime().put(one.getString("moc_id"), Fm_Time_Model.to_yyMMdd(new Date()));
								pmTempBean.getMocTagAllID().put(one.getString("moc_id"), tagAllIDs);
							}
						});
					} else {
						// 單筆資料
						if (!pmNewTempBean.getUpdate_value().equals("")) {
							JSONObject new_note = new JSONObject();
							JSONArray new_notes = new JSONArray(upd_pm.getMpr_note());
							new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
							new_note.put("user", new_name);
							new_note.put("ms", pmNewTempBean.getUpdate_value());
							new_notes.put(new_note);
							upd_pm.setMpr_note(new_notes.toString());
							upd_pm.setSys_modify_date(new Date());
							upd_pm.setSys_modify_user(new_name);
							// 標記有修正
							mocTagId = pmTempBean.getMocTagId();
							mocTagId.put(new_id, pmTempBean.getMapPmEntity().get(new_id).getMoc_cuser());
							pmTempBean.setMocTagId(mocTagId);
							// 標記修改
							if (tagAllID.toString().indexOf("s_17") < 0) {
								tagAllID.put("s_17");
							}
						}
						// 齊料日
						if (!pmNewTempBean.getMpr_date().equals("")) {
							upd_pm.setMpr_date(pmNewTempBean.getMpr_date());
							upd_pm.setSys_modify_date(new Date());
							upd_pm.setSys_modify_user(new_name);
							// 標記修改
							if (tagAllID.toString().indexOf("s_16") < 0) {
								tagAllID.put("s_16");
							}
						}
						pmTempBean.getMapPmEntity().put(new_id, upd_pm);
						// 標記修改
						pmTempBean.getMocTagAllID().put(new_id, tagAllID);
						pmTempBean.getMocTagAllTime().put(new_id, Fm_Time_Model.to_yyMMdd(new Date()));
					}
				}
				// 倉庫
				else if (pmNewTempBean.getUpdate_cell().equals("ivn_note")) {
					if (!pmNewTempBean.getUpdate_value().equals("")) {
						JSONObject new_note = new JSONObject();
						JSONArray new_notes = new JSONArray(upd_pm.getIvn_note());
						new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
						new_note.put("user", new_name);
						new_note.put("ms", pmNewTempBean.getUpdate_value());
						new_notes.put(new_note);
						upd_pm.setIvn_note(new_notes.toString());
						upd_pm.setSys_modify_date(new Date());
						upd_pm.setSys_modify_user(new_name);
						// 標記有修正
						mocTagId = pmTempBean.getMocTagId();
						mocTagId.put(new_id, pmTempBean.getMapPmEntity().get(new_id).getMoc_cuser());
						pmTempBean.setMocTagId(mocTagId);
						// 標記修改
						if (tagAllID.toString().indexOf("s_20") < 0) {
							tagAllID.put("s_20");
						}
					}
					pmTempBean.getMapPmEntity().put(new_id, upd_pm);
					// 標記修改
					pmTempBean.getMocTagAllID().put(new_id, tagAllID);
					pmTempBean.getMocTagAllTime().put(new_id, Fm_Time_Model.to_yyMMdd(new Date()));
				}
				// 製造
				else if (pmNewTempBean.getUpdate_cell().equals("mes_note")) {
					if (!pmNewTempBean.getUpdate_value().equals("")) {
						JSONObject new_note = new JSONObject();
						JSONArray new_notes = new JSONArray(upd_pm.getMes_note());
						new_note.put("date", Fm_Time_Model.to_yMd_Hms(new Date()));
						new_note.put("user", new_name);
						new_note.put("ms", pmNewTempBean.getUpdate_value());
						new_notes.put(new_note);
						upd_pm.setMes_note(new_notes.toString());
						upd_pm.setSys_modify_date(new Date());
						upd_pm.setSys_modify_user(new_name);
						// 標記有修正
						mocTagId = pmTempBean.getMocTagId();
						mocTagId.put(new_id, pmTempBean.getMapPmEntity().get(new_id).getMoc_cuser());
						pmTempBean.setMocTagId(mocTagId);
						// 標記修改
						if (tagAllID.toString().indexOf("s_21") < 0) {
							tagAllID.put("s_21");
						}
					}
					pmTempBean.getMapPmEntity().put(new_id, upd_pm);
					// 標記修改
					pmTempBean.getMocTagAllID().put(new_id, tagAllID);
					pmTempBean.getMocTagAllTime().put(new_id, Fm_Time_Model.to_yyMMdd(new Date()));
				}

				// 解鎖時 已經有人用?
				if (pmTempBean.getLockPmID().containsKey(new_id)) {
					String old_name = pmTempBean.getLockPmID().get(new_id);
					// 同一人?
					if (old_name.equals(new_name)) {
						pmTempBean.getLockPmID().remove(new_id);
						pmTempBean.getLockPmTime().remove(new_id);
					}
				}
				doSameDataProductionManagement(new_id);
				check = true;
				message = "OK";
				break;
			default:
				check = true;
				break;
			}
			// =============
			break;
		case "server":
			doSameDataProductionManagement();
			message = "OK";
			check = true;
			break;
		default:
			check = false;
			message = "Fail";
			break;
		}

		// Step2.Temp一般資料回傳
		if (pmTempBean.getMapPmEntity() != null && //
				(!action.equals("only_unlock") && !action.equals("only_lock"))) {// 一般解鎖 上鎖不用更新
			moc_data.put(new JSONArray());
			pmTempBean.getMapPmEntity().forEach((id, entity) -> {
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(entity.getMoc_priority());// 生管-優先權(越大越前面)
				jsonArray.put(entity.getMoc_ta009()); // 預計開工
				jsonArray.put(entity.getMoc_ta010()); // 預計完工
				jsonArray.put(entity.getMoc_id()); // 製令單號
				jsonArray.put(entity.getMoc_ta006()); // 產品品號
				// 5
				jsonArray.put(entity.getMoc_ta034() == null ? "" : entity.getMoc_ta034()); // 產品品名
				jsonArray.put(entity.getMoc_ta035() == null ? "" : entity.getMoc_ta035()); // 產品規格
				jsonArray.put(entity.getMoc_ta015()); // 預計產量
				jsonArray.put(entity.getMoc_ta017()); // 已生產量
				jsonArray.put(entity.getMoc_ta011()); // 製令狀態
				// 10
				jsonArray.put(entity.getMoc_ta029()); // 製令備註
				jsonArray.put(entity.getCop_tc012()); // 客戶訂單號
				jsonArray.put(entity.getMoc_cuser());// 負責人
				jsonArray.put(entity.getBom_kind()); // 生管-產品狀態
				jsonArray.put(entity.getMoc_status()); // 生管-延展開單
				// 15
				jsonArray.put(entity.getMoc_note()); // 生管-備註
				jsonArray.put(entity.getMpr_date()); // 物控-預計齊料
				jsonArray.put(entity.getMpr_note()); // 物控-備註
				jsonArray.put(entity.getIvn_items()); // 倉庫-備料狀態
				jsonArray.put(entity.getIvn_items()); // 倉庫-缺料項數
				// 20
				jsonArray.put(entity.getIvn_note()); // 倉庫-備註
				jsonArray.put(entity.getMes_note()); // 倉庫-備註
				// 22 隱藏 系統
				jsonArray.put(entity.getId());
				jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
				jsonArray.put(entity.getSys_create_user());
				jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
				jsonArray.put(entity.getSys_modify_user());
				// 27
				jsonArray.put(entity.getNote());
				jsonArray.put(entity.getUseful());
				jsonArray.put(entity.getSys_check_sum());
				jsonArray.put(entity.getMoc_week());
				moc_data.put(jsonArray);
			});
		}

		// Step3.Temp鎖定資料回傳
		if (pmTempBean.getLockPmID() != null) {
			ArrayList<String> timeOut = new ArrayList<String>();
			Long new_time = new Date().getTime();
			pmTempBean.getLockPmID().forEach((moc_id, userName) -> {
				JSONObject lock_one = new JSONObject();
				lock_one.put(moc_id, userName);
				moc_id_lock.put(lock_one);
				System.out.println(new_time);
				System.out.println(pmTempBean.getLockPmTime().get(moc_id));
				if ((new_time - pmTempBean.getLockPmTime().get(moc_id) > 300000L)) {
					timeOut.add(moc_id);
				}
			});
			// 清除超時 5分鐘=300,000
			timeOut.forEach(x -> {
				pmTempBean.getLockPmID().remove(x);
				pmTempBean.getLockPmTime().remove(x);
			});
		}
		// 標記更新
		pmTempBean.getMocTagId().forEach((moc_id, userName) -> {
			JSONObject tag_one = new JSONObject();
			tag_one.put(moc_id, userName);
			moc_id_tag.put(tag_one);
		});
		// 標記指定 欄位 更新
		pmTempBean.getMocTagAllID().forEach((moc_id, cell) -> {
			JSONObject tag_one = new JSONObject();
			tag_one.put(moc_id, cell.toString());
			moc_id_tag_all.put(tag_one);
		});

		action = "all_update";
		// Step4. 訊息-廣播
		data.put("message", message);// OK or Fail
		data.put("moc_id_lock", moc_id_lock);// 固定傳回正在鎖定工單
		data.put("moc_id_tag", moc_id_tag);// 固定傳回有更新項目
		data.put("moc_id_tag_all", moc_id_tag_all);// 固定傳回有更新項目
		data.put("moc_data", moc_data);
		data.put("action", action);// 動作
		pmController.synchronize_pm_from_server(data);
		return check;
	}

	/** JSON to 清單 **/
	public ERP_PM_Entity jsonToEntities(JSONObject content) {
		ERP_PM_Entity entity = new ERP_PM_Entity();
		entity.setSys_create_date(new Date());
		entity.setSys_create_user(loginService.getSessionUserBean().getAccount());
		entity.setSys_modify_date(new Date());
		entity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
		// 查詢
		if (!content.isNull("moc_id") && !content.getString("moc_id").equals(""))// 製令單號
			entity.setMoc_id('%' + content.getString("moc_id") + '%');
		if (!content.isNull("order_id") && !content.getString("order_id").equals(""))// 訂單
			entity.setOrder_id('%' + content.getString("order_id") + '%');
		if (!content.isNull("moc_ta006") && !content.getString("moc_ta006").equals(""))// 產品品號
			entity.setMoc_ta006('%' + content.getString("moc_ta006") + '%');
		if (!content.isNull("useful"))// 單據狀態
			entity.setUseful(content.getInt("useful"));
		if (!content.isNull("moc_ta006_not") && !content.getString("moc_ta006_not").equals("")) {// 產品品號
			String ta006[] = content.getString("moc_ta006_not").split(" ");
			String ta006_not = "";
			for (int i = 0; i < ta006.length; i++) {
				ta006_not += "'" + ta006[i] + "%',";
			}
			ta006_not += "'null%'";
			entity.setMoc_ta006_not(ta006_not);
		}
		if (!content.isNull("moc_cuser") && !content.getString("moc_cuser").equals(""))// 負責人
			entity.setMoc_cuser('%' + content.getString("moc_cuser") + '%');
		if (!content.isNull("mpr_date_have") && content.getInt("mpr_date_have") != 0)// 齊料日?
			entity.setMpr_date(content.getInt("mpr_date_have") + "");
		if (!content.isNull("today_modify") && content.getInt("today_modify") != 0) { // 異動日?
			entity.setSys_modify_date(Fm_Time_Model.toDateHHmmss(Fm_Time_Model.to_yyMMdd(new Date()) + " 00:00:00"));
		} else {
			entity.setSys_modify_date(null);
		}
		return entity;
	}

	/**
	 * 清單 to JSON
	 * 
	 * @param p_Entities 使用者清單資料
	 **/
	public JSONObject entitiesToJson(List<ERP_PM_Entity> p_Entities) {
		JSONObject list = new JSONObject();
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();
		jsonArray.put("優先");
		jsonArray.put("預計開工");
		jsonArray.put("預計完工");
		jsonArray.put("製令單號");
		jsonArray.put("產品品號");
		// 5
		jsonArray.put("產品品名");
		jsonArray.put("產品規格");
		jsonArray.put("預計產量");
		jsonArray.put("已生產量");
		jsonArray.put("製令狀態");
		// 10
		jsonArray.put("製令備註");
		jsonArray.put("客戶訂單");
		jsonArray.put("負責人");
		jsonArray.put("產品狀態");
		jsonArray.put("延展開單");
		// 15
		jsonArray.put("生管-備註");
		jsonArray.put("預計齊料");
		jsonArray.put("物控-備註");
		jsonArray.put("備料狀態");
		jsonArray.put("缺料項數");
		// 20
		jsonArray.put("倉庫-備註");
		jsonArray.put("製造-備註");

		jsonAll.put(jsonArray);
		// 內容
		for (ERP_PM_Entity entity : p_Entities) {
			jsonArray = new JSONArray();
			jsonArray.put(entity.getMoc_priority());// 生管-優先權(越大越前面)
			jsonArray.put(entity.getMoc_ta009()); // 預計開工
			jsonArray.put(entity.getMoc_ta010()); // 預計完工
			jsonArray.put(entity.getMoc_id()); // 製令單號
			jsonArray.put(entity.getMoc_ta006()); // 產品品號
			// 5
			jsonArray.put(entity.getMoc_ta034() == null ? "" : entity.getMoc_ta034()); // 產品品名
			jsonArray.put(entity.getMoc_ta035() == null ? "" : entity.getMoc_ta035()); // 產品規格
			jsonArray.put(entity.getMoc_ta015()); // 預計產量
			jsonArray.put(entity.getMoc_ta017()); // 已生產量
			jsonArray.put(entity.getMoc_ta011()); // 製令狀態
			// 10
			jsonArray.put(entity.getMoc_ta029()); // 製令單備註
			jsonArray.put(entity.getCop_tc012()); // 客戶訂單號
			jsonArray.put(entity.getMoc_cuser()); // 負責人(開單人)
			jsonArray.put(entity.getBom_kind()); // 生管-產品狀態
			jsonArray.put(entity.getMoc_status()); // 生管-延展開單
			// 15
			jsonArray.put(entity.getMoc_note()); // 生管-備註
			jsonArray.put(entity.getMpr_date()); // 物控-預計齊料
			jsonArray.put(entity.getMpr_note()); // 物控-備註
			jsonArray.put(entity.getIvn_items()); // 倉庫-備料狀態
			jsonArray.put(entity.getIvn_items()); // 倉庫-缺料項數
			// 20
			jsonArray.put(entity.getIvn_note()); // 倉庫-備註
			jsonArray.put(entity.getMes_note()); // 產線-備註

			// 22 隱藏 系統
			jsonArray.put(entity.getId());
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
			jsonArray.put(entity.getSys_create_user());
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
			jsonArray.put(entity.getSys_modify_user());
			// 27
			jsonArray.put(entity.getNote());
			jsonArray.put(entity.getUseful());
			jsonArray.put(entity.getSys_check_sum());
			jsonArray.put(entity.getMoc_week());
			jsonAll.put(jsonArray);
		}
		list.put("list", jsonAll);
		// 鎖定
		JSONArray moc_id_lock = new JSONArray();
		if (pmTempBean.getLockPmID() != null) {
			ArrayList<String> timeOut = new ArrayList<String>();
			Long new_time = new Date().getTime();
			pmTempBean.getLockPmID().forEach((moc_id, userName) -> {
				JSONObject lock_one = new JSONObject();
				lock_one.put(moc_id, userName);
				moc_id_lock.put(lock_one);
				System.out.println(new_time);
				System.out.println(pmTempBean.getLockPmTime().get(moc_id));
				if ((new_time - pmTempBean.getLockPmTime().get(moc_id) > 300000L)) {
					timeOut.add(moc_id);
				}
			});
			// 清除超時 5分鐘=300,000
			timeOut.forEach(x -> {
				pmTempBean.getLockPmID().remove(x);
				pmTempBean.getLockPmTime().remove(x);
			});
		}
		list.put("moc_id_lock", moc_id_lock);

		return list;
	}

	/**
	 * 統整回傳包裝
	 * 
	 * @param p_Obj     回傳查詢資料
	 * @param frontData 前端傳入的"控制"資訊 回傳 成功
	 * @param r_Message 回傳訊息
	 **/
	public JSONObject ajaxRspJson(JSONObject p_Obj, JSONObject frontData, String r_Message) {
		JsonDataModel data = new JsonDataModel();
		JSONObject r_allData = new JSONObject();
		JsonObjBean objBean = new JsonObjBean();
		JsonTemplateBean templateBean = new JsonTemplateBean();

		templateBean.setWebPageBody("html/body/production_management_body.html");
		templateBean.setBodyData(p_Obj);

		objBean.setR_cellBackName(frontData.getString("cellBackName"));
		objBean.setR_action(frontData.getString("action"));
		objBean.setR_status(true);
		objBean.setR_cellBackOrder(frontData.getString("cellBackOrder"));
		objBean.setR_message(r_Message);

		data.setR_JsonData(objBean);
		data.setR_template(templateBean);
		r_allData = data.getR_allData();
		return r_allData;
	}

	/**
	 * 統整回傳包裝
	 * 
	 * @param p_Obj     回傳查詢資料
	 * @param frontData 前端傳入的"控制"資訊 回傳 成功
	 **/
	public JSONObject fail_ajaxRspJson(JSONObject frontData, String r_Message) {
		JsonDataModel data = new JsonDataModel();
		JSONObject r_allData = new JSONObject();
		JsonObjBean objBean = new JsonObjBean();
		JsonTemplateBean templateBean = new JsonTemplateBean();

		objBean.setR_cellBackName(frontData.getString("cellBackName"));
		objBean.setR_action(frontData.getString("action"));
		objBean.setR_status(false);
		objBean.setR_cellBackOrder(frontData.getString("cellBackOrder"));
		objBean.setR_message(r_Message);
		// Step5.包裝
		data.setR_JsonData(objBean);
		data.setR_template(templateBean);
		r_allData = data.getR_allData();
		return r_allData;
	}
}
