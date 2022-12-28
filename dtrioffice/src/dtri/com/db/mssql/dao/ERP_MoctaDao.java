package dtri.com.db.mssql.dao;

import java.util.List;

import dtri.com.db.entity.ERP_MoctaEntity;

public interface ERP_MoctaDao {

	/**
	 * 
	 * 一般查詢(清單查詢 )
	 * 
	 * @param 時間區間/清單排除
	 **/
	List<ERP_MoctaEntity> queryErpMocta(ERP_MoctaEntity entity);

}