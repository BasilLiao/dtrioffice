package dtri.com.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dtri.com.db.entity.GroupEntity;
import dtri.com.db.entity.PermissionEntity;
import dtri.com.service.LoginService;
import dtri.com.service.PermissionService;
import dtri.com.tools.JsonDataModel;

@Controller
public class SysPermissionController {

	@Autowired
	LoginService loginService;
	@Autowired
	PermissionService permissionService;
	// 功能
	final static String SYS_F = "sys_permission.do";

	/**
	 * 
	 * 基本載入 /查詢
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/sys_permission", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String search_sys_permission(@RequestBody String ajaxJSON) {
		System.out.println("---controller - sys_permission");
		// Step1.取出 session 訊息 & 檢查權限
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000001");

		// Step2.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();

		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			PermissionEntity entity = new PermissionEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = permissionService.jsonToEntities(frontData.getJSONObject("content"));

			}
			entity.setGroup_id(0);
			List<PermissionEntity> p_Entities = permissionService.searchPermission(entity, false);
			JSONObject p_Obj = permissionService.entitiesToJson(p_Entities);

			// Step4-2 .包裝資料
			r_allData = permissionService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = permissionService.fail_ajaxRspJson(frontData);
		}

		// Step6.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}

	/**
	 * 新增
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/added_sys_permission", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String added_sys_permission(@RequestBody String ajaxJSON) {
		System.out.println("---controller - added_sys_permission");
		// Step2.取出 session 訊息 & 檢查權限( 新增 更新 )
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01001001");

		// Step1.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();

		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			PermissionEntity entity = new PermissionEntity();
			PermissionEntity entitycheck = new PermissionEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = permissionService.jsonToEntities(frontData.getJSONObject("content"));

				// 檢查 群組名稱 是否有重複 群組
				entitycheck = permissionService.jsonToEntities(frontData.getJSONObject("content"));
				entitycheck.setName("");
				entitycheck.setControl("");
				List<PermissionEntity> p_Entities = permissionService.searchPermission(entitycheck, false);
				// 有重複(同個群組 將ID 一樣)?
				if (p_Entities.size() > 0) {
					entity.setGroup_id(p_Entities.get(0).getGroup_id());
					permissionService.addedRepeatGroupPermission(entity);
				} else {
					// Step4-1 .DB 新增 正確 資料
					permissionService.addedPermission(entity);
				}
			}

			// Step4-2 .DB 查詢 正確 資料
			entity = new PermissionEntity();
			entity.setGroup_id(0);
			List<PermissionEntity> p_Entities = permissionService.searchPermission(entity, false);
			JSONObject p_Obj = permissionService.entitiesToJson(p_Entities);

			// Step4-2 .包裝資料
			r_allData = permissionService.ajaxRspJson(p_Obj, frontData, "新增成功!!");
		} else {

			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = permissionService.fail_ajaxRspJson(frontData);
		}

		// Step6.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}

	/**
	 * 更新
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/update_sys_permission", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String update_sys_permission(@RequestBody String ajaxJSON) {
		System.out.println("---controller - update_sys_permission");
		// Step2.取出 session 訊息 & 檢查權限( 新增 更新 )
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000101");

		// Step1.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();

		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			PermissionEntity entity = new PermissionEntity();
			PermissionEntity search_entity = new PermissionEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = permissionService.jsonToEntities(frontData.getJSONObject("content"));
				search_entity.setGroup_name(entity.getGroup_name());
				// 取出相同 群組ID
				List<PermissionEntity> pEntities = permissionService.searchPermission(search_entity, false);
				// 有相同群組?
				if (pEntities.size() >= 1) {
					entity.setGroup_name(pEntities.get(0).getGroup_name());
					entity.setGroup_id(pEntities.get(0).getGroup_id());
					// Step4-1 .DB 更新 正確 資料
					permissionService.updatePermission(entity);
				} else {
					// 不同群組
					// 移除舊資料
					permissionService.deletePermission(entity);
					// Step4-1 .DB 新增 正確 資料(群組沒重複)
					entity.setId(null);
					entity.setGroup_id(null);
					permissionService.addedPermission(entity);
				}
			}

			// Step4-2 .DB 查詢 正確 資料
			entity = new PermissionEntity();
			entity.setGroup_id(0);
			List<PermissionEntity> p_Entities = permissionService.searchPermission(entity, false);
			JSONObject p_Obj = permissionService.entitiesToJson(p_Entities);

			// Step4-3 .包裝資料
			r_allData = permissionService.ajaxRspJson(p_Obj, frontData, "更新成功!!");
		} else {

			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = permissionService.fail_ajaxRspJson(frontData);
		}

		// Step5.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}

	/**
	 * 移除
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/delete_sys_permission", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String delete_sys_permission(@RequestBody String ajaxJSON) {
		System.out.println("---controller - delete_sys_permission");
		// Step2.取出 session 訊息 & 檢查權限( 新增 更新 )
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000011");

		// Step1.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();

		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {

			// Step4-1 .DB 取出 正確 資料
			PermissionEntity entity = new PermissionEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = permissionService.jsonToEntities(frontData.getJSONObject("content"));

				// Step4-1 .DB 移除 正確 資料
				permissionService.deletePermission(entity);
			}

			// Step4-2 .DB 查詢 正確 資料
			entity = new PermissionEntity();
			entity.setGroup_id(0);
			List<PermissionEntity> p_Entities = permissionService.searchPermission(entity, false);
			JSONObject p_Obj = permissionService.entitiesToJson(p_Entities);

			// Step4-3 .包裝資料
			r_allData = permissionService.ajaxRspJson(p_Obj, frontData, "移除成功!!");
		} else {

			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = permissionService.fail_ajaxRspJson(frontData);
		}

		// Step5.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}
}
