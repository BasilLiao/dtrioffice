package dtri.com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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

import dtri.com.bean.PMTempBean;
import dtri.com.db.entity.ERP_PM_Entity;
import dtri.com.db.entity.GroupEntity;
import dtri.com.service.ERP_ProductionManagementService;
import dtri.com.service.LoginService;
import dtri.com.tools.JsonDataModel;

@Controller
public class ProductionManagementController {
	@Autowired
	LoginService loginService;
	// 功能
	final static String SYS_F = "production_management.do";
	@Autowired
	ERP_ProductionManagementService managementService;

	final Logger logger = LogManager.getLogger();

	@Autowired
	private SimpMessagingTemplate template;// 後臺主動發送訊息

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
		// Step4.檢查許可權 & 輸入物件

		if (checkPermission) { // Step4-1 .DB 取出 正確 資料
			ERP_PM_Entity entity = new ERP_PM_Entity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = managementService.jsonToEntities(frontData.getJSONObject("content"));
			}
			List<ERP_PM_Entity> p_Entities = managementService.searchProductionManagement(entity);
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
	 * @param 限定用JSON 對象all/client/userName{"userName":XXXX,}
	 * 
	 * 
	 */
	@MessageMapping("/synPnmt") // 接收管道
	@SendTo("/toAllClient/synPnmt") // (回給有 [訂閱/連上] 的使用者)回傳管道
	public String synchronize_pm_from_client(String ajaxJSON) {
		System.out.println("--webSocket-controller - " + ajaxJSON);
		logger.info("receive msg from client: {}", ajaxJSON);
		String action = "";
		String re_action = "nothing";
		String ms = "";
		String moc_data = "";
		String moc_user = "";
		boolean check = false;

		try {
			JSONObject content = new JSONObject(ajaxJSON);
			PMTempBean pmNewTempBean = new PMTempBean();
			Map<String, String> lockPmID = new HashMap<String, String>();
			Map<String, Long> lockPmTime = new HashMap<String, Long>();
			action = content.getString("action");
			moc_user = content.getString("userName");
			// step1.資料判斷 action
			switch (action) {
			case "only_lock":// 單獨選取
				re_action = "only_lock_modify";
				break;
			case "only_unlock":// 單獨取消
				re_action = "only_unlock_modify";
				break;
			case "only_unlock_save":// 單獨存入內容
				
				pmNewTempBean.setUpdate_cell(content.getString("note_cell"));// 哪一格
				pmNewTempBean.setUpdate_value(content.getString("note_value"));// 內容物
				pmNewTempBean.setMoc_priority(content.getInt("moc_priority"));
				pmNewTempBean.setMpr_date(content.getString("mpr_date"));
				pmNewTempBean.setExcel_json(new JSONArray(content.getString("excel_json")));
				// 請求->成功
				re_action = "only_unlock_save_modify";
				break;
			default:
				break;
			}
			// step2. 通知伺服器-> 回傳給所有人
			lockPmID.put(content.getString("moc_id"), moc_user);
			lockPmTime.put(content.getString("moc_id"), new Date().getTime());
			pmNewTempBean.setLockPmID(lockPmID);
			pmNewTempBean.setLockPmTime(lockPmTime);
			pmNewTempBean.setUserName("all");
			check = managementService.doDataProductionManagement(action, pmNewTempBean);
			// step3. 回傳給當事人
			if (check) {
				ms = "OK";// 請求->成功
			} else {
				ms = "Fail";// 請求->失敗
			}
		} catch (Exception e) {
			System.out.println(e);
			ms = "Fail";
		}
		// step4. 包裝回復
		JSONObject resq = new JSONObject();
		resq.put("message", ms);
		resq.put("action", re_action);
		resq.put("moc_user", moc_user);
		resq.put("moc_data", moc_data);
		return resq.toString();
	}

	// 伺服器回傳
	public void synchronize_pm_from_server(JSONObject date) {
		template.convertAndSend("/toAllClient/synPnmt", "" + date.toString());

	}
}
