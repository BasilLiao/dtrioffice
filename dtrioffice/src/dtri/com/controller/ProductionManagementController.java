package dtri.com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dtri.com.db.entity.GroupEntity;
import dtri.com.db.entity.SoftwareVersionEntity;
import dtri.com.service.LoginService;
import dtri.com.service.ProductionManagementService;
import dtri.com.tools.JsonDataModel;

@Controller
public class ProductionManagementController {
	@Autowired
	LoginService loginService;
	// 功能
	final static String SYS_F = "production_management.do";
	@Autowired
	ProductionManagementService managementService;
	final Logger logger = LogManager.getLogger();

	private SimpMessagingTemplate template;

	@Autowired
	public ProductionManagementController(SimpMessagingTemplate template) {
		this.template = template;
	}

	/**
	 * 
	 * 基本載入 /查詢
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/production_management", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String search_production_management(@RequestBody String ajaxJSON) {
		System.out.println("---controller - production_management");
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

		if (checkPermission) { // Step4-1 .DB 取出 正確 資料
			SoftwareVersionEntity entity = new SoftwareVersionEntity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = managementService.jsonToEntities(frontData.getJSONObject("content"));
				page_nb = managementService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = managementService.jsonToPageTotal(frontData.getJSONObject("content"));
			}
			List<SoftwareVersionEntity> p_Entities = managementService.searchSoftwareVersion(entity, page_nb, page_total);
			JSONObject p_Obj = managementService.entitiesToJson(p_Entities);

			// Step4-2 .包裝資料
			r_allData = managementService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else { // Step4-1 .登出 && 包裝 錯誤 資料 r_allData =
			managementService.fail_ajaxRspJson(frontData, "你沒有權限!!");
		}

		// Step6.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}

	/**
	 * 同步(syn_pnmt)
	 * 
	 * @param 限定用JSON
	 */
	@MessageMapping("/synPnmt") // 接收管道
	@SendTo("/toAllClient/synPnmt") // (回給有 [訂閱/連上] 的使用者)回傳管道
	public String synchronize_production_management(String ajaxJSON) {
		System.out.println("--webSocket-controller - synchronize_production_management");
		logger.info("receive msg from client: {}", ajaxJSON);

		Map<String, Object> map = new HashMap<>();
		map.put("message", ajaxJSON);
		map.put("from", "server");
		map.put("now", new Date().getTime());
		return "OK";
	}
}
