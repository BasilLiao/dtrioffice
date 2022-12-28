package dtri.com.bean;

import java.util.List;

import org.json.JSONObject;

import dtri.com.db.entity.BomGroupEntity;
import dtri.com.db.entity.BomProductEntity;
import dtri.com.db.entity.BomTypeItemEntity;

public class BomProductGroupBean {
	private List<BomProductEntity> bomProductEntities;
	private List<BomGroupEntity> bomGroupEntities;
	private List<BomTypeItemEntity> bomTypeItemEntities;
	private List<BomTypeItemEntity> bomAccessoriesTypeItemEntities;
	private JSONObject tempAutoBomPrint;

	public List<BomProductEntity> getBomProductEntities() {
		return bomProductEntities;
	}

	public void setBomProductEntities(List<BomProductEntity> bomProductEntities) {
		this.bomProductEntities = bomProductEntities;
	}

	public List<BomGroupEntity> getBomGroupEntities() {
		return bomGroupEntities;
	}

	public void setBomGroupEntities(List<BomGroupEntity> bomGroupEntities) {
		this.bomGroupEntities = bomGroupEntities;
	}

	public List<BomTypeItemEntity> getBomTypeItemEntities() {
		return bomTypeItemEntities;
	}

	public void setBomTypeItemEntities(List<BomTypeItemEntity> bomTypeItemEntities) {
		this.bomTypeItemEntities = bomTypeItemEntities;
	}

	public List<BomTypeItemEntity> getBomAccessoriesTypeItemEntities() {
		return bomAccessoriesTypeItemEntities;
	}

	public void setBomAccessoriesTypeItemEntities(List<BomTypeItemEntity> bomAccessoriesTypeItemEntities) {
		this.bomAccessoriesTypeItemEntities = bomAccessoriesTypeItemEntities;
	}

	public JSONObject getTempAutoBomPrint() {
		return tempAutoBomPrint;
	}

	public void setTempAutoBomPrint(JSONObject tempAutoBomPrint) {
		this.tempAutoBomPrint = tempAutoBomPrint;
	}
}
