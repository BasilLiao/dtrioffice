package dtri.com.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dtri.com.db.entity.GroupEntity;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.service.BomCoustomPrintService;
import dtri.com.service.BomPrintService;
import dtri.com.service.LoginService;
import dtri.com.service.PermissionService;
import dtri.com.service.ProductionPrintService;
import dtri.com.service.UserService;
import dtri.com.tools.JsonDataModel;

@Controller
public class BomCoustomPrintController {

	@Autowired
	LoginService loginService;
	@Autowired
	BomPrintService bomPrintService;
	@Autowired
	ProductionPrintService productionPrintService;
	@Autowired
	BomCoustomPrintService bomCoustomPrintService;

	@Autowired
	UserService userService;
	@Autowired
	PermissionService permissionService;
	// 功能
	final static String SYS_F = "bom_custom_print.do";
	// log 訊息
	Logger logger = LogManager.getLogger();

	/**
	 * 
	 * 基本載入 /查詢
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/bom_custom_print", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String search_production(@RequestBody String ajaxJSON) {
		System.out.println("---controller - bom_custom_print");
		// Step1.取出 session 訊息 & 檢查權限
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000001");

		// Step2.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();
		int page_nb = 0, page_total = 100;
		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			ProductionRecordsEntity entity = new ProductionRecordsEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = bomCoustomPrintService.jsonToEntities(frontData.getJSONObject("content"));
				page_nb = bomCoustomPrintService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = bomCoustomPrintService.jsonToPageTotal(frontData.getJSONObject("content"));
			}
			// Step4-2 .DB 查詢 正確 資料
			// 取得換頁碼 如果沒有 0

			List<ProductionRecordsEntity> p_Entities = bomCoustomPrintService.searchAll(entity, page_nb, page_total);
			JSONObject p_Obj = bomCoustomPrintService.entitiesToJson(p_Entities);

			// Step4-2 .包裝資料
			r_allData = bomCoustomPrintService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = bomCoustomPrintService.fail_ajaxRspJson(frontData, "你沒有權限!!");
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
	@RequestMapping(value = "/create_bom_custom_print", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String stop_production(@RequestBody String ajaxJSON) {
		System.out.println("---controller - create_bom_custom_print");
		// Step2.取出 session 訊息 & 檢查權限( 新增 更新 )
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000011");

		// Step1.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		// System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();
		int page_nb = 0, page_total = 100;
		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {

			// Step4-1 .DB 取出 正確 資料
			ProductionRecordsEntity entity = new ProductionRecordsEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = bomCoustomPrintService.jsonToEntities(frontData.getJSONObject("content"));
				page_nb = bomCoustomPrintService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = bomCoustomPrintService.jsonToPageTotal(frontData.getJSONObject("content"));
				// Step4-1 .DB 新增 正確 資料
				bomCoustomPrintService.addRecords(entity);
			}

			// Step4-2 .DB 查詢 正確 資料
			// 取得換頁碼 如果沒有 0
			entity = new ProductionRecordsEntity();
			List<ProductionRecordsEntity> p_Entities = bomCoustomPrintService.searchAll(entity, page_nb, page_total);
			JSONObject p_Obj = bomCoustomPrintService.entitiesToJson(p_Entities);

			// Step4-2 .包裝資料
			r_allData = bomCoustomPrintService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = bomCoustomPrintService.fail_ajaxRspJson(frontData, "你沒有權限!!");
		}

		// Step5.結果回傳
		// System.out.println(r_allData);
		return r_allData.toString();
	}
}
