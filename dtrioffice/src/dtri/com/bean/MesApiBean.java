package dtri.com.bean;

/**
 * 
 * 串接用API MES
 * 
 **/
public class MesApiBean {
	private String pr_type;// 製令單類型
	private String pr_w_years;// 保固年分
	private String pr_p_v; // 產品版本
	private String ph_wpro_id; // 工作程序
	private String ph_wc_line; // 產線線別

	private String ps_b_f_sn; // 特殊SN 固定
	private String ps_b_sn; // 特殊SN 序列
	private String ph_mfg_p_no; // 產品 驗證碼
	private String ph_ps_no; // 產品 組件號

	private String ph_p_name;// 產品編號/名稱

	public String getPr_type() {
		return pr_type;
	}

	public void setPr_type(String pr_type) {
		this.pr_type = pr_type;
	}

	public String getPr_w_years() {
		return pr_w_years;
	}

	public void setPr_w_years(String pr_w_years) {
		this.pr_w_years = pr_w_years;
	}

	public String getPh_wpro_id() {
		return ph_wpro_id;
	}

	public void setPh_wpro_id(String ph_wpro_id) {
		this.ph_wpro_id = ph_wpro_id;
	}

	public String getPs_b_f_sn() {
		return ps_b_f_sn;
	}

	public void setPs_b_f_sn(String ps_b_f_sn) {
		this.ps_b_f_sn = ps_b_f_sn;
	}

	public String getPs_b_sn() {
		return ps_b_sn;
	}

	public void setPs_b_sn(String ps_b_sn) {
		this.ps_b_sn = ps_b_sn;
	}

	public String getPh_p_name() {
		return ph_p_name;
	}

	public void setPh_p_name(String ph_p_name) {
		this.ph_p_name = ph_p_name;
	}

	public String getPh_wc_line() {
		return ph_wc_line;
	}

	public void setPh_wc_line(String ph_wc_line) {
		this.ph_wc_line = ph_wc_line;
	}

	public String getPr_p_v() {
		return pr_p_v;
	}

	public void setPr_p_v(String pr_p_v) {
		this.pr_p_v = pr_p_v;
	}

	public String getPh_mfg_p_no() {
		return ph_mfg_p_no;
	}

	public void setPh_mfg_p_no(String ph_mfg_p_no) {
		this.ph_mfg_p_no = ph_mfg_p_no;
	}

	public String getPh_ps_no() {
		return ph_ps_no;
	}

	public void setPh_ps_no(String ph_ps_no) {
		this.ph_ps_no = ph_ps_no;
	}

}
