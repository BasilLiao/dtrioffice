package dtri.com.db.mssql.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dtri.com.db.entity.ERP_BOMMD_Entity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_INVMC_Entity;
import dtri.com.db.entity.ERP_MOC_PUR_Entity;

public interface ERP_BomDao {

	/**
	 * 
	 * 一般查詢(清單查詢 )
	 * 
	 * @param 時間區間/清單排除
	 **/
	List<ERP_INVMB_Entity> queryERP_INVMB_List(@Param("invmb_entity") ERP_INVMB_Entity entity, @Param("erp_limit") String limit);

	/**
	 * 
	 * 一般查詢(庫存查詢 )
	 * 
	 * @param 時間區間/清單排除
	 **/
	List<ERP_INVMC_Entity> queryERP_INVMC_List(@Param("invmc_entity") ERP_INVMC_Entity entity);

	/**
	 * 
	 * 一般查詢(BOM查詢 )
	 * 
	 * @param 時間區間/清單排除
	 **/
	List<ERP_BOMMD_Entity> queryERP_BOMMD_List(@Param("bommd_entity") ERP_BOMMD_Entity entity);

	/**
	 * 
	 * 一般查詢(未來異動 )
	 * 
	 * @param 時間區間/清單排除
	 **/
	List<ERP_MOC_PUR_Entity> queryERP_MOC_PUR_List(@Param("mocpur_entity") ERP_MOC_PUR_Entity entity);

}