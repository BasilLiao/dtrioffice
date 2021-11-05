package dtri.com.db.entity;

import java.util.Date;

import dtri.com.models.Fm_Time_Model;

//BOM 規格查詢
/**
 * --smc.MC002 庫別 名稱 --smc.MC001 庫別 代號 --ctb.TB015 預計 領料日 --ctb.TB004 預計 用數量
 * --ctb.TB001+ctb.TB003+ ctb.TB014 預計 (工單/採購) & 主件品號 --cta.TA010 預計 預計完工 入庫日
 * --cta.TA015 預計 預計產量 入庫量 --rtd.TD007 倉別 --rtd.TD012 愈交日 --rtd.TD008 採購數量
 */
public class ERP_MOC_PUR_Entity {

	private String s000;// 料號
	private String tp000;// 類型(進貨/生產/製造)
	private String tp001;// 倉別
	private String tp002;// 倉號
	private String tp003;// 日期
	private String tp004;// 出庫數
	private String tp005;// 入庫數
	private String note000;// 備註

	public ERP_MOC_PUR_Entity() {
		this.tp003 = Fm_Time_Model.to_yMd(new Date());
		this.s000 = "0000000000";
	}

	public String getTp000() {
		return tp000;
	}

	public void setTp000(String tp000) {
		this.tp000 = tp000;
	}

	public String getTp001() {
		return tp001;
	}

	public void setTp001(String tp001) {
		this.tp001 = tp001;
	}

	public String getTp002() {
		return tp002;
	}

	public void setTp002(String tp002) {
		this.tp002 = tp002;
	}

	public String getTp003() {
		return tp003;
	}

	public void setTp003(String tp003) {
		this.tp003 = tp003;
	}

	public String getTp004() {
		return tp004;
	}

	public void setTp004(String tp004) {
		this.tp004 = tp004;
	}

	public String getTp005() {
		return tp005;
	}

	public void setTp005(String tp005) {
		this.tp005 = tp005;
	}

	public String getNote000() {
		return note000;
	}

	public void setNote000(String note000) {
		this.note000 = note000;
	}

	public String getS000() {
		return s000;
	}

	public void setS000(String s000) {
		this.s000 = s000;
	}

}
