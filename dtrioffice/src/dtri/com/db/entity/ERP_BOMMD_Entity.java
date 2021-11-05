package dtri.com.db.entity;

//BOM 規格查詢
/**
 * --PM 在看 --> MD001 主件品號 = 查詢JOIN ->[BOM產品資訊 BOMMD] *[基本物料查詢 INVMB] MB001 品號 -
 * --MB002 品名 --MB003 規格
 * 
 * --MD003 元件品號 -> 查詢 **基本物料查詢** --MD006 組成用量 --MD009 製程 --MD011 生效日期 --MD012
 * 失效日期 --MD016 備註
 * 
 * --電子件 RD 在看 --> MD001 主件品號 = 查詢JOIN ->[BOM產品資訊 BOMMD] *[基本物料查詢 INVMB] MB001
 * 品號 - --MB002 品名 --MB003 規格 --MD009 製程 --MD015, MD201 - MD207 插件位置1-8
 * 
 * 
 */
public class ERP_BOMMD_Entity {

	private String md001;// 主件(品號)
	private String md003;// 元件(品號)
	private String mb002;// 元件(品名)
	private String mb003;// 元件(內容描述)
	private String md006;// 元件(數量)
	private String md009;// 主件(製程)

	private String md011;// 元件(生效日期)
	private String md012;// 元件(失效日期)
	private String md016;// 備註
	private String md015;// 元件(腳位)

	public String getMd001() {
		return md001;
	}

	public void setMd001(String md001) {
		this.md001 = md001;
	}

	public String getMd003() {
		return md003;
	}

	public void setMd003(String md003) {
		this.md003 = md003;
	}

	public String getMb002() {
		return mb002;
	}

	public void setMb002(String mb002) {
		this.mb002 = mb002;
	}

	public String getMb003() {
		return mb003;
	}

	public void setMb003(String mb003) {
		this.mb003 = mb003;
	}

	public String getMd006() {
		return md006;
	}

	public void setMd006(String md006) {
		this.md006 = md006;
	}

	public String getMd009() {
		return md009;
	}

	public void setMd009(String md009) {
		this.md009 = md009;
	}

	public String getMd011() {
		return md011;
	}

	public void setMd011(String md011) {
		this.md011 = md011;
	}

	public String getMd012() {
		return md012;
	}

	public void setMd012(String md012) {
		this.md012 = md012;
	}

	public String getMd016() {
		return md016;
	}

	public void setMd016(String md016) {
		this.md016 = md016;
	}

	public String getMd015() {
		return md015;
	}

	public void setMd015(String md015) {
		this.md015 = md015;
	}

}
